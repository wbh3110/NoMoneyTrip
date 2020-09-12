package com.mtjin.nomoneytrip.views.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.nomoneytrip.base.BaseViewModel
import com.mtjin.nomoneytrip.data.community.UserReview
import com.mtjin.nomoneytrip.data.community.source.CommunityRepository
import com.mtjin.nomoneytrip.data.reservation_history.ReservationProduct
import com.mtjin.nomoneytrip.utils.SingleLiveEvent
import com.mtjin.nomoneytrip.utils.TAG
import com.mtjin.nomoneytrip.utils.getTimestamp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class CommunityViewModel(private val repository: CommunityRepository) : BaseViewModel() {

    private val _goTourHistory = SingleLiveEvent<List<ReservationProduct>>()
    private val _goTourNoHistory = SingleLiveEvent<Unit>()
    private val _userReviewList = MutableLiveData<List<UserReview>>()

    val goTourHistory: LiveData<List<ReservationProduct>> get() = _goTourHistory
    val goTourNoHistory: LiveData<Unit> get() = _goTourNoHistory
    val userReviewList: LiveData<List<UserReview>> get() = _userReviewList

    fun goReview() {
        compositeDisposable.add(
            repository.requestMyReservations()
                .map { it.filter { item -> !item.reservation.reviewed && item.reservation.endDateTimestamp <= getTimestamp() && item.reservation.state } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        if (it.isEmpty()) _goTourNoHistory.call()
                        else _goTourHistory.value = it

                    },
                    onError = {
                        Log.d(TAG, "CommunityViewModel goReview() -> $it")
                    }
                )
        )
    }

    fun requestReviews() {
        compositeDisposable.add(
            repository.requestReviews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        _userReviewList.value = it
                    },
                    onError = {
                        Log.d(TAG, "CommunityViewModel requestReviews() -> $it")
                    }
                )
        )
    }

    fun updateReviewRecommend(userReview: UserReview) {
        compositeDisposable.add(
            repository.updateReviewRecommend(userReview)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {},
                    onError = { Log.d(TAG, it.toString()) }
                )
        )
    }
}