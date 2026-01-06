package com.mincorn.capstone.presentation.other

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

fun signInKakao(context: Context, onResult: (String, String) -> Unit) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error == null && token != null) {
            UserApiClient.instance.me { user, _ ->
                val aka = user?.kakaoAccount?.profile?.nickname ?: "사용자"
                val email = user?.kakaoAccount?.email ?: "noemail@kakao.com"
                onResult(aka, email)
            }
        }
    }
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}