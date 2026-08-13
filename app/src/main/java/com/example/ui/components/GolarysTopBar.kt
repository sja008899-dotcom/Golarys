package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GolarysTopBar(
    cartCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCartClick: () -> Unit,
    onVendorTabClick: () -> Unit,
    isVendorMode: Boolean,
    onAccountClick: () -> Unit = {},
    isLoggedIn: Boolean = false
) {
    val context = LocalContext.current
    val logoResId = remember(context) {
        val id = context.resources.getIdentifier("img_app_logo_1786618714248", "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_foreground
    }

    var showSearchBar by remember { mutableStateOf(false) }

    Surface(
        color = BotanicalGreenDark,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "سبد خرید",
                                tint = HeritageGold
                            )
                        }
                    }

                    // Account / Login Button
                    IconButton(
                        onClick = onAccountClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isLoggedIn) HeritageGold else Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = if (isLoggedIn) Icons.Default.VerifiedUser else Icons.Default.AccountCircle,
                            contentDescription = "حساب کاربری",
                            tint = if (isLoggedIn) DeepNavy else HeritageGold
                        )
                    }
                }

                // Vendor / Customer Mode Switcher
                FilterChip(
                    selected = isVendorMode,
                    onClick = { onVendorTabClick() },
                    label = {
                        Text(
                            text = if (isVendorMode) "پنل فروشنده" else "فروشگاه آنلاین",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isVendorMode) Icons.Default.Storefront else Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = HeritageGold,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HeritageGold,
                        selectedLabelColor = DeepNavy,
                        containerColor = Color.White.copy(alpha = 0.12f),
                        labelColor = Color.White
                    ),
                    border = BorderStroke(1.dp, HeritageGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp)
                )

                // Brand Logo & Title (Golarys / گل آریس)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Text(
                            text = "گل آریس",
                            color = HeritageGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Golarys.ir",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar Input Field
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = {
                    Text(
                        text = "جستجوی گل، گیاه، دسته‌گل، گلدان...",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = HeritageGold
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "پاک کردن",
                                tint = Color.White
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
