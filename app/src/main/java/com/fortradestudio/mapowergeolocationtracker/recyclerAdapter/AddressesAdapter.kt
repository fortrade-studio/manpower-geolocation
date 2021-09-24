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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationDao
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.room.*
import com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment
import com.fortradestudio.mapowergeolocationtracker.utils.CacheUtils
import com.fortradestudio.mapowergeolocationtracker.utils.ErrorUtils
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class AddressesAdapter(
    var listOfAddress: ArrayList<VendorEntity>,
    val context: Context,
    val activity: Activity,
    view: View,
    val name: String,
    val category:String
) : RecyclerView.Adapter<AddressesAdapter.AddressesViewHolder>() {


    companion object {
        private const val TAG = "AddressesAdapter"
        private const val number_cache_key = "phoneNumber"
    }

    private val mainScope = CoroutineScope(Dispatchers.Main)

    inner class AddressesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val projectIdTextView = view.findViewById<TextView>(R.id.projectIdTextView)
        val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
        val distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
        val cardAddress = view.findViewById<ConstraintLayout>(R.id.cardAddress)
        val joinButton = view.findViewById<AppCompatButton>(R.id.joinButton)
    }

    fun updateAndDispatch(newList: List<VendorEntity>) {
        val diffResult = DiffUtil.calculateDiff(AddressDiffUtils(newList, listOfAddress))
        diffResult.dispatchUpdatesTo(this)
        listOfAddress = ArrayList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.addresses_recycler, parent, false)
        return AddressesViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressesViewHolder, position: Int) {

        with(holder) {
            val map =
                mapVendorEntityToVendorAddress("s", listOfAddress[position])
            if (map.latitude != -1.0 || map.longitude != -1.0) {
                checkIfLocationIsUnderDistance(
                    LocationDao(map.latitude, map.longitude)
                ) { it, distance ->
                    if (it) {
                        projectIdTextView.text = listOfAddress[position].Project_ID
                        addressTextView.text = listOfAddress[position].Address
                        distanceTextView.text = distance.toDistance()
                        joinButton.visibility = View.VISIBLE
                        joinButton.setOnClickListener {
                            insertUser(
                                User(
                                    name = name.trim(),
                                    vendorName = listOfAddress[position].Vendor_Name.trim(),
                                    phoneNumber = Utils(activity).getFromCache(number_cache_key)!!.trim(),
                                    projectId = listOfAddress[position].Project_ID.trim(),
                                    category = category.trim(),
                                    address = listOfAddress[position].Address.trim()
                                )
                            )
                            mainScope.launch {
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_homeFragment_to_clockFragment)
                            }
                        }

                    } else {
                        projectIdTextView.text = listOfAddress[position].Project_ID
                        addressTextView.text = listOfAddress[position].Address
                        distanceTextView.text = distance.toDistance()

                        joinButton.visibility = View.INVISIBLE
                    }
                }
            } else {
                holder.cardAddress.visibility = View.GONE
                Toast.makeText(context, R.string.lat_long_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertUser(user: User) =
        CoroutineScope(Dispatchers.IO).launch {
            val dao = VendorAddressDatabase.getDatabase(activity).getUserDao()
            val repo = UserRepository(dao)
            repo.deleteFromDb()
            repo.insertUser(user)
        }


    private fun Double.toDistance(): String {
        return if (this >= 1000) {
            // then the distance is in km that we have to show
            this.div(1000).toInt().toString() + " km"
        } else {
            this.toInt().toString() + " m"
        }
    }

    private fun mapVendorEntityToVendorAddress(
        labourName: String,
        v: VendorEntity
    ): VendorAddresses {
        val latiLongi = Utils(activity).getLocationFromAddress(v.Address)
        if(latiLongi == null){
            throw NullPointerException()
        }
        try {
            val latitude = latiLongi.latitude
            val longitude = latiLongi.longitude


            return VendorAddresses(
                labourName = labourName,
                address = v.Address,
                vendorName = v.Vendor_Name,
                projectId = v.Project_ID,
                // 8 to 9 ,
                latitude = latitude,
                longitude = longitude
            )
        } catch (e: Exception) {
            return VendorAddresses(
                labourName = labourName,
                address = v.Address,
                vendorName = v.Vendor_Name,
                projectId = v.Project_ID,
                // 8 to 9 ,
                latitude = -1.0,
                longitude = -1.0
            )
        }

    }


    private fun checkIfLocationIsUnderDistance(
        target: LocationDao,
        onFetched: (Boolean, Double) -> Unit
    ) {
        LocationUtils(activity = activity).getLocationCoordinates({

            Log.d(TAG, "checkIfLocationIsUnderDistance: ${it.toString()}")
            val calculateLinearDistance =
                LocationUtils.calculateLinearDistance(
                    it,
                    LocationDao(target.latitude, target.longitude)
                )

            onFetched(abs(calculateLinearDistance) <= 500, calculateLinearDistance)

        }) {
            Log.e(TAG, "onViewCreated: ", it)
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
            ErrorUtils().report(it)
        }
    }


    override fun getItemCount(): Int = listOfAddress.size
}