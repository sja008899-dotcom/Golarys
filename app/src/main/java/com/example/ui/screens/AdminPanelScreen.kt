package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FlowerProductEntity
import com.example.data.local.OrderEntity
import com.example.data.local.UserEntity
import com.example.data.local.VendorApplicationEntity
import com.example.data.local.WalletTransactionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: GolarysViewModel,
    users: List<UserEntity>,
    vendorApplications: List<VendorApplicationEntity>,
    allProducts: List<FlowerProductEntity>,
    allOrders: List<OrderEntity>,
    transactions: List<WalletTransactionEntity>
) {
    var selectedAdminTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("📊 آمار و KPI", "🏪 تایید فروشندگان", "🌺 نظارت محصولات", "💰 مالی و تسویه", "⚙️ تنظیمات سئو")

    var showAddProductDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Admin Top Header
        Surface(
            color = DeepNavy,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "پنل مدیریت ارشد گل آریس",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "نظارت بر بازارگاه، فروشندگان، کاتالوگ و تسویه‌های مالی",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Surface(
                        color = HeritageGold,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Super Admin",
                            color = DeepNavy,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Admin Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedAdminTab,
                    containerColor = DeepNavy,
                    contentColor = HeritageGold,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedAdminTab == index,
                            onClick = { selectedAdminTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedAdminTab == index) HeritageGold else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedAdminTab) {
            0 -> AdminKpiOverviewTab(allOrders, users, vendorApplications, allProducts)
            1 -> AdminVendorApprovalTab(vendorApplications, viewModel)
            2 -> AdminProductModerationTab(allProducts, viewModel, onAddNewProduct = { showAddProductDialog = true })
            3 -> AdminFinancialSettlementTab(transactions, allOrders)
            4 -> AdminSeoAndMetadataTab()
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
fun AdminKpiOverviewTab(
    orders: List<OrderEntity>,
    users: List<UserEntity>,
    vendorApps: List<VendorApplicationEntity>,
    products: List<FlowerProductEntity>
) {
    val totalRevenue = orders.sumOf { it.totalAmountToman }
    val commissionRevenue = (totalRevenue * 0.05).toLong()
    val pendingVendors = vendorApps.count { it.status == "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "📈 خلاصه عملکرد و شاخص‌های کلیدی پلتفرم",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        // KPI 4-Card Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiStatCard(
                        title = "فروش ناخالص پلتفرم",
                        value = "${GolarysViewModel.formatPrice(totalRevenue)} ت",
                        icon = Icons.Default.MonetizationOn,
                        color = BotanicalGreen,
                        modifier = Modifier.weight(1f)
                    )
                    KpiStatCard(
                        title = "درآمد کارمزد گل آریس (۵٪)",
                        value = "${GolarysViewModel.formatPrice(commissionRevenue)} ت",
                        icon = Icons.Default.AccountBalance,
                        color = HeritageGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiStatCard(
                        title = "تعداد کل سفارشات",
                        value = "${orders.size} سفارش",
                        icon = Icons.Default.LocalShipping,
                        color = DeepNavy,
                        modifier = Modifier.weight(1f)
                    )
                    KpiStatCard(
                        title = "فروشندگان در انتظار تایید",
                        value = "$pendingVendors فروشگاه",
                        icon = Icons.Default.PendingActions,
                        color = if (pendingVendors > 0) ErrorRed else SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Users & Inventory Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👥 توزیع کاربران و گلخانه‌های عضو",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "مشتریان عادی ثبت‌نامی:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${users.count { it.role == "CUSTOMER" }} نفر", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = CardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "فروشگاه‌ها و گلخانه‌های فعال:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${vendorApps.count { it.status == "APPROVED" }} فروشگاه", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = CardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "تنوع محصولات در کاتالوگ:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${products.size} گونه گل و گیاه", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Recent Orders Log
        item {
            Text(
                text = "📦 آخرین سفارشات ثبت شده در بازارگاه",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }

        items(orders.take(5)) { order ->
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
                        Text(text = "سفارش #${order.orderId} - ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "فروشنده: ${order.vendorName}", fontSize = 11.sp, color = TextSecondary)
                        Text(text = order.itemsSummary, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${GolarysViewModel.formatPrice(order.totalAmountToman)} ت",
                            fontWeight = FontWeight.Bold,
                            color = BotanicalGreen,
                            fontSize = 13.sp
                        )
                        Surface(
                            color = BotanicalGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = order.statusFa,
                                color = BotanicalGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        }
    }
}

@Composable
fun AdminVendorApprovalTab(
    vendorApplications: List<VendorApplicationEntity>,
    viewModel: GolarysViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "📋 درخواست‌های عضویت و احراز هویت فروشندگان",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = "بررسی مدارک هویتی، جواز کسب گلخانه و شماره شبا جهت فعال‌سازی پنل فروشنده",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        if (vendorApplications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "هیچ درخواستی ثبت نشده است.", color = TextSecondary)
                }
            }
        } else {
            items(vendorApplications) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(
                        1.dp,
                        when (app.status) {
                            "PENDING" -> HeritageGold
                            "APPROVED" -> SuccessGreen
                            else -> ErrorRed
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = app.shopName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "مدیر مسئول: ${app.managerName} | تاریخ ثبت: ${app.registrationDateShamsi}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                color = when (app.status) {
                                    "PENDING" -> HeritageGold.copy(alpha = 0.2f)
                                    "APPROVED" -> SuccessGreen.copy(alpha = 0.15f)
                                    else -> ErrorRed.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = when (app.status) {
                                        "PENDING" -> "در انتظار بررسی"
                                        "APPROVED" -> "تایید شده"
                                        else -> "رد شده"
                                    },
                                    color = when (app.status) {
                                        "PENDING" -> DeepNavy
                                        "APPROVED" -> SuccessGreen
                                        else -> ErrorRed
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                        Text(text = "💳 شماره شبا بانکی: ${app.iban}", fontSize = 11.sp, color = TextPrimary)
                        Text(text = "🆔 کد ملی: ${app.nationalId} | شماره جواز: ${app.licenseNumber}", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "📍 آدرس: ${app.city}، ${app.address}", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "📞 تلفن: ${app.phone}", fontSize = 11.sp, color = TextSecondary)

                        if (app.status == "PENDING") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.approveVendorApplication(app.id, app.userId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "تایید و فعال‌سازی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.rejectVendorApplication(app.id, "نقص در مدارک جواز کسب") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                    border = BorderStroke(1.dp, ErrorRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "رد درخواست", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductModerationTab(
    products: List<FlowerProductEntity>,
    viewModel: GolarysViewModel,
    onAddNewProduct: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🌺 نظارت و تایید محصولات کاتالوگ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "مدیریت ویترین، تایید اصالت و نشان برگزیده محصولات",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onAddNewProduct,
                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "افزودن گل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(products) { prod ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = prod.titleFa, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "فروشنده: ${prod.vendorName} | دسته‌بندی: ${prod.category}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${GolarysViewModel.formatPrice(prod.priceToman)} تومان",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BotanicalGreen
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Featured toggle
                            IconButton(onClick = { viewModel.toggleProductFeatured(prod.id, prod.isFeatured) }) {
                                Icon(
                                    imageVector = if (prod.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "ویژه",
                                    tint = if (prod.isFeatured) HeritageGold else TextSecondary
                                )
                            }

                            // Delete button
                            IconButton(onClick = { viewModel.deleteProduct(prod.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = ErrorRed
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (prod.isApprovedByAdmin) "✓ تأیید شده و در حال نمایش" else "⏳ در انتظار تأیید ادمین",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (prod.isApprovedByAdmin) SuccessGreen else ErrorRed
                        )

                        Switch(
                            checked = prod.isApprovedByAdmin,
                            onCheckedChange = { viewModel.toggleProductApproval(prod.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = BotanicalGreen, checkedTrackColor = BotanicalGreen.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFinancialSettlementTab(
    transactions: List<WalletTransactionEntity>,
    orders: List<OrderEntity>
) {
    val totalSales = orders.sumOf { it.totalAmountToman }
    val commissionAmount = (totalSales * 0.05).toLong()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "💰 امور مالی، کارمزد پلتفرم و تسویه شبا",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = "محاسبه ۵٪ کارمزد گل آریس و تسویه حساب با فروشگاه‌های عضو",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "مجموع فروش بازارگاه:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(
                        text = "${GolarysViewModel.formatPrice(totalSales)} تومان",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "سهم کارمزد ۵٪ گل آریس:", color = HeritageGold, fontSize = 12.sp)
                    Text(
                        text = "${GolarysViewModel.formatPrice(commissionAmount)} تومان",
                        color = HeritageGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text(
                text = "📜 تاریخچه تراکنش‌ها و تسویه‌های بانکی",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }

        items(transactions) { tx ->
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
                        Text(text = tx.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = tx.dateShamsi, fontSize = 10.sp, color = TextSecondary)
                    }
                    Text(
                        text = "${GolarysViewModel.formatPrice(tx.amountToman)} تومان",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (tx.isDeposit) SuccessGreen else DeepNavy
                    )
                }
            }
        }
    }
}

@Composable
fun AdminSeoAndMetadataTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "⚙️ بهینه‌سازی موتورهای جستجو (SEO) و متادیتا",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = "پیکربندی استانداردهای گوگل، اسکیما مارک‌آپ JSON-LD و نقشه سایت (Sitemap.xml)",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🌐 پیش‌نمایش در نتایج گوگل (Google SERP Snippet):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "https://golarys.ir › flower-delivery", color = BotanicalGreenDark, fontSize = 11.sp)
                    Text(
                        text = "گل آریس | خرید آنلاین گل و گیاه آپارتمانی و دسته‌گل لوکس در تهران",
                        color = Color(0xFF1A0DAB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "ارسال فوری انواع دسته‌گل رز هلندی، زنبق، سانسوریا، ارکیده و باکس هدیه با ضمانت شادابی و تایید تصویر قبل از ارسال.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Schema JSON-LD (Rich Snippets)", color = HeritageGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Surface(color = BotanicalGreen, shape = RoundedCornerShape(6.dp)) {
                            Text(text = "Google Validated", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
{
  "@context": "https://schema.org",
  "@type": "Florist",
  "name": "Golarys Flower Marketplace",
  "url": "https://golarys.ir",
  "logo": "https://golarys.ir/assets/logo.png",
  "priceRange": "IRR",
  "telephone": "+98-21-88990000",
  "address": {
    "@type": "PostalAddress",
    "addressLocality": "Tehran",
    "addressCountry": "IR"
  }
}
                        """.trimIndent(),
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Sitemap & Robots.txt Auto-Generator", color = HeritageGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
User-agent: *
Allow: /
Disallow: /admin/
Disallow: /checkout/
Sitemap: https://golarys.ir/sitemap.xml
                        """.trimIndent(),
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (String, String, String, Long, Long, String, String, String, String) -> Unit
) {
    var titleFa by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("دسته‌گل") }
    var priceText by remember { mutableStateOf("") }
    var origPriceText by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var light by remember { mutableStateOf("نور غیرمستقیم") }
    var water by remember { mutableStateOf("هفته‌ای یک‌بار") }
    var temp by remember { mutableStateOf("۱۸ تا ۲۴ درجه") }

    val categories = listOf("دسته‌گل", "گل‌های آپارتمانی", "گلدان", "هدیه", "رز جاودان")

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "➕ افزودن گل یا گیاه جدید به بازارگاه",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BotanicalGreenDark
                    )
                }

                item {
                    OutlinedTextField(
                        value = titleFa,
                        onValueChange = { titleFa = it },
                        label = { Text("نام فارسی محصول (مثال: دسته‌گل رز هلندی)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = titleEn,
                        onValueChange = { titleEn = it },
                        label = { Text("نام انگلیسی محصول", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                item {
                    Text(text = "دسته‌بندی:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("قیمت (تومان)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = origPriceText,
                            onValueChange = { origPriceText = it },
                            label = { Text("قیمت قبل از تخفیف", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("توضیحات و مشخصات گل", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (titleFa.isNotBlank()) {
                                    val price = priceText.toLongOrNull() ?: 1000000L
                                    val orig = origPriceText.toLongOrNull() ?: 0L
                                    onAddProduct(titleFa, titleEn, category, price, orig, desc, light, water, temp)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ثبت و انتشار محصول", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("انصراف", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
