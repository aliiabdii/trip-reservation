package org.aliabdi.trip.api

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.aliabdi.trip.core.service.ReservationService
import javax.validation.Valid

@Validated
@Controller("/trip")
class TripController(
    private val reservationService: ReservationService
) {
    companion object {
        const val RESERVATION_SUCCESS_MESSAGE = "Reservation was made successfully"
        const val MAX_CAPACITY_EXCEED_MESSAGE = "Your requested amount of seats is not available"
        const val CANCELLATION_SUCCESS_MESSAGE = "Reservation was made successfully"
        const val NO_SUITABLE_RESERVATION_TO_CANCEL_MESSAGE = "No suitable reservation found for cancellation request"
    }

    @Post("/reserve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Reserve a specific amount of spots on a given trip for a given customer")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Not enough seats is available for the given trip"),
        ApiResponse(responseCode = "201", description = "Seats were reserved successfully"),
        ApiResponse(responseCode = "400", description = "Wrong or missing data in the request"),
        ApiResponse(responseCode = "404", description = "Trip or Customer not found in the system")
    ])
    fun reserve(@Valid @Body data: ReservationData): HttpResponse<String> {
        return if (reservationService.makeReservation(data)) HttpResponse.created(RESERVATION_SUCCESS_MESSAGE)
            else HttpResponse.ok(MAX_CAPACITY_EXCEED_MESSAGE)
    }

    @Post("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Cancels already reserved spots on a given trip for a given customer")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "No reservation found for the customer in the given trip"),
        ApiResponse(responseCode = "201", description = "Seats were cancelled successfully"),
        ApiResponse(responseCode = "400", description = "Wrong or missing data in the request"),
        ApiResponse(responseCode = "404", description = "Trip or Customer not found in the system")
    ])
    fun cancel(@Valid @Body data: ReservationData): HttpResponse<String> {
        return if (reservationService.cancelReservation(data)) HttpResponse.created(CANCELLATION_SUCCESS_MESSAGE)
            else HttpResponse.ok(NO_SUITABLE_RESERVATION_TO_CANCEL_MESSAGE)
    }

    @Get("/data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets data for all the trips with reservations in the system")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Data is provided in the response"),
        ApiResponse(responseCode = "500", description = "Internal Server Error")
    ])
    fun getReservationsData(): HttpResponse<List<TripInformationDto>>? {
        return HttpResponse.ok(reservationService.getReservationData())
    }
}