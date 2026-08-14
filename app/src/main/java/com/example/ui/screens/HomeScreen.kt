package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FlowerProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    products: List<FlowerProductEntity>,
    selectedCategory: String,
    searchQuery: String,
    onCategorySelect: (String) -> Unit,
    onProductClick: (FlowerProductEntity) -> Unit,
    onAddToCartClick: (FlowerProductEntity) -> Unit,
    onWishlistClick: (FlowerProductEntity) -> Unit,
    onPlantDoctorClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val heroResId = remember(context) {
        val id = context.resources.getIdentifier("img_hero_bouquet_1786618726074", "drawable", context.packageName)
        if (id != 0) id else R.drawable.ic_launcher_background
    }

    val categories = listOf("همه", "دسته‌گل", "گل‌های آپارتمانی", "هدیه", "گلدان")

    val filteredProducts = remember(products, selectedCategory, searchQuery) {
        products.filter { p ->
            val matchCategory = selectedCategory == "همه" || p.category == selectedCategory
            val matchQuery = searchQuery.isEmpty() ||
                    p.titleFa.contains(searchQuery, ignoreCase = true) ||
                    p.titleEn.contains(searchQuery, ignoreCase = true) ||
                    p.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Hero Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = heroResId),
                    contentDescription = "جشنواره گل آریس",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DeepNavy.copy(alpha = 0.88f),
                                    BotanicalGreen.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = HeritageGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "جشنواره گل آریس 🌺",
                            color = HeritageGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "۱۵٪ تخفیف ویژه سفارش اول: GOLARYS1403",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ارسال سریع ۲ ساعته در تهران با ضمانت طراوت",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        // Quick AI Doctor Shortcut Card
        Surface(
            onClick = onPlantDoctorClick,
            color = DeepNavy,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("home_plant_doctor_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(HeritageGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = DeepNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🩺 اسکنر هوشمند تشخیص بیماری گیاه (AI)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "تشخیص فوری علت زردی، آفات و دریافت نسخه درمانی",
                            fontSize = 9.5.sp,
                            color = HeritageGoldLight
                        )
                    }
                }

                Surface(
                    color = BotanicalGreen,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "اسکن رایگان",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(cat) },
                    label = {
                        Text(
                            text = cat,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BotanicalGreen,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceVariantLight,
                        labelColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Products Grid
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🌱",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "هیچ گلی با این مشخصات یافت نشد!",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "لطفاً عبارت دیگری را جستجو کنید یا دسته‌بندی را تغییر دهید.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { item ->
                    ProductCard(
                        product = item,
                        onProductClick = onProductClick,
                        onAddToCartClick = onAddToCartClick,
                        onWishlistClick = onWishlistClick
                    )
                }
            }
        }
    }
}
