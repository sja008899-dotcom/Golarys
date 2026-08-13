package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class FlowerProductEntity(
    @PrimaryKey val id: String,
    val titleFa: String,
    val titleEn: String,
    val category: String, // دسته‌گل, گل‌های آپارتمانی, گلدان, هدیه
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
    val vendorName: String = "گل‌فروشی آریس مرکزی"
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
    val orderDateShamsi: String,
    val deliveryTimeSlot: String,
    val address: String,
    val totalAmountToman: Long,
    val statusFa: String, // در انتظار تایید, در حال آماده‌سازی, تایید عکس پیش از ارسال, در حال ارسال, تحویل شده
    val trackingCode: String,
    val vendorPhotoProofUrl: String? = null,
    val vendorName: String = "گل‌فروشی آریس مرکزی",
    val itemsSummary: String
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // e.g., "شارژ کیف پول", "پرداخت سفارش #GL-8912"
    val amountToman: Long,
    val isDeposit: Boolean, // true = deposit, false = withdrawal/payment
    val dateShamsi: String,
    val status: String // "موفق", "در انتظار"
)
