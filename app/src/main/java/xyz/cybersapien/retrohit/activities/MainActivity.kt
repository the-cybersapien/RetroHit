package xyz.cybersapien.retrohit.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import xyz.cybersapien.retrohit.R
import xyz.cybersapien.retrohit.network.buildRequest
import xyz.cybersapien.retrohit.network.isValidURL
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val LOG_TAG = "MainActivity"
    lateinit var httpClient: OkHttpClient
    lateinit var methodsAdapter: ArrayAdapter<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        httpClient = clientBuilder()

        methodsAdapter = ArrayAdapter.createFromResource(this, R.array.methods_array, android.R.layout.simple_spinner_item)
        methodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        methods_spinner.adapter = methodsAdapter

        fab.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            fab.id -> {
                var url = url_edit_text.text.toString()
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url
                if (isValidURL(url)) {
                    performRequest(url)
                } else {
                    url_edit_text.error = "Invalid URL!"
                }
                Log.d(LOG_TAG, methods_spinner.selectedItem.toString())
            }
            else -> {
                // Do Nothing
            }
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

    fun performRequest(url: String): Unit {

        val method = methods_spinner.selectedItem as String

        val getRequest = buildRequest(url, method)

        httpClient.newCall(getRequest)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        val handler = Handler(Looper.getMainLooper())
                        handler.post({
                            result_textview.text = getString(R.string.error_string, e.toString())
                        })
                    }

                    override fun onResponse(call: Call?, response: Response) {
                        val handler = Handler(Looper.getMainLooper())
                        val responseBody = response.body()
                        val headers = response.headers()
                        val resString = response.toString()
                        val resBodyString = responseBody?.string()
                        handler.post({
                            Log.d(LOG_TAG, resString)
                            Log.d(LOG_TAG, headers.toString())
                            result_textview.text = resBodyString
                        })
                    }
                })
    }

}
