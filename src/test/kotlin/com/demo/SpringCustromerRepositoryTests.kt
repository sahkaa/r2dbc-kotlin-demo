package com.demo

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec
import io.kotlintest.spring.SpringListener
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(DemoApplication::class)])
class SpringCustromerRepositoryTests : StringSpec() {

    override fun listeners() = listOf(SpringListener)

    @Autowired
    val connectionFactory: ConnectionFactory? = null

    @Autowired
    val databaseClient: DatabaseClient? = null

    @Autowired
    var customers: CustomerRepository? = null

    @Autowired
    var database: DatabaseClient? = null

    init {
        "Spring Extension" {
            "have wired up the bean" {
                customers shouldNotBe null
            }
        }
    }
}
