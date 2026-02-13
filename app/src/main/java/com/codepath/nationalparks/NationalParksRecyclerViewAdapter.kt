package com.codepath.nationalparks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.nationalparks.R.id

/**
 * [RecyclerView.Adapter] that can display a [NationalPark] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class NationalParksRecyclerViewAdapter(
    private val parks: List<NationalPark>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<NationalParksRecyclerViewAdapter.ParkViewHolder>() {

    // Inflate the item layout from XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_national_park, parent, false)
        return ParkViewHolder(view)
    }

    // ViewHolder class holds references to all UI elements inside the list item layout
    inner class ParkViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mItem: NationalPark? = null

        val mParkName: TextView = mView.findViewById(id.park_name) as TextView
        val mParkDescription: TextView = mView.findViewById(id.park_description) as TextView
        val mParkLocation: TextView = mView.findViewById(id.park_location) as TextView
        val mParkImage: ImageView = mView.findViewById(id.park_image) as ImageView
        val mParkWeatherText: TextView = mView.findViewById(id.park_weathertext) as TextView
        val mParkWeatherIcon: ImageView = mView.findViewById(id.park_weathericon) as ImageView

        override fun toString(): String {
            return mParkName.toString() + " '" + mParkDescription.text + "'"
        }
    }

    override fun onBindViewHolder(holder: ParkViewHolder, position: Int) {
        val park = parks[position]
        fun updateWeather() {
            holder.mParkWeatherText.text = "Weather: ${park.weatherText}"
            Glide.with(holder.mView)
                .load(park.weatherIconUrl)
                .centerInside()
                .into(holder.mParkWeatherIcon)
        }
        park.loadWeather { updateWeather() }

        // Step 4b - Bind the park data to the views
        holder.mItem = park
        holder.mParkName.text = park.name
        holder.mParkDescription.text = park.description
        holder.mParkLocation.text = park.location

        // Step 4c - Use Glide to load the first image
        val imageUrl = park.imageUrl
        Glide.with(holder.mView)
            .load(imageUrl)
            .centerInside()
            .into(holder.mParkImage)
        updateWeather()


        // Sets up click listener for this park item
        holder.mView.setOnClickListener {
            holder.mItem?.let { park ->
                mListener?.onItemClick(park)
            }
        }
    }

    // Tells the RecyclerView how many items to display
    override fun getItemCount(): Int {
        return parks.size
    }
}
