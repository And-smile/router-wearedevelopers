package com.wearedevelopers.router.serviceone

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class RoutingService(
    @Value("\${routing.percentage:100}") val rolloutPercent: Int,
) {
    fun isRoutingNeeded(): Boolean = Random.nextInt(0, 100) < rolloutPercent
}