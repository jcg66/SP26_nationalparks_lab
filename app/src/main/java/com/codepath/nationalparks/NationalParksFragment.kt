package com.codepath.nationalparks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import org.json.JSONArray


// --------------------------------//
// CHANGE THIS TO BE YOUR API KEY  //
// --------------------------------//
private const val API_KEY = BuildConfig.API_KEY

/*
 * The class for the only fragment in the app, which contains the progress bar,
 * recyclerView, and performs the network calls to the National Parks API.

 */
class NationalParksFragment : Fragment(), OnListFragmentInteractionListener {
    var totalItems: Int? = null
    val pageLimit = 5
    var pageNum: Int = 0
    lateinit var prevPageButton: Button
    lateinit var nextPageButton: Button
    lateinit var pageNumText: TextView
    lateinit var progressBar: ContentLoadingProgressBar
    lateinit var recyclerView: RecyclerView
    fun updatePageControls() {
        if (totalItems == null) {
            prevPageButton.isEnabled = false
            nextPageButton.isEnabled = false
        } else {
            val totalPages = totalItems!! / pageLimit
            pageNumText.text = "$pageNum / $totalPages"
            prevPageButton.isEnabled = (pageNum > 0)
            nextPageButton.isEnabled = (pageNum < totalPages)
        }
    }
    fun updatePage(newNum: Int) {
        pageNum = newNum
        updateAdapter()
    }
        /*
     * Constructing the view
     */
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_national_parks_list, container, false)
            progressBar = view.findViewById<View>(R.id.progress) as ContentLoadingProgressBar
            recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
            val context = view.context

            prevPageButton = view.findViewById<Button>(R.id.page_prev)
            nextPageButton = view.findViewById<Button>(R.id.page_next)
            pageNumText = view.findViewById<TextView>(R.id.page_num)
            prevPageButton.setOnClickListener { updatePage(pageNum - 1) }
            nextPageButton.setOnClickListener { updatePage(pageNum + 1) }

            recyclerView.layoutManager = LinearLayoutManager(context)

            updateAdapter()
            return view
        }

    /*
     * Updates the RecyclerView adapter with new data.  This is where the
     * networking magic happens!
     */
    private fun updateAdapter() {
        progressBar.show()
        // disable buttons while loading
        prevPageButton.isEnabled = false
        nextPageButton.isEnabled = false

        // Create and set up an AsyncHTTPClient() here

        val client = AsyncHttpClient()
        val params = RequestParams()

        // Using the client, perform the HTTP request

        params["api_key"] = API_KEY
        params["limit"] = pageLimit.toString()
        params["start"] = (pageLimit * pageNum).toString()
        client["https://developer.nps.gov/api/v1/parks", params, object : JsonHttpResponseHandler()
        /* Uncomment me once you complete the above sections! */
        {
            /*
             * The onSuccess function gets called when
             * HTTP response status is "200 OK"
             */
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JSON
            ) {
                // The wait for a response is over
                progressBar.hide()

                // update page controls using total
                totalItems = json.jsonObject.getInt("total")
                updatePageControls()

                // Filter out the "data" JSON array and turn into a String
                val dataJSON = json.jsonObject.get("data") as JSONArray

                val parksRawJSON = dataJSON.toString()

                // Create a Gson instance to help parse the raw JSON
                val gson = Gson()

                // Tell Gson what type we’re expecting (a list of NationalPark objects)
                val arrayParkType = object : TypeToken<List<NationalPark>>() {}.type

                // Convert the raw JSON string into a list of actual NationalPark data models
                val models: List<NationalPark> = gson.fromJson(parksRawJSON, arrayParkType)

                recyclerView.swapAdapter(null,false) // clear old adapter (if any)
                recyclerView.adapter = NationalParksRecyclerViewAdapter(models, this@NationalParksFragment)

                // Look for this in Logcat:
                Log.d("NationalParksFragment", "response successful")
            }

            /*
             * The onFailure function gets called when
             * HTTP response status is "4XX" (eg. 401, 403, 404)
             */
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                t: Throwable?
            ) {
                // The wait for a response is over
                progressBar.hide()

                // If the error is not null, log it!
                t?.message?.let {
                    Log.e("NationalParksFragment", errorResponse)
                }
            }
        }]
        /**/

    }

    /*
     * What happens when a particular park is clicked.
     */
    override fun onItemClick(item: NationalPark) {
        Toast.makeText(context, "Park Name: " + item.name, Toast.LENGTH_LONG).show()
    }

}
