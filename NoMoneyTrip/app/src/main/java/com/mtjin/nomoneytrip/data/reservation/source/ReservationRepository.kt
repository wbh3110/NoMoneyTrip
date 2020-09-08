package com.mtjin.nomoneytrip.data.reservation.source

import com.mtjin.nomoneytrip.data.reservation.Reservation
import io.reactivex.Completable

interface ReservationRepository {
    fun insertReservation(reservation: Reservation): Completable
}