package com.codepath.nationalparks

import android.util.Log
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.annotations.SerializedName
import okhttp3.Headers
import org.json.JSONArray

private const val WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY

/**
 * The Model for storing a single park from the National Parks API.
 *
 * SerializedName tags MUST match the JSON response for the
 * object to correctly parse with the gson library.
 */
class NationalPark {

    // Name field
    @JvmField
    @SerializedName("fullName")
    var name: String? = null

    // Description field
    @JvmField
    @SerializedName("description")
    var description: String? = null

    // Location or State field
    @JvmField
    @SerializedName("states")
    var location: String? = null

    // parkImageUrl
    @SerializedName("images")
    var images: List<Image>? = null

    // Convenience property to access the first image’s URL
    val imageUrl: String? get() = images?.firstOrNull()?.url

    class Image {
        @SerializedName("url")
        var url: String? = null
    }


    // Latitude and longitude
    @JvmField
    @SerializedName("latitude")
    var latitude: Float? = null

    @JvmField
    @SerializedName("longitude")
    var longitude: Float? = null

    // Weather data
    var weatherText: String? = "Loading..."
    val weatherIconUrl: String get() = "https://openweathermap.org/payload/api/media/file/$weatherIcon.png"
    var weatherIcon: String? = "02d" // placeholder

    fun loadWeather(callback: ()->Any?) {
        // call the weather API
        val client = AsyncHttpClient()
        val params = RequestParams()
        params["appid"] = WEATHER_API_KEY
        params["lat"] = latitude.toString()
        params["lon"] = longitude.toString()
        Log.d("NationalParkWeatherInit",params.toString())
        client["https://api.openweathermap.org/data/2.5/weather", params, object : JsonHttpResponseHandler()
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

                // Filter out the "data" JSON array and turn into a String
                val dataJSON = json.jsonObject.get("weather") as JSONArray
                //val parksRawJSON = dataJSON.toString()

                val weather = dataJSON.optJSONObject(0)
                if (weather != null) {
                    weatherText = weather.optString("description","(no description given)")
                    weatherIcon = weather.optString("icon","01d")
                }

                Log.d("NationalParkWeatherInit", "response successful")
                callback()
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
                weatherText = "(error)"
                weatherIcon = "01d"
                // If the error is not null, log it!
                t?.message?.let {
                    Log.e("NationalParkWeatherInit", errorResponse)
                }
                callback()
            }
        }]
    }
}
