package xyz.cybersapien.retrohit.retrofit

import okhttp3.*

fun makeGETRequest(
        URL: String,
        headers: Headers? = null,
        requestBody: RequestBody? = null
): Response? {
    val OkHttpClient = OkHttpClient()

    val requestBuilder = Request.Builder()
            .url(URL)

    if (headers != null)
        requestBuilder.headers(headers)
    if (requestBody != null)
        requestBuilder.method("GET", requestBody)

    val request = requestBuilder.build()
    val response = OkHttpClient.newCall(request).execute()
    return response
}