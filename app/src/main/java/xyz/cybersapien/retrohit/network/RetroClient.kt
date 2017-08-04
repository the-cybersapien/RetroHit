package xyz.cybersapien.retrohit.network

import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody

fun buildGETRequest(
        URL: String,
        headers: Headers? = null,
        requestBody: RequestBody? = null
): Request {

    val requestBuilder = Request.Builder()
            .url(URL)

    if (headers != null)
        requestBuilder.headers(headers)
    if (requestBody != null)
        requestBuilder.method("GET", requestBody)

    return requestBuilder.build()
}