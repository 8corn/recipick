package com.mincorn.capstone.join

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.mincorn.capstone.MainActivity
import com.mincorn.capstone.R

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Login()
        }
    }
}

private val _isFailState = mutableStateOf(false)
val isFailState: State<Boolean> = _isFailState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun Login() {
    val context = LocalContext.current
    val (id, setId) = remember { mutableStateOf("") }
    val (pw, setPw) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val corner = RoundedCornerShape(8.dp)

    Scaffold(
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp,),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .padding(
                        bottom = 60.dp,
                        top = 60.dp
                    )
                    .size(140.dp)
            )
            
            TextField(
                value = id,
                onValueChange = setId,
                placeholder = { Text("아이디를 입력해주세요.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp)
                    .padding(top = 10.dp)
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

            TextField(
                value = pw,
                onValueChange = setPw,
                placeholder = { Text("비밀번호를 입력해주세요.") },
                visualTransformation = PasswordVisualTransformation('\u2022'),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 19.dp)
                    .height(52.dp)
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

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(id, pw)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                                if (context is LoginActivity) {
                                    context.finish()
                                }
                            } else {
                                _isFailState.value = true
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 44.dp),
                shape = corner,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                )
            ) {
                Text(
                    text = "레시픽 로그인",
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.kakao_login_btn),
                contentDescription = "kakao login",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 44.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.naver_login_btn),
                contentDescription = "naver login",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 44.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "또는",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(300),
                    color = Color(0xFF868686),
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            ClickableText(
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