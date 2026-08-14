package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
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
import kotlinx.coroutines.launch

enum class VendorNavSection(
    val titleFa: String,
    val titleEn: String,
    val icon: ImageVector,
    val testTag: String
) {
    PRODUCT_MANAGEMENT("مدیریت محصولات", "Product Management", Icons.Default.Inventory2, "nav_product_management"),
    RECENT_ORDERS("سفارشات اخیر", "Recent Orders", Icons.Default.ReceiptLong, "nav_recent_orders"),
    EARNINGS_OVERVIEW("نمای کلی درآمد", "Earnings Overview", Icons.Default.AccountBalanceWallet, "nav_earnings_overview")
}

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

    if (!isVendor) {
        // Not a vendor yet -> Show Application Form with preview switch
        VendorRegistrationForm(
            userApp = userApp,
            onPreviewClick = { isVendorPreviewMode = true },
            onSubmit = { shop, manager, nid, iban, city, addr, phone, lic ->
                viewModel.submitVendorApplication(shop, manager, nid, iban, city, addr, phone, lic)
            }
        )
    } else {
        // Vendor Dashboard with Side Navigation Drawer
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
                // Dashboard Header with Side Menu Drawer Trigger
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

                        // Quick Navigation Strip for convenience
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VendorNavSection.entries.forEach { section ->
                                val isSelected = selectedSection == section
                                Surface(
                                    onClick = { selectedSection = section },
                                    color = if (isSelected) HeritageGold else Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = section.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) DeepNavy else Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = section.titleFa,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) DeepNavy else Color.White,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Dashboard Main Content Section with Crossfade Transition
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
            onAddProduct = { titleFa, titleEn, category, price, origPrice, desc, light, water, temp ->
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
                        text = "غرفه گل رز پالاس",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "فروشنده برتر گل آریس",
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
            text = "منوی مدیریت فروشگاه (Side Navigation)",
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
                VendorNavSection.EARNINGS_OVERVIEW -> "فعال"
            }

            Surface(
                onClick = { onSectionSelected(section) },
                color = if (isSelected) BotanicalGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag(section.testTag)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
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
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) DeepNavy else Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Side Menu Footer Card
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
                        text = "وضعیت فروشگاه: فعال و آنلاین",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ارسال سریع و تضمین طراوت گل آریس",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/* ========================================================================= */
/* 1. PRODUCT MANAGEMENT SECTION (مدیریت محصولات)                           */
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
        // Product Management Placeholder & Stats Banner
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

                    // Metrics row
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

        // Search & Filter controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجوی نام گل در فهرست...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
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

        // Products List / Placeholders
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
/* 2. RECENT ORDERS SECTION (سفارشات اخیر)                                   */
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
        // Recent Orders Header & Overview Placeholder
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

        // Status Filter Chips
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
/* 3. EARNINGS OVERVIEW SECTION (نمای کلی درآمد)                             */
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
        // Main Balance Highlight Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💰 درآمد و موجودی قابل تسویه",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            color = HeritageGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Earnings Overview",
                                color = HeritageGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${GolarysViewModel.formatPrice(vendorBalance)} تومان",
                        color = HeritageGold,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تسویه آنی پایا و ساتنا ظرف ۲۴ ساعت پس از ثبت درخواست",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Financial Analytics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "فروش ناخالص کل", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${GolarysViewModel.formatPrice(totalSalesToman)} ت",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BotanicalGreenDark
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "کارمزد پلتفرم (۵٪)", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${GolarysViewModel.formatPrice(platformFeeToman)} ت",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "درآمد خالص", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${GolarysViewModel.formatPrice(netEarningsToman)} ت",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        // Payout Request Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💳 ثبت درخواست واریز وجه و تسویه شبا",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawIban,
                        onValueChange = { withdrawIban = it },
                        label = { Text("شماره شبا مقصد (IR...)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = withdrawAmountText,
                            onValueChange = { withdrawAmountText = it },
                            placeholder = { Text("مبلغ تسویه (تومان)...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amt = withdrawAmountText.toLongOrNull() ?: 1000000L
                                viewModel.withdrawVendorBalance(withdrawIban, amt)
                                withdrawAmountText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "ثبت تسویه", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Settlement History Placeholder
        item {
            Text(
                text = "📋 تاریخچه تسویه‌حساب‌های اخیر",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = TextPrimary
            )
        }

        item {
            SettlementHistoryItem(
                id = "SET-9941",
                date = "امروز - ۱۲:۳۰",
                amountToman = 2500000L,
                status = "تکمیل شده (پایا)",
                statusColor = SuccessGreen
            )
        }

        item {
            SettlementHistoryItem(
                id = "SET-8812",
                date = "دیروز - ۱۸:۰۰",
                amountToman = 1800000L,
                status = "واریز شده",
                statusColor = SuccessGreen
            )
        }
    }
}

@Composable
fun SettlementHistoryItem(
    id: String,
    date: String,
    amountToman: Long,
    status: String,
    statusColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "شناسه تسویه: $id", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = date, fontSize = 10.5.sp, color = TextSecondary)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${GolarysViewModel.formatPrice(amountToman)} تومان",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BotanicalGreenDark
                )
                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

/* ========================================================================= */
/* HELPER COMPONENTS                                                         */
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
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = contentColor
            )
            Text(
                text = title,
                fontSize = 9.5.sp,
                color = contentColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
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
                    .size(54.dp)
                    .background(SurfaceVariantLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BotanicalGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (actionLabel != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    var iban by remember { mutableStateOf("IR") }
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
        // Quick Preview Action Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🏪 پیش‌نمایش پنل فروشندگان گل آریس",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = HeritageGold
                        )
                        Text(
                            text = "مشاهده منوی سایدبار، مدیریت کالاها، سفارشات و تسویه حساب",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.8f)
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
