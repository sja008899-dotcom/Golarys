package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<FlowerProductEntity>>

    @Query("SELECT * FROM products WHERE isWishlisted = 1")
    fun getWishlistedProducts(): Flow<List<FlowerProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<FlowerProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: FlowerProductEntity)

    @Query("UPDATE products SET isWishlisted = :isWishlisted WHERE id = :productId")
    suspend fun updateWishlistStatus(productId: String, isWishlisted: Boolean)
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET statusFa = :newStatus, vendorPhotoProofUrl = :photoProofUrl WHERE orderId = :orderId")
    suspend fun updateOrderStatusAndProof(orderId: String, newStatus: String, photoProofUrl: String?)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY id DESC")
    fun getTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)
}
