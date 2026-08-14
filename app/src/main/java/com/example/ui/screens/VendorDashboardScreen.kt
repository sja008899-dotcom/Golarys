package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FlowerProductEntity
import com.example.data.local.OrderEntity
import com.example.data.local.UserEntity
import com.example.data.local.VendorApplicationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class VendorNavSection(
    val titleFa: String,
    val titleEn: String,
    val icon: ImageVector,
    val testTag: String
) {
    PRODUCT_MANAGEMENT("محصولات", "Products", Icons.Default.Inventory2, "nav_product_management"),
    RECENT_ORDERS("سفارشات", "Orders", Icons.Default.ReceiptLong, "nav_recent_orders"),
    SALES_ANALYTICS("نمودار فروش", "Analytics", Icons.Default.BarChart, "nav_sales_analytics"),
    FLASH_DEALS("تخفیف شگفت‌انگیز", "Flash Deals", Icons.Default.FlashOn, "nav_flash_deals"),
    EARNINGS_OVERVIEW("درآمد و تسویه", "Earnings", Icons.Default.AccountBalanceWallet, "nav_earnings_overview")
}

data class FlashDealItem(
    val id: String,
    val product: FlowerProductEntity,
    val discountPercent: Int,
    val discountedPriceToman: Long,
    val originalPriceToman: Long,
    val durationHours: Int,
    var remainingSeconds: Long,
    var isActive: Boolean = true,
    val soldCount: Int = 12
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    currentUser: UserEntity?,
    orders: List<OrderEntity>,
    products: List<FlowerProductEntity>,
    vendorApplications: List<VendorApplicationEntity>,
    viewModel: GolarysViewModel
) {
    var isVendorPreviewMode by remember { mutableStateOf(false) }
    val isVendor = currentUser?.role == "VENDOR" || currentUser?.role == "SUPER_ADMIN" || isVendorPreviewMode
    val userApp = vendorApplications.find { it.userId == currentUser?.id }

    var selectedSection by remember { mutableStateOf(VendorNavSection.PRODUCT_MANAGEMENT) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showAddProductDialog by remember { mutableStateOf(false) }

    // Flash Deals in-memory state
    val flashDeals = remember(products) {
        mutableStateListOf(
            FlashDealItem(
                id = "FLASH-01",
                product = products.firstOrNull() ?: FlowerProductEntity(
                    id = "PROD-101",
                    titleFa = "دسته‌گل رز هلندی و زنبق طلایی",
                    titleEn = "Golden Iris & Rose",
                    category = "دسته‌گل",
                    priceToman = 1450000,
                    originalPriceToman = 1750000,
                    description = "ترکیب زنبق و رز ممتاز",
                    lightReq = "نور غیرمستقیم",
                    waterReq = "تعویض آب",
                    tempReq = "۱۸ درجه"
                ),
                discountPercent = 25,
                discountedPriceToman = 1087500,
                originalPriceToman = 1450000,
                durationHours = 6,
                remainingSeconds = 14320,
                isActive = true,
                soldCount = 18
            )
        )
    }

    if (!isVendor) {
        VendorRegistrationForm(
            userApp = userApp,
            onPreviewClick = { isVendorPreviewMode = true },
            onSubmit = { shop, manager, nid, iban, city, addr, phone, lic ->
                viewModel.submitVendorApplication(shop, manager, nid, iban, city, addr, phone, lic)
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = DeepNavy,
                    drawerContentColor = Color.White,
                    modifier = Modifier.width(300.dp)
                ) {
                    VendorSideNavContent(
                        currentSection = selectedSection,
                        productsCount = products.size,
                        ordersCount = orders.size,
                        flashDealsCount = flashDeals.count { it.isActive },
                        onSectionSelected = { section ->
                            selectedSection = section
                            coroutineScope.launch { drawerState.close() }
                        },
                        onCloseDrawer = {
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
            ) {
                // Header with Side Drawer Button
                Surface(
                    color = BotanicalGreenDark,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                        }
                                    },
                                    modifier = Modifier.testTag("vendor_side_menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "منوی ناوبری فروشنده",
                                        tint = HeritageGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = selectedSection.titleFa,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = HeritageGold.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = selectedSection.titleEn,
                                                color = HeritageGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "پنل مدیریت غرفه فروشنده گل آریس",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            if (selectedSection == VendorNavSection.PRODUCT_MANAGEMENT) {
                                IconButton(
                                    onClick = { showAddProductDialog = true },
                                    modifier = Modifier
                                        .background(HeritageGold, CircleShape)
                                        .size(36.dp)
                                        .testTag("add_product_fab")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "افزودن محصول جدید",
                                        tint = DeepNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Quick Navigation Strip
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(VendorNavSection.entries) { section ->
                                val isSelected = selectedSection == section
                                Surface(
                                    onClick = { selectedSection = section },
                                    color = if (isSelected) HeritageGold else Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = section.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) DeepNavy else Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = section.titleFa,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) DeepNavy else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Main Content Switching
                Box(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = selectedSection,
                        label = "VendorSectionTransition"
                    ) { section ->
                        when (section) {
                            VendorNavSection.PRODUCT_MANAGEMENT -> ProductManagementSection(
                                products = products,
                                viewModel = viewModel,
                                onAddProductClick = { showAddProductDialog = true }
                            )

                            VendorNavSection.RECENT_ORDERS -> RecentOrdersSection(
                                orders = orders,
                                viewModel = viewModel
                            )

                            VendorNavSection.SALES_ANALYTICS -> SalesAnalyticsSection(
                                orders = orders,
                                products = products
                            )

                            VendorNavSection.FLASH_DEALS -> FlashDealsSection(
                                products = products,
                                flashDeals = flashDeals,
                                viewModel = viewModel
                            )

                            VendorNavSection.EARNINGS_OVERVIEW -> EarningsOverviewSection(
                                currentUser = currentUser,
                                orders = orders,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAddProduct = { titleFa: String, titleEn: String, category: String, price: Long, origPrice: Long, desc: String, light: String, water: String, temp: String ->
                viewModel.addProduct(titleFa, titleEn, category, price, origPrice, desc, light, water, temp)
                showAddProductDialog = false
            }
        )
    }
}

@Composable
fun VendorSideNavContent(
    currentSection: VendorNavSection,
    productsCount: Int,
    ordersCount: Int,
    flashDealsCount: Int,
    onSectionSelected: (VendorNavSection) -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp, horizontal = 14.dp)
    ) {
        // Drawer Header / Vendor Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(HeritageGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "غرفه بوتیک گل آریس",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "فروشنده برتر و مورد تایید",
                        fontSize = 10.5.sp,
                        color = HeritageGold
                    )
                }
            }

            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "بستن منو",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "منوی ناوبری و مدیریت فروشگاه:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Side Navigation Menu Items
        VendorNavSection.entries.forEach { section ->
            val isSelected = currentSection == section
            val badgeText = when (section) {
                VendorNavSection.PRODUCT_MANAGEMENT -> "$productsCount کالا"
                VendorNavSection.RECENT_ORDERS -> "$ordersCount سفارش"
                VendorNavSection.SALES_ANALYTICS -> "+۱۸٪ رشد"
                VendorNavSection.FLASH_DEALS -> "$flashDealsCount فعال"
                VendorNavSection.EARNINGS_OVERVIEW -> "تسویه شبا"
            }

            Surface(
                onClick = { onSectionSelected(section) },
                color = if (isSelected) BotanicalGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .testTag(section.testTag)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = if (isSelected) HeritageGold else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = section.titleFa,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = section.titleEn,
                                fontSize = 10.sp,
                                color = if (isSelected) HeritageGoldLight else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Surface(
                        color = if (isSelected) HeritageGold else Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) DeepNavy else Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer status badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DeepNavyLight),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "وضعیت غرفه: فعال و آنلاین",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تضمین تحویل ۲ ساعته در سراسر تهران",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/* ========================================================================= */
/* 1. PRODUCT MANAGEMENT SECTION                                             */
/* ========================================================================= */
@Composable
fun ProductManagementSection(
    products: List<FlowerProductEntity>,
    viewModel: GolarysViewModel,
    onAddProductClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("همه") }
    val categories = listOf("همه", "دسته‌گل", "گل‌های آپارتمانی", "گلدان", "هدیه", "رز جاودان")

    val filteredProducts = products.filter { product ->
        (selectedCategoryFilter == "همه" || product.category == selectedCategoryFilter) &&
                (searchQuery.isBlank() || product.titleFa.contains(searchQuery, ignoreCase = true) || product.titleEn.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🌱 مدیریت محصولات و موجودی انبار",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BotanicalGreenDark
                            )
                            Text(
                                text = "Product Management & Inventory",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onAddProductClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_add_new_product")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "محصول جدید", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPlaceholderChip(
                            title = "کل محصولات",
                            value = "${products.size}",
                            icon = Icons.Default.Inventory2,
                            containerColor = BotanicalGreen.copy(alpha = 0.1f),
                            contentColor = BotanicalGreenDark,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPlaceholderChip(
                            title = "در حال فروش",
                            value = "${products.count { it.isApprovedByAdmin }}",
                            icon = Icons.Default.CheckCircle,
                            containerColor = SuccessGreen.copy(alpha = 0.1f),
                            contentColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPlaceholderChip(
                            title = "در انتظار تایید",
                            value = "${products.count { !it.isApprovedByAdmin }}",
                            icon = Icons.Default.HourglassTop,
                            containerColor = HeritageGold.copy(alpha = 0.2f),
                            contentColor = DeepNavy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Search & Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("جستجوی نام گل در فهرست...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Category filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BotanicalGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            item {
                EmptyPlaceholderCard(
                    icon = Icons.Default.Inventory2,
                    title = "محصولی در این دسته‌بندی یافت نشد",
                    subtitle = "با کلیک بر روی دکمه «محصول جدید»، گل یا گیاه جدیدی را به فروشگاه خود اضافه کنید.",
                    actionLabel = "➕ افزودن گل یا محصول جدید",
                    onAction = onAddProductClick
                )
            }
        } else {
            items(filteredProducts) { prod ->
                ProductManagementCard(
                    product = prod,
                    onDelete = { viewModel.deleteProduct(prod.id) }
                )
            }
        }
    }
}

@Composable
fun ProductManagementCard(
    product: FlowerProductEntity,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(product.drawableResName, context) {
        val id = context.resources.getIdentifier(product.drawableResName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = product.titleFa,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.titleFa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )

                    Surface(
                        color = if (product.isApprovedByAdmin) SuccessGreen.copy(alpha = 0.12f) else HeritageGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (product.isApprovedByAdmin) "تأیید شده" else "در انتظار بررسی",
                            color = if (product.isApprovedByAdmin) SuccessGreen else DeepNavy,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${product.category} • ${product.titleEn}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${GolarysViewModel.formatPrice(product.priceToman)} تومان",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BotanicalGreen
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف محصول",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ========================================================================= */
/* 2. RECENT ORDERS SECTION                                                  */
/* ========================================================================= */
@Composable
fun RecentOrdersSection(
    orders: List<OrderEntity>,
    viewModel: GolarysViewModel
) {
    var selectedOrderTab by remember { mutableStateOf("همه") }
    val orderFilters = listOf("همه", "در حال آماده‌سازی", "تایید عکس", "تحویل شده")

    val filteredOrders = orders.filter { order ->
        when (selectedOrderTab) {
            "همه" -> true
            "در حال آماده‌سازی" -> order.statusFa.contains("آماده") || order.statusFa.contains("پردازش")
            "تایید عکس" -> order.statusFa.contains("عکس")
            "تحویل شده" -> order.statusFa.contains("تحویل") || order.statusFa.contains("پیک")
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📦 مدیریت سفارشات اخیر و کنترل کیفیت",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BotanicalGreenDark
                    )
                    Text(
                        text = "Recent Orders & Quality Assurance Photo Proof",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPlaceholderChip(
                            title = "کل سفارشات",
                            value = "${orders.size}",
                            icon = Icons.Default.ReceiptLong,
                            containerColor = DeepNavy.copy(alpha = 0.08f),
                            contentColor = DeepNavy,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPlaceholderChip(
                            title = "نیازمند ارسال عکس",
                            value = "${orders.count { it.vendorPhotoProofUrl == null }}",
                            icon = Icons.Default.CameraAlt,
                            containerColor = HeritageGold.copy(alpha = 0.2f),
                            contentColor = DeepNavy,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPlaceholderChip(
                            title = "تحویل شده",
                            value = "${orders.count { it.statusFa.contains("تحویل") }}",
                            icon = Icons.Default.LocalShipping,
                            containerColor = SuccessGreen.copy(alpha = 0.1f),
                            contentColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(orderFilters) { tab ->
                    FilterChip(
                        selected = selectedOrderTab == tab,
                        onClick = { selectedOrderTab = tab },
                        label = { Text(tab, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BotanicalGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item {
                EmptyPlaceholderCard(
                    icon = Icons.Default.ReceiptLong,
                    title = "سفارشی در این وضعیت وجود ندارد",
                    subtitle = "سفارش‌های جدید ثبت‌شده توسط مشتریان بلافاصله در این بخش نمایان می‌شوند.",
                    actionLabel = null,
                    onAction = {}
                )
            }
        } else {
            items(filteredOrders) { order ->
                RecentOrderItemCard(
                    order = order,
                    onSendPhoto = {
                        viewModel.updateOrderStatusAndProof(
                            order.orderId,
                            "تایید عکس پیش از ارسال",
                            "img_hero_bouquet_1786618726074"
                        )
                    },
                    onDeliverToCourier = {
                        viewModel.updateOrderStatusAndProof(
                            order.orderId,
                            "تحویل به پیک",
                            order.vendorPhotoProofUrl
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun RecentOrderItemCard(
    order: OrderEntity,
    onSendPhoto: () -> Unit,
    onDeliverToCourier: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(order.vendorPhotoProofUrl, context) {
        val id = context.resources.getIdentifier(order.vendorPhotoProofUrl ?: "", "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = BotanicalGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سفارش #${order.orderId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${GolarysViewModel.formatPrice(order.totalAmountToman)} تومان",
                    fontWeight = FontWeight.ExtraBold,
                    color = BotanicalGreenDark,
                    fontSize = 13.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "اقلام: ${order.itemsSummary}",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "گیرنده: ${order.customerName} (${order.customerPhone}) | زمان تحویل: ${order.deliveryTimeSlot}",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = "آدرس: ${order.address}",
                fontSize = 10.5.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "وضعیت:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.statusFa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = BotanicalGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onSendPhoto,
                            colors = ButtonDefaults.buttonColors(containerColor = HeritageGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ارسال عکس گل", fontSize = 10.sp, color = DeepNavy, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onDeliverToCourier,
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تحویل به پیک", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/* ========================================================================= */
/* 3. SALES ANALYTICS SECTION (نمودار و آمار تحلیلی فروش)                     */
/* ========================================================================= */
@Composable
fun SalesAnalyticsSection(
    orders: List<OrderEntity>,
    products: List<FlowerProductEntity>
) {
    // Days sales data for weekly bar chart (شنبه تا جمعه)
    val weeklyDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    val dayNames = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه")
    val dayValues = listOf(1450000L, 2100000L, 1890000L, 3200000L, 2800000L, 4650000L, 5100000L)
    val maxDayValue = dayValues.maxOrNull() ?: 5000000L
    var selectedDayIndex by remember { mutableIntStateOf(6) } // Default Friday peak

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Analytics Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📊 آمار و شاخص‌های تحلیلی فروش",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BotanicalGreenDark
                            )
                            Text(
                                text = "Weekly Sales & Performance KPI Analytics",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            color = SuccessGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📈 +۱۸.۴٪ رشد هفتگی",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Key KPI Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPlaceholderChip(
                            title = "فروش کل هفته",
                            value = "۲۱,۱۹۰,۰۰۰ تومان",
                            icon = Icons.Default.TrendingUp,
                            containerColor = BotanicalGreen.copy(alpha = 0.1f),
                            contentColor = BotanicalGreenDark,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPlaceholderChip(
                            title = "میانگین سبد (AOV)",
                            value = "۱,۶۸۰,۰۰۰ تومان",
                            icon = Icons.Default.ShoppingBag,
                            containerColor = HeritageGold.copy(alpha = 0.2f),
                            contentColor = DeepNavy,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPlaceholderChip(
                            title = "رضایت مشتریان",
                            value = "۴.۹ از ۵ ⭐ (۹۸٪)",
                            icon = Icons.Default.Star,
                            containerColor = HeritageGold.copy(alpha = 0.15f),
                            contentColor = DeepNavy,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPlaceholderChip(
                            title = "میانگین آماده‌سازی",
                            value = "۳۵ دقیقه",
                            icon = Icons.Default.Timer,
                            containerColor = DeepNavy.copy(alpha = 0.08f),
                            contentColor = DeepNavy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Custom Weekly Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📈 نمودار میله‌ای درآمد روزانه (۷ روز اخیر)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = TextPrimary
                        )

                        Text(
                            text = "${dayNames[selectedDayIndex]}: ${GolarysViewModel.formatPrice(dayValues[selectedDayIndex])} تومان",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BotanicalGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bar Chart with Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barCount = dayValues.size
                            val spacing = size.width / (barCount * 2)
                            val barWidth = (size.width - (spacing * (barCount + 1))) / barCount

                            for (i in 0 until barCount) {
                                val value = dayValues[i]
                                val barHeightRatio = (value.toFloat() / maxDayValue.toFloat()).coerceIn(0.1f, 1f)
                                val barHeight = (size.height - 30.dp.toPx()) * barHeightRatio
                                val left = spacing + i * (barWidth + spacing)
                                val top = size.height - 30.dp.toPx() - barHeight

                                val isSelected = i == selectedDayIndex
                                val barBrush = if (isSelected) {
                                    Brush.verticalGradient(
                                        colors = listOf(HeritageGold, BotanicalGreen)
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(BotanicalGreenLight, BotanicalGreen.copy(alpha = 0.6f))
                                    )
                                }

                                drawRoundRect(
                                    brush = barBrush,
                                    topLeft = Offset(left, top),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }

                        // Day selector buttons over chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(top = 130.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weeklyDays.forEachIndexed { index, day ->
                                val isSelected = index == selectedDayIndex
                                Text(
                                    text = day,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BotanicalGreenDark else TextSecondary,
                                    modifier = Modifier
                                        .clickable { selectedDayIndex = index }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Sales Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💐 سهم دسته‌بندی‌ها در سبد فروش غرفه",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryShareItem(title = "دسته‌گل و باکس طبیعی", percent = 45, color = BotanicalGreen, amountText = "۹,۵۰۰,۰۰۰ تومان")
                    CategoryShareItem(title = "گیاهان آپارتمانی و مقاوم", percent = 30, color = DeepNavy, amountText = "۶,۳۵۰,۰۰۰ تومان")
                    CategoryShareItem(title = "رز جاودان و پکیج‌های هدیه", percent = 15, color = HeritageGold, amountText = "۳,۱۸۰,۰۰۰ تومان")
                    CategoryShareItem(title = "گلدان سرامیکی و لوازم جانبی", percent = 10, color = AccentCoral, amountText = "۲,۱۶۰,۰۰۰ تومان")
                }
            }
        }

        // Top Selling Products Leaderboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏆 ۳ محصول پرفروش این هفته (رتبه‌بندی غرفه)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TopSellerRankItem(
                        rank = "🥇 رتبه اول",
                        title = "دسته‌گل رز هلندی و زنبق طلایی آریس",
                        soldCount = "۴۲ عدد",
                        revenueText = "۶۰,۹۰۰,۰۰۰ تومان",
                        rankColor = HeritageGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TopSellerRankItem(
                        rank = "🥈 رتبه دوم",
                        title = "باکس چرمی گل رز و شکلات بلژیکی",
                        soldCount = "۲۸ عدد",
                        revenueText = "۶۵,۸۰۰,۰۰۰ تومان",
                        rankColor = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TopSellerRankItem(
                        rank = "🥉 رتبه سوم",
                        title = "سانسوریا ابلق پا کوتاه (گلدان طلایی)",
                        soldCount = "۲۴ عدد",
                        revenueText = "۱۶,۳۲۰,۰۰۰ تومان",
                        rankColor = DeepNavy
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryShareItem(
    title: String,
    percent: Int,
    color: Color,
    amountText: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 11.5.sp, color = TextPrimary)
            Text(text = "$percent٪ ($amountText)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun TopSellerRankItem(
    rank: String,
    title: String,
    soldCount: String,
    revenueText: String,
    rankColor: Color
) {
    Surface(
        color = SurfaceLight,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = rankColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = rank,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "فروش: $soldCount", fontSize = 10.5.sp, color = TextSecondary)
                }
            }

            Text(text = revenueText, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BotanicalGreen)
        }
    }
}

/* ========================================================================= */
/* 4. FLASH DEALS & PROMOTIONS SECTION (تخفیف‌های زمان‌دار و شگفت‌انگیز)      */
/* ========================================================================= */
@Composable
fun FlashDealsSection(
    products: List<FlowerProductEntity>,
    flashDeals: MutableList<FlashDealItem>,
    viewModel: GolarysViewModel
) {
    var showCreateDealDialog by remember { mutableStateOf(false) }

    // Live countdown timer effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            flashDeals.forEach { deal ->
                if (deal.isActive && deal.remainingSeconds > 0) {
                    deal.remainingSeconds--
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flash Deals Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(DeepNavy, BotanicalGreenDark)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = HeritageGold, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تخفیف‌های شگفت‌انگیز و زمان‌دار",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "ایجاد حراج ساعتی برای فروش سریع گل‌های تازه",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = { showCreateDealDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = HeritageGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_create_flash_deal")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ایجاد تخفیف", color = DeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Promotions List
        if (flashDeals.isEmpty()) {
            item {
                EmptyPlaceholderCard(
                    icon = Icons.Default.FlashOn,
                    title = "هیچ تخفیف شگفت‌انگیزی فعال نیست",
                    subtitle = "با تعریف تخفیف زمان‌دار، محصولات شما با برچسب ویژه در صفحه اول به خریداران پیشنهاد می‌شود.",
                    actionLabel = "⚡ تعریف تخفیف شگفت‌انگیز جدید",
                    onAction = { showCreateDealDialog = true }
                )
            }
        } else {
            items(flashDeals) { deal ->
                FlashDealCard(
                    deal = deal,
                    onToggleActive = {
                        deal.isActive = !deal.isActive
                        viewModel.showToast(if (deal.isActive) "تخفیف شگفت‌انگیز فعال شد." else "تخفیف موقتاً متوقف گردید.")
                    },
                    onDelete = {
                        flashDeals.remove(deal)
                        viewModel.showToast("تخفیف شگفت‌انگیز حذف شد.")
                    }
                )
            }
        }
    }

    if (showCreateDealDialog) {
        CreateFlashDealDialog(
            products = products,
            onDismiss = { showCreateDealDialog = false },
            onCreate = { product, percent, durationHours ->
                val discountAmount = (product.priceToman * percent) / 100
                val discountedPrice = product.priceToman - discountAmount
                val newDeal = FlashDealItem(
                    id = "FLASH-${System.currentTimeMillis() % 10000}",
                    product = product,
                    discountPercent = percent,
                    discountedPriceToman = discountedPrice,
                    originalPriceToman = product.priceToman,
                    durationHours = durationHours,
                    remainingSeconds = durationHours * 3600L,
                    isActive = true
                )
                flashDeals.add(0, newDeal)
                viewModel.showToast("تخفیف شگفت‌انگیز $percent٪ برای «${product.titleFa}» فعال گردید!")
                showCreateDealDialog = false
            }
        )
    }
}

@Composable
fun FlashDealCard(
    deal: FlashDealItem,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(deal.product.drawableResName, context) {
        val id = context.resources.getIdentifier(deal.product.drawableResName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    val hours = deal.remainingSeconds / 3600
    val minutes = (deal.remainingSeconds % 3600) / 60
    val seconds = deal.remainingSeconds % 60
    val timerString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (deal.isActive) HeritageGold else CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ErrorRed,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${deal.discountPercent}٪ تخفیف",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = deal.product.titleFa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = if (deal.isActive) SuccessGreen.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (deal.isActive) "در حال اجرا" else "متوقف شده",
                        color = if (deal.isActive) SuccessGreen else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${GolarysViewModel.formatPrice(deal.discountedPriceToman)} تومان",
                            fontWeight = FontWeight.ExtraBold,
                            color = BotanicalGreenDark,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${GolarysViewModel.formatPrice(deal.originalPriceToman)}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "زمان باقی‌مانده: $timerString",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📦 تعداد فروخته شده با این آفر: ${deal.soldCount} عدد",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = onToggleActive) {
                        Text(
                            text = if (deal.isActive) "توقف موقت" else "فعال‌سازی مجدد",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (deal.isActive) DeepNavy else BotanicalGreen
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف تخفیف", tint = ErrorRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CreateFlashDealDialog(
    products: List<FlowerProductEntity>,
    onDismiss: () -> Unit,
    onCreate: (FlowerProductEntity, Int, Int) -> Unit
) {
    var selectedProduct by remember { mutableStateOf(products.firstOrNull()) }
    var selectedDiscountPercent by remember { mutableIntStateOf(20) }
    var selectedDurationHours by remember { mutableIntStateOf(6) }

    val discountPresets = listOf(15, 20, 25, 35, 50)
    val durationPresets = listOf(2, 6, 12, 24)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⚡ ایجاد تخفیف شگفت‌انگیز ساعتی",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BotanicalGreenDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "انتخاب گل یا گیاه از فهرست:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(products) { prod ->
                        val isSelected = selectedProduct?.id == prod.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedProduct = prod },
                            label = { Text(prod.titleFa, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BotanicalGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Text(text = "درصد تخفیف شگفت‌انگیز:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    discountPresets.forEach { p ->
                        val isSelected = selectedDiscountPercent == p
                        Surface(
                            onClick = { selectedDiscountPercent = p },
                            color = if (isSelected) ErrorRed else SurfaceLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$p٪",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Text(text = "مدت زمان اعتبار تخفیف:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    durationPresets.forEach { d ->
                        val isSelected = selectedDurationHours == d
                        Surface(
                            onClick = { selectedDurationHours = d },
                            color = if (isSelected) DeepNavy else SurfaceLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$d ساعت",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                selectedProduct?.let { prod ->
                    val discounted = prod.priceToman - (prod.priceToman * selectedDiscountPercent / 100)
                    Surface(
                        color = BotanicalGreen.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "پیش‌نمایش قیمت فروش شگفت‌انگیز:", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${GolarysViewModel.formatPrice(discounted)} تومان",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BotanicalGreenDark
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedProduct?.let { prod ->
                        onCreate(prod, selectedDiscountPercent, selectedDurationHours)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                enabled = selectedProduct != null
            ) {
                Text("شروع کمپین حراج ⚡", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}

/* ========================================================================= */
/* 5. EARNINGS & SETTLEMENT SECTION                                          */
/* ========================================================================= */
@Composable
fun EarningsOverviewSection(
    currentUser: UserEntity?,
    orders: List<OrderEntity>,
    viewModel: GolarysViewModel
) {
    val vendorBalance = currentUser?.walletBalanceToman ?: 4800000L
    var withdrawIban by remember { mutableStateOf("IR820170000000123456789012") }
    var withdrawAmountText by remember { mutableStateOf("") }

    val totalSalesToman = orders.sumOf { it.totalAmountToman }
    val platformFeeToman = (totalSalesToman * 0.05).toLong()
    val netEarningsToman = totalSalesToman - platformFeeToman

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BotanicalGreenDark, DeepNavy)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "موجودی قابل تسویه غرفه",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${GolarysViewModel.formatPrice(vendorBalance)} تومان",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تسویه اتوماتیک پایا و ساتنا هر ۴۸ ساعت یک‌بار",
                            fontSize = 11.sp,
                            color = HeritageGoldLight
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💳 درخواست تسویه حساب به شماره شبا",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BotanicalGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawIban,
                        onValueChange = { withdrawIban = it },
                        label = { Text("شماره شبا بانکی (IR...)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("مبلغ تسویه (تومان)", fontSize = 12.sp) },
                        placeholder = { Text("مثلاً ۲,۰۰۰,۰۰۰", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val amount = withdrawAmountText.toLongOrNull() ?: 1000000L
                            viewModel.withdrawVendorBalance(withdrawIban, amount)
                            withdrawAmountText = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ثبت درخواست تسویه و واریز به حساب", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/* ========================================================================= */
/* SHARED HELPER COMPOSABLES                                                 */
/* ========================================================================= */
@Composable
fun MetricPlaceholderChip(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 9.5.sp, color = TextSecondary, maxLines = 1)
            Text(text = value, fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = contentColor, maxLines = 1)
        }
    }
}

@Composable
fun EmptyPlaceholderCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(BotanicalGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 11.5.sp, color = TextSecondary, textAlign = TextAlign.Center)

            actionLabel?.let { label ->
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VendorRegistrationForm(
    userApp: VendorApplicationEntity?,
    onPreviewClick: () -> Unit,
    onSubmit: (String, String, String, String, String, String, String, String) -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var managerName by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("تهران") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(BotanicalGreenDark, DeepNavy)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = HeritageGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "همکاری با گل آریس به عنوان فروشنده",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "به بزرگترین پلتفرم آنلاین گل و گیاه کشور بپیوندید و فروشگاه اختصاصی خود را مدیریت کنید.",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HeritageGold.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, HeritageGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مشاهده پیش‌نمایش پنل غرفه‌داران",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }

                    Button(
                        onClick = onPreviewClick,
                        colors = ButtonDefaults.buttonColors(containerColor = HeritageGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "ورود به پیش‌نمایش", color = DeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (userApp != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (userApp.status == "PENDING") HeritageGold.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(1.dp, if (userApp.status == "PENDING") HeritageGold else SuccessGreen)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (userApp.status == "PENDING") Icons.Default.HourglassTop else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (userApp.status == "PENDING") DeepNavy else SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (userApp.status == "PENDING") "درخواست شما در حال بررسی توسط ادمین است" else "درخواست شما تأیید گردید",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "نام فروشگاه: ${userApp.shopName} | شماره شبا: ${userApp.iban}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📝 فرم اطلاعات گلخانه / فروشگاه گل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BotanicalGreenDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("نام گلخانه یا گالری گل", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = managerName,
                        onValueChange = { managerName = it },
                        label = { Text("نام و نام خانوادگی صاحب امتیاز", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nationalId,
                            onValueChange = { nationalId = it },
                            label = { Text("کد ملی", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = licenseNumber,
                            onValueChange = { licenseNumber = it },
                            label = { Text("شماره جواز کسب", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = iban,
                        onValueChange = { iban = it },
                        label = { Text("شماره شبا بانکی جهت تسویه فروش (IR...)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("آدرس دقیق گلخانه / فروشگاه", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("تلفن ثابت یا همراه رابط فروشگاه", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (shopName.isNotBlank() && managerName.isNotBlank() && iban.length > 5) {
                                onSubmit(shopName, managerName, nationalId, iban, city, address, phone, licenseNumber)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ارسال درخواست احراز هویت و عقد قرارداد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
