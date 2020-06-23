package com.mtjin.nomoneytrip.views.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.mtjin.nomoneytrip.R
import com.mtjin.nomoneytrip.base.BaseActivity
import com.mtjin.nomoneytrip.databinding.ActivityLoginBinding
import com.mtjin.nomoneytrip.utils.Fb
import com.mtjin.nomoneytrip.views.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var auth: FirebaseAuth

    // 세션 콜백 구현
    private val sessionCallback: ISessionCallback = object : ISessionCallback {
        override fun onSessionOpened() {
            Log.i(TAG, "로그인 성공")
        }

        override fun onSessionOpenFailed(exception: KakaoException) {
            Log.e(TAG, "로그인 실패", exception)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        Session.getCurrentSession().addCallback(sessionCallback)
        initViewModelCallback()
        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            kakaoLogin.observe(this@LoginActivity, Observer {
                kakaoLogin.value?.addCallback(SessionCallback())
                kakaoLogin.value?.open(AuthType.KAKAO_LOGIN_ALL, this@LoginActivity)
            })
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    inner class SessionCallback : ISessionCallback {
        // 로그인에 성공한 상태
        override fun onSessionOpened() {
            requestMe()
        }

        // 로그인에 실패한 상태
        override fun onSessionOpenFailed(exception: KakaoException) {
            Log.e(LoginActivity.TAG, "onSessionOpenFailed : " + exception.message)
        }

        // 사용자 정보 요청
        private fun requestMe() {
            UserManagement.getInstance()
                .me(object : MeV2ResponseCallback() {
                    override fun onSessionClosed(errorResult: ErrorResult) {
                        Log.e(LoginActivity.TAG, "세션이 닫혀 있음: $errorResult")
                    }

                    override fun onFailure(errorResult: ErrorResult) {
                        Log.e(LoginActivity.TAG, "사용자 정보 요청 실패: $errorResult")
                    }

                    override fun onSuccess(result: MeV2Response) {
                        Log.i(LoginActivity.TAG, "사용자 아이디: " + result.id)
                        val email: String = "" + result.id + "@mujeon.com"
                        val password: String = "111111"
                        //구글이메일 로그인
                        googleAuth(email, password)
                    }
                })
        }

        fun googleAuth(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        Fb.user = auth.currentUser
                        val intent: Intent = Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                        startActivity(intent)
                        showToast("로그인 성공")
                        finish()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this@LoginActivity) { task ->
                                if (task.isSuccessful) {
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this@LoginActivity) { task ->
                                            if (task.isSuccessful) {
                                                Fb.user = auth.currentUser
                                                val intent: Intent = Intent(
                                                    this@LoginActivity,
                                                    MainActivity::class.java
                                                )
                                                startActivity(intent)
                                                showToast("로그인 성공")
                                                finish()
                                            } else {
                                                showToast("로그인 실패")
                                            }
                                        }
                                } else {
                                    showToast("로그인 실패")
                                }
                            }
                    }
                }
        }
    }

    companion object {
        const val TAG: String = "LoginActivityTAG"
    }
}
