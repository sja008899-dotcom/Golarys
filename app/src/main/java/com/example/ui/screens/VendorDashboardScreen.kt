package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    orders: List<OrderEntity>,
    onAddProduct: (String, String, Long, String, String, String, String) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("دسته‌گل") }
    var priceInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var lightInput by remember { mutableStateOf("نور غیرمستقیم") }
    var waterInput by remember { mutableStateOf("هفته‌ای یک‌بار") }
    var tempInput by remember { mutableStateOf("۲۰ تا ۲۵ درجه") }

    val categories = listOf("دسته‌گل", "گل‌های آپارتمانی", "هدیه", "گلدان")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vendor Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BotanicalGreen),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "داشبورد اختصاصی فروشندگان گل آریس",
                            color = HeritageGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = HeritageGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "فروش امروز:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text(text = "۳,۸۰۰,۰۰۰ تومان", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column {
                            Text(text = "سفارشات جدید:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text(text = "${orders.size} عدد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column {
                            Text(text = "کارمزد پلتفرم:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text(text = "۵٪", color = HeritageGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // Section: Add Product to Shop
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "➕ افزودن گل یا گیاه جدید به ویترین",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BotanicalGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("نام گل یا محصول (فارسی)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("قیمت (تومان)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Category Chip dropdown simple select
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "دسته‌بندی:", fontSize = 11.sp, color = TextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = true,
                                    onClick = { },
                                    label = { Text(categoryInput, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HeritageGold,
                                        selectedLabelColor = DeepNavy
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("توضیحات و ویژگی‌ها", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = lightInput,
                            onValueChange = { lightInput = it },
                            label = { Text("شرایط نور", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = waterInput,
                            onValueChange = { waterInput = it },
                            label = { Text("شرایط آبیاری", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val price = priceInput.toLongOrNull() ?: 0L
                            if (titleInput.isNotBlank() && price > 0) {
                                onAddProduct(
                                    titleInput,
                                    categoryInput,
                                    price,
                                    descInput,
                                    lightInput,
                                    waterInput,
                                    tempInput
                                )
                                titleInput = ""
                                priceInput = ""
                                descInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ثبت محصول در فروشگاه", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Order Management & Pre-dispatch Photo Upload
        item {
            Text(
                text = "📸 مدیریت سفارشات و ارسال عکس تایید به مشتری",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        items(orders) { order ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سفارش #${order.orderId}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "وضعیت: ${order.statusFa}",
                            color = BotanicalGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "اقلام: ${order.itemsSummary}", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onUpdateOrderStatus(order.orderId, "تایید عکس پیش از ارسال")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, HeritageGold)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = HeritageGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ارسال عکس تایید", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                        }

                        Button(
                            onClick = {
                                onUpdateOrderStatus(order.orderId, "در حال ارسال با پیک")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تحویل پیک", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
