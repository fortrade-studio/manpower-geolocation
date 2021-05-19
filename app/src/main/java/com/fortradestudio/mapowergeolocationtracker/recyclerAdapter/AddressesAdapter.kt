package com.fortradestudio.mapowergeolocationtracker.recyclerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddresses

class AddressesAdapter(
    var listOfAddress:ArrayList<VendorEntity>,
    val context:Context
) : RecyclerView.Adapter<AddressesAdapter.AddressesViewHolder>() {


     inner class AddressesViewHolder(val view:View) : RecyclerView.ViewHolder(view){
         val projectIdTextView = view.findViewById<TextView>(R.id.projectIdTextView)
         val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
         val joinButton = view.findViewById<AppCompatButton>(R.id.joinButton)
     }

    fun updateAndDispatch(newList:List<VendorEntity>){
        val diffResult = DiffUtil.calculateDiff(AddressDiffUtils(newList,listOfAddress))
        diffResult.dispatchUpdatesTo(this)
        listOfAddress.addAll(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.addresses_recycler,parent,false)
        return AddressesViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressesViewHolder, position: Int) {
        with(holder){
            projectIdTextView.text = listOfAddress[position].Project_ID
            addressTextView.text = listOfAddress[position].Address
        }
    }

    override fun getItemCount(): Int  = listOfAddress.size
}