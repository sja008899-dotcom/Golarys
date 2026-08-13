package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image Box with Close & Wishlist Buttons
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

                    // Top Bar Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { onWishlistToggle(product) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "علاقه‌مندی",
                                tint = if (product.isWishlisted) ErrorRed else TextSecondary
                            )
                        }
                    }
                }

                // Details Content
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
                        Surface(
                            color = BotanicalGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = product.category,
                                color = BotanicalGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                        text = "🪴 راهنمای نگهداری تخصصی",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BotanicalGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CareInfoCard(
                            icon = Icons.Default.WbSunny,
                            title = "نور",
                            desc = product.lightReq,
                            modifier = Modifier.weight(1f)
                        )
                        CareInfoCard(
                            icon = Icons.Default.WaterDrop,
                            title = "آبیاری",
                            desc = product.waterReq,
                            modifier = Modifier.weight(1f)
                        )
                        CareInfoCard(
                            icon = Icons.Default.Thermostat,
                            title = "دما",
                            desc = product.tempReq,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vendor & Guarantee Badge
                    Surface(
                        color = SurfaceVariantLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = BotanicalGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "فروشنده: ${product.vendorName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "✓ ضمانت ۱۰۰٪ شادابی و سلامت گل تا تحویل",
                                    fontSize = 10.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Price & Add to Cart Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "قیمت کل:", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${GolarysViewModel.formatPrice(product.priceToman)} تومان",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = BotanicalGreen
                            )
                        }

                        Button(
                            onClick = {
                                onAddToCart(product)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "افزودن به سبد خرید", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CareInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HeritageGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
