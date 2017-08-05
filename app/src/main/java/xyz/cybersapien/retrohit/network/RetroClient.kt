package xyz.cybersapien.retrohit.network

import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody

fun buildRequest(
        URL: String,
        method: String,
        headers: Headers? = null,
        requestBody: RequestBody? = null
): Request {

    val requestBuilder = Request.Builder().url(URL)

    if (headers != null)
        requestBuilder.headers(headers)
    if (requestBody != null)
        requestBuilder.method(method, requestBody)

    return requestBuilder.build()
}

fun isValidURL(url: String): Boolean {
    return url.matches(Regex("^(http[s]?://)([^:/\\s]+)(/.*)*$"))
}