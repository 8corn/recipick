package com.mincorn.capstone.join

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mincorn.capstone.MainActivity
import com.mincorn.capstone.R
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNaver(this)

        enableEdgeToEdge()
        setContent {
            Login()
        }
    }
}

private val isFailState = mutableStateOf(false)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun Login() {
    val context = LocalContext.current
    val (id, setId) = remember { mutableStateOf("") }
    val (pw, setPw) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .padding(top = 160.dp)
                    .size(140.dp)
            )

            Spacer(modifier = Modifier.height(140.dp))

            TextField(
                value = id,
                onValueChange = setId,
                placeholder = { Text("이메일을 입력해주세요.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 30.dp)
                    .border(1.dp, Color(0xFF868686), RoundedCornerShape(19.dp)),
                textStyle = TextStyle(fontSize = 20.sp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = Color(0xFF868686),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(22.dp))

            TextField(
                value = pw,
                onValueChange = setPw,
                placeholder = { Text("비밀번호를 입력해주세요.") },
                visualTransformation = PasswordVisualTransformation('\u2022'),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 30.dp)
                    .border(1.dp, Color(0xFF868686), RoundedCornerShape(19.dp)),
                textStyle = TextStyle(fontSize = 20.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = Color(0xFF868686),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(70.dp))

            Box(
                modifier = Modifier
                    .height(45.dp)
                    .width(291.dp)
                    .background(Color.Black, shape = RoundedCornerShape(8.dp))
                    .clickable {
                        keyboardController?.hide()
                        FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(id.trim(), pw.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                    if (context is LoginActivity) {
                                        context.finish()
                                    }
                                } else {
                                    isFailState.value = true
                                    val errorMessage = when (val e = task.exception) {
                                        is FirebaseAuthInvalidUserException -> "존재하지 않는 계정입니다."
                                        is FirebaseAuthInvalidCredentialsException -> "이메일 또는 비밀번호가 잘못되었습니다."
                                        else -> e?.localizedMessage ?: "로그인에 실패하였습니다."
                                    }

                                    Log.e("Login", "로그인 실패: $errorMessage", task.exception)
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "레시픽 로그인",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(23.dp))

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clickable {
                        signInKakao(context)
                    },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kakao_login_btn),
                    contentDescription = "kakao login",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(23.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clickable {
                        signInNaver(context)
                    },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.naver_login_btn),
                    contentDescription = "naver login",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "또는",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(300),
                    color = Color(0xFF868686),
                )
            )

            Spacer(modifier = Modifier.height(13.dp))

            ClickableText(
                modifier = Modifier
                    .padding(bottom = 67.dp),
                text = AnnotatedString("회원가입 하러가기"),
                onClick = {
                    val intent = Intent(context, JoinActivity::class.java)
                    context.startActivity(intent)
                },
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            )
        }
    }
}

fun signInKakao(context: Context) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            handleKakaoLoginResult(token, error, context)
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            handleKakaoLoginResult(token, error, context)
        }
    }
}

private fun handleKakaoLoginResult(token: OAuthToken?, error: Throwable?, context: Context) {
    if (error != null) {
        Log.e("KakaoLogin", "카카오 로그인 실패", error)
        return
    }

    if (token != null) {
        UserApiClient.instance.me { user, userError ->
            if (userError != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", userError)
                return@me
            }

            val aka = user?.kakaoAccount?.profile?.nickname ?: "사용자"
            val email = user?.kakaoAccount?.email ?: "noemail@kakao.com"

            val uid = email.hashCode().toString()
            val db = FirebaseFirestore.getInstance()

            db.collection("user").document(uid).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        val users = hashMapOf(
                            "aka" to aka,
                            "email" to email,
                            "provider" to "kakao"
                        )
                        db.collection("user").document(uid).set(users)
                            .addOnSuccessListener {
                                Log.d("KakaoLogin", "카카오 Firestore 저장 성공")
                            }
                            .addOnFailureListener {
                                Log.e("KakaoLogin", "카카오 Firestore 저장 실패", it)
                            }
                    }
                }
            FirebaseAuth.getInstance().signInAnonymously()
                .addOnSuccessListener {
                    saveOAuthUserToFirebase(
                        aka = aka,
                        email = email,
                        from = "kakao",
                        onSuccess = {
                            Log.d("KakaoLogin", "카카오 로그인 성공 (익명 + Firestore")
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            if (context is LoginActivity) {
                                context.finish()
                            }
                        },
                        onFailure = {
                            Log.e("KakaoLogin", "Firestore 저장 실패", it)
                        }
                    )
                }
                .addOnFailureListener { e ->
                    Log.e("KakaoLogin", "Firebase 익명 로그인 실패", e)
                    Toast.makeText(context, "네트워크 상태가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
        }
    }
}

fun signInNaver(context: Context) {
    NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
        override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(result: NidProfileResponse) {
                    val response = result.profile
                    val aka = response?.nickname ?: "네이버"
                    val email = response?.email ?: "noemail@naver.com"

                    val uid = email.hashCode().toString()
                    val db = FirebaseFirestore.getInstance()

                    db.collection("user").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                val user = hashMapOf(
                                    "aka" to aka,
                                    "email" to email,
                                    "provider" to "naver"
                                )
                                db.collection("user").document(uid).set(user)
                                    .addOnSuccessListener {
                                        Log.d("NaverLogin", "네이버 회원가입 성공")
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                        if (context is LoginActivity) {
                                            context.finish()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Log.e("NaverLogin", "Firebase 접근 실패", it)
                                    }
                            }
                        }

                    FirebaseAuth.getInstance().signInAnonymously()
                        .addOnSuccessListener {
                            saveOAuthUserToFirebase(aka, email, "naver", {
                                Log.d("NaverLogin", "네이버 로그인 성공")
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                                if (context is LoginActivity) {
                                    context.finish()
                                }
                            }, {
                                Log.e("NaverLogin", "Naver: Firestore 저장 실패", it)
                            })
                        }
                }

                override fun onError(errorCode: Int, message: String) {
                    Log.e("NaverLogin", "프로필 불러오기 에러: $message")
                    Toast.makeText(context, "네트워크 상태가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    Log.e("NaverLogin", "프로필 불러오기 실패: $message")
                }
            })
        }

        override fun onFailure(httpStatus: Int, message: String) {
            Log.e("NaverLogin", "네이버 로그인 실패: $message")
        }

        override fun onError(errorCode: Int, message: String) {
            Log.e("NaverLogin", "네이버 로그인 에러: $message")
            Toast.makeText(context, "네트워크 상태가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
        }
    })
}

fun initNaver(context: Context) {
    NaverIdLoginSDK.initialize(
        context,
        context.getString(R.string.naver_client_id),
        context.getString(R.string.naver_secret_id),
        context.getString(R.string.app_name),
    )
}

fun saveOAuthUserToFirebase(
    aka: String,
    email: String,
    from: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("user")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { result ->
            val uid = if (!result.isEmpty) {
                result.documents[0].id
            } else {
                FirebaseAuth.getInstance().currentUser?.uid
            }
            if (uid == null) {
                onFailure(Exception("UID 없음"))
                return@addOnSuccessListener
            }

            val user = hashMapOf(
                "aka" to aka,
                "email" to email,
                "provider" to from,
            )
            db.collection("user").document(uid)
                .set(user)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
        .addOnFailureListener { e -> onFailure(e) }
}