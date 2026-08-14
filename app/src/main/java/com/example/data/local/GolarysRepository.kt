package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GolarysRepository(private val db: AppDatabase) {

    val allProducts: Flow<List<FlowerProductEntity>> = db.productDao().getAllProducts()
    val approvedProducts: Flow<List<FlowerProductEntity>> = db.productDao().getApprovedProducts()
    val wishlistedProducts: Flow<List<FlowerProductEntity>> = db.productDao().getWishlistedProducts()
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getCartItems()
    val orders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    val walletTransactions: Flow<List<WalletTransactionEntity>> = db.walletDao().getTransactions()
    val vendorApplications: Flow<List<VendorApplicationEntity>> = db.vendorDao().getAllApplications()
    val supportTickets: Flow<List<SupportTicketEntity>> = db.supportTicketDao().getAllTickets()
    val articles: Flow<List<ArticleEntity>> = db.articleDao().getAllArticles()
    val allUsers: Flow<List<UserEntity>> = db.userDao().getAllUsers()

    suspend fun initDefaultDataIfNeeded() {
        // 1. Default Users (Customer, Vendor, Super Admin)
        val existingUsers = db.userDao().getAllUsers().first()
        if (existingUsers.isEmpty()) {
            val defaultUsers = listOf(
                UserEntity(
                    id = "USR-ADMIN",
                    fullName = "مدیریت ارشد گل آریس",
                    email = "admin@golarys.ir",
                    phone = "09120000001",
                    passwordHash = "admin123",
                    role = "SUPER_ADMIN",
                    isVerified = true,
                    address = "تهران، دفتر مرکزی گل آریس، برج نیلوفر، طبقه ۱۲",
                    walletBalanceToman = 15000000L
                ),
                UserEntity(
                    id = "USR-VENDOR-01",
                    fullName = "مهندس علیرضا رادمنش",
                    email = "vendor@golarys.ir",
                    phone = "09120000002",
                    passwordHash = "vendor123",
                    role = "VENDOR",
                    isVerified = true,
                    address = "تهران، بازار گل شهید محلاتی، سالن ۳، غرفه ۲۴",
                    walletBalanceToman = 4800000L
                ),
                UserEntity(
                    id = "USR-CUST-01",
                    fullName = "سارا احمدی",
                    email = "sara@golarys.ir",
                    phone = "09120000003",
                    passwordHash = "123456",
                    role = "CUSTOMER",
                    isVerified = true,
                    address = "تهران، خیابان فرشته، پلاک ۴۲، واحد ۳",
                    walletBalanceToman = 850000L
                )
            )
            for (u in defaultUsers) {
                db.userDao().insertUser(u)
            }
        }

        // 2. Default Vendor Applications
        val existingApps = db.vendorDao().getAllApplications().first()
        if (existingApps.isEmpty()) {
            val defaultApps = listOf(
                VendorApplicationEntity(
                    id = "VAPP-101",
                    userId = "USR-VENDOR-01",
                    shopName = "گل‌فروشی و بوتیک آریس مرکزی",
                    managerName = "علیرضا رادمنش",
                    nationalId = "۰۰۱۲۳۴۵۶۷۸",
                    iban = "IR820170000000123456789012",
                    city = "تهران",
                    address = "تهران، بازار گل شهید محلاتی، سالن ۳، غرفه ۲۴",
                    phone = "۰۲۱-۸۸۷۷۶۶۵۵",
                    licenseNumber = "LIC-984210",
                    status = "APPROVED",
                    registrationDateShamsi = "۱۴۰۳/۰۴/۱۵"
                ),
                VendorApplicationEntity(
                    id = "VAPP-102",
                    userId = "USR-VENDOR-NEW",
                    shopName = "گلخانه ارکیده و زنبق نیاوران",
                    managerName = "مریم کریمی",
                    nationalId = "۰۴۵۱۱۲۲۳۳۴",
                    iban = "IR550190000000987654321098",
                    city = "تهران",
                    address = "تهران، نیاوران، خیابان یاسر، کوچه لاله، پلاک ۷",
                    phone = "۰۹۱۲۳۴۵۶۷۸۰",
                    licenseNumber = "LIC-993215",
                    status = "PENDING",
                    registrationDateShamsi = "۱۴۰۳/۰۵/۲۴"
                )
            )
            for (app in defaultApps) {
                db.vendorDao().insertApplication(app)
            }
        }

        // 3. Default Products
        val existingProducts = db.productDao().getAllProducts().first()
        if (existingProducts.isEmpty()) {
            val sampleProducts = listOf(
                FlowerProductEntity(
                    id = "PROD-101",
                    titleFa = "دسته‌گل رز هلندی و زنبق طلایی آریس",
                    titleEn = "Golden Iris & Red Rose Bouquet",
                    category = "دسته‌گل",
                    priceToman = 1450000,
                    originalPriceToman = 1750000,
                    rating = 4.9f,
                    reviewsCount = 38,
                    description = "ترکیب منحصربه‌فرد زنبق زرد و ۲۰ شاخه رز هلندی ممتاز با روبان طلایی و کاور ضدآب مخصوص گل آریس.",
                    lightReq = "نور غیرمستقیم",
                    waterReq = "تعویض آب هر ۲ روز",
                    tempReq = "۱۵ تا ۲۲ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = true,
                    isWishlisted = true,
                    vendorName = "گل‌فروشی آریس مرکزی",
                    isApprovedByAdmin = true
                ),
                FlowerProductEntity(
                    id = "PROD-102",
                    titleFa = "سانسوریا ابلق پا کوتاه (گلدان سرامیکی طلایی)",
                    titleEn = "Sansevieria Dwarf Variegated",
                    category = "گل‌های آپارتمانی",
                    priceToman = 680000,
                    originalPriceToman = 800000,
                    rating = 4.8f,
                    reviewsCount = 52,
                    description = "گیاه بسیار مقاوم تصفیه‌کننده سموم هوا، همراه با خاک کوکوپیت غنی‌شده و گلدان لوکس سرامیکی.",
                    lightReq = "مقاوم به سایه و کم‌نوری",
                    waterReq = "هر ۱۰ تا ۱۴ روز یک‌بار",
                    tempReq = "۱۶ تا ۲۸ درجه",
                    drawableResName = "img_app_logo_1786618714248",
                    isFeatured = true,
                    isWishlisted = false,
                    vendorName = "گلخانه ارکیده و زنبق نیاوران",
                    isApprovedByAdmin = true
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
                    description = "کمیاب‌ترین و لوکس‌ترین گونه زامیفولیا با شاخه‌های پرپشت مشکی براق برای دکوراسیون‌های مدرن.",
                    lightReq = "نور متوسط تا کم",
                    waterReq = "خشک شدن کامل خاک",
                    tempReq = "۱۸ تا ۲۶ درجه",
                    drawableResName = "img_app_logo_1786618714248",
                    isFeatured = false,
                    isWishlisted = false,
                    vendorName = "گل‌فروشی آریس مرکزی",
                    isApprovedByAdmin = true
                ),
                FlowerProductEntity(
                    id = "PROD-104",
                    titleFa = "باکس چرمی گل رز و شکلات دست‌ساز بلژیکی",
                    titleEn = "Luxury Leather Rose & Chocolate Box",
                    category = "هدیه",
                    priceToman = 2350000,
                    originalPriceToman = 2600000,
                    rating = 4.9f,
                    reviewsCount = 44,
                    description = "جعبه دست‌دوز چرم قهوه‌ای به همراه رزهای قرمز تازه و کارت‌پستال خوش‌نویسی اختصاصی گل آریس.",
                    lightReq = "محیط خنک دور از آفتاب",
                    waterReq = "اسفنج هیدروفیلیک مرطوب",
                    tempReq = "۱۵ تا ۲۰ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = true,
                    isWishlisted = false,
                    vendorName = "بوتیک گل آریس تجریش",
                    isApprovedByAdmin = true
                ),
                FlowerProductEntity(
                    id = "PROD-105",
                    titleFa = "ارکیده فالانوپسیس بنفش دو شاخه پرگل",
                    titleEn = "Purple Phalaenopsis Orchid 2-Spike",
                    category = "گل‌های آپارتمانی",
                    priceToman = 2100000,
                    originalPriceToman = 0,
                    rating = 4.7f,
                    reviewsCount = 29,
                    description = "ارکیده اصیل تایوانی با ماندگاری گل بالای ۳ ماه در گلدان شیشه‌ای استوانه‌ای با بستر خزه اسفاگنوم.",
                    lightReq = "نور زیاد فیلتر شده",
                    waterReq = "غوطه‌وری هفتگی در آب مقطر",
                    tempReq = "۲۰ تا ۲۵ درجه",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = false,
                    isWishlisted = false,
                    vendorName = "گل‌فروشی آریس مرکزی",
                    isApprovedByAdmin = true
                ),
                FlowerProductEntity(
                    id = "PROD-106",
                    titleFa = "باکس رز جاودان طلایی با محفظه شیشه‌ای موزیکال",
                    titleEn = "Preserved Forever Gold Rose",
                    category = "رز جاودان",
                    priceToman = 1650000,
                    originalPriceToman = 1950000,
                    rating = 5.0f,
                    reviewsCount = 31,
                    description = "رز مومیایی‌شده طبیعی با ماندگاری تضمینی ۳ تا ۵ سال بدون نیاز به آب و خاک در قاب بلوری.",
                    lightReq = "دور از نور مستقیم خورشید",
                    waterReq = "بدون نیاز به آب",
                    tempReq = "دمای معمولی اتاق",
                    drawableResName = "img_hero_bouquet_1786618726074",
                    isFeatured = true,
                    isWishlisted = false,
                    vendorName = "بوتیک گل آریس تجریش",
                    isApprovedByAdmin = true
                )
            )
            db.productDao().insertProducts(sampleProducts)
        }

        // 4. Default Orders
        val existingOrders = db.orderDao().getAllOrders().first()
        if (existingOrders.isEmpty()) {
            val sampleOrders = listOf(
                OrderEntity(
                    orderId = "GL-8921",
                    userId = "USR-CUST-01",
                    customerName = "سارا احمدی",
                    customerPhone = "۰۹۱۲۳۴۵۶۷۸۹",
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
                    userId = "USR-CUST-01",
                    customerName = "سارا احمدی",
                    customerPhone = "۰۹۱۲۳۴۵۶۷۸۹",
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

        // 5. Default Wallet Transactions
        val existingTransactions = db.walletDao().getTransactions().first()
        if (existingTransactions.isEmpty()) {
            val sampleTx = listOf(
                WalletTransactionEntity(
                    userId = "USR-CUST-01",
                    title = "شارژ کیف پول (درگاه شاپرک - بانک سامان)",
                    amountToman = 2000000,
                    isDeposit = true,
                    dateShamsi = "۱۴۰۳/۰۵/۲۰ - ۱۴:۳۰",
                    status = "موفق"
                ),
                WalletTransactionEntity(
                    userId = "USR-CUST-01",
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

        // 6. Default Support Tickets
        val existingTickets = db.supportTicketDao().getAllTickets().first()
        if (existingTickets.isEmpty()) {
            val sampleTickets = listOf(
                SupportTicketEntity(
                    id = "TCK-401",
                    userId = "USR-CUST-01",
                    userFullName = "سارا احمدی",
                    userEmailOrPhone = "sara@golarys.ir",
                    subject = "مشاوره درباره زمان کوددهی سانسوریا",
                    department = "مشاوره با گیاه‌پزشک",
                    message = "سلام، سانسوریای من نوک برگ‌هاش کمی زرد شده، از چه کودی باید استفاده کنم؟",
                    adminReply = "درود بر شما. زردی نوک برگ معمولاً از آبیاری نامنظم یا املاح بالای آب لوله‌کشی است. پیشنهاد می‌شود از کود NPK 12-12-36 با پتاسیم بالا و آب جوشیده سرد شده استفاده فرمایید.",
                    status = "ANSWERED",
                    dateShamsi = "۱۴۰۳/۰۵/۲۳"
                ),
                SupportTicketEntity(
                    id = "TCK-402",
                    userId = "USR-CUST-01",
                    userFullName = "سارا احمدی",
                    userEmailOrPhone = "sara@golarys.ir",
                    subject = "درخواست هماهنگی تحویل قبل از ساعت ۱۸",
                    department = "پیگیری سفارش",
                    message = "سفارش شماره GL-8945 را ثبت کرده‌ام، آیا امکان دارد پیک رأس ساعت ۱۷ در محل باشد؟",
                    adminReply = "سلام و احترام. هماهنگی لازم با سفیر گل آریس انجام شد و سفارش تا ساعت ۱۷ تحویل خواهد گردید.",
                    status = "ANSWERED",
                    dateShamsi = "۱۴۰۳/۰۵/۲۴"
                )
            )
            for (t in sampleTickets) {
                db.supportTicketDao().insertTicket(t)
            }
        }

        // 7. Default Articles (Journal)
        val existingArticles = db.articleDao().getAllArticles().first()
        if (existingArticles.isEmpty()) {
            val sampleArticles = listOf(
                ArticleEntity(
                    id = "ART-01",
                    titleFa = "🪴 ۵ راز شادابی و تکثیر سریع سانسوریا در آپارتمان",
                    category = "گیاهان آپارتمانی",
                    author = "دکتر رضا گلشن (گیاه‌پزشک گل آریس)",
                    readTimeMinutes = 4,
                    summary = "سانسوریا از مقاوم‌ترین گیاهان است، اما عدم شناخت نیاز نوری و آبیاری مکرر می‌تواند باعث پوسیدگی ریشه شود...",
                    content = """
                        سانسوریا (Sansevieria) به گیاه زبان مادر شوهر نیز معروف است و جزو موثرترین تصفیه‌کننده‌های هوای خانگی طبق تاییدیه ناسا به شمار می‌رود.
                        
                        ۱. سنجش رطوبت خاک: قبل از هر بار آبیاری، با یک خلال چوبی مطمئن شوید حداقل ۳ الی ۵ سانتی‌متر خاک سطحی کاملاً خشک است.
                        ۲. نوردهی مناسب: اگرچه در سایه زنده می‌ماند، اما نور فیلترشده پنجره جنوبی باعث افزایش چشمگیر پهنای برگ‌ها و رنگ ابلق زرد می‌شود.
                        ۳. زهکشی خاک: ترکیب خاک برگ، پرلیت، پیت‌ماس و پوکه معدنی مانع تجمع رطوبت اضافی دور ریزوم‌ها می‌گردد.
                        ۴. تمیز کردن برگ‌ها: گرد و غبار فتوسنتز را کاهش می‌دهد؛ با پارچه نخی نم‌دار ماهانه سطح برگ را تمیز کنید.
                    """.trimIndent(),
                    dateShamsi = "۱۴۰۳/۰۵/۱۸"
                ),
                ArticleEntity(
                    id = "ART-02",
                    titleFa = "🌸 ترفندهای نگهداری ارکیده فالانوپسیس و گل‌دهی مجدد",
                    category = "گل‌های لوکس",
                    author = "تیم متخصصین کلینیک گل آریس",
                    readTimeMinutes = 5,
                    summary = "پس از ریختن گل‌های ارکیده نگران نباشید؛ با هرس اصولی ساقه و تغییر ملایم دما می‌توانید گیاه را مجدداً به گل ببرید...",
                    content = """
                        ارکیده فالانوپسیس از محبوب‌ترین گل‌های آپارتمانی با شکوفه‌های ماندگار است.
                        
                        • روش آبیاری غوطه‌وری: هفته‌ای یک‌بار گلدان را به مدت ۱۵ دقیقه در ظرف آب بدون کلر قرار دهید تا ریشه‌های نقره‌ای به سبز درخشان تغییر رنگ دهند.
                        • ایجاد شوک دمایی: برای تحریک جوانه گل‌دهی، در فصل پاییز اختلاف دمای شب و روز را حدود ۵ تا ۷ درجه سانتی‌گراد حفظ کنید.
                        • هرس ساقه گل: ساقه خشک‌شده را تا بالای گره دوم از پایین با قیچی ضدعفونی شده کوتاه نمایید.
                    """.trimIndent(),
                    dateShamsi = "۱۴۰۳/۰۵/۲۰"
                ),
                ArticleEntity(
                    id = "ART-03",
                    titleFa = "🌹 راهنمای افزایش ماندگاری دسته‌گل و باکس گل طبیعی",
                    category = "دسته‌گل و هدیه",
                    author = "بوتیک گل آریس",
                    readTimeMinutes = 3,
                    summary = "چگونه طراوت و عطر گل‌های بریده‌شده نظیر رز، زنبق و لیلیوم را تا بیش از ۲ هفته حفظ کنیم؟",
                    content = """
                        گل‌های شاخه‌بریده به توجه ویژه به دمای محیط و نظافت گلدان نیاز دارند.
                        
                        ۱. برش مورب ساقه: ساقه‌ها را با زاویه ۴۵ درجه در زیر آب جاری کوتاه کنید تا حباب هوا وارد آوندها نشود.
                        ۲. حذف برگ‌های پایین: هیچ برگی نباید داخل آب گلدان قرار گیرد زیرا باعث رشد باکتری و فساد آب می‌شود.
                        ۳. استفاده از غذای گل آریس: پودر محافظ شامل گلوکز و اسیدسیتریک ماندگاری شکوفه‌ها را دوبرابر می‌کند.
                        ۴. دوری از میوه‌های رسیده: گاز اتیلن متصاعدشده از سیب و موز فرآیند پژمردگی گل‌ها را تسریع می‌کند.
                    """.trimIndent(),
                    dateShamsi = "۱۴۰۳/۰۵/۲۲"
                )
            )
            db.articleDao().insertArticles(sampleArticles)
        }
    }

    // User Operations
    suspend fun getUserByEmailOrPhone(query: String): UserEntity? = db.userDao().getUserByEmailOrPhone(query)
    suspend fun getUserById(userId: String): UserEntity? = db.userDao().getUserById(userId)
    suspend fun insertUser(user: UserEntity) = db.userDao().insertUser(user)
    suspend fun updateUser(user: UserEntity) = db.userDao().updateUser(user)

    // Vendor Application Operations
    suspend fun submitVendorApplication(app: VendorApplicationEntity) = db.vendorDao().insertApplication(app)
    suspend fun updateVendorApplicationStatus(appId: String, newStatus: String, reason: String? = null) {
        db.vendorDao().updateApplicationStatus(appId, newStatus, reason)
    }

    // Product Operations
    suspend fun toggleWishlist(productId: String, currentStatus: Boolean) {
        db.productDao().updateWishlistStatus(productId, !currentStatus)
    }

    suspend fun updateProductApproval(productId: String, isApproved: Boolean) {
        db.productDao().updateProductApproval(productId, isApproved)
    }

    suspend fun toggleProductFeatured(productId: String, isFeatured: Boolean) {
        db.productDao().updateProductFeatured(productId, !isFeatured)
    }

    suspend fun deleteProduct(productId: String) {
        db.productDao().deleteProduct(productId)
    }

    suspend fun addProductByVendor(product: FlowerProductEntity) {
        db.productDao().insertProduct(product)
    }

    // Cart Operations
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

    suspend fun clearCart() {
        db.cartDao().clearCart()
    }

    // Order Operations
    suspend fun placeOrder(
        userId: String,
        customerName: String,
        customerPhone: String,
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
            userId = userId,
            customerName = customerName,
            customerPhone = customerPhone,
            orderDateShamsi = "۱۴۰۳/۰۵/۲۵",
            deliveryTimeSlot = deliverySlot,
            address = address,
            totalAmountToman = totalAmount,
            statusFa = "در حال آماده‌سازی",
            trackingCode = trackingCode,
            vendorPhotoProofUrl = items.firstOrNull()?.drawableResName ?: "img_hero_bouquet_1786618726074",
            vendorName = "گل‌فروشی آریس مرکزی",
            itemsSummary = itemsSummary,
            paymentMethod = paymentMethod
        )

        db.orderDao().insertOrder(newOrder)

        if (paymentMethod == "کیف پول") {
            db.walletDao().insertTransaction(
                WalletTransactionEntity(
                    userId = userId,
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

    suspend fun updateOrderStatusAndProof(orderId: String, newStatus: String, photoProofUrl: String?) {
        db.orderDao().updateOrderStatusAndProof(orderId, newStatus, photoProofUrl)
    }

    // Wallet Operations
    suspend fun rechargeWallet(userId: String, amountToman: Long) {
        db.walletDao().insertTransaction(
            WalletTransactionEntity(
                userId = userId,
                title = "شارژ آنلاین کیف پول (درگاه شاپرک)",
                amountToman = amountToman,
                isDeposit = true,
                dateShamsi = "۱۴۰۳/۰۵/۲۵ - هم‌اکنون",
                status = "موفق"
            )
        )
    }

    suspend fun withdrawWallet(userId: String, iban: String, amountToman: Long) {
        db.walletDao().insertTransaction(
            WalletTransactionEntity(
                userId = userId,
                title = "درخواست تسویه حساب به شبا $iban",
                amountToman = amountToman,
                isDeposit = false,
                dateShamsi = "۱۴۰۳/۰۵/۲۵ - هم‌اکنون",
                status = "در انتظار تایید ادمین"
            )
        )
    }

    // Support Ticket Operations
    suspend fun createSupportTicket(
        userId: String,
        userFullName: String,
        userEmailOrPhone: String,
        subject: String,
        department: String,
        message: String
    ) {
        val ticketId = "TCK-${(500..999).random()}"
        val newTicket = SupportTicketEntity(
            id = ticketId,
            userId = userId,
            userFullName = userFullName,
            userEmailOrPhone = userEmailOrPhone,
            subject = subject,
            department = department,
            message = message,
            adminReply = null,
            status = "OPEN",
            dateShamsi = "۱۴۰۳/۰۵/۲۵"
        )
        db.supportTicketDao().insertTicket(newTicket)
    }

    suspend fun replySupportTicket(ticketId: String, reply: String) {
        db.supportTicketDao().replyTicket(ticketId, reply, "ANSWERED")
    }
}
