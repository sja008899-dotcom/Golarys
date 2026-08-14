package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.data.local.ArticleEntity
import com.example.ui.theme.*

@Composable
fun JournalScreen(
    articles: List<ArticleEntity>
) {
    val context = LocalContext.current
    var selectedArticle by remember { mutableStateOf<ArticleEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(16.dp)
    ) {
        Text(
            text = "📖 مجله و دانشنامه گیاهی گل آریس",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = BotanicalGreenDark
        )
        Text(
            text = "آموزش تخصصی نگهداری گل‌های آپارتمانی، بیماری‌های گیاهی و ترفندهای گل‌آرایی",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(articles) { article ->
                val imageResId = remember(article.drawableResName, context) {
                    val id = context.resources.getIdentifier(article.drawableResName, "drawable", context.packageName)
                    if (id != 0) id else R.drawable.ic_launcher_background
                }
                val isExpanded = selectedArticle?.id == article.id

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = BotanicalGreen.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = article.category,
                                        fontSize = 10.sp,
                                        color = BotanicalGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = article.titleFa,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "✍️ ${article.author} • ⏱️ ${article.readTimeMinutes} دقیقه",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = article.summary,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Divider(color = CardBorder)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = article.content,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                selectedArticle = if (isExpanded) null else article
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = if (isExpanded) "بستن متن کامل" else "مطالعه مقاله کامل ⇦",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BotanicalGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
