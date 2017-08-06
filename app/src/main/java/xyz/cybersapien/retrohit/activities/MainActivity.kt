package xyz.cybersapien.retrohit.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import xyz.cybersapien.retrohit.R
import xyz.cybersapien.retrohit.adapters.HeaderViewAdapter
import xyz.cybersapien.retrohit.network.buildRequest
import xyz.cybersapien.retrohit.network.isValidURL
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val LOG_TAG = "MainActivity"
    lateinit var httpClient: OkHttpClient
    lateinit var methodsAdapter: ArrayAdapter<CharSequence>
    lateinit var headersAdapter: HeaderViewAdapter
    var headers = Headers.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        httpClient = clientBuilder()

        methodsAdapter = ArrayAdapter.createFromResource(this, R.array.methods_array, android.R.layout.simple_spinner_item)
        methodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        methods_spinner.adapter = methodsAdapter

        headersAdapter = HeaderViewAdapter(headers.build(), true)
        headers_list.layoutManager = LinearLayoutManager(this)
        headers_list.adapter = headersAdapter

        fab.setOnClickListener(this)
        button_add_header.setOnClickListener(this)
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
            button_add_header.id -> {
                val headerName = new_header_name.text.toString()
                val headerValue = new_header_value.text.toString()
                var problem = false
                if (headerName.isNullOrBlank()) {
                    new_header_name.error = "Error! Name Can't be empty!"
                    problem = true
                }
                if (headerValue.isNullOrBlank()) {
                    new_header_value.error = "Error! Header Value is empty!"
                    problem = true
                }
                if (!problem) {
                    headers = headers.add(headerName, headerValue)
                    headersAdapter.headers = headers.build()
                    headersAdapter.notifyDataSetChanged()
                }
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

        val getRequest = buildRequest(url, method, headersAdapter.headers)

        httpClient.newCall(getRequest)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        val handler = Handler(Looper.getMainLooper())
                        handler.post({
                        })
                    }

                    override fun onResponse(call: Call?, response: Response) {
                        val handler = Handler(Looper.getMainLooper())
                        val responseBody = response.body()
                        val headers = response.headers()
                        val resString = response.toString()
                        val resBodyString = responseBody?.string()
                        handler.post {
                            headersAdapter.headers = headers
                            headersAdapter.notifyDataSetChanged()
                        }
                    }
                })
    }

}
