package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    currentUser: FirebaseUser?,
    onDismiss: () -> Unit,
    onAuthSuccess: (FirebaseUser) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = remember { runCatching { FirebaseAuth.getInstance() }.getOrNull() }

    var isSignUp by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentUser != null) "حساب کاربری گل آریس" else if (isSignUp) "ثبت‌نام در گل آریس" else "ورود به حساب کاربری",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = BotanicalGreen
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentUser != null) {
                    // Signed In View
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(BotanicalGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BotanicalGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser.displayName.takeIf { !it.isNullOrBlank() } ?: "کاربر گل آریس",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = currentUser.email ?: "بدون ایمیل",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = HeritageGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "✨ کاربر تایید شده گل آریس (Golarys VIP)",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            auth?.signOut()
                            onSignOut()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "خروج از حساب کاربری", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Sign In / Sign Up Form
                    if (isSignUp) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("نام و نام خانوادگی", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("آدرس ایمیل", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("رمز عبور", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = err,
                            color = ErrorRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = BotanicalGreen)
                    } else {
                        Button(
                            onClick = {
                                if (emailInput.isBlank() || passwordInput.isBlank()) {
                                    errorMessage = "لطفاً ایمیل و رمز عبور را وارد کنید"
                                    return@Button
                                }
                                if (auth == null) {
                                    errorMessage = "سرویس Firebase در دسترس نیست."
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                if (isSignUp) {
                                    auth.createUserWithEmailAndPassword(emailInput.trim(), passwordInput)
                                        .addOnSuccessListener { result ->
                                            isLoading = false
                                            result.user?.let { u ->
                                                onAuthSuccess(u)
                                                onDismiss()
                                            }
                                        }
                                        .addOnFailureListener { exc ->
                                            isLoading = false
                                            errorMessage = "خطا در ثبت‌نام: ${exc.localizedMessage}"
                                        }
                                } else {
                                    auth.signInWithEmailAndPassword(emailInput.trim(), passwordInput)
                                        .addOnSuccessListener { result ->
                                            isLoading = false
                                            result.user?.let { u ->
                                                onAuthSuccess(u)
                                                onDismiss()
                                            }
                                        }
                                        .addOnFailureListener { exc ->
                                            isLoading = false
                                            errorMessage = "خطا در ورود: ${exc.localizedMessage}"
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isSignUp) "ثبت‌نام حساب جدید" else "ورود به Golarys",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Credential Manager / Google Sign In Button
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    try {
                                        val credentialManager = CredentialManager.create(context)
                                        val googleIdOption = GetGoogleIdOption.Builder()
                                            .setFilterByAuthorizedAccounts(false)
                                            .setServerClientId("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
                                            .setAutoSelectEnabled(false)
                                            .build()

                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(googleIdOption)
                                            .build()

                                        val result = credentialManager.getCredential(context = context, request = request)
                                        val credential = result.credential
                                        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                                            if (auth != null) {
                                                auth.signInWithCredential(firebaseCredential)
                                                    .addOnSuccessListener { authResult ->
                                                        isLoading = false
                                                        authResult.user?.let { u ->
                                                            onAuthSuccess(u)
                                                            onDismiss()
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        isLoading = false
                                                        errorMessage = "ورود با گوگل ناموفق بود: ${e.localizedMessage}"
                                                    }
                                            } else {
                                                isLoading = false
                                                errorMessage = "سرویس Firebase در دسترس نیست"
                                            }
                                        } else {
                                            isLoading = false
                                            errorMessage = "نوع اعتبارنامه‌ی گوگل معتبر نیست"
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = "خطا در فراخوانی ورود با گوگل (Credential Manager): ${e.localizedMessage}"
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, HeritageGold)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = HeritageGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ورود سریع با گوگل (Credential Manager)",
                                color = DeepNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        isSignUp = !isSignUp
                        errorMessage = null
                    }) {
                        Text(
                            text = if (isSignUp) "قبلاً حساب کاربری دارید؟ ورود" else "حساب کاربری ندارید؟ ثبت‌نام",
                            fontSize = 12.sp,
                            color = BotanicalGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
