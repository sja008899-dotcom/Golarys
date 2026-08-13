package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GolarysViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GolarysRepository
    private val firebaseAuth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()

    val currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth?.currentUser)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GolarysRepository(db)
        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
        }
        firebaseAuth?.addAuthStateListener { auth ->
            currentUser.value = auth.currentUser
        }
    }

    val allProducts: StateFlow<List<FlowerProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val walletTransactions: StateFlow<List<WalletTransactionEntity>> = repository.walletTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCategory = MutableStateFlow("همه")
    val searchQuery = MutableStateFlow("")
    val selectedProductForDetail = MutableStateFlow<FlowerProductEntity?>(null)
    val currentSelectedTab = MutableStateFlow(0) // 0: Store, 1: Cart, 2: Orders, 3: Wallet, 4: Vendor, 5: Journal/SEO
    val userMessage = MutableStateFlow<String?>(null)

    // Derived wallet balance
    val walletBalanceToman: StateFlow<Long> = walletTransactions.map { txList ->
        txList.sumOf { if (it.isDeposit) it.amountToman else -it.amountToman }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 550000L)

    val cartTotalToman: StateFlow<Long> = cartItems.map { items ->
        items.sumOf { it.priceToman * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun selectProductDetail(product: FlowerProductEntity?) {
        selectedProductForDetail.value = product
    }

    fun toggleWishlist(product: FlowerProductEntity) {
        viewModelScope.launch {
            repository.toggleWishlist(product.id, product.isWishlisted)
            userMessage.value = if (!product.isWishlisted)
                "«${product.titleFa}» به لیست علاقه‌مندی‌ها اضافه شد"
            else
                "«${product.titleFa}» از علاقه‌مندی‌ها حذف شد"
        }
    }

    fun addToCart(product: FlowerProductEntity) {
        viewModelScope.launch {
            repository.addToCart(product)
            userMessage.value = "«${product.titleFa}» به سبد خرید اضافه شد"
        }
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, delta)
        }
    }

    fun placeOrder(
        address: String,
        deliverySlot: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val currentCart = cartItems.value
            if (currentCart.isEmpty()) {
                userMessage.value = "سبد خرید شما خالی است"
                return@launch
            }
            val total = cartTotalToman.value
            if (paymentMethod == "کیف پول" && walletBalanceToman.value < total) {
                userMessage.value = "موجودی کیف پول کافی نیست. لطفاً کیف پول را شارژ کنید."
                return@launch
            }

            val orderId = repository.placeOrder(
                address = address,
                deliverySlot = deliverySlot,
                items = currentCart,
                totalAmount = total,
                paymentMethod = paymentMethod
            )
            userMessage.value = "سفارش #$orderId با موفقیت ثبت شد!"
            currentSelectedTab.value = 2 // Navigate to Orders Tab
        }
    }

    fun rechargeWallet(amountToman: Long) {
        viewModelScope.launch {
            repository.rechargeWallet(amountToman)
            userMessage.value = "کیف پول با موفقیت به میزان ${formatPrice(amountToman)} تومان شارژ شد"
        }
    }

    fun addVendorProduct(
        titleFa: String,
        category: String,
        priceToman: Long,
        description: String,
        lightReq: String,
        waterReq: String,
        tempReq: String
    ) {
        viewModelScope.launch {
            val newId = "PROD-${(200..999).random()}"
            val newProduct = FlowerProductEntity(
                id = newId,
                titleFa = titleFa,
                titleEn = "Vendor Added Flower",
                category = category,
                priceToman = priceToman,
                description = description,
                lightReq = lightReq,
                waterReq = waterReq,
                tempReq = tempReq,
                drawableResName = "img_hero_bouquet_1786618726074",
                vendorName = "فروشگاه من (گل آریس)"
            )
            repository.addProductByVendor(newProduct)
            userMessage.value = "محصول جدید با موفقیت به انبار گل آریس اضافه شد"
        }
    }

    fun updateOrderStatusByVendor(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateVendorOrderProof(orderId, newStatus, "img_hero_bouquet_1786618726074")
            userMessage.value = "وضعیت سفارش #$orderId به «$newStatus» تغییر یافت و تصویر برای مشتری ارسال شد"
        }
    }

    fun clearUserMessage() {
        userMessage.value = null
    }

    companion object {
        fun formatPrice(amount: Long): String {
            return String.format("%,d", amount)
        }
    }
}
