package com.fortradestudio.mapowergeolocationtracker.ui

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentHomeBinding
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationDao
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils.Companion.calculateLinearDistance
import com.fortradestudio.mapowergeolocationtracker.recyclerAdapter.AddressesAdapter
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    lateinit var homeFragmentBinding : FragmentHomeBinding
    lateinit var homeFragmentViewModel : HomeFragmentViewModel
    lateinit var dialog:AlertDialog

    companion object {
        private const val TAG = "HomeFragment"
        const val notification_Cache = "notificationcache"
        private const val number_cache_key = "phoneNumber"
    }

    val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater,container,false)
        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    try {
                        dialog.cancel()
                    }catch (e:UninitializedPropertyAccessException){}
                }
                override fun onLost(network: Network) {
                    super.onLost(network)
                    showDialog();
                }
            })
        }

        homeFragmentViewModel = ViewModelProvider(this,HomeFragmentViewModelFactory(requireView(),
            requireActivity())).get(HomeFragmentViewModel::class.java)

       // checkIfLocationIsUnderDistance(LocationDao(0.0, 0.0));


        homeFragmentViewModel.getLabourName{ s: String, s1: String , category:String ->
            val complete_string = getString(R.string.welcome_livespace) + " " + s.toUpperCase(Locale.ROOT)
            homeFragmentBinding.headerNameTextView.text=complete_string
            homeFragmentBinding.vendorNameTextView.text=s1


            homeFragmentBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            val addressesAdapter = AddressesAdapter(ArrayList<VendorEntity>(), requireContext(), requireActivity(),requireView(),s,category)
            homeFragmentBinding.recyclerView.adapter = addressesAdapter
            homeFragmentViewModel.vendorAddressesLiveData.observe(viewLifecycleOwner){
                // init the recycler view
                addressesAdapter.updateAndDispatch(it)
            }

        }

    }

    private fun showDialog(){
        dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.internet_dialog_title)
            .setMessage(R.string.internet_dialog_message)
            .setCancelable(false)
            .create()

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        LocationUtils(requireActivity()).getLocationCoordinates({

        }) {
            Log.e(TAG, "onViewCreated: ", it)
        }
    }

}