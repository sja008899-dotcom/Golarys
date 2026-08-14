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
import com.example.data.local.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GolarysTopBar(
    currentUser: UserEntity?,
    cartCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCartClick: () -> Unit,
    onVendorTabClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAdminClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    val context = LocalContext.current
    val logoResId = remember(context) {
        val id = context.resources.getIdentifier("img_app_logo_1786618714248", "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_foreground
    }

    val isSuperAdmin = currentUser?.role == "SUPER_ADMIN"
    val isVendor = currentUser?.role == "VENDOR"

    Surface(
        color = BotanicalGreenDark,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Action Buttons Left
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Cart Icon with Badge
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(
                                    containerColor = HeritageGold,
                                    contentColor = DeepNavy
                                ) {
                                    Text(
                                        text = cartCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        },
                        modifier = Modifier.clickable { onCartClick() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "سبد خرید",
                                tint = HeritageGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Account / Profile Button
                    IconButton(
                        onClick = onAccountClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (currentUser != null) HeritageGold else Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = if (isSuperAdmin) Icons.Default.AdminPanelSettings else if (currentUser != null) Icons.Default.Person else Icons.Default.AccountCircle,
                            contentDescription = "حساب کاربری",
                            tint = if (currentUser != null) DeepNavy else HeritageGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Support Quick Dial
                    IconButton(
                        onClick = onSupportClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "پشتیبانی",
                            tint = HeritageGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Middle: Vendor / Admin Shortcut Chip
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isSuperAdmin) {
                        FilterChip(
                            selected = true,
                            onClick = onAdminClick,
                            label = { Text("پنل ادمین", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HeritageGold,
                                selectedLabelColor = DeepNavy
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else {
                        FilterChip(
                            selected = isVendor,
                            onClick = onVendorTabClick,
                            label = { Text(if (isVendor) "پنل فروشنده" else "عضویت فروشندگان", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = if (isVendor) DeepNavy else HeritageGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HeritageGold,
                                selectedLabelColor = DeepNavy,
                                containerColor = Color.White.copy(alpha = 0.12f),
                                labelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Brand Logo & Title (Golarys / گل آریس)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "گل آریس",
                            color = HeritageGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Golarys.ir",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 9.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, HeritageGold, CircleShape)
                            .background(DeepNavy)
                    ) {
                        Image(
                            painter = painterResource(id = logoResId),
                            contentDescription = "لوگوی گل آریس",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar Input Field
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(22.dp)),
                placeholder = {
                    Text(
                        text = "جستجوی گل، گیاه، دسته‌گل، گلدان، سانسوریا...",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = HeritageGold,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "پاک کردن",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
    }
}
