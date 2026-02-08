package com.mincorn.capstone.presentation.recipick

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.other.RecentSearchTag
import com.mincorn.capstone.presentation.viewmodel.SearchViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SearchRecipick(
    navController: NavController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val (query, setQuery) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val recentSearches by searchViewModel.recentSearches.collectAsStateWithLifecycle()

    val performSearch = { searchQuery: String ->

        val cleanQuery = searchQuery.trim()

        if (cleanQuery.isNotBlank()) {
            searchViewModel.addSearchKeyword(cleanQuery)

            val encodedQuery = URLEncoder.encode(cleanQuery, StandardCharsets.UTF_8.toString())

            keyboardController?.hide()
            navController.navigate("SearchMenu/$encodedQuery")
        }
    }

    val onSearchAction = {
        performSearch(query)
    }

    Surface (
        color = Color.White
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back_arrow),
                        contentDescription = "back",
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(28.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                navController.popBackStack()
                            }
                    )

                    TextField(
                        value = query,
                        onValueChange = setQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF868686),
                                shape = RoundedCornerShape(40.dp)
                            ),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "원하는 레시피를 검색해주세요.",
                                color = Color(0xFF868686),
                                modifier = Modifier
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onSearchAction()
                                    },
                                tint = Color(0xFF868686)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearchAction()
                            }
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            cursorColor = Color(0xFF868686),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFF868686),
            )

            Column (
                modifier = Modifier
                    .padding(horizontal = 25.dp)
            ) {
                Text(
                    text = "최근 검색어",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                FlowRow (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = Int.MAX_VALUE
                ) {
                    recentSearches.forEach { searchItem ->
                        RecentSearchTag(
                            text = searchItem,
                            onClick = {
                                setQuery(searchItem)
                                performSearch(searchItem)
                            },
                            onDelete = {
                                searchViewModel.removeSearchKeyword(searchItem)
                            }
                        )
                    }
                }
            }
        }
    }
}