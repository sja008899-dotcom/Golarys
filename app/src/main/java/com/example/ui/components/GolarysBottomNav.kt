package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavItem(
    val index: Int,
    val labelFa: String,
    val icon: ImageVector
)

@Composable
fun GolarysBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    cartBadgeCount: Int = 0,
    isSuperAdmin: Boolean = false
) {
    val items = mutableListOf(
        NavItem(0, "فروشگاه", Icons.Default.LocalFlorist),
        NavItem(1, "سبد خرید", Icons.Default.ShoppingCart),
        NavItem(2, "سفارشات", Icons.Default.LocalShipping),
        NavItem(3, "کیف پول", Icons.Default.AccountBalanceWallet),
        NavItem(4, "فروشندگان", Icons.Default.Storefront),
        NavItem(5, "پشتیبانی", Icons.Default.HeadsetMic),
        NavItem(6, "مجله", Icons.Default.AutoStories)
    )

    if (isSuperAdmin) {
        items.add(NavItem(7, "ادمین کل", Icons.Default.AdminPanelSettings))
    }

    Surface(
        color = DeepNavy,
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display RTL reversed order
            items.reversed().forEach { item ->
                val isSelected = selectedTab == item.index
                val animatedBgColor by animateColorAsState(
                    if (isSelected) HeritageGold else Color.Transparent,
                    label = "bg"
                )
                val animatedIconColor by animateColorAsState(
                    if (isSelected) DeepNavy else Color.White.copy(alpha = 0.7f),
                    label = "icon"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .background(animatedBgColor)
                        .clickable { onTabSelected(item.index) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        BadgedBox(
                            badge = {
                                if (item.index == 1 && cartBadgeCount > 0) {
                                    Badge(
                                        containerColor = BotanicalGreen,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = cartBadgeCount.toString(), fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.labelFa,
                                tint = animatedIconColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = item.labelFa,
                            color = animatedIconColor,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            maxLines = 1,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
