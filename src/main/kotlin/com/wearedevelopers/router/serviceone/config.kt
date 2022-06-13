package com.wearedevelopers.router.serviceone

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RoutingConfig {

    @Bean
    fun kycServiceProxyClient(
        @Value("\${routing.host}") host: String,
        restTemplate: RestTemplate,
    ) = HttpServletProxyClient(host, restTemplate,)

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}