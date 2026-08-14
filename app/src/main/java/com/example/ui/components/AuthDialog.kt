package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun AuthDialog(
    viewModel: GolarysViewModel,
    onDismiss: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    val pendingOtpEmail by viewModel.pendingVerificationEmail.collectAsState()
    val generatedOtpCode by viewModel.generatedOtpCode.collectAsState()
    val otpNoticeMessage by viewModel.otpNoticeMessage.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // "CUSTOMER" or "VENDOR"
    var otpInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo / Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BotanicalGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (pendingOtpEmail != null) Icons.Default.MarkEmailRead else Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = BotanicalGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (pendingOtpEmail != null) "تأیید ایمیل و کد فعال‌سازی"
                    else if (isRegisterMode) "ثبت‌نام در گل آریس"
                    else "ورود به حساب کاربری",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = BotanicalGreenDark
                )

                Text(
                    text = if (pendingOtpEmail != null) "کد ۵ رقمی ارسالی به ایمیل خود را وارد نمایید"
                    else if (isRegisterMode) "لطفاً اطلاعات خود را جهت ایجاد حساب وارد کنید"
                    else "دسترسی به سفارشات، کیف پول و پنل‌های اختصاصی",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                // OTP Verification Step
                if (pendingOtpEmail != null) {
                    // Visual Email Delivery Simulation Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BotanicalGreen.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MailOutline, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "پیامک / ایمیل ارسالی از Golarys Auth",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BotanicalGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "کد تأیید شما: ${generatedOtpCode ?: "12345"}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { if (it.length <= 5) otpInput = it },
                        label = { Text("کد تأیید ۵ رقمی", fontSize = 12.sp) },
                        placeholder = { Text(generatedOtpCode ?: "12345", fontSize = 12.sp) },
                        trailingIcon = {
                            TextButton(onClick = { otpInput = generatedOtpCode ?: "12345" }) {
                                Text("درج خودکار", fontSize = 11.sp, color = BotanicalGreen, fontWeight = FontWeight.Bold)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (viewModel.verifyOtpCode(otpInput)) {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "تأیید و ورود به برنامه", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Registration / Login Inputs
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("نام و نام خانوادگی", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BotanicalGreen) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Role Selector Chip
                        Text(
                            text = "نوع کاربری:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedRole == "CUSTOMER",
                                onClick = { selectedRole = "CUSTOMER" },
                                label = { Text("مشتری عادی", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = selectedRole == "VENDOR",
                                onClick = { selectedRole = "VENDOR" },
                                label = { Text("فروشنده گل و گیاه", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("ایمیل یا شماره همراه", fontSize = 12.sp) },
                        placeholder = { Text(if (isRegisterMode) "مثال: info@golarys.ir" else "ایمیل یا شماره موبایل", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BotanicalGreen) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("رمز عبور", fontSize = 12.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BotanicalGreen) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                if (emailOrPhone.isNotBlank() && password.isNotBlank()) {
                                    viewModel.startRegistration(
                                        fullName = fullName,
                                        email = emailOrPhone,
                                        phone = emailOrPhone,
                                        password = password,
                                        role = selectedRole
                                    )
                                } else {
                                    viewModel.showToast("لطفاً ایمیل و رمز عبور را وارد نمایید.")
                                }
                            } else {
                                if (emailOrPhone.isNotBlank()) {
                                    viewModel.login(emailOrPhone, password)
                                    onDismiss()
                                } else {
                                    viewModel.showToast("لطفاً اطلاعات ورود را وارد فرمایید.")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isRegisterMode) "ارسال کد فعال‌سازی ۵ رقمی" else "ورود به حساب",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                        Text(
                            text = if (isRegisterMode) "قبلاً حساب کاربری داشته‌اید؟ ورود" else "حساب کاربری ندارید؟ ثبت‌نام رایگان",
                            fontSize = 12.sp,
                            color = BotanicalGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorder)

                // Quick Switcher for Instant Testing of all Roles
                Text(
                    text = "🚀 ورود سریع برای تست کامل امکانات پلتفرم:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.quickSwitchRole("SUPER_ADMIN")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, HeritageGold)
                    ) {
                        Text("ادمین کل", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.quickSwitchRole("VENDOR")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BotanicalGreen)
                    ) {
                        Text("فروشنده", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BotanicalGreen)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.quickSwitchRole("CUSTOMER")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("مشتری", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}
