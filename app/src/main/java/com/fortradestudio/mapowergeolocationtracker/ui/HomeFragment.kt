package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocationUtils(requireActivity()).getLocationCoordinates({
            // here we need to calculate the sites which is closest or in range of 500 m
            Toast.makeText(requireContext(), it.latitude.toString()+","+it.longitude, Toast.LENGTH_SHORT).show()
        }){
            Log.e(TAG, "onViewCreated: ",it )
        }

    }

    override fun onResume() {
        super.onResume()
        LocationUtils(requireActivity()).getLocationCoordinates({
            Toast.makeText(requireContext(), it.latitude.toString()+","+it.longitude, Toast.LENGTH_SHORT).show()
        }){
            Log.e(TAG, "onViewCreated: ",it )
        }
    }

}