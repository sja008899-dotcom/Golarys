package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String, // "CUSTOMER", "VENDOR", "SUPER_ADMIN"
    val isVerified: Boolean = true,
    val verificationCode: String? = null,
    val address: String = "تهران، خیابان فرشته، پلاک ۴۲",
    val walletBalanceToman: Long = 500000L,
    val registeredDateShamsi: String = "۱۴۰۳/۰۵/۰۱"
)

@Entity(tableName = "vendor_applications")
data class VendorApplicationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val shopName: String,
    val managerName: String,
    val nationalId: String,
    val iban: String, // شماره شبا
    val city: String,
    val address: String,
    val phone: String,
    val licenseNumber: String,
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val registrationDateShamsi: String
)

@Entity(tableName = "products")
data class FlowerProductEntity(
    @PrimaryKey val id: String,
    val titleFa: String,
    val titleEn: String,
    val category: String, // دسته‌گل, گل‌های آپارتمانی, گلدان, هدیه, تاج گل, رز جاودان
    val priceToman: Long,
    val originalPriceToman: Long = 0,
    val rating: Float = 4.8f,
    val reviewsCount: Int = 24,
    val description: String,
    val lightReq: String, // e.g., "نور غیرمستقیم زیاد"
    val waterReq: String, // e.g., "هفته‌ای یک‌بار"
    val tempReq: String,  // e.g., "۱۸ تا ۲۵ درجه"
    val drawableResName: String = "img_hero_bouquet_1786618726074",
    val isFeatured: Boolean = false,
    val isWishlisted: Boolean = false,
    val vendorId: String = "VEN-01",
    val vendorName: String = "گل‌فروشی آریس مرکزی",
    val isApprovedByAdmin: Boolean = true,
    val stockQuantity: Int = 15
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val titleFa: String,
    val priceToman: Long,
    val quantity: Int,
    val drawableResName: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userId: String = "USER-01",
    val customerName: String = "کاربر گل آریس",
    val customerPhone: String = "۰۹۱۲۳۴۵۶۷۸۹",
    val orderDateShamsi: String,
    val deliveryTimeSlot: String,
    val address: String,
    val totalAmountToman: Long,
    val statusFa: String, // در انتظار تایید, در حال آماده‌سازی, تایید عکس پیش از ارسال, در حال ارسال, تحویل شده
    val trackingCode: String,
    val vendorPhotoProofUrl: String? = null,
    val vendorName: String = "گل‌فروشی آریس مرکزی",
    val itemsSummary: String,
    val paymentMethod: String = "درگاه بانکی"
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "USER-01",
    val title: String, // e.g., "شارژ کیف پول", "پرداخت سفارش #GL-8912", "تسویه حساب فروشنده"
    val amountToman: Long,
    val isDeposit: Boolean, // true = deposit/credit, false = withdrawal/debit
    val dateShamsi: String,
    val status: String // "موفق", "در انتظار"
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userFullName: String,
    val userEmailOrPhone: String,
    val subject: String,
    val department: String, // "پیگیری سفارش", "مشاوره با گیاه‌پزشک", "انتقادات و امور مالی", "پشتیبانی فنی"
    val message: String,
    val adminReply: String? = null,
    val status: String = "OPEN", // "OPEN", "ANSWERED", "CLOSED"
    val dateShamsi: String
)

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val titleFa: String,
    val category: String,
    val author: String,
    val readTimeMinutes: Int,
    val summary: String,
    val content: String,
    val drawableResName: String = "img_hero_bouquet_1786618726074",
    val dateShamsi: String
)
