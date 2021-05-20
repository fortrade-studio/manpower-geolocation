package com.fortradestudio.mapowergeolocationtracker.recyclerAdapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationDao
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.room.*
import com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment
import com.fortradestudio.mapowergeolocationtracker.utils.CacheUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressesAdapter(
    var listOfAddress:ArrayList<VendorEntity>,
    val context:Context,
    val activity:Activity
) : RecyclerView.Adapter<AddressesAdapter.AddressesViewHolder>() {


    companion object{
        private const val TAG = "AddressesAdapter"
    }

    private val mainScope = CoroutineScope(Dispatchers.Main)

     inner class AddressesViewHolder(val view:View) : RecyclerView.ViewHolder(view){
         val projectIdTextView = view.findViewById<TextView>(R.id.projectIdTextView)
         val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
         val distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
         val cardAddress = view.findViewById<ConstraintLayout>(R.id.cardAddress)
         val joinButton = view.findViewById<AppCompatButton>(R.id.joinButton)
     }

    fun updateAndDispatch(newList:List<VendorEntity>){
        val diffResult = DiffUtil.calculateDiff(AddressDiffUtils(newList,listOfAddress))
        diffResult.dispatchUpdatesTo(this)
        listOfAddress = ArrayList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.addresses_recycler,parent,false)
        return AddressesViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressesViewHolder, position: Int) {

        with(holder){
            val map =
                mapVendorEntityToVendorAddress("s", listOfAddress[position])
            checkIfLocationIsUnderDistance(
                LocationDao(map.latitude,map.longitude)
            ) {it,distance->
                if(it) {
                    projectIdTextView.text = listOfAddress[position].Project_ID
                    addressTextView.text = listOfAddress[position].Address
                    distanceTextView.text = distance.toDistance()

                    joinButton.setOnClickListener{
                        CacheUtils(context).getUserData {
                            mainScope.launch {
                                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }else{
                    cardAddress.visibility=View.GONE
                }
            }
        }
    }


    private fun Double.toDistance():String{
        return if(this>=1000){
            // then the distance is in km that we have to show
            this.div(1000).toInt().toString()+" km"
        }else{
            this.toInt().toString() +" m"
        }
    }

    private fun mapVendorEntityToVendorAddress(
        labourName: String,
        v: VendorEntity
    ): VendorAddresses {
        val latiLongi = v.Lati_Longi
        val split = latiLongi.split(",")
//        val latitude = split[0]
//        val longitude = split[1]

        return VendorAddresses(
            labourName = labourName,
            address = v.Address,
            vendorName = v.Vendor_Name,
            projectId = v.Project_ID,
            // 8 to 9 ,
            latitude = 29.5699061,
            longitude = 77.7697311
        )
    }


    private fun checkIfLocationIsUnderDistance(target: LocationDao, onFetched:(Boolean,Double)->Unit) {
        LocationUtils(activity = activity).getLocationCoordinates({

            val calculateLinearDistance =
                LocationUtils.calculateLinearDistance(
                    it,
                    LocationDao(target.latitude, target.longitude)
                )

            onFetched(Math.abs(calculateLinearDistance) <=500,calculateLinearDistance)

        }) {
            Log.e(TAG, "onViewCreated: ", it)
        }
    }


    override fun getItemCount(): Int  = listOfAddress.size
}