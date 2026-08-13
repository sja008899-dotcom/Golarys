package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    onToggleWishlist: (FlowerProductEntity) -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(product.drawableResName, context) {
        val id = context.resources.getIdentifier(product.drawableResName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
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
                            .padding(12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White
                        )
                    }

                    // Wishlist button
                    IconButton(
                        onClick = { onToggleWishlist(product) },
                        modifier = Modifier
                            .padding(12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "علاقه‌مندی",
                            tint = if (product.isWishlisted) ErrorRed else TextSecondary
                        )
                    }
                }

                // Details Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Title and Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.category,
                            color = BotanicalGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BotanicalGreen.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "امتیاز ${product.rating} (${product.reviewsCount} نظر)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.titleFa,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = product.titleEn,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Plant Care Guide Section
                    Text(
                        text = "🌱 راهنمای نگهداری تخصصی گل آریس",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BotanicalGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "☀️ نیاز نوری: ${product.lightReq}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = CardBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "💧 نیاز آبی: ${product.waterReq}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = CardBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "🌡️ دمای ایده‌آل: ${product.tempReq}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vendor Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = BotanicalGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فروشنده: ${product.vendorName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bottom Bar: Price & Add to Cart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (product.originalPriceToman > product.priceToman) {
                                Text(
                                    text = "${GolarysViewModel.formatPrice(product.originalPriceToman)} تومان",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = GolarysViewModel.formatPrice(product.priceToman),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = BotanicalGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تومان",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onAddToCart(product)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BotanicalGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "افزودن به سبد خرید", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
