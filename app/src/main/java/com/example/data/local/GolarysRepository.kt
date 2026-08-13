package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GolarysRepository(private val db: AppDatabase) {

    val allProducts: Flow<List<FlowerProductEntity>> = db.productDao().getAllProducts()
    val wishlistedProducts: Flow<List<FlowerProductEntity>> = db.productDao().getWishlistedProducts()
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getCartItems()
    val orders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    val walletTransactions: Flow<List<WalletTransactionEntity>> = db.walletDao().getTransactions()

    suspend fun initDefaultDataIfNeeded() {
        val existingProducts = db.productDao().getAllProducts().first()
        if (existingProducts.isEmpty()) {
            val sampleProducts = listOf(
                FlowerProductEntity(
                    id = "PROD-101",
                    titleFa = "دسته‌گل رز و زنبق طلایی آریس",
                    titleEn = "Golden Iris & Red Rose Bouquet",
                    category = "دسته‌گل",
                    priceToman = 1450000,
                    originalPriceToman = 1750000,
                    rating = 4.9f,
                    reviewsCount = 38,
                    description = "ترکیب منحصربه‌فرد زنبق زرد و رز قرمز هلندی با کاور ویژه لوکس گل آریس، مناسب هدایای خاص و تبریک.",
                    lightReq = "نور غیرمستقیم",
                    waterReq = "تعویض آب هر ۲ روز",
                    tempReq = "۱۵ تا ۲۲ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = true,
                    isWishlisted = true,
                    vendorName = "گل‌فروشی آریس مرکزی"
                ),
                FlowerProductEntity(
                    id = "PROD-102",
                    titleFa = "سانسوریا ابلق پا کوتاه (گلدان سرامیکی)",
                    titleEn = "Sansevieria Dwarf Variegated",
                    category = "گل‌های آپارتمانی",
                    priceToman = 680000,
                    originalPriceToman = 800000,
                    rating = 4.8f,
                    reviewsCount = 52,
                    description = "گیاه بسیار مقاوم پاکسازی‌کننده هوای محیط، با گلدان سرامیکی سفید طلایی و زهکشی عالی.",
                    lightReq = "مقاوم به کم‌نوری",
                    waterReq = "هر ۱۰ تا ۱۴ روز یک‌بار",
                    tempReq = "۱۶ تا ۲۸ درجه",
                    drawableResName = "img_app_logo_1786618714248",
                    isFeatured = true,
                    isWishlisted = false,
                    vendorName = "گلخانه تخصصی زنبق"
                ),
                FlowerProductEntity(
                    id = "PROD-103",
                    titleFa = "زامیفولیا کلاغی بلک (Zamioculcas Raven)",
                    titleEn = "Black Raven ZZ Plant",
                    category = "گل‌های آپارتمانی",
                    priceToman = 1890000,
                    originalPriceToman = 2100000,
                    rating = 5.0f,
                    reviewsCount = 19,
                    description = "لوکس‌ترین گونه زامیفولیا با برگ‌های مشکی براق و مقاوم برای دکوراسیون اداری و منزل.",
                    lightReq = "نور متوسط تا کم",
                    waterReq = "پس از خشک شدن کامل خاک",
                    tempReq = "۱۸ تا ۲۶ درجه",
                    drawableResName = "img_app_logo_1786618714248",
                    isFeatured = false,
                    isWishlisted = false,
                    vendorName = "گلخانه تخصصی زنبق"
                ),
                FlowerProductEntity(
                    id = "PROD-104",
                    titleFa = "باکس چرمی گل رز و شکلات بلژیکی",
                    titleEn = "Luxury Leather Rose & Chocolate Box",
                    category = "هدیه",
                    priceToman = 2350000,
                    originalPriceToman = 2600000,
                    rating = 4.9f,
                    reviewsCount = 44,
                    description = "باکس چرمی با کیفیت، رزهای هلندی تازه به همراه شکلات‌های دست‌ساز و کارت تبریک اختصاصی گل آریس.",
                    lightReq = "محیط خنک",
                    waterReq = "اسفنج مرطوب",
                    tempReq = "۱۵ تا ۲۰ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = true,
                    isWishlisted = false,
                    vendorName = "بوتیک گل آریس تجریش"
                ),
                FlowerProductEntity(
                    id = "PROD-105",
                    titleFa = "ارکیده فالانوپسیس بنفش دو شاخه",
                    titleEn = "Purple Phalaenopsis Orchid 2-Spike",
                    category = "گل‌های آپارتمانی",
                    priceToman = 2100000,
                    originalPriceToman = 0,
                    rating = 4.7f,
                    reviewsCount = 29,
                    description = "شاهکار گل‌های آپارتمانی با ماندگاری گل‌دهی بالای ۳ ماه در گلدان بلور لوکس.",
                    lightReq = "نور زیاد غیرمستقیم",
                    waterReq = "غوطه‌وری هفته‌ای یک‌بار",
                    tempReq = "۲۰ تا ۲۵ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = false,
                    isWishlisted = false,
                    vendorName = "گل‌فروشی آریس مرکزی"
                ),
                FlowerProductEntity(
                    id = "PROD-106",
                    titleFa = "دسته‌گل بابونه و آفتابگردان آفتابی",
                    titleEn = "Daisy & Sunflower Summer Bouquet",
                    category = "دسته‌گل",
                    priceToman = 890000,
                    originalPriceToman = 990000,
                    rating = 4.8f,
                    reviewsCount = 17,
                    description = "طراوت و انرژی تابستان با بابونه‌های وحشی و آفتابگردان‌های تازه مزرعه گل آریس.",
                    lightReq = "نور غیرمستقیم",
                    waterReq = "تعویض آب روزانه",
                    tempReq = "۱۸ تا ۲۲ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = false,
                    isWishlisted = false,
                    vendorName = "بوتیک گل آریس تجریش"
                )
            )
            db.productDao().insertProducts(sampleProducts)
        }

        val existingOrders = db.orderDao().getAllOrders().first()
        if (existingOrders.isEmpty()) {
            val sampleOrders = listOf(
                OrderEntity(
                    orderId = "GL-8921",
                    orderDateShamsi = "۱۴۰۳/۰۵/۲۲",
                    deliveryTimeSlot = "ساعت ۱۶:۰۰ الی ۲۰:۰۰",
                    address = "تهران، خیابان فرشته، پلاک ۴۲، واحد ۳",
                    totalAmountToman = 1450000,
                    statusFa = "تحویل شده",
                    trackingCode = "TRK-982301",
                    vendorPhotoProofUrl = "img_hero_bouquet_1786618726074",
                    vendorName = "گل‌فروشی آریس مرکزی",
                    itemsSummary = "دسته‌گل رز و زنبق طلایی آریس (۱ عدد)"
                ),
                OrderEntity(
                    orderId = "GL-8945",
                    orderDateShamsi = "۱۴۰۳/۰۵/۲۴",
                    deliveryTimeSlot = "ساعت ۱۰:۰۰ الی ۱۳:۰۰",
                    address = "تهران، سعادت‌آباد، صرافهای جنوبی، پلاک ۱۸",
                    totalAmountToman = 2350000,
                    statusFa = "تایید عکس پیش از ارسال",
                    trackingCode = "TRK-982345",
                    vendorPhotoProofUrl = "img_hero_bouquet_1786618726074",
                    vendorName = "بوتیک گل آریس تجریش",
                    itemsSummary = "باکس چرمی گل رز و شکلات بلژیکی (۱ عدد)"
                )
            )
            for (ord in sampleOrders) {
                db.orderDao().insertOrder(ord)
            }
        }

        val existingTransactions = db.walletDao().getTransactions().first()
        if (existingTransactions.isEmpty()) {
            val sampleTx = listOf(
                WalletTransactionEntity(
                    title = "شارژ کیف پول (درگاه بانک سامان)",
                    amountToman = 2000000,
                    isDeposit = true,
                    dateShamsi = "۱۴۰۳/۰۵/۲۰ - ۱۴:۳۰",
                    status = "موفق"
                ),
                WalletTransactionEntity(
                    title = "پرداخت سفارش #GL-8921",
                    amountToman = 1450000,
                    isDeposit = false,
                    dateShamsi = "۱۴۰۳/۰۵/۲۲ - ۱۱:۱۵",
                    status = "موفق"
                )
            )
            for (tx in sampleTx) {
                db.walletDao().insertTransaction(tx)
            }
        }
    }

    suspend fun toggleWishlist(productId: String, currentStatus: Boolean) {
        db.productDao().updateWishlistStatus(productId, !currentStatus)
    }

    suspend fun addToCart(product: FlowerProductEntity) {
        val currentItems = db.cartDao().getCartItems().first()
        val existing = currentItems.find { it.productId == product.id }
        if (existing != null) {
            db.cartDao().insertOrUpdateCart(
                existing.copy(quantity = existing.quantity + 1)
            )
        } else {
            db.cartDao().insertOrUpdateCart(
                CartItemEntity(
                    productId = product.id,
                    titleFa = product.titleFa,
                    priceToman = product.priceToman,
                    quantity = 1,
                    drawableResName = product.drawableResName
                )
            )
        }
    }

    suspend fun updateCartQuantity(productId: String, delta: Int) {
        val currentItems = db.cartDao().getCartItems().first()
        val existing = currentItems.find { it.productId == productId }
        if (existing != null) {
            val newQty = existing.quantity + delta
            if (newQty <= 0) {
                db.cartDao().deleteCartItem(productId)
            } else {
                db.cartDao().insertOrUpdateCart(existing.copy(quantity = newQty))
            }
        }
    }

    suspend fun placeOrder(
        address: String,
        deliverySlot: String,
        items: List<CartItemEntity>,
        totalAmount: Long,
        paymentMethod: String
    ): String {
        val orderNum = (1000..9999).random()
        val orderId = "GL-$orderNum"
        val trackingCode = "TRK-${(100000..999999).random()}"
        val itemsSummary = items.joinToString(", ") { "${it.titleFa} (${it.quantity} عدد)" }

        val newOrder = OrderEntity(
            orderId = orderId,
            orderDateShamsi = "۱۴۰۳/۰۵/۲۵",
            deliveryTimeSlot = deliverySlot,
            address = address,
            totalAmountToman = totalAmount,
            statusFa = "در حال آماده‌سازی",
            trackingCode = trackingCode,
            vendorPhotoProofUrl = items.firstOrNull()?.drawableResName ?: "img_hero_bouquet_1786618726074",
            vendorName = "گل‌فروشی آریس مرکزی",
            itemsSummary = itemsSummary
        )

        db.orderDao().insertOrder(newOrder)

        // If paying with wallet, log wallet transaction
        if (paymentMethod == "کیف پول") {
            db.walletDao().insertTransaction(
                WalletTransactionEntity(
                    title = "پرداخت سفارش #$orderId",
                    amountToman = totalAmount,
                    isDeposit = false,
                    dateShamsi = "۱۴۰۳/۰۵/۲۵ - هم‌اکنون",
                    status = "موفق"
                )
            )
        }

        db.cartDao().clearCart()
        return orderId
    }

    suspend fun rechargeWallet(amountToman: Long) {
        db.walletDao().insertTransaction(
            WalletTransactionEntity(
                title = "شارژ آنلاین کیف پول (درگاه شتاب)",
                amountToman = amountToman,
                isDeposit = true,
                dateShamsi = "۱۴۰۳/۰۵/۲۵ - هم‌اکنون",
                status = "موفق"
            )
        )
    }

    suspend fun addProductByVendor(product: FlowerProductEntity) {
        db.productDao().insertProduct(product)
    }

    suspend fun updateVendorOrderProof(orderId: String, newStatus: String, photoProofUrl: String?) {
        db.orderDao().updateOrderStatusAndProof(orderId, newStatus, photoProofUrl)
    }
}
