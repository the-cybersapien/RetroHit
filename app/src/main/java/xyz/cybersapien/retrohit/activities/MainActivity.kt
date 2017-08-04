package xyz.cybersapien.retrohit.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import xyz.cybersapien.retrohit.R
import xyz.cybersapien.retrohit.network.buildGETRequest
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var httpClient: OkHttpClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        httpClient = clientBuilder()

        fab.setOnClickListener { _ ->
            var url = url_edit_text.text.toString()
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url
            val getRequest = buildGETRequest(url)
            httpClient.newCall(getRequest)
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call?, e: IOException?) {
                            val handler = Handler(Looper.getMainLooper())
                            handler.post({
                                result_textview.text = "Error!\n${call.toString()}\n${e.toString()}"
                            })
                        }

                        override fun onResponse(call: Call?, response: Response) {
                            val handler = Handler(Looper.getMainLooper())
                            val responseBody = response.body()
                            handler.post({
                                result_textview.text = responseBody?.string()
                            })
                        }
                    })
        }
    }

    fun clientBuilder(): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        // TODO: Add Timeouts Shared Preferences
        clientBuilder.connectTimeout(25, TimeUnit.SECONDS)
        clientBuilder.readTimeout(25, TimeUnit.SECONDS)
        clientBuilder.writeTimeout(25, TimeUnit.SECONDS)
        // TODO: Add Retry param
        clientBuilder.retryOnConnectionFailure(true)
        return clientBuilder.build()
    }

}
