package com.fortradestudio.mapowergeolocationtracker.ui

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentHomeBinding
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationDao
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils.Companion.calculateLinearDistance
import com.fortradestudio.mapowergeolocationtracker.recyclerAdapter.AddressesAdapter
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourEntity
import com.fortradestudio.mapowergeolocationtracker.retrofit.RetrofitProvider
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.service.NotificationService
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    lateinit var homeFragmentBinding : FragmentHomeBinding
    lateinit var homeFragmentViewModel : HomeFragmentViewModel

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

        homeFragmentViewModel = ViewModelProvider(this,HomeFragmentViewModelFactory(requireView(),
            requireActivity())).get(HomeFragmentViewModel::class.java)

        checkIfLocationIsUnderDistance(LocationDao(0.0, 0.0));
        val preferences = requireContext().applicationContext.getSharedPreferences(
            "notification",
            Context.MODE_PRIVATE
        )
        val editor = preferences.getString(notification_Cache, null)
        if (editor == null) workerBuilder()

        homeFragmentViewModel.getLabourName{ s: String, s1: String ->
            val complete_string = getString(R.string.welcome_livespace) + " " + s.toUpperCase(Locale.ROOT)
            homeFragmentBinding.headerNameTextView.text=complete_string
            homeFragmentBinding.vendorNameTextView.text=s1
        }

        homeFragmentBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val addressesAdapter = AddressesAdapter(ArrayList<VendorEntity>(), requireContext())
        homeFragmentBinding.recyclerView.adapter = addressesAdapter
        homeFragmentViewModel.vendorAddressesLiveData.observe(viewLifecycleOwner){
            // init the recycler view
            addressesAdapter.updateAndDispatch(it)
        }

    }

    private fun workerBuilder() {
        val component = ComponentName(requireContext(), NotificationService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            val builder = JobInfo.Builder(124, component)
            builder.setPersisted(true)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            builder.setPeriodic(1000 * 60 * 15)

            val scheduler =
                requireContext().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val preferences = requireContext().applicationContext.getSharedPreferences(
                "notification",
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit();
            editor.putString(notification_Cache, "y");
            editor.apply()
            scheduler.schedule(builder.build())
        }
    }
    private fun checkIfLocationIsUnderDistance(target: LocationDao) {
        LocationUtils(requireActivity()).getLocationCoordinates({

            val calculateLinearDistance =
                calculateLinearDistance(it, LocationDao(target.latitude, target.longitude))
            Log.i(
                TAG,
                "onViewCreated: $calculateLinearDistance  " + it.latitude + " " + it.longitude
            )

//            Toast.makeText(
//                requireContext(),
//                it.latitude.toString() + "," + it.longitude,
//                Toast.LENGTH_SHORT
//            ).show()
        }) {
            Log.e(TAG, "onViewCreated: ", it)
        }
    }

    override fun onResume() {
        super.onResume()
        LocationUtils(requireActivity()).getLocationCoordinates({
//            Toast.makeText(
//                requireContext(),
//                it.latitude.toString() + "," + it.longitude,
//                Toast.LENGTH_SHORT
//            ).show()
        }) {
            Log.e(TAG, "onViewCreated: ", it)
        }
    }

}