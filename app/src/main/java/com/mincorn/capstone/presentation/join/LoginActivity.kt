package com.mincorn.capstone.presentation.join

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.other.signInKakao
import com.mincorn.capstone.presentation.other.signInNaver
import com.mincorn.capstone.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val isFailState = mutableStateOf(false)

@Composable
fun LoginActivity(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val (id, setId) = remember { mutableStateOf("") }
    val (pw, setPw) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val activity = context as? FragmentActivity
    var backPressedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(authViewModel.loginSuccess) {
        if (authViewModel.loginSuccess) {
            navController.navigate("Recipick") {
                popUpTo("LoginActivity") {
                    inclusive = true
                }
            }
        }
    }

    Surface(
        color = Color.White
    ) {
        BackHandler {
            if (backPressedOnce) {
                activity?.finish()
            } else {
                backPressedOnce = true
                Toast.makeText(context, "뒤로가기 버튼을 두번 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }
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
                placeholder = {
                    Text(
                        "이메일을 입력해주세요."
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 30.dp)
                    .border(1.dp, Color(0xFF868686), RoundedCornerShape(19.dp)),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                ),
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
                placeholder = {
                    Text(
                        text = "비밀번호를 입력해주세요.",
                    )
                },
                visualTransformation = PasswordVisualTransformation('\u2022'),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 30.dp)
                    .border(1.dp, Color(0xFF868686), RoundedCornerShape(19.dp)),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                ),
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
                                    navController.navigate("Recipick") {
                                        popUpTo("LoginActivity") {
                                            inclusive = true
                                        }
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
                        signInKakao(context) { nickname, email ->
                            authViewModel.saveSocialUser(aka = nickname, email = email, provider = "kakao")
                        }
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
                        signInNaver(context) { nickname, email ->
                            authViewModel.saveSocialUser(aka = nickname, email = email, provider = "naver")
                        }
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
                    navController.navigate("JoinActivity")
                },
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            )
        }
    }
}

