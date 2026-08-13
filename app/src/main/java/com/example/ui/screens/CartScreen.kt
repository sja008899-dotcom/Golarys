package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.R
import com.example.data.local.CartItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun CartScreen(
    cartItems: List<CartItemEntity>,
    totalToman: Long,
    walletBalanceToman: Long,
    onUpdateQuantity: (String, Int) -> Unit,
    onPlaceOrder: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    var address by remember { mutableStateOf("تهران، خیابان ولیعصر، نرسیده به میدان ونک، پلاک ۱۲۰") }
    var selectedTimeSlot by remember { mutableStateOf("ساعت ۱۶:۰۰ الی ۲۰:۰۰") }
    var couponInput by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf(0) }
    var couponApplied by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("درگاه شتاب") } // "درگاه شتاب" or "کیف پول"

    val timeSlots = listOf("ساعت ۱۰:۰۰ الی ۱۳:۰۰", "ساعت ۱۶:۰۰ الی ۲۰:۰۰", "فردا صبح ۱۰:۰۰ الی ۱۳:۰۰")

    val discountAmount = (totalToman * discountPercent) / 100
    val shippingFee = if (totalToman > 1500000 || totalToman == 0L) 0L else 45000L
    val finalPayable = maxOf(0L, totalToman - discountAmount + shippingFee)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = BotanicalGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "سبد خرید شما خالی است",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "گل‌ها و گیاهان مورد علاقه خود را به سبد خرید اضافه کنید.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cart Header
                item {
                    Text(
                        text = "سبد خرید گل آریس (${cartItems.size} آیتم)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }

                // Items list
                items(cartItems) { item ->
                    val imageResId = remember(item.drawableResName, context) {
                        val id = context.resources.getIdentifier(item.drawableResName, "drawable", context.packageName)
                        if (id != 0) id else R.drawable.ic_launcher_background
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = item.titleFa,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.titleFa,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${GolarysViewModel.formatPrice(item.priceToman)} تومان",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = BotanicalGreen
                                )
                            }

                            // Quantity Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceVariantLight)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.productId, 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "افزایش",
                                        tint = BotanicalGreen
                                    )
                                }

                                Text(
                                    text = item.quantity.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                IconButton(
                                    onClick = { onUpdateQuantity(item.productId, -1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                        contentDescription = "کاهش",
                                        tint = ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }

                // Delivery Options Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📍 آدرس تحویل سفارش",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "🕒 زمان تحویل تحویلی با پیک اختصاصی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                timeSlots.forEach { slot ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        RadioButton(
                                            selected = (selectedTimeSlot == slot),
                                            onClick = { selectedTimeSlot = slot },
                                            colors = RadioButtonDefaults.colors(selectedColor = BotanicalGreen)
                                        )
                                        Text(text = slot, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Promo Coupon & Payment Method
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎁 کد تخفیف",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("کد GOLARYS1403", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (couponInput.trim().equals("GOLARYS1403", ignoreCase = true)) {
                                            discountPercent = 15
                                            couponApplied = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "اعمال", fontSize = 12.sp)
                                }
                            }
                            if (couponApplied) {
                                Text(
                                    text = "✓ تخفیف ۱۵ درصدی با موفقیت اعمال شد!",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "💳 روش پرداخت",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = (paymentMethod == "درگاه شتاب"),
                                    onClick = { paymentMethod = "درگاه شتاب" },
                                    label = { Text("درگاه بانکی شتاب", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BotanicalGreen,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = (paymentMethod == "کیف پول"),
                                    onClick = { paymentMethod = "کیف پول" },
                                    label = { Text("کیف پول (${GolarysViewModel.formatPrice(walletBalanceToman)} ت)", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HeritageGold,
                                        selectedLabelColor = DeepNavy
                                    ),
                                    modifier = Modifier.weight(1.2f)
                                )
                            }
                        }
                    }
                }
            }

            // Fixed Bottom Checkout Summary Bar
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "مجموع خرید:", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${GolarysViewModel.formatPrice(totalToman)} تومان", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    if (discountAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "تخفیف (۱۵٪):", fontSize = 13.sp, color = SuccessGreen)
                            Text(text = "- ${GolarysViewModel.formatPrice(discountAmount)} تومان", fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "هزینه ارسال:", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            text = if (shippingFee == 0L) "رایگان (خرید بالای ۱.۵ میلیون)" else "${GolarysViewModel.formatPrice(shippingFee)} تومان",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (shippingFee == 0L) SuccessGreen else TextPrimary
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "مبلغ قابل پرداخت:", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = "${GolarysViewModel.formatPrice(finalPayable)} تومان",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BotanicalGreen
                            )
                        }

                        Button(
                            onClick = {
                                onPlaceOrder(address, selectedTimeSlot, paymentMethod)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "ثبت و پرداخت نهایی", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
