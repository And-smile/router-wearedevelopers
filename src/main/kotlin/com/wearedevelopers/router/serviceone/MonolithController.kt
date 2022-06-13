package com.wearedevelopers.router.serviceone

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class MonolithController {
    private val log = LoggerFactory.getLogger(javaClass)
    @PostMapping("/echo")
    fun getNumber(@RequestBody number: Int): ResponseEntity<String> {
        log.info("request id $number : monolith")
        return ResponseEntity.ok("request id $number : monolith")
    }
}