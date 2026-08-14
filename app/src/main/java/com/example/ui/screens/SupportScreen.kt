package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SupportTicketEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GolarysViewModel

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    currentUser: UserEntity?,
    tickets: List<SupportTicketEntity>,
    viewModel: GolarysViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("💬 چت زنده با گیاه‌پزشک", "🎫 تیکت‌های پشتیبانی", "📞 تماس مستقیم", "❓ سوالات متداول")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Support Top Header
        Surface(
            color = BotanicalGreenDark,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مرکز پشتیبانی و کلینیک گل آریس",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "مشاوره رایگان گیاه‌پزشکی، پیگیری ۲۴ ساعته سفارشات و پاسخگویی آنلاین",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BotanicalGreenDark,
                    contentColor = HeritageGold,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) HeritageGold else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Contents
        when (selectedTab) {
            0 -> LiveChatTab()
            1 -> TicketsTab(tickets, viewModel)
            2 -> DirectContactTab(context)
            3 -> FaqTab()
        }
    }
}

@Composable
fun LiveChatTab() {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    isUser = false,
                    text = "درود بر شما! من دستیار هوشمند و مشاور گیاه‌پزشکی گل آریس هستم. چطور می‌توانم در انتخاب یا نگهداری گل‌ها به شما کمک کنم؟",
                    time = "هم‌اکنون"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "چرا برگ‌های سانسوریا زرد می‌شوند؟",
        "ضمانت طراوت گل‌ها تا چند ساعت است؟",
        "چگونه سفارش خود را پیگیری کنم؟",
        "کد تخفیف خرید اول چیست؟"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Quick suggestions
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "💡 سوالات پرکاربرد جهت دریافت پاسخ آنی:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickQuestions.take(2).forEach { q ->
                        SuggestionChip(
                            onClick = {
                                val userMsg = ChatMessage(true, q, "هم‌اکنون")
                                val reply = when {
                                    q.contains("سانسوریا") -> "زرد شدن برگ سانسوریا اغلب به دلیل آبیاری بیش از حد یا سنگین بودن بافت خاک است. اجازه دهید خاک کاملاً خشک شود و گیاه را در محیطی با نور فیلترشده قرار دهید."
                                    q.contains("ضمانت") -> "کلیه دسته‌گل‌ها و گیاهان گل آریس دارای ضمانت ۱۰۰٪ شادابی تا ۴۸ ساعت پس از تحویل می‌باشند. همچنین قبل از ارسال، عکس سفارش برای شما ارسال خواهد شد."
                                    q.contains("پیگیری") -> "برای پیگیری سفارش می‌توانید به تب «سفارشات» در منوی پایین مراجعه فرمایید و وضعیت زنده و عکس محصول را مشاهده نمایید."
                                    else -> "کد تخفیف اختصاصی سفارش اول شما: GOLARYS1403 (۱۵٪ تخفیف بدون محدودیت)"
                                }
                                val botMsg = ChatMessage(false, reply, "هم‌اکنون")
                                messages = messages + userMsg + botMsg
                            },
                            label = { Text(q, fontSize = 10.sp) }
                        )
                    }
                }
            }

            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (msg.isUser) 14.dp else 2.dp,
                            bottomEnd = if (msg.isUser) 2.dp else 14.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.isUser) BotanicalGreen else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 12.sp,
                                color = if (msg.isUser) Color.White else TextPrimary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.time,
                                fontSize = 9.sp,
                                color = if (msg.isUser) Color.White.copy(alpha = 0.7f) else TextSecondary,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Chat Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("سوال یا پیام خود را بنویسید...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val userMsg = ChatMessage(true, inputText, "هم‌اکنون")
                        val botMsg = ChatMessage(
                            false,
                            "پیام شما دریافت گردید. کارشناس گیاه‌پزشک گل آریس در حال بررسی است و به زودی با شما تبادل نظر خواهد کرد.",
                            "هم‌اکنون"
                        )
                        messages = messages + userMsg + botMsg
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BotanicalGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "ارسال",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun TicketsTab(
    tickets: List<SupportTicketEntity>,
    viewModel: GolarysViewModel
) {
    var subject by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("پیگیری سفارش") }
    var message by remember { mutableStateOf("") }
    val departments = listOf("پیگیری سفارش", "مشاوره با گیاه‌پزشک", "انتقادات و امور مالی", "پشتیبانی فنی")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Create New Ticket Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎫 ثبت تیکت پشتیبانی جدید",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BotanicalGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("موضوع تیکت", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "دپارتمان مربوطه:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        departments.take(2).forEach { dept ->
                            FilterChip(
                                selected = department == dept,
                                onClick = { department = dept },
                                label = { Text(dept, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("شرح کامل درخواست", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (subject.isNotBlank() && message.isNotBlank()) {
                                viewModel.createSupportTicket(subject, department, message)
                                subject = ""
                                message = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ارسال تیکت", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // History of tickets
        item {
            Text(
                text = "📜 تاریخچه تیکت‌های شما",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }

        items(tickets) { ticket ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = ticket.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(
                            color = if (ticket.status == "ANSWERED") SuccessGreen.copy(alpha = 0.15f) else HeritageGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (ticket.status == "ANSWERED") "پاسخ داده شده" else "در انتظار بررسی",
                                color = if (ticket.status == "ANSWERED") SuccessGreen else DeepNavy,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = "دپارتمان: ${ticket.department} | تاریخ: ${ticket.dateShamsi}", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "پیام شما: ${ticket.message}", fontSize = 11.sp, color = TextPrimary)

                    if (!ticket.adminReply.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BotanicalGreen.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "🛡️ پاسخ پشتیبان گل آریس:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BotanicalGreen)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = ticket.adminReply, fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DirectContactTab(context: android.content.Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "📞 مرکز تماس و پشتیبانی ۲۴ ساعته گل آریس", color = HeritageGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "تلفن پشتیبانی: ۰۲۱-۸۸۹۹۰۰۰۰", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "شماره مستقیم گیاه‌پزشک: ۰۹۱۲۰۰۰۰۰۰۱", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:02188990000"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeritageGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = DeepNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "برقراری تماس فوری با پشتیبانی", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "🏢 نشانی دفتر مرکزی و گلخانه گل آریس", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "تهران، خیابان ولیعصر، برج آریس، طبقه ۱۲، واحد ۴", fontSize = 12.sp, color = TextSecondary)
                Text(text = "کد پستی: ۱۹۳۹۵-۴۵۶۷ | ایمیل: support@golarys.ir", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "ساعات کار حضوری: همه‌روزه حتی ایام تعطیل از ساعت ۸:۰۰ الی ۲۳:۰۰", fontSize = 11.sp, color = BotanicalGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FaqTab() {
    val faqs = listOf(
        "آیا قبل از ارسال از دسته‌گل عکس ارسال می‌شود؟" to "بله، در گل آریس تمامی سفارشات پس از آماده‌سازی توسط گل‌آرای مجرب عکاسی شده و در تب سفارشات شما قرار می‌گیرد و تا تایید نفرمایید ارسال نخواهد شد.",
        "نحوه اعمال کد تخفیف به چه صورت است؟" to "کد تخفیف GOLARYS1403 را در بخش نهایی سبد خرید وارد نموده و دکمه اعمال را بزنید تا ۱۵٪ تخفیف کسر گردد.",
        "شرایط مرجوعی و گارانتی طراوت گل چیست؟" to "در صورت هرگونه عدم رضایت از تازگی یا آسیب‌دیدگی در حین حمل پیک، گل آریس متعهد به ارسال مجدد رایگان یا بازگشت ۱۰۰٪ وجه است.",
        "آیا امکان نوشتن متن دلخواه روی کارت‌پستال وجود دارد؟" to "بله، در قسمت توضیحات سفارش آدرس، می‌توانید متن دلخواه خود را یادداشت فرمایید تا با خط خوش‌نویسی به صورت هدیه همراه گل ارسال شود."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(faqs) { (q, a) ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = q, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = BotanicalGreen
                        )
                    }
                    if (expanded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = a, fontSize = 11.sp, color = TextSecondary, lineHeight = 17.sp)
                    }
                }
            }
        }
    }
}
