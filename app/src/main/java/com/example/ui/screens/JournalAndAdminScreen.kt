package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun JournalAndAdminScreen() {
    var selectedSection by remember { mutableStateOf(0) } // 0: Journal Articles, 1: SEO & Metadata Admin

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(16.dp)
    ) {
        // Tab Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = (selectedSection == 0),
                onClick = { selectedSection = 0 },
                label = { Text("📖 مجله گل آریس (راهنمای گیاهان)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BotanicalGreen,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = (selectedSection == 1),
                onClick = { selectedSection = 1 },
                label = { Text("⚡ تنظیمات سئو و متادیتا", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = HeritageGold,
                    selectedLabelColor = DeepNavy
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSection == 0) {
            // Botanical Journal
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "مقالات تخصصی نگهداری و پرورش گل و گیاه",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🪴 ۵ راز شادابی سانسوریا در آپارتمان",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BotanicalGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "سانسوریا از مقاوم‌ترین گیاهان آپارتمانی است، اما آبیاری بیش از حد باعث پوسیدگی ریشه آن می‌شود. در این مقاله روش صحیح سنجش رطوبت خاک و تغذیه با کود پوتاسیم را بررسی می‌کنیم...",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "نویسنده: تیم کارشناسان گل آریس • زمان مطالعه: ۴ دقیقه",
                                fontSize = 10.sp,
                                color = HeritageGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🌸 نگهداری ارکیده فالانوپسیس بعد از اتمام گل‌دهی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BotanicalGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "پس از ریزش گل‌های ارکیده، ساقه گل‌دهنده را از بند دوم هرس کنید تا شرایط برای رشد شاخه جانبی جدید فراهم شود. استفاده از غبارپاشی معتدل با آب بدون املاح ضروری است...",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "نویسنده: کلینیک گیاه‌پزشکی گل آریس • زمان مطالعه: ۵ دقیقه",
                                fontSize = 10.sp,
                                color = HeritageGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // SEO & Super Admin Metadata Inspector
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "تنظیمات فنی سئو و متادیتای وب‌اپلیکیشن (golarys.ir)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepNavy),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🌐 پیکربندی SEO و Schema Markup",
                                color = HeritageGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = """
                                    • Title Tag: گل آریس | فروشگاه آنلاین گل و گیاه
                                    • Domain: https://golarys.ir
                                    • Open Graph: og:image -> img_app_logo.jpg
                                    • Status Sitemap: /sitemap.xml (فعال)
                                    • Status Robots: /robots.txt (مجاز برای Googlebot)
                                    • API Base Endpoint: https://golarys.ir/api/v1
                                """.trimIndent(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📋 کد نمونه JSON-LD Schema (محصولات گل آریس)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BotanicalGreen
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = SurfaceVariantLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = """
                                        {
                                          "@context": "https://schema.org/",
                                          "@type": "Product",
                                          "name": "دسته‌گل رز و زنبق طلایی آریس",
                                          "image": "https://golarys.ir/images/golden_iris.jpg",
                                          "description": "دسته‌گل رز و زنبق با کاور لوکس",
                                          "brand": { "@type": "Brand", "name": "Golarys" },
                                          "offers": {
                                            "@type": "Offer",
                                            "priceCurrency": "IRR",
                                            "price": "14500000",
                                            "availability": "https://schema.org/InStock"
                                          }
                                        }
                                    """.trimIndent(),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(10.dp),
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
