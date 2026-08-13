package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WalletTransactionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

@Composable
fun WalletScreen(
    balanceToman: Long,
    transactions: List<WalletTransactionEntity>,
    onRecharge: (Long) -> Unit
) {
    var customAmountText by remember { mutableStateOf("") }
    val quickAmounts = listOf(100000L, 250000L, 500000L, 1000000L)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(16.dp)
    ) {
        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DeepNavy, BotanicalGreenDark)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "کیف پول گل آریس (Golarys Wallet)",
                            color = HeritageGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = HeritageGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "موجودی فعلی:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = GolarysViewModel.formatPrice(balanceToman),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تومان",
                            color = HeritageGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Recharge Section
        Text(
            text = "⚡ شارژ سریع آنلاین کیف پول",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickAmounts.forEach { amount ->
                OutlinedButton(
                    onClick = { onRecharge(amount) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BotanicalGreen
                    )
                ) {
                    Text(
                        text = "${amount / 1000} هزار",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Amount Recharge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customAmountText,
                onValueChange = { customAmountText = it },
                placeholder = { Text("مبلغ دلخواه به تومان...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val amt = customAmountText.toLongOrNull() ?: 0L
                    if (amt > 0) {
                        onRecharge(amt)
                        customAmountText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "شارژ درگاه", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction History
        Text(
            text = "📜 تاریخچه تراکنش‌ها",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (tx.isDeposit) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.isDeposit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (tx.isDeposit) SuccessGreen else ErrorRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = tx.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = tx.dateShamsi,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (tx.isDeposit) "+" else "-"} ${GolarysViewModel.formatPrice(tx.amountToman)} تومان",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = if (tx.isDeposit) SuccessGreen else ErrorRed
                            )
                            Text(
                                text = tx.status,
                                fontSize = 10.sp,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
