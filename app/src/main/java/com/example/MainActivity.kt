package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.FlowerProductEntity
import com.example.ui.components.AuthDialog
import com.example.ui.components.GolarysBottomNav
import com.example.ui.components.GolarysTopBar
import com.example.ui.components.ProductDetailDialog
import com.example.ui.screens.*
import com.example.ui.theme.GolarysTheme
import com.example.ui.theme.SurfaceLight
import com.example.ui.viewmodel.GolarysViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: GolarysViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GolarysTheme {
                // Force Right-To-Left layout for Persian / Farsi language & Iranian e-commerce standards
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    GolarysAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GolarysAppContent(viewModel: GolarysViewModel) {
    val currentTab by viewModel.currentSelectedTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val walletBalance by viewModel.walletBalanceToman.collectAsStateWithLifecycle()
    val walletTransactions by viewModel.walletTransactions.collectAsStateWithLifecycle()

    val totalCartPrice by viewModel.cartTotalToman.collectAsStateWithLifecycle()
    val totalCartCount = cartItems.sumOf { it.quantity }

    var activeDialogProduct by remember { mutableStateOf<FlowerProductEntity?>(null) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GolarysTopBar(
                cartCount = totalCartCount,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onCartClick = { viewModel.currentSelectedTab.value = 1 },
                onVendorTabClick = { viewModel.currentSelectedTab.value = 4 },
                isVendorMode = (currentTab == 4),
                onAccountClick = { showAuthDialog = true },
                isLoggedIn = (currentUser != null)
            )
        },
        bottomBar = {
            GolarysBottomNav(
                selectedTab = currentTab,
                onTabSelected = { viewModel.currentSelectedTab.value = it },
                cartBadgeCount = totalCartCount
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceLight,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .align(Alignment.TopCenter)
            ) {
                Crossfade(
                    targetState = currentTab,
                    label = "ScreenTransition"
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> HomeScreen(
                            products = products,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            onCategorySelect = { viewModel.setCategory(it) },
                            onProductClick = { activeDialogProduct = it },
                            onAddToCartClick = { product ->
                                viewModel.addToCart(product)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("🌹 «${product.titleFa}» به سبد خرید اضافه شد.")
                                }
                            },
                            onWishlistClick = { product ->
                                viewModel.toggleWishlist(product)
                                coroutineScope.launch {
                                    val msg = if (product.isWishlisted) "از علاقه‌مندی‌ها حذف شد" else "به علاقه‌مندی‌ها اضافه شد"
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )

                        1 -> CartScreen(
                            cartItems = cartItems,
                            totalToman = totalCartPrice,
                            walletBalanceToman = walletBalance,
                            onUpdateQuantity = { productId, delta ->
                                viewModel.updateCartQuantity(productId, delta)
                            },
                            onPlaceOrder = { address, timeSlot, paymentMethod ->
                                viewModel.placeOrder(address, timeSlot, paymentMethod)
                            }
                        )

                        2 -> OrdersScreen(
                            orders = orders
                        )

                        3 -> WalletScreen(
                            balanceToman = walletBalance,
                            transactions = walletTransactions,
                            onRecharge = { amount ->
                                viewModel.rechargeWallet(amount)
                            }
                        )

                        4 -> VendorDashboardScreen(
                            orders = orders,
                            onAddProduct = { title, category, price, desc, light, water, temp ->
                                viewModel.addVendorProduct(title, category, price, desc, light, water, temp)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("✅ محصول جدید با موفقیت به ویترین گل آریس افزوده شد.")
                                }
                            },
                            onUpdateOrderStatus = { orderId, newStatus ->
                                viewModel.updateOrderStatusByVendor(orderId, newStatus)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("وضعیت سفارش #$orderId بروزرسانی شد.")
                                }
                            }
                        )

                        5 -> JournalAndAdminScreen()

                        else -> HomeScreen(
                            products = products,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            onCategorySelect = { viewModel.setCategory(it) },
                            onProductClick = { activeDialogProduct = it },
                            onAddToCartClick = { product -> viewModel.addToCart(product) },
                            onWishlistClick = { product -> viewModel.toggleWishlist(product) }
                        )
                    }
                }
            }
        }

        // Product Detail Modal
        activeDialogProduct?.let { product ->
            ProductDetailDialog(
                product = product,
                onDismiss = { activeDialogProduct = null },
                onAddToCart = { p ->
                    viewModel.addToCart(p)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("🌹 «${p.titleFa}» به سبد خرید اضافه شد.")
                    }
                },
                onWishlistToggle = { p ->
                    viewModel.toggleWishlist(p)
                }
            )
        }

        // Auth Dialog (Sign In / Sign Up / Profile)
        if (showAuthDialog) {
            AuthDialog(
                currentUser = currentUser,
                onDismiss = { showAuthDialog = false },
                onAuthSuccess = { user ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("خوش آمدید ${user.email ?: ""}!")
                    }
                },
                onSignOut = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("از حساب کاربری خارج شدید.")
                    }
                }
            )
        }
    }
}
