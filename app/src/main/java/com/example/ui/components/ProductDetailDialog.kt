package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.FlowerProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun ProductDetailDialog(
    product: FlowerProductEntity,
    onDismiss: () -> Unit,
    onAddToCart: (FlowerProductEntity) -> Unit,
    onWishlistToggle: (FlowerProductEntity) -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(product.drawableResName, context) {
        val id = context.resources.getIdentifier(product.drawableResName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = product.titleFa,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = TextPrimary)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.titleFa,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = product.titleEn,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = { onWishlistToggle(product) }) {
                            Icon(
                                imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (product.isWishlisted) ErrorRed else TextSecondary
                            )
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "☀️ نور", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = product.lightReq, fontSize = 10.sp, color = TextSecondary)
                            }
                            Divider(
                                modifier = Modifier
                                    .height(28.dp)
                                    .width(1.dp),
                                color = CardBorder
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "💧 آبیاری", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = product.waterReq, fontSize = 10.sp, color = TextSecondary)
                            }
                            Divider(
                                modifier = Modifier
                                    .height(28.dp)
                                    .width(1.dp),
                                color = CardBorder
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🌡️ دما", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = product.tempReq, fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "توضیحات و مشخصات:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = product.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فروشنده: ${product.vendorName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BotanicalGreenDark
                        )
                    }
                }

                item {
                    Divider(color = CardBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (product.originalPriceToman > product.priceToman) {
                                Text(
                                    text = "${GolarysViewModel.formatPrice(product.originalPriceToman)} تومان",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Text(
                                text = "${GolarysViewModel.formatPrice(product.priceToman)} تومان",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = BotanicalGreen
                            )
                        }

                        Button(
                            onClick = {
                                onAddToCart(product)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "افزودن به سبد خرید", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
