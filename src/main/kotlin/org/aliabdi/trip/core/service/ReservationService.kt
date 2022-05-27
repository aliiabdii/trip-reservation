package org.aliabdi.trip.core.service

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Singleton
import org.aliabdi.trip.api.ReservationData
import org.aliabdi.trip.api.ReservationDto
import org.aliabdi.trip.api.TripInformationDto
import org.aliabdi.trip.core.ReservationType
import org.aliabdi.trip.core.data.Customer
import org.aliabdi.trip.core.data.Reservation
import org.aliabdi.trip.core.data.Trip
import org.aliabdi.trip.core.repository.CustomerRepository
import org.aliabdi.trip.core.repository.ReservationRepository
import org.aliabdi.trip.core.repository.TripRepository
import org.apache.commons.lang3.exception.ExceptionUtils
import org.postgresql.util.PSQLException
import javax.persistence.PersistenceException

@Singleton
open class ReservationService(
    private val customerRepository: CustomerRepository,
    private val tripRepository: TripRepository,
    private val reservationRepository: ReservationRepository
) {
    /**
     * Make a reservation for a given customer and trip.
     * @param reservationData contains the data for reservation
     */
    open fun makeReservation(reservationData: ReservationData): Boolean {
        val trip: Trip = getTrip(reservationData.tripId)
        val customer: Customer = getCustomer(reservationData.customerId)

        val reservation = Reservation(
            trip = trip,
            customer = customer,
            numberOfSeats = reservationData.numOfSeats,
            type = ReservationType.RESERVATION
        )

        try {
            reservationRepository.save(reservation)
        } catch (persistenceException: PersistenceException) {
            //  If PSQL trigger failed, it means not enough seats are available
            return if (ExceptionUtils.getRootCause(persistenceException) is PSQLException) false
            else throw persistenceException
        }

        return true
    }

    /**
     * Cancel a reservation for a given customer and trip.
     * @param reservationData contains the data for reservation
     */
    open fun cancelReservation(reservationData: ReservationData): Boolean {
        val trip: Trip = getTrip(reservationData.tripId)
        val customer: Customer = getCustomer(reservationData.customerId)

        // Check whether the number of seats to be cancelled is valid
        val currentCustomerReservedSeats = reservationRepository.getTotalReservedSeatsForCustomerAndTrip(customer.id, trip.id)
        if (currentCustomerReservedSeats.isEmpty || currentCustomerReservedSeats.get() < reservationData.numOfSeats) {
            return false
        }

        val reservation = Reservation(
            trip = trip,
            customer = customer,
            numberOfSeats = (-1) * reservationData.numOfSeats, // Cancellation will have negative seats
            type = ReservationType.CANCELLATION
        )
        reservationRepository.save(reservation)
        return true
    }

    open fun getReservationData(): List<TripInformationDto> {
        val allReservations: Iterable<Reservation> = reservationRepository.findAll()

        return allReservations.groupBy { r -> r.trip }.map { (trip, reservations) ->
            TripInformationDto(
                tripId = trip.id,
                tripFrom = trip.from.name,
                tripTo = trip.to.name,
                totalSeats = trip.totalSeats,
                departureDate = trip.departureDate,
                reservations = reservations.groupBy { r2 -> r2.customer }.map { (customer, customerReservations) ->
                    ReservationDto(
                        customer.name,
                        customerReservations.sumOf { r3 -> r3.numberOfSeats }
                    )
                }.filter { dto -> dto.reservedSeats > 0 }.toList()
            )
        }.toList()
    }

    /**
     * Retrieve a trip by ID, throw if not found
     *
     * @param tripId ID of the trip
     * @return Trip
     */
    private fun getTrip(tripId: Long): Trip {
        return tripRepository.findById(tripId)
            .orElseThrow { throw HttpStatusException(HttpStatus.NOT_FOUND, "No trip found for id $tripId") }
    }

    /**
     * Retrieve a customer by ID, throw if not found
     *
     * @param customerId ID of the customer
     * @return Customer
     */
    private fun getCustomer(customerId: Long): Customer {
        return customerRepository.findById(customerId)
            .orElseThrow { throw HttpStatusException(HttpStatus.NOT_FOUND, "No customer found for id $customerId") }
    }
}