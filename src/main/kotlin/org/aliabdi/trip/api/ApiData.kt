package org.aliabdi.trip.api

import io.micronaut.core.annotation.Introspected
import kotlinx.serialization.Serializable
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@Serializable
@Introspected
data class ReservationData(
    @get:NotNull
    val customerId: Long,
    @get:NotNull
    val tripId: Long,
    @get:NotNull
    @get:Positive
    val numOfSeats: Int
)

@Introspected
data class TripInformationDto (
    val tripId: Long,
    val tripFrom: String,
    val tripTo: String,
    val totalSeats: Int,
    var departureDate: Date,
    var reservations: List<ReservationDto>
)

@Introspected
data class ReservationDto (
    val customerName: String,
    val reservedSeats: Int
)