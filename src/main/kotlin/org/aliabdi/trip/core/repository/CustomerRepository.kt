package org.aliabdi.trip.core.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.aliabdi.trip.core.data.Customer

@Repository
interface CustomerRepository : CrudRepository<Customer, Long>