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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.FlowerProductEntity
import com.example.ui.components.AuthDialog
import com.example.ui.components.GolarysBottomNav
import com.example.ui.components.GolarysTopBar
import com.example.ui.components.ProductDetailDialog
import com.example.ui.components.ProfileDialog
import com.example.ui.screens.*
import com.example.ui.theme.GolarysTheme
import com.example.ui.theme.SurfaceLight
import com.example.ui.viewmodel.GolarysViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: GolarysViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GolarysTheme {
                // Force Right-To-Left layout for Persian language
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
    var currentTab by remember { mutableIntStateOf(0) }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val uiToast by viewModel.uiToast.collectAsStateWithLifecycle()

    val products by viewModel.approvedProducts.collectAsStateWithLifecycle()
    val allProductsAdmin by viewModel.allProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val walletTransactions by viewModel.walletTransactions.collectAsStateWithLifecycle()
    val vendorApplications by viewModel.vendorApplications.collectAsStateWithLifecycle()
    val supportTickets by viewModel.supportTickets.collectAsStateWithLifecycle()
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    val totalCartPrice = cartItems.sumOf { it.priceToman * it.quantity }
    val totalCartCount = cartItems.sumOf { it.quantity }
    val walletBalance = currentUser?.walletBalanceToman ?: 500000L

    var activeDialogProduct by remember { mutableStateOf<FlowerProductEntity?>(null) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiToast) {
        uiToast?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    val isSuperAdmin = currentUser?.role == "SUPER_ADMIN"

    Scaffold(
        topBar = {
            GolarysTopBar(
                currentUser = currentUser,
                cartCount = totalCartCount,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.searchQuery.value = it },
                onCartClick = { currentTab = 1 },
                onVendorTabClick = { currentTab = 4 },
                onSupportClick = { currentTab = 5 },
                onAdminClick = { currentTab = 7 },
                onAccountClick = {
                    if (currentUser != null) {
                        showProfileDialog = true
                    } else {
                        showAuthDialog = true
                    }
                }
            )
        },
        bottomBar = {
            GolarysBottomNav(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it },
                cartBadgeCount = totalCartCount,
                isSuperAdmin = isSuperAdmin
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
                    .widthIn(max = 640.dp)
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
                            onCategorySelect = { viewModel.selectedCategory.value = it },
                            onProductClick = { activeDialogProduct = it },
                            onAddToCartClick = { product -> viewModel.addToCart(product) },
                            onWishlistClick = { product -> viewModel.toggleWishlist(product) }
                        )

                        1 -> CartScreen(
                            cartItems = cartItems,
                            totalToman = totalCartPrice,
                            walletBalanceToman = walletBalance,
                            onUpdateQuantity = { productId, delta ->
                                viewModel.updateCartQuantity(productId, delta)
                            },
                            onPlaceOrder = { address, timeSlot, paymentMethod ->
                                viewModel.placeOrder(address, timeSlot, paymentMethod, totalCartPrice)
                                currentTab = 2 // Navigate to Orders
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
                            currentUser = currentUser,
                            orders = orders,
                            products = allProductsAdmin.filter { it.vendorId == currentUser?.id || currentUser?.role == "SUPER_ADMIN" },
                            vendorApplications = vendorApplications,
                            viewModel = viewModel
                        )

                        5 -> SupportScreen(
                            currentUser = currentUser,
                            tickets = supportTickets,
                            viewModel = viewModel
                        )

                        6 -> JournalScreen(
                            articles = articles
                        )

                        7 -> AdminPanelScreen(
                            viewModel = viewModel,
                            users = allUsers,
                            vendorApplications = vendorApplications,
                            allProducts = allProductsAdmin,
                            allOrders = orders,
                            transactions = walletTransactions
                        )

                        else -> HomeScreen(
                            products = products,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            onCategorySelect = { viewModel.selectedCategory.value = it },
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
                onAddToCart = { p -> viewModel.addToCart(p) },
                onWishlistToggle = { p -> viewModel.toggleWishlist(p) }
            )
        }

        // Auth Dialog (Sign Up with OTP, Login)
        if (showAuthDialog) {
            AuthDialog(
                viewModel = viewModel,
                onDismiss = { showAuthDialog = false }
            )
        }

        // User Profile & Fast Switcher Dialog
        if (showProfileDialog) {
            ProfileDialog(
                user = currentUser,
                viewModel = viewModel,
                onDismiss = { showProfileDialog = false },
                onOpenAuth = { showAuthDialog = true }
            )
        }
    }
}
