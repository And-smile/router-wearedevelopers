package com.wearedevelopers.router.servicetwo

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ExtractedServiceController {
    private val log = LoggerFactory.getLogger(javaClass)
    @PostMapping("/service-two/echo")
    fun getNUmber(@RequestBody number: Int): ResponseEntity<String> {
        if(number.mod(2) == 0) {
            log.error("request id $number : extracted-service, exception")
            return ResponseEntity.internalServerError().body("request id $number : extracted-service, exception")
        }
        log.info("request id $number : extracted-service ")
        return ResponseEntity.ok("request id $number : extracted-service")
    }
}