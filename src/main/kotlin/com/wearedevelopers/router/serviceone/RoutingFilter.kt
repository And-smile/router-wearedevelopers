package com.wearedevelopers.router.serviceone

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class RoutingFilter @Autowired internal constructor(
    val httpProxy: HttpServletProxyClient,
    val routingService: RoutingService,
    @Value("\${routing.type:DEFAULT}") val routingType: RoutingType,
) : HttpFilter() {

    companion object {
        private val API = "/echo"
    }

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val readableRequest = CachedBodyHttpServletRequest(request);
        when {
            API == readableRequest.requestURI -> routeToServiceTwo(readableRequest, response, chain)
            else -> chain.doFilter(readableRequest, response)
        }
    }

    private fun routeToServiceTwo(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        when (routingType) {
            RoutingType.DEFAULT -> defaultRouting(request, response, chain)
            RoutingType.FALLBACK -> routingWithFallback(request, response, chain)
            RoutingType.PARALLEL -> parallelProcessing(request, response, chain)
        }
    }

    private fun defaultRouting(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        if (routingService.isRoutingNeeded()) {
            val responseWrapper = httpProxy.forward(request, response)
            responseWrapper.response.copyTo(response, emptyList())
            return
        }
        chain.doFilter(request, response)
    }

    private fun routingWithFallback(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        if (routingService.isRoutingNeeded()) {
            val responseWrapper = httpProxy.forward(request, response)
            if (!responseWrapper.success) {
                chain.doFilter(request, response)
                return
            }
            responseWrapper.response.copyTo(response, emptyList())
        } else {
            chain.doFilter(request, response)
        }
    }

    private fun parallelProcessing(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        httpProxy.forward(request, response)
        chain.doFilter(request, response)
    }
}

enum class RoutingType {
    DEFAULT,
    PARALLEL,
    FALLBACK
}
