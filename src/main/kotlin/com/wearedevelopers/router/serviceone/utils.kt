package com.wearedevelopers.router.serviceone

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.util.StreamUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class CachedBodyServletInputStream(cachedBody: ByteArray) : ServletInputStream() {
    private val cachedBodyInputStream = ByteArrayInputStream(cachedBody)

    override fun read(): Int = cachedBodyInputStream.read()
    override fun isFinished(): Boolean = cachedBodyInputStream.available() == 0
    override fun isReady(): Boolean = true
    override fun setReadListener(readListener: ReadListener) { throw RuntimeException("setReadListener is not implemented") }
}

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val cachedBody = StreamUtils.copyToByteArray(request.inputStream)

    override fun getInputStream(): ServletInputStream = CachedBodyServletInputStream(cachedBody)
    override fun getReader(): BufferedReader = BufferedReader(InputStreamReader(ByteArrayInputStream(cachedBody)))
}

fun ResponseEntity<ByteArray>.copyTo(servletResponse: HttpServletResponse, copiedResponseHeaders: List<String>) {
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