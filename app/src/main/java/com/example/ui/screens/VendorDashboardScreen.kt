package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FlowerProductEntity
import com.example.data.local.OrderEntity
import com.example.data.local.UserEntity
import com.example.data.local.VendorApplicationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    currentUser: UserEntity?,
    orders: List<OrderEntity>,
    products: List<FlowerProductEntity>,
    vendorApplications: List<VendorApplicationEntity>,
    viewModel: GolarysViewModel
) {
    val isVendor = currentUser?.role == "VENDOR" || currentUser?.role == "SUPER_ADMIN"
    val userApp = vendorApplications.find { it.userId == currentUser?.id }

    var selectedVendorTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("📊 پیشخوان فروشگاه", "📦 سفارشات و تایید عکس", "🌱 مدیریت محصولات")
    var showAddProductDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Vendor Header
        Surface(
            color = BotanicalGreenDark,
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
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isVendor) "پنل اختصاصی فروشنده گل آریس" else "ثبت‌نام و عضویت فروشندگان",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = if (isVendor) "مدیریت سفارشات، ارسال تصویر محصول به مشتری و تسویه حساب"
                            else "به جمع تامین‌کنندگان برتر گل و گیاه بازارگاه گل آریس بپیوندید",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (isVendor) {
                        Surface(
                            color = HeritageGold,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "تأیید شده",
                                color = DeepNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (isVendor) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ScrollableTabRow(
                        selectedTabIndex = selectedVendorTab,
                        containerColor = BotanicalGreenDark,
                        contentColor = HeritageGold,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedVendorTab == index,
                                onClick = { selectedVendorTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedVendorTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedVendorTab == index) HeritageGold else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        if (!isVendor) {
            // Not a vendor yet -> Show Application Form
            VendorRegistrationForm(
                userApp = userApp,
                onSubmit = { shop, manager, nid, iban, city, addr, phone, lic ->
                    viewModel.submitVendorApplication(shop, manager, nid, iban, city, addr, phone, lic)
                }
            )
        } else {
            // Vendor Hub
            when (selectedVendorTab) {
                0 -> VendorOverviewTab(currentUser, orders, viewModel)
                1 -> VendorOrdersProofTab(orders, viewModel)
                2 -> VendorProductsTab(products, viewModel, onAddProductClick = { showAddProductDialog = true })
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
fun VendorRegistrationForm(
    userApp: VendorApplicationEntity?,
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
                        Text(
                            text = "کارشناسان گل آریس ظرف ۲۴ ساعت کاری با شما تماس خواهند گرفت.",
                            fontSize = 11.sp,
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

@Composable
fun VendorOverviewTab(
    currentUser: UserEntity?,
    orders: List<OrderEntity>,
    viewModel: GolarysViewModel
) {
    var withdrawIban by remember { mutableStateOf("IR820170000000123456789012") }
    var withdrawAmountText by remember { mutableStateOf("") }

    val vendorBalance = currentUser?.walletBalanceToman ?: 4800000L

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Balance Card & IBAN settlement
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "موجودی قابل تسویه فروشگاه:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${GolarysViewModel.formatPrice(vendorBalance)} تومان",
                        color = HeritageGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "تسویه اتوماتیک پایا و ساتنا ظرف ۲۴ ساعت پس از ثبت درخواست",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Quick Settlement Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💸 درخواست تسویه حساب به شماره شبا",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawIban,
                        onValueChange = { withdrawIban = it },
                        label = { Text("شماره شبا مقصد", fontSize = 11.sp) },
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

        // Quick Vendor Stats
        item {
            Text(
                text = "📦 وضعیت سفارشات فعال در شعبه شما",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }

        items(orders) { order ->
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "سفارش #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "گیرنده: ${order.customerName} (${order.customerPhone})", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "زمان تحویل: ${order.deliveryTimeSlot}", fontSize = 11.sp, color = BotanicalGreenDark)
                    }
                    Surface(
                        color = BotanicalGreen.copy(alpha = 0.15f),
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

@Composable
fun VendorOrdersProofTab(
    orders: List<OrderEntity>,
    viewModel: GolarysViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "📸 مدیریت سفارشات و ارسال عکس تایید کیفیت به خریدار",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = "طبق منشور حقوق مشتریان گل آریس، پیش از تحویل به پیک، تصویر گل آماده‌شده را ارسال نمایید.",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        items(orders) { order ->
            val imageResId = remember(order.vendorPhotoProofUrl, context) {
                val id = context.resources.getIdentifier(order.vendorPhotoProofUrl ?: "", "drawable", context.packageName)
                if (id != 0) id else R.drawable.ic_launcher_background
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "سفارش #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${GolarysViewModel.formatPrice(order.totalAmountToman)} تومان", fontWeight = FontWeight.Bold, color = BotanicalGreen, fontSize = 13.sp)
                    }
                    Text(text = "محصولات: ${order.itemsSummary}", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "آدرس تحویل: ${order.address}", fontSize = 11.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "وضعیت: ${order.statusFa}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.updateOrderStatusAndProof(
                                            order.orderId,
                                            "تایید عکس پیش از ارسال",
                                            "img_hero_bouquet_1786618726074"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HeritageGold),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "ارسال عکس گل", fontSize = 10.sp, color = DeepNavy, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.updateOrderStatusAndProof(
                                            order.orderId,
                                            "تحویل شده",
                                            order.vendorPhotoProofUrl
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "تحویل به پیک", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
fun VendorProductsTab(
    products: List<FlowerProductEntity>,
    viewModel: GolarysViewModel,
    onAddProductClick: () -> Unit
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
                        text = "🌱 گل‌ها و گیاهان موجود در غرفه شما",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(text = "تعداد: ${products.size} قلم کالا", fontSize = 11.sp, color = TextSecondary)
                }

                Button(
                    onClick = onAddProductClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "محصول جدید", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(products) { prod ->
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
                        Text(text = prod.titleFa, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "دسته‌بندی: ${prod.category} | قیمت: ${GolarysViewModel.formatPrice(prod.priceToman)} ت", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = if (prod.isApprovedByAdmin) "✓ در حال فروش در سایت" else "⏳ در انتظار بررسی ادمین",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (prod.isApprovedByAdmin) SuccessGreen else ErrorRed
                        )
                    }

                    IconButton(onClick = { viewModel.deleteProduct(prod.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = ErrorRed)
                    }
                }
            }
        }
    }
}
