package com.mtjin.nomoneytrip.data.login.source.remote

import com.google.firebase.database.DatabaseReference
import com.kakao.auth.Session
import com.mtjin.nomoneytrip.data.login.User
import com.mtjin.nomoneytrip.utils.USER
import io.reactivex.Completable

class LoginRemoteDataSourceImpl(private val database: DatabaseReference) : LoginRemoteDataSource {
    override fun kakaoLogin(): Session = Session.getCurrentSession()
    override fun insertUser(user: User): Completable {
        return Completable.create { emitter ->
            database.child(USER).child(user.id).setValue(user).addOnSuccessListener {
                emitter.onComplete()
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }

    }
}