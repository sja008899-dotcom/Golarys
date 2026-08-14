package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.api.GeminiPlantDoctorService
import com.example.data.api.PlantDiagnosisResult
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel
import kotlinx.coroutines.launch

data class PlantConditionPreset(
    val id: String,
    val plantName: String,
    val title: String,
    val symptoms: String,
    val drawableResName: String,
    val iconEmoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDoctorScreen(
    viewModel: GolarysViewModel,
    onNavigateToSupport: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val plantDoctorService = remember { GeminiPlantDoctorService() }

    var plantNameInput by remember { mutableStateOf("سانسوریا ابلق") }
    var symptomInput by remember { mutableStateOf("نوک و حاشیه برگ‌ها زرد و شل شده و لکه‌های آبکی قهوه‌ای در حال گسترش است.") }
    var selectedPresetId by remember { mutableStateOf("PRESET-1") }

    var isAnalyzing by remember { mutableStateOf(false) }
    var diagnosisResult by remember { mutableStateOf<PlantDiagnosisResult?>(null) }

    val presets = remember {
        listOf(
            PlantConditionPreset(
                id = "PRESET-1",
                plantName = "سانسوریا ابلق",
                title = "زردی و لکه‌های قهوه‌ای",
                symptoms = "نوک و حاشیه برگ‌ها زرد و شل شده و لکه‌های آبکی قهوه‌ای در حال گسترش است.",
                drawableResName = "img_hero_bouquet_1786618726074",
                iconEmoji = "🪴"
            ),
            PlantConditionPreset(
                id = "PRESET-2",
                plantName = "فیکوس لیراتا",
                title = "سوختگی حاشیه و لکه‌برگی",
                symptoms = "برگ‌های پایینی لکه‌های قهوه‌ای تیره و سوختگی پیدا کرده و حاشیه‌ها خشک شده است.",
                drawableResName = "img_hero_bouquet_1786618726074",
                iconEmoji = "🌿"
            ),
            PlantConditionPreset(
                id = "PRESET-3",
                plantName = "نخل اریکا",
                title = "تارهای نازک و کنه آفت",
                symptoms = "پشت برگ‌ها تارهای عنکبوتی بسیار ریز تشکیل شده و برگ‌ها زرد و مات شده‌اند.",
                drawableResName = "img_hero_bouquet_1786618726074",
                iconEmoji = "🕷️"
            ),
            PlantConditionPreset(
                id = "PRESET-4",
                plantName = "زامیفولیا بلک",
                title = "ساقه نرم و پوسیدگی ریزوم",
                symptoms = "ساقه‌ها از پایین زرد و چروکیده شده و هنگام لمس بافت نرم و اسفنجی دارند.",
                drawableResName = "img_app_logo_1786618714248",
                iconEmoji = "🌱"
            ),
            PlantConditionPreset(
                id = "PRESET-5",
                plantName = "فیکوس بنجامین",
                title = "برگ‌ریزی ناگهانی",
                symptoms = "برگ‌های سبز بدون تغییر رنگ سریعاً می‌ریزند و شاخه‌ها لخت شده‌اند.",
                drawableResName = "img_hero_bouquet_1786618726074",
                iconEmoji = "🍃"
            ),
            PlantConditionPreset(
                id = "PRESET-6",
                plantName = "ارکیده فالانوپسیس",
                title = "پوسیدگی ریشه و ریزش غنچه",
                symptoms = "ریشه‌های خاکستری قهوه‌ای و نرم شده و غنچه‌ها قبل از باز شدن خشک شده و می‌افتند.",
                drawableResName = "img_hero_bouquet_1786618726074",
                iconEmoji = "🌸"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero AI Doctor Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BotanicalGreenDark, DeepNavy, BotanicalGreen)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(HeritageGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = DeepNavy,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🩺 کلینیک هوشمند گیاه‌پزشک گل آریس",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Powered by Google Gemini AI Botanical Engine",
                                        fontSize = 10.sp,
                                        color = HeritageGoldLight
                                    )
                                }
                            }

                            Surface(
                                color = HeritageGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "هوش مصنوعی",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HeritageGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "با انتخاب گیاه و علائم ظاهری، بیماری یا آفت را در چند ثانیه شناسایی کنید و نسخه دقیق درمانی و تقویم کودی متناسب را دریافت نمایید.",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Preset Conditions Selector
        item {
            Column {
                Text(
                    text = "🌱 انتخاب سریع از بین عارضه‌های رایج گیاهان:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BotanicalGreenDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets) { preset ->
                        val isSelected = selectedPresetId == preset.id
                        Card(
                            modifier = Modifier
                                .width(170.dp)
                                .clickable {
                                    selectedPresetId = preset.id
                                    plantNameInput = preset.plantName
                                    symptomInput = preset.symptoms
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BotanicalGreen.copy(alpha = 0.12f) else Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) BotanicalGreen else CardBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = preset.iconEmoji,
                                        fontSize = 18.sp
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BotanicalGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.plantName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = preset.title,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Form (Plant Name & Symptoms)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📝 مشخصات و علائم ظاهری گیاه",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = plantNameInput,
                        onValueChange = { plantNameInput = it },
                        label = { Text("نام گیاه یا گل (مثلاً زامیفولیا، سانسوریا، پوتوس)", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = BotanicalGreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_plant_name"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = symptomInput,
                        onValueChange = { symptomInput = it },
                        label = { Text("شرح علائم و تغییرات ظاهری (رنگ برگ، رطوبت خاک، لکه‌ها)", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = BotanicalGreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("input_plant_symptoms"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isAnalyzing = true
                                diagnosisResult = null
                                try {
                                    val result = plantDoctorService.diagnosePlant(
                                        plantType = plantNameInput,
                                        symptoms = symptomInput
                                    )
                                    diagnosisResult = result
                                    viewModel.showToast("تشخیص هوشمند با موفقیت انجام شد!")
                                } catch (e: Exception) {
                                    viewModel.showToast("خطا در پردازش هوش مصنوعی.")
                                } finally {
                                    isAnalyzing = false
                                }
                            }
                        },
                        enabled = !isAnalyzing && plantNameInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_run_plant_diagnosis")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("در حال آنالیز پاتولوژی و بیماری‌شناسی با Gemini...", fontSize = 12.sp, color = Color.White)
                        } else {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("اسکن و تشخیص بیماری با هوش مصنوعی", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Diagnosis Results Card
        diagnosisResult?.let { result ->
            item {
                DiagnosisResultCard(
                    result = result,
                    onSendToSupport = {
                        val subject = "مشاوره درباره بیماری: ${result.diseaseName}"
                        val message = "گیاه: ${result.plantName}\nعلائم: $symptomInput\nتشخیص هوش مصنوعی: ${result.diseaseName}\nشدت: ${result.severity}"
                        onNavigateToSupport(subject, message)
                        viewModel.createSupportTicket(
                            subject = subject,
                            department = "مشاوره با گیاه‌پزشک",
                            message = message
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DiagnosisResultCard(
    result: PlantDiagnosisResult,
    onSendToSupport: () -> Unit
) {
    val severityColor = when (result.severity) {
        "شدید و فوری" -> ErrorRed
        "متوسط" -> HeritageGold
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_diagnosis_result"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, BotanicalGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = BotanicalGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نتیجه پرونده گیاه‌پزشکی",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = BotanicalGreenDark
                    )
                }

                Surface(
                    color = BotanicalGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = result.confidence,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BotanicalGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Identified Disease Title
            Text(
                text = "🔍 بیماری تشخیص داده شده:",
                fontSize = 11.5.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = result.diseaseName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "سطح خطر و فوریت:",
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = severityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = result.severity,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Root Causes
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "🧪 علل و ریشه‌های بروز عارضه:",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            result.causes.forEach { cause ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "•", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = cause, fontSize = 11.5.sp, color = TextPrimary, lineHeight = 17.sp)
                }
            }

            // Treatment Protocol
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "💊 پروتکل درمان فوری و گام‌به‌گام:",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = BotanicalGreenDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            result.treatmentSteps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = BotanicalGreen,
                        shape = CircleShape,
                        modifier = Modifier.size(18.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "${index + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = step, fontSize = 11.5.sp, color = TextPrimary, lineHeight = 17.sp)
                }
            }

            // Recommended Fertilizers & Chemical Dosages
            if (result.recommendedFertilizers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = BotanicalGreen.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vaccines, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "نسخه کودی و قارچ‌کش‌های پیشنهادی:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BotanicalGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        result.recommendedFertilizers.forEach { item ->
                            Text(
                                text = "✓ $item",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Preventative Tips
            if (result.preventativeTips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "🛡️ نکات پیشگیری و مراقبت دوره‌ای:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.preventativeTips.forEach { tip ->
                    Text(
                        text = "🔹 $tip",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendToSupport,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BotanicalGreen),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_send_diagnosis_to_doctor")
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ارسال به گیاه‌پزشک پشتیبان", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
