package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun ProfileDialog(
    user: UserEntity?,
    viewModel: GolarysViewModel,
    onDismiss: () -> Unit,
    onOpenAuth: () -> Unit
) {
    if (user == null) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = BotanicalGreen,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "شما وارد حساب کاربری نشده‌اید", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenAuth()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "ورود یا ثبت‌نام در گل آریس", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    var fullName by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phone) }
    var address by remember { mutableStateOf(user.address) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BotanicalGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (user.role) {
                                "SUPER_ADMIN" -> Icons.Default.AdminPanelSettings
                                "VENDOR" -> Icons.Default.Storefront
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = BotanicalGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                item {
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = BotanicalGreenDark
                    )
                    Text(
                        text = user.email,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = when (user.role) {
                            "SUPER_ADMIN" -> HeritageGold
                            "VENDOR" -> BotanicalGreen
                            else -> DeepNavy
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = when (user.role) {
                                "SUPER_ADMIN" -> "مدیریت ارشد پلتفرم"
                                "VENDOR" -> "فروشنده تایید شده"
                                else -> "مشتری ویژه گل آریس"
                            },
                            color = if (user.role == "SUPER_ADMIN") DeepNavy else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = CardBorder)
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("نام و نام خانوادگی", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("شماره همراه", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("آدرس پیش‌فرض تحویل سفارشات", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }

                item {
                    Button(
                        onClick = {
                            viewModel.updateProfile(fullName, phone, address)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "ذخیره تغییرات حساب", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = CardBorder)
                    Text(
                        text = "تغییر سریع سطح دسترسی (برای تست):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.quickSwitchRole("SUPER_ADMIN")
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("ادمین", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.quickSwitchRole("VENDOR")
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("فروشنده", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.quickSwitchRole("CUSTOMER")
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("مشتری", fontSize = 10.sp)
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = BorderStroke(1.dp, ErrorRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "خروج از حساب کاربری", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
