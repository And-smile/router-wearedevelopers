package com.wearedevelopers.router.serviceone

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI

class HttpServletProxyClient(
    private val host: String,
    private val restTemplate: RestTemplate,
    private val copiedResponseHeaders: List<String> = emptyList(),
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Sends the given [request] to the specified [host], copying the result to the given [response].
     *
     * Only the specified [copiedResponseHeaders] are written to the [response].
     */
    fun forward(request: HttpServletRequest, response: HttpServletResponse): ResponseWrapper {
        val exchangeResponse = try {
            val requestEntity = request.toRequestEntity()
            restTemplate.exchange<ByteArray>(requestEntity)
        } catch (ex: HttpClientErrorException) {
            return ResponseWrapper(ResponseEntity(ex.responseBodyAsByteArray, ex.responseHeaders, ex.statusCode), false)
        } catch (ex: HttpServerErrorException) {
//            log.error("Error occured while proxying request.", ex)
            return ResponseWrapper(ResponseEntity(ex.responseBodyAsByteArray, ex.responseHeaders, ex.statusCode), false)
        }
        return ResponseWrapper(exchangeResponse)
    }

    private fun HttpServletRequest.toRequestEntity(): RequestEntity<ByteArray> {
        val method = HttpMethod.resolve(method)
        requireNotNull(method) { "cannot resolve incoming method $method" }
        val headers =
            headerNames
                .toList()
                .fold(HttpHeaders()) { all, next ->
                    all[next] = getHeaders(next).toList()
                    all
                }

        return RequestEntity<ByteArray>(
            inputStream.readAllBytes(),
            headers,
            method,
            URI("http://$host$requestURI${if (queryString != null) "?$queryString" else ""}")
        )
    }

    private fun ResponseEntity<ByteArray>.copyTo(servletResponse: HttpServletResponse) {
        copiedResponseHeaders.forEach { name ->
            headers[name]
                ?.takeUnless {
                    it.isEmpty()
                }
                ?.forEach { it -> servletResponse.addHeader(name, it) }
        }

        servletResponse.status = statusCodeValue

        if (body != null) {
            servletResponse.contentType =
                headers.contentType
                    ?.toString()
                    ?: ""
            servletResponse.outputStream.write(body!!)
        }
    }

    data class ResponseWrapper(
        val response: ResponseEntity<ByteArray>,
        val success: Boolean = true
    )
}
