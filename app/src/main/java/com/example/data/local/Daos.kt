package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :emailOrPhone OR phone = :emailOrPhone LIMIT 1")
    suspend fun getUserByEmailOrPhone(emailOrPhone: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface VendorDao {
    @Query("SELECT * FROM vendor_applications ORDER BY id DESC")
    fun getAllApplications(): Flow<List<VendorApplicationEntity>>

    @Query("SELECT * FROM vendor_applications WHERE userId = :userId LIMIT 1")
    suspend fun getApplicationByUserId(userId: String): VendorApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: VendorApplicationEntity)

    @Query("UPDATE vendor_applications SET status = :newStatus, rejectionReason = :reason WHERE id = :appId")
    suspend fun updateApplicationStatus(appId: String, newStatus: String, reason: String? = null)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<FlowerProductEntity>>

    @Query("SELECT * FROM products WHERE isApprovedByAdmin = 1")
    fun getApprovedProducts(): Flow<List<FlowerProductEntity>>

    @Query("SELECT * FROM products WHERE isWishlisted = 1")
    fun getWishlistedProducts(): Flow<List<FlowerProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<FlowerProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: FlowerProductEntity)

    @Query("UPDATE products SET isWishlisted = :isWishlisted WHERE id = :productId")
    suspend fun updateWishlistStatus(productId: String, isWishlisted: Boolean)

    @Query("UPDATE products SET isApprovedByAdmin = :isApproved WHERE id = :productId")
    suspend fun updateProductApproval(productId: String, isApproved: Boolean)

    @Query("UPDATE products SET isFeatured = :isFeatured WHERE id = :productId")
    suspend fun updateProductFeatured(productId: String, isFeatured: Boolean)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCart(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY orderId DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderId DESC")
    fun getOrdersByUserId(userId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET statusFa = :newStatus, vendorPhotoProofUrl = :photoProofUrl WHERE orderId = :orderId")
    suspend fun updateOrderStatusAndProof(orderId: String, newStatus: String, photoProofUrl: String?)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY id DESC")
    fun getTransactions(): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY id DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)
}

@Dao
interface SupportTicketDao {
    @Query("SELECT * FROM support_tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY id DESC")
    fun getTicketsByUserId(userId: String): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)

    @Query("UPDATE support_tickets SET adminReply = :reply, status = :newStatus WHERE id = :ticketId")
    suspend fun replyTicket(ticketId: String, reply: String, newStatus: String = "ANSWERED")
}

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY id DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)
}
