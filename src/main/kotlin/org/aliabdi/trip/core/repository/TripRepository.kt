package org.aliabdi.trip.core.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.aliabdi.trip.core.data.Trip

@Repository
interface TripRepository : CrudRepository<Trip, Long>