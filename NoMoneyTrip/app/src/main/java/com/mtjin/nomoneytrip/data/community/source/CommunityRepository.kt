package com.mtjin.nomoneytrip.data.community.source

import com.mtjin.nomoneytrip.data.community.UserReview
import com.mtjin.nomoneytrip.data.reservation_history.ReservationProduct
import io.reactivex.Completable
import io.reactivex.Single

interface CommunityRepository {
    fun requestMyReservations(): Single<List<ReservationProduct>>
    fun requestReviews(cityCode: String): Single<List<UserReview>>
    fun updateReviewRecommend(userReview: UserReview): Completable
}