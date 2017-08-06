package xyz.cybersapien.retrohit.activities.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_request.*
import kotlinx.android.synthetic.main.fragment_request.view.*
import okhttp3.*
import xyz.cybersapien.retrohit.R
import xyz.cybersapien.retrohit.activities.MainActivity
import xyz.cybersapien.retrohit.adapters.HeaderViewAdapter
import xyz.cybersapien.retrohit.network.buildRequest
import xyz.cybersapien.retrohit.network.isValidURL
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by ogcybersapien on 6/8/17.
 */
class RequestFragment : Fragment(), View.OnClickListener {

    lateinit var rootView: View
    lateinit var httpClient: OkHttpClient
    lateinit var methodsAdapter: ArrayAdapter<CharSequence>
    lateinit var headersAdapter: HeaderViewAdapter
    lateinit var thisActivity: MainActivity
    var headersBuilder = Headers.Builder()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.fragment_request, container, false)

        thisActivity = activity as MainActivity
        httpClient = clientBuilder()

        methodsAdapter = ArrayAdapter.createFromResource(context, R.array.methods_array, android.R.layout.simple_spinner_item)
        methodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        rootView.methods_spinner.adapter = methodsAdapter

        headersAdapter = HeaderViewAdapter(headersBuilder.build(), true)
        rootView.headers_list.layoutManager = LinearLayoutManager(context)
        rootView.headers_list.adapter = headersAdapter

        thisActivity.floatingActionButton.setOnClickListener(this)
        rootView.button_add_header.setOnClickListener(this)
        return rootView
    }

    fun clientBuilder(): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        // TODO: Add Timeouts from Shared Preferences
        clientBuilder.connectTimeout(25, TimeUnit.SECONDS)
        clientBuilder.readTimeout(25, TimeUnit.SECONDS)
        clientBuilder.writeTimeout(25, TimeUnit.SECONDS)
        // TODO: Add Retry Param
        clientBuilder.retryOnConnectionFailure(true)
        return clientBuilder.build()
    }

    override fun onClick(p0: View?) {
        when (view?.id) {
            thisActivity.floatingActionButton.id -> {
                var url = url_edit_text.text.toString()
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url

                if (isValidURL(url))
                    performRequest(url)
                else
                    url_edit_text.error = "Invalid URL!"
            }
            button_add_header.id -> {
                val headerName = new_header_name.text.toString()
                val headerValue = new_header_value.text.toString()
                var problem = false
                if (headerName.isNullOrBlank()) {
                    new_header_name.error = "Error! Name Can't be Empty"
                    problem = true
                }
                if (headerValue.isNullOrBlank()) {
                    new_header_value.error = "Error! Value Can't be Empty"
                    problem = true
                }
                if (!problem) {
                    headersBuilder = headersBuilder.add(headerName, headerValue)
                    headersAdapter.headers = headersBuilder.build()
                    headersAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun performRequest(url: String): Unit {

        val method = rootView.methods_spinner.selectedItem as String
        val request = buildRequest(url, method, headersAdapter.headers)

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response?) {

            }
        })
    }

    companion object {
        val ARG_SECTION_NUMBER = "RequestSectionFragment"
        var requestFragment: RequestFragment? = null

        fun getInstance(): RequestFragment {
            requestFragment = requestFragment ?: RequestFragment()
            return requestFragment!!
        }
    }
}