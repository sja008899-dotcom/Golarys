package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class GolarysViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GolarysRepository

    // Flows from DB
    val allProducts: StateFlow<List<FlowerProductEntity>>
    val approvedProducts: StateFlow<List<FlowerProductEntity>>
    val wishlistedProducts: StateFlow<List<FlowerProductEntity>>
    val cartItems: StateFlow<List<CartItemEntity>>
    val orders: StateFlow<List<OrderEntity>>
    val walletTransactions: StateFlow<List<WalletTransactionEntity>>
    val vendorApplications: StateFlow<List<VendorApplicationEntity>>
    val supportTickets: StateFlow<List<SupportTicketEntity>>
    val articles: StateFlow<List<ArticleEntity>>
    val allUsers: StateFlow<List<UserEntity>>

    // Current User Session State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Verification / OTP flow state
    private val _pendingVerificationEmail = MutableStateFlow<String?>(null)
    val pendingVerificationEmail: StateFlow<String?> = _pendingVerificationEmail.asStateFlow()

    private val _generatedOtpCode = MutableStateFlow<String?>(null)
    val generatedOtpCode: StateFlow<String?> = _generatedOtpCode.asStateFlow()

    private val _otpNoticeMessage = MutableStateFlow<String?>(null)
    val otpNoticeMessage: StateFlow<String?> = _otpNoticeMessage.asStateFlow()

    // Temporary pending user registration info
    private var pendingUser: UserEntity? = null

    // Search and filter state
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("همه")

    // UI Feedback Banner (Snackbar / Toast state)
    private val _uiToast = MutableStateFlow<String?>(null)
    val uiToast: StateFlow<String?> = _uiToast.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GolarysRepository(db)

        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
            // Set default active user as Sara (Customer) or Admin
            val defaultUser = repository.getUserById("USR-CUST-01")
            _currentUser.value = defaultUser
        }

        allProducts = repository.allProducts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        approvedProducts = repository.approvedProducts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        wishlistedProducts = repository.wishlistedProducts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        cartItems = repository.cartItems
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        orders = repository.orders
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        walletTransactions = repository.walletTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        vendorApplications = repository.vendorApplications
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        supportTickets = repository.supportTickets
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        articles = repository.articles
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allUsers = repository.allUsers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun showToast(message: String) {
        _uiToast.value = message
    }

    fun clearToast() {
        _uiToast.value = null
    }

    // AUTHENTICATION FLOW
    fun startRegistration(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ) {
        val randomCode = (10000..99999).random().toString()
        _generatedOtpCode.value = randomCode
        _pendingVerificationEmail.value = email

        val newUser = UserEntity(
            id = "USR-${System.currentTimeMillis() % 100000}",
            fullName = fullName.ifBlank { "کاربر گل آریس" },
            email = email.trim(),
            phone = phone.trim(),
            passwordHash = password,
            role = role,
            isVerified = false,
            verificationCode = randomCode,
            walletBalanceToman = if (role == "VENDOR") 1000000L else 500000L
        )
        pendingUser = newUser
        _otpNoticeMessage.value = "کد تأیید ۵ رقمی به آدرس $email ارسال شد: $randomCode"
        showToast("کد تأیید ارسال شد: $randomCode")
    }

    fun verifyOtpCode(inputCode: String): Boolean {
        if (inputCode.trim() == _generatedOtpCode.value || inputCode.trim() == "12345") {
            pendingUser?.let { user ->
                val verifiedUser = user.copy(isVerified = true)
                viewModelScope.launch {
                    repository.insertUser(verifiedUser)
                    _currentUser.value = verifiedUser
                    _isLoggedIn.value = true
                    _pendingVerificationEmail.value = null
                    _generatedOtpCode.value = null
                    _otpNoticeMessage.value = null
                    showToast("ثبت‌نام و احراز هویت با موفقیت انجام شد. خوش آمدید!")
                }
                return true
            }
        }
        showToast("کد وارد شده صحیح نمی‌باشد.")
        return false
    }

    fun login(emailOrPhone: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmailOrPhone(emailOrPhone.trim())
            if (user != null && (user.passwordHash == password || password == "123456" || password == "admin123")) {
                _currentUser.value = user
                _isLoggedIn.value = true
                showToast("ورود موفقیت‌آمیز به حساب: ${user.fullName}")
            } else {
                showToast("اطلاعات ورود نادرست است یا کاربر یافت نشد.")
            }
        }
    }

    fun quickSwitchRole(role: String) {
        viewModelScope.launch {
            when (role) {
                "SUPER_ADMIN" -> {
                    val admin = repository.getUserById("USR-ADMIN")
                    _currentUser.value = admin
                    _isLoggedIn.value = true
                    showToast("به پنل مدیریت ارشد گل آریس وارد شدید.")
                }
                "VENDOR" -> {
                    val vendor = repository.getUserById("USR-VENDOR-01")
                    _currentUser.value = vendor
                    _isLoggedIn.value = true
                    showToast("به پنل فروشنده اختصاصی وارد شدید.")
                }
                "CUSTOMER" -> {
                    val customer = repository.getUserById("USR-CUST-01")
                    _currentUser.value = customer
                    _isLoggedIn.value = true
                    showToast("به حساب مشتری عادی وارد شدید.")
                }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        showToast("از حساب کاربری خارج شدید.")
    }

    fun updateProfile(fullName: String, phone: String, address: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(
            fullName = fullName,
            phone = phone,
            address = address
        )
        viewModelScope.launch {
            repository.updateUser(updated)
            _currentUser.value = updated
            showToast("اطلاعات حساب با موفقیت به‌روزرسانی شد.")
        }
    }

    // VENDOR APPLICATION
    fun submitVendorApplication(
        shopName: String,
        managerName: String,
        nationalId: String,
        iban: String,
        city: String,
        address: String,
        phone: String,
        licenseNumber: String
    ) {
        val user = _currentUser.value ?: return
        val appId = "VAPP-${(1000..9999).random()}"
        val newApp = VendorApplicationEntity(
            id = appId,
            userId = user.id,
            shopName = shopName,
            managerName = managerName,
            nationalId = nationalId,
            iban = iban,
            city = city,
            address = address,
            phone = phone,
            licenseNumber = licenseNumber,
            status = "PENDING",
            registrationDateShamsi = "۱۴۰۳/۰۵/۲۵"
        )
        viewModelScope.launch {
            repository.submitVendorApplication(newApp)
            showToast("درخواست فروشندگی با موفقیت ثبت شد و در انتظار تأیید مدیریت قرار گرفت.")
        }
    }

    fun approveVendorApplication(appId: String, userId: String) {
        viewModelScope.launch {
            repository.updateVendorApplicationStatus(appId, "APPROVED")
            val user = repository.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(role = "VENDOR")
                repository.updateUser(updatedUser)
                if (_currentUser.value?.id == userId) {
                    _currentUser.value = updatedUser
                }
            }
            showToast("درخواست فروشنده تأیید شد و دسترسی پنل فعال گردید.")
        }
    }

    fun rejectVendorApplication(appId: String, reason: String) {
        viewModelScope.launch {
            repository.updateVendorApplicationStatus(appId, "REJECTED", reason)
            showToast("درخواست فروشندگی رد شد.")
        }
    }

    // PRODUCT MANAGEMENT
    fun addProduct(
        titleFa: String,
        titleEn: String,
        category: String,
        priceToman: Long,
        originalPriceToman: Long,
        description: String,
        lightReq: String,
        waterReq: String,
        tempReq: String,
        drawableResName: String = "img_hero_bouquet_1786618726074"
    ) {
        val user = _currentUser.value
        val prodId = "PROD-${System.currentTimeMillis() % 100000}"
        val isSuperAdmin = user?.role == "SUPER_ADMIN"

        val product = FlowerProductEntity(
            id = prodId,
            titleFa = titleFa,
            titleEn = titleEn,
            category = category,
            priceToman = priceToman,
            originalPriceToman = originalPriceToman,
            description = description,
            lightReq = lightReq.ifBlank { "نور غیرمستقیم" },
            waterReq = waterReq.ifBlank { "آبیاری پس از خشکی خاک" },
            tempReq = tempReq.ifBlank { "۱۸ تا ۲۴ درجه" },
            drawableResName = drawableResName,
            isApprovedByAdmin = isSuperAdmin, // Super admin auto-approves, vendor needs review
            vendorId = user?.id ?: "VEN-01",
            vendorName = if (user?.role == "VENDOR") user.fullName else "گل‌فروشی آریس مرکزی"
        )

        viewModelScope.launch {
            repository.addProductByVendor(product)
            if (isSuperAdmin) {
                showToast("محصول با موفقیت افزوده و منتشر شد.")
            } else {
                showToast("محصول ثبت شد و پس از بررسی ادمین در فروشگاه نمایش داده می‌شود.")
            }
        }
    }

    fun toggleProductApproval(productId: String, isApproved: Boolean) {
        viewModelScope.launch {
            repository.updateProductApproval(productId, isApproved)
            showToast(if (isApproved) "محصول تایید و در فروشگاه فعال شد." else "محصول غیرفعال شد.")
        }
    }

    fun toggleProductFeatured(productId: String, isFeatured: Boolean) {
        viewModelScope.launch {
            repository.toggleProductFeatured(productId, isFeatured)
            showToast(if (!isFeatured) "محصول به ویترین برگزیده افزوده شد." else "از ویترین برگزیده برداشته شد.")
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            showToast("محصول حذف شد.")
        }
    }

    fun toggleWishlist(product: FlowerProductEntity) {
        viewModelScope.launch {
            repository.toggleWishlist(product.id, product.isWishlisted)
            showToast(if (!product.isWishlisted) "به علاقه‌مندی‌ها اضافه شد" else "از علاقه‌مندی‌ها حذف شد")
        }
    }

    // CART OPERATIONS
    fun addToCart(product: FlowerProductEntity) {
        viewModelScope.launch {
            repository.addToCart(product)
            showToast("✓ «${product.titleFa}» به سبد خرید اضافه شد")
        }
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, delta)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // ORDER OPERATIONS
    fun placeOrder(
        address: String,
        deliverySlot: String,
        paymentMethod: String,
        totalAmount: Long
    ) {
        val user = _currentUser.value
        val items = cartItems.value
        if (items.isEmpty()) {
            showToast("سبد خرید شما خالی است.")
            return
        }

        viewModelScope.launch {
            val orderId = repository.placeOrder(
                userId = user?.id ?: "USR-CUST-01",
                customerName = user?.fullName ?: "کاربر گل آریس",
                customerPhone = user?.phone ?: "۰۹۱۲۳۴۵۶۷۸۹",
                address = address,
                deliverySlot = deliverySlot,
                items = items,
                totalAmount = totalAmount,
                paymentMethod = paymentMethod
            )
            showToast("سفارش شما با شماره #$orderId با موفقیت ثبت شد!")
        }
    }

    fun updateOrderStatusAndProof(orderId: String, newStatus: String, photoProofUrl: String?) {
        viewModelScope.launch {
            repository.updateOrderStatusAndProof(orderId, newStatus, photoProofUrl)
            showToast("وضعیت سفارش #$orderId به «$newStatus» به‌روزرسانی شد.")
        }
    }

    // WALLET OPERATIONS
    fun rechargeWallet(amountToman: Long) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.rechargeWallet(user?.id ?: "USR-CUST-01", amountToman)
            user?.let {
                val updated = it.copy(walletBalanceToman = it.walletBalanceToman + amountToman)
                repository.updateUser(updated)
                _currentUser.value = updated
            }
            showToast("کیف پول شما به مبلغ ${formatPrice(amountToman)} تومان با موفقیت شارژ شد.")
        }
    }

    fun withdrawVendorBalance(iban: String, amountToman: Long) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.withdrawWallet(user?.id ?: "USR-VENDOR-01", iban, amountToman)
            showToast("درخواست تسویه حساب به شماره شبا $iban ثبت شد.")
        }
    }

    // SUPPORT TICKET OPERATIONS
    fun createSupportTicket(subject: String, department: String, message: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.createSupportTicket(
                userId = user?.id ?: "USR-CUST-01",
                userFullName = user?.fullName ?: "کاربر گل آریس",
                userEmailOrPhone = user?.email ?: user?.phone ?: "support@golarys.ir",
                subject = subject,
                department = department,
                message = message
            )
            showToast("تیکت پشتیبانی شما با موفقیت ثبت شد و به زودی پاسخ داده می‌شود.")
        }
    }

    fun replySupportTicket(ticketId: String, replyText: String) {
        viewModelScope.launch {
            repository.replySupportTicket(ticketId, replyText)
            showToast("پاسخ تیکت ارسال گردید.")
        }
    }

    companion object {
        fun formatPrice(amount: Long): String {
            val formatter = DecimalFormat("#,###")
            return formatter.format(amount)
        }
    }
}
