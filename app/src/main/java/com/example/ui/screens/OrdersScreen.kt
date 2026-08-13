package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.OrderEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun OrdersScreen(
    orders: List<OrderEntity>
) {
    val context = LocalContext.current
    var selectedOrderForPhotoProof by remember { mutableStateOf<OrderEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(16.dp)
    ) {
        Text(
            text = "پیگیری سفارشات گل آریس",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Text(
            text = "مشاهده وضعیت لحظه‌ای سفارش و تصویر محصول آماده‌شده قبل از ارسال",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = BotanicalGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "هیچ سفارشی ثبت نشده است",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header: Order ID & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = null,
                                        tint = BotanicalGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "سفارش #${order.orderId}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                }

                                Surface(
                                    color = when (order.statusFa) {
                                        "تحویل شده" -> SuccessGreen.copy(alpha = 0.15f)
                                        "تایید عکس پیش از ارسال" -> HeritageGold.copy(alpha = 0.25f)
                                        else -> BotanicalGreen.copy(alpha = 0.15f)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = order.statusFa,
                                        color = when (order.statusFa) {
                                            "تحویل شده" -> SuccessGreen
                                            "تایید عکس پیش از ارسال" -> DeepNavy
                                            else -> BotanicalGreen
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                            Text(
                                text = "📦 اقلام: ${order.itemsSummary}",
                                fontSize = 13.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "🕒 زمان تحویل: ${order.deliveryTimeSlot}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "📍 آدرس: ${order.address}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "مبلغ کل:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${GolarysViewModel.formatPrice(order.totalAmountToman)} تومان",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BotanicalGreen
                                    )
                                }

                                // Photo Proof Button
                                if (!order.vendorPhotoProofUrl.isNullOrEmpty()) {
                                    OutlinedButton(
                                        onClick = { selectedOrderForPhotoProof = order },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, HeritageGold),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepNavy)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = HeritageGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "عکس قبل از ارسال", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Photo Proof Dialog
    selectedOrderForPhotoProof?.let { order ->
        val imageResId = remember(order.vendorPhotoProofUrl, context) {
            val id = context.resources.getIdentifier(order.vendorPhotoProofUrl ?: "", "drawable", context.packageName)
            if (id != 0) id else R.drawable.ic_launcher_background
        }

        Dialog(onDismissRequest = { selectedOrderForPhotoProof = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📸 تصویر محصول آماده‌شده قبل از ارسال",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BotanicalGreen
                    )
                    Text(
                        text = "ارسال شده توسط فروشنده: ${order.vendorName}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = "عکس تایید گل",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { selectedOrderForPhotoProof = null },
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "تایید و بستن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
