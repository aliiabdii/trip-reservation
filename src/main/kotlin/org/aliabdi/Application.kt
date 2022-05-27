package org.aliabdi

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*

@OpenAPIDefinition(
    info = Info(
            title = "trip",
            version = "1.0"
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut
            .build(null)
            .mainClass(Application.javaClass)
            .banner(false)
            .start()
    }
}

