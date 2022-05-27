package org.aliabdi.trip.test

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.serialization.json.Json
import org.aliabdi.Application
import org.aliabdi.trip.api.ReservationData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows

@MicronautTest(application = Application::class)
class IntegrationTest (
    @Client("/trip") private var client : HttpClient
) {

    @Test
    fun testNonPositiveSeats() {
        assertThrows (
            HttpClientResponseException::class.java
        ) { exchange(uri = "/reserve", customerId = 1, tripId = 1, numOfSeats = 0) }
    }

    @Test
    fun testInvalidTrip() {
        assertThrows (
            HttpClientResponseException::class.java
        ) { exchange(uri = "/reserve", customerId = 1, tripId = 5000, numOfSeats = 1) }
    }

    @Test
    fun testInvalidCustomer() {
        assertThrows (
            HttpClientResponseException::class.java
        ) { exchange(uri = "/reserve", customerId = 5000, tripId = 1, numOfSeats = 1) }
    }

    @Test
    fun testSuccessfulReservation() {
        val result = exchange(uri = "/reserve", customerId = 1, tripId = 1, numOfSeats = 1)
        Assertions.assertEquals(HttpStatus.CREATED, result.status)
    }

    @Test
    fun testSuccessfulCancellation() {
        exchange(uri = "/reserve", customerId = 2, tripId = 2, numOfSeats = 3)

        val result = exchange(uri = "/cancel", customerId = 2, tripId = 2, numOfSeats = 1)
        Assertions.assertEquals(HttpStatus.CREATED, result.status)
    }

    @Test
    fun testInvalidCancellation() {
        exchange(uri = "/reserve", customerId = 3, tripId = 3, numOfSeats = 3)

        val result = exchange("/cancel", customerId = 3, tripId = 3, numOfSeats = 5)
        Assertions.assertEquals(HttpStatus.OK, result.status)
    }

    @Test
    fun testNoSeatsAvailable_simple() {
        exchange(uri = "/reserve", customerId = 4, tripId = 4, numOfSeats = 5)

        val result = exchange(uri = "/reserve", customerId = 5, tripId = 4, numOfSeats = 100)
        Assertions.assertEquals(HttpStatus.OK, result.status)
    }

    @Test
    fun testNoSeatsAvailable_complex() {
        // Trip 6 has 43 seats
        exchange(uri = "/reserve", customerId = 6, tripId = 6, numOfSeats = 5)
        exchange(uri = "/reserve", customerId = 7, tripId = 6, numOfSeats = 10)
        val result1 = exchange(uri = "/reserve", customerId = 8, tripId = 6, numOfSeats = 30)
        Assertions.assertEquals(HttpStatus.OK, result1.status)

        exchange(uri = "/cancel", customerId = 7, tripId = 6, numOfSeats = 5)
        val result2 = exchange(uri = "/reserve", customerId = 8, tripId = 6, numOfSeats = 30)
        Assertions.assertEquals(HttpStatus.CREATED, result2.status)
    }

    private fun exchange(uri: String, customerId: Long, tripId: Long, numOfSeats: Int) : HttpResponse<String> {
        val request = buildReservationRequest(
            uri, ReservationData(customerId = customerId, tripId = tripId, numOfSeats = numOfSeats)
        )
        return client.toBlocking().exchange(request, String::class.java)
    }

    private fun buildReservationRequest(uri: String, data : ReservationData) : HttpRequest<String> {
        val payload : String = Json.encodeToString(ReservationData.serializer(), data)
        return HttpRequest.POST(uri, payload)
            .header(HttpHeaders.ACCEPT, "application/json")
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
    }

}
