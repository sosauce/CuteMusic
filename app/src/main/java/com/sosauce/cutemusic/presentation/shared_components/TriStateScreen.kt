@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi

//@Composable
//fun <T: TriStateUi> TriStateScreen(
//    state: T,
//    successContent: @Composable (data: T) -> Unit
//) {
//    when(state) {
//        is TriStateUi.Loading -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                LoadingIndicator(color = LoadingIndicatorDefaults.containedIndicatorColor)
//            }
//        }
//        is TriStateUi.Success<*> -> { successContent(state.data as T) }
//        is TriStateUi.Error -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(state.message)
//            }
//        }
//    }
//}