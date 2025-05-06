package com.mincorn.capstone.recipick

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mincorn.capstone.R
import com.mincorn.capstone.RecipeStorage
import com.mincorn.capstone.SavedRecipe
import com.mincorn.capstone.main.Gemini

class PickRecipick : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Gemini.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val pickName = intent.getStringExtra("pickName") ?: ""
            Pick(pickName)
        }
    }
}

@Composable
fun Pick(pickName: String) {
    val result = remember { mutableStateOf("불러오는 중...") }
    val context = LocalContext.current

    LaunchedEffect(pickName) {
        val prompt = """
            $pickName 를 만들기 위해 필요한 재료를 먼저 알려주고,
            그 다음 줄부터는 그 재료를 사용한 요리 방법(레시피)를 설명해줘,
            줄 바꿈을 사용해서 재료와 레시피를 나워서 작성해줘.
        """.trimIndent()
        val response = Gemini.generateText(prompt)
        Log.d("PickRecipick", "response: $response")
        result.value = response
    }

    val (ingredients, recipe) = result.value.split("\n", limit = 2).let {
        if (it.size >= 2) it[0] to it[1] else it[0] to ""
    }

    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
            ) {
                Text(
                    text = pickName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = "save",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 13.dp)
                        .size(24.dp)
                        .clickable {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid

                            if (uid != null) {
                                saveRecipeToFirebase(
                                    uid,
                                    SavedRecipe(
                                        name = pickName,
                                        ingredients = ingredients,
                                        recipe = recipe,
                                        image = R.drawable.vmon,

                                    )
                                )
                            }
                            RecipeStorage.add(SavedRecipe(pickName, ingredients, recipe, R.drawable.vmon))
                            Toast.makeText(context, "저장공간에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686),
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vmon),
                    contentDescription = "food image",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                        .size(280.dp)
                )

                Text(
                    text = "필요한 재료",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                )

                Text(
                    text = ingredients,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "레시픽",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )

                Text(
                    text = recipe,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun saveRecipeToFirebase (uid: String, recipes: SavedRecipe) {
    val db = FirebaseFirestore.getInstance()
    db.collection("user")
        .document(uid)
        .collection("storage")
        .add(recipes)
        .addOnSuccessListener {
            Log.d("Firebase", "레시피 저장 성공")
        }
        .addOnFailureListener {
            Log.e("Firebase", "레시피 저장 실패", it)
        }
}

fun loadSavedRecipeFromFirebase (uid: String, onComplete: (List<SavedRecipe>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("user")
        .document(uid)
        .collection("storage")
        .get()
        .addOnSuccessListener { result ->
            val recipes = result.mapNotNull { doc ->
                doc.toObject(SavedRecipe::class.java)
            }
            onComplete(recipes)
        }
        .addOnFailureListener {
            Log.e("Firebase", "레시피 불러오기 실패", it)
            onComplete(emptyList())
        }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Pick(pickName = "제육볶음")
}

