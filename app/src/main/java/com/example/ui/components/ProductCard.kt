package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FlowerProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun ProductCard(
    product: FlowerProductEntity,
    onProductClick: (FlowerProductEntity) -> Unit,
    onAddToCartClick: (FlowerProductEntity) -> Unit,
    onWishlistClick: (FlowerProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = remember(product.drawableResName, context) {
        val id = context.resources.getIdentifier(product.drawableResName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onProductClick(product) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(SurfaceVariantLight)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = product.titleFa,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Wishlist Toggle Button
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { onWishlistClick(product) }
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "علاقه‌مندی",
                        tint = if (product.isWishlisted) ErrorRed else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Rating Pill
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepNavy.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.rating.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = HeritageGold,
                        modifier = Modifier.size(12.dp)
                    )
                }

                // Category Tag
                Surface(
                    color = BotanicalGreen,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = product.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.titleFa,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Plant Care attributes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "☀️ ${product.lightReq}",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceVariantLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "💧 ${product.waterReq}",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceVariantLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price & Add to Cart Button
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
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = GolarysViewModel.formatPrice(product.priceToman),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = BotanicalGreen
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "تومان",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    // Add to Cart Button
                    Button(
                        onClick = { onAddToCartClick(product) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BotanicalGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "افزودن",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "خرید",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
