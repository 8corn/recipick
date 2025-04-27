package com.mincorn.capstone.join

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mincorn.capstone.MainActivity
import com.mincorn.capstone.join.ui.theme.CapstoneTheme

class JoinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Join()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Join() {
    val (aka, setAka) = remember { mutableStateOf("") }
    val (id, setId) = remember { mutableStateOf("") }
    val (idErrorText, setIdErrorText) = remember { mutableStateOf<String?>(null) }
    val (pw, setPw) = remember { mutableStateOf("") }
    val (pwCheck, setPwCheck) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val (pwErrorText, setPwErrorText) = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
            ) {
                Text(
                    text = "회원가입",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
            ) {
                Text(
                    text = "닉네임",
                    fontSize = 15.sp,
                    color = Color(0xFF868686),
                    modifier = Modifier
                        .padding(top = 39.dp)
                )
                TextField(
                    value = aka,
                    onValueChange = setAka,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 2.dp)
                        .border(1.dp, Color(0xFF868686), RoundedCornerShape(19.dp)),
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
                Text(
                    text = "이메일",
                    fontSize = 15.sp,
                    color = Color(0xFF868686),
                    modifier = Modifier
                        .padding(top = 19.dp)
                )
                TextField(
                    value = id,
                    onValueChange = {
                        setId(it)
                        setIdErrorText(
                            if (isValidEmail(it)) null else "올바른 이메일 형식이 아닙니다."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 2.dp)
                        .border(
                            1.dp,
                            if (idErrorText != null) Color.Red else Color(0xFF868686),
                            RoundedCornerShape(19.dp)
                        ),
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
                if (idErrorText != null) {
                    Text(
                        text = idErrorText,
                        color = Color.Red,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 10.dp)
                    )
                }
                Text(
                    text = "비밀번호",
                    fontSize = 15.sp,
                    color = Color(0xFF868686),
                    modifier = Modifier
                        .padding(top = 19.dp)
                )
                TextField(
                    value = pw,
                    onValueChange = setPw,
                    visualTransformation = PasswordVisualTransformation('\u2022'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 2.dp)
                        .border(1.dp, Color(0xFF868686), RoundedCornerShape(19.dp)),
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
                Text(
                    text = "비밀번호 확인",
                    fontSize = 15.sp,
                    color = Color(0xFF868686),
                    modifier = Modifier
                        .padding(top = 19.dp)
                )
                TextField(
                    value = pwCheck,
                    visualTransformation = PasswordVisualTransformation('\u2022'),
                    onValueChange = {
                        setPwCheck(it)
                        setPwErrorText(if (pw != it) "비밀번호가 일치하지 않습니다." else null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 2.dp)
                        .border(
                            1.dp,
                            if (pwErrorText != null) Color.Red else Color(0xFF868686),
                            RoundedCornerShape(19.dp)
                        ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        cursorColor = Color(0xFF868686),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    isError = pwErrorText != null
                )
                Text(
                    text = "비밀번호는 소문자 및 순자로 8자리 이상 입력해주세요.",
                    color = Color(0xFF868686),
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(top = 3.dp, start = 10.dp)
                )

                if (pwErrorText != null) {
                    Text(
                        text = pwErrorText,
                        color = Color.Red,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 10.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 43.dp)
                    ) {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                if (!isValidEmail(id)) {
                                    setIdErrorText("올바른 이메일 형식이 아닙니다.")
                                    Toast.makeText(context, "이메일을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (pw != pwCheck) {
                                    setPwErrorText("비밀번호가 일치하지 않습니다.")
                                    Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                saveUserToFirebase(aka, id, pw,
                                    onSuccess = {
                                        Toast.makeText(context, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                    },
                                    onFailure = { e ->
                                        Toast.makeText(context, "회원가입에 실패하였습니다. ${e.message}", Toast.LENGTH_LONG).show()
                                        Log.e("Join", "회원가입 실패", e)
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            )
                        ) {
                            Text(
                                text = "회원가입 완료",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun saveUserToFirebase(
    aka: String,
    id: String,
    pw: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = Firebase.firestore
    db.collection("user")
        .whereEqualTo("id", id)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                onFailure(Exception("이미 사용중인 이메일입니다."))
            } else {
                val uid = System.currentTimeMillis().toString()
                val user = hashMapOf(
                    "aka" to aka,
                    "id" to id,
                    "pw" to pw,
                )
                db.collection("user").document(uid).set(user)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            }
        }
        .addOnFailureListener { e -> onFailure(e) }
}