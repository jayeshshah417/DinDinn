package com.jayesh.dindinntest

import com.jayesh.dindinntest.adapters.OrderAdapter
import com.jayesh.dindinntest.orderModels.Orders
import org.junit.Test
import kotlin.time.ExperimentalTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {
    @Test
    fun is_not_already_expired() {
        val orderAdapter=OrderAdapter(ArrayList<Orders>())
        assert(orderAdapter.is_not_already_expired(1))
    }

    @ExperimentalTime
    @Test
    fun is_in_alertZone_not_expired() {
        val orderAdapter=OrderAdapter(ArrayList<Orders>())
        assert(!orderAdapter.is_in_alertZone_not_expired("2021-09-13T23:24+00Z","2021-09-13T23:25+00Z","2021-09-13T23:24+00Z"))
    }

    @ExperimentalTime
    @Test
    fun is_not_in_alertZone_not_expired() {
        val orderAdapter=OrderAdapter(ArrayList<Orders>())
        assert(!orderAdapter.is_in_alertZone_not_expired("2021-09-13T23:24+00Z","2021-09-13T23:25+00Z","2021-09-13T23:23+00Z"))
    }
    @ExperimentalTime
    @Test
    fun is_not_in_alertZone_expired() {
        val orderAdapter=OrderAdapter(ArrayList<Orders>())
        assert(!orderAdapter.is_in_alertZone_not_expired("2021-09-13T23:23+00Z","2021-09-13T23:24+00Z","2021-09-13T23:24+00Z"))
    }

}
