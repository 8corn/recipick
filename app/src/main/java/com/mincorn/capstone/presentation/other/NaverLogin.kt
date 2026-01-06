package com.mincorn.capstone.presentation.other

import android.content.Context
import android.util.Log
import com.mincorn.capstone.R
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.util.NidProfileCallback

fun signInNaver(context: Context, onResult: (String, String) -> Unit) {
    NidOAuth.requestLogin(context, object : NidOAuthCallback {
        override fun onSuccess() {
            // 로그인 성공 시 프로필 정보 요청
            fetchNaverProfile(onResult)
        }

        override fun onFailure(errorCode: String, errorDesc: String) {
            Log.e("NaverLogin", "로그인 실패 - 코드: $errorCode, 설명: $errorDesc")
        }
    })
}

private fun fetchNaverProfile(onResult: (String, String) -> Unit) {
    NidOAuth.getUserProfile(object : NidProfileCallback<NidProfile> {
        override fun onSuccess(result: NidProfile) {
            val nickname = result.profile.nickname.ifEmpty { "네이버 사용자" }
            val email = result.profile.email.ifEmpty { "noemail@naver.com" }

            onResult(nickname, email)
        }

        override fun onFailure(errorCode: String, errorDesc: String) {
            Log.e("NaverLogin", "프로필 호출 실패: $errorDesc")
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