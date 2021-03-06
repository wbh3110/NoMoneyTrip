package com.mtjin.nomoneytrip.data.reservation_history.source

import com.mtjin.nomoneytrip.data.reservation.Reservation
import com.mtjin.nomoneytrip.data.reservation_history.ReservationProduct
import io.reactivex.Completable
import io.reactivex.Flowable

interface ReservationHistoryRepository {
    fun requestReservations(): Flowable<List<ReservationProduct>>
    fun deleteReservation(reservation: Reservation): Completable
    fun updateReservationCancel(reservationProduct: ReservationProduct): Completable
    fun sendFCM(reservationProduct: ReservationProduct)
}