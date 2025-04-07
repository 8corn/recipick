package com.mincorn.capstone.join

import android.os.Bundle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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
    val (pw, setPw) = remember { mutableStateOf("") }
    val (pwCheck, setPwCheck) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val (pwErrorText, setPwErrorText) = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
        ) {
            Text(
                text = "회원가입",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
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

        Column (
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
                text = "아이디",
                fontSize = 15.sp,
                color = Color(0xFF868686),
                modifier = Modifier
                    .padding(top = 19.dp)
            )
            TextField(
                value = id,
                onValueChange = setId,
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
                text = "비밀번호",
                fontSize = 15.sp,
                color = Color(0xFF868686),
                modifier = Modifier
                    .padding(top = 19.dp)
            )
            TextField(
                value = pw,
                onValueChange = setPw,
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
                onValueChange = setPwCheck,
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

            if (pwErrorText !=null) {
                Text(
                    text = pwErrorText,
                    color = Color.Red,
                    fontSize = 6.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Box (
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 43.dp)
                ) {
                    Button(
                        onClick = {
                            if (pw != pwCheck) {
                                setPwErrorText("비밀번호가 일치하지 않습니다.")
                                return@Button
                            } else {
                                setPwErrorText(null)
                            }
                            val uid = System.currentTimeMillis().toString()
                            val user = hashMapOf(
                                "aka" to aka,
                                "id" to id,
                                "pw" to pw
                            )
                            val db = Firebase.firestore
                            db.collection("user").document(uid).set(user)
                                .addOnSuccessListener {
                                    keyboardController?.hide()
                                }
                                .addOnFailureListener{e ->
                                    Toast.makeText(context, "회원가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
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