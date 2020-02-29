package com.demo

import io.kotlintest.shouldBe
import io.r2dbc.spi.ConnectionFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.StepVerifier.FirstStep
import java.io.IOException
import java.util.*
import java.util.function.Consumer

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [DemoApplication::class])
open class CustomerRepositoryIntegrationTests {

    @Autowired
    val connectionFactory: ConnectionFactory? = null

    @Autowired
    val databaseClient: DatabaseClient? = null

    @Autowired
    var customers: CustomerRepository? = null

    @Autowired
    var database: DatabaseClient? = null

    @Before
    fun setUp() {
        Hooks.onOperatorDebug()
        val statements = Arrays.asList( //
            "DROP TABLE IF EXISTS customer;",
            "CREATE TABLE customer ( id SERIAL PRIMARY KEY, firstname VARCHAR(100) NOT NULL, lastname VARCHAR(100) NOT NULL);"
        )
        statements.forEach(Consumer { it: String? ->
            database!!.execute(it) //
                .fetch() //
                .rowsUpdated() //
                .`as`<FirstStep<Int>> { publisher: Mono<Int?>? ->
                    StepVerifier.create(
                        publisher
                    )
                } //
                .expectNextCount(1) //
                .verifyComplete()
        })
    }

    @Test
    @Throws(IOException::class)
    fun executesFindAll() {
        val dave = Customer(null, "Dave", "Matthews")
        val carter = Customer(null, "Carter", "Beauford")
        insertCustomers(dave, carter)
        StepVerifier.create(customers!!.findAll())
            .assertNext { it shouldBe dave } //
            .assertNext { it shouldBe  carter } //
            .verifyComplete()
    }

    @Test
    @Throws(IOException::class)
    fun executesAnnotatedQuery() {
        val dave = Customer(null, "Dave", "Matthews")
        val carter = Customer(null, "Carter", "Beauford")
        insertCustomers(dave, carter)

        StepVerifier.create(customers!!.findByLastname("Matthews"))
            .assertNext { it shouldBe dave }
            .verifyComplete()
    }

    private fun insertCustomers(vararg customers: Customer) {
        StepVerifier.create(this.customers!!.saveAll(Arrays.asList(*customers))) //
            .expectNextCount(2) //
            .verifyComplete()
    }
}
