package xyz.cybersapien.retrohit.activities.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_request.*
import kotlinx.android.synthetic.main.fragment_request.view.*
import okhttp3.*
import xyz.cybersapien.retrohit.R
import xyz.cybersapien.retrohit.activities.MainActivity
import xyz.cybersapien.retrohit.adapters.RequestAdapter
import xyz.cybersapien.retrohit.network.buildRequest
import xyz.cybersapien.retrohit.network.isValidURL
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by the-cybersapien on 6/8/17.
 *
 */
class RequestFragment : Fragment(), View.OnClickListener, OnItemSelectedListener {

    val TAG = "RequestFragment"

    lateinit var rootView: View
    lateinit var httpClient: OkHttpClient
    lateinit var methodsAdapter: ArrayAdapter<CharSequence>
    lateinit var requestAdapter: RequestAdapter
    lateinit var thisActivity: MainActivity
    var headersBuilder = Headers.Builder()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.fragment_request, container, false)

        thisActivity = activity as MainActivity
        httpClient = clientBuilder()

        methodsAdapter = ArrayAdapter.createFromResource(context, R.array.methods_array, android.R.layout.simple_spinner_item)
        methodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        requestAdapter = RequestAdapter(headersBuilder.build(), true)
        rootView.headers_list.layoutManager = LinearLayoutManager(context)
        rootView.headers_list.adapter = requestAdapter

        thisActivity.floatingActionButton.setOnClickListener(this)
        rootView.button_add_header.setOnClickListener(this)

        rootView.methods_spinner.adapter = methodsAdapter
        rootView.methods_spinner.onItemSelectedListener = this

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
        Log.d(TAG, "CLICKED! ${p0?.id}")
        when (p0!!.id) {
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
                Log.d(TAG, "HEADER BUTTON!")
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
                    requestAdapter.headers = headersBuilder.build()
                    requestAdapter.notifyItemRangeChanged(1, requestAdapter.headers?.size() ?: 0)
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (adapterView?.selectedItem as String == "GET" && requestAdapter.show_body) {
            requestAdapter.show_body = false
            requestAdapter.notifyItemRemoved(0)
        } else if (!requestAdapter.show_body) {
            requestAdapter.show_body = true
            requestAdapter.notifyItemInserted(0)
        }
    }

    fun performRequest(url: String): Unit {

        val method = rootView.methods_spinner.selectedItem as String
        val request = buildRequest(url, method, requestAdapter.headers, RequestBody.create(null, requestAdapter.requestBody))

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.d(TAG, response?.isSuccessful.toString())
                Log.d(TAG, response?.toString())
                Log.d(TAG, response?.body()?.string())
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