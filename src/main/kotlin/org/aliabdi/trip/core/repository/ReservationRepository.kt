package org.aliabdi.trip.core.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.aliabdi.trip.core.data.Reservation
import java.util.*

@Repository
interface ReservationRepository : CrudRepository<Reservation, Long> {
    @Query("SELECT SUM(r.numberOfSeats) FROM Reservation r WHERE r.customer.id = :customerId AND r.trip.id = :tripId")
    fun getTotalReservedSeatsForCustomerAndTrip(customerId: Long, tripId: Long): Optional<Long>
}