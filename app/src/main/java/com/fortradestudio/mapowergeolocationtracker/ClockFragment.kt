package com.fortradestudio.mapowergeolocationtracker

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentClockBinding
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourRecord
import com.fortradestudio.mapowergeolocationtracker.room.User
import com.fortradestudio.mapowergeolocationtracker.utils.CacheUtils
import com.fortradestudio.mapowergeolocationtracker.utils.ErrorUtils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class ClockFragment : Fragment(){

    lateinit var clockFragmentViewModel: ClockFragmentViewModel
    lateinit var clockFragmentViewBinding: FragmentClockBinding

    companion object {
        private const val TAG = "ClockFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        clockFragmentViewBinding = FragmentClockBinding.inflate(inflater, container, false)
        return clockFragmentViewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        clockFragmentViewModel = ViewModelProvider(
            this, ClockFragmentViewModelFactory(
                requireActivity(),
                requireView()
            )
        ).get(ClockFragmentViewModel::class.java)



        clockFragmentViewModel.getMutableState().observe(viewLifecycleOwner) {
            if (it == 1) {
                // we have to show clock in
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockInButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.uploadData({
                        if (it) {
                            // upload success
                            clockFragmentViewModel.postState(2)
                            findNavController().navigate(R.id.action_clockFragment_to_successFragment)
                        /*
                            animateClock(false)
                            Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT)
                                .show()

                             */
                        } else {
                            Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "onViewCreated: ", it)
                    }
                }
            } else if (it == 2) {
                // we have to show clock out
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockOutButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.clockOutData({
                        clockFragmentViewModel.postState(1)
                        findNavController().navigate(R.id.action_clockFragment_to_homeFragment)
                    }) {
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        clockFragmentViewModel.filterClockInOrClockOut({

            if (it) {
                // means that result don't exists -> user needs to clock In
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockInButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.uploadData({
                        if (it) {
                            // upload success

                            findNavController().navigate(R.id.action_clockFragment_to_successFragment)

                            Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "onViewCreated: ", it)
                    }
                }

            } else {
                // means that result exists -> user can clock out
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockOutButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.clockOutData({
                        findNavController().navigate(R.id.action_clockFragment_to_homeFragment)
                    }) {
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }) {
            if (it != null) {
                ErrorUtils().report(it)
                Snackbar.make(requireView(),R.string.clockError,Snackbar.LENGTH_LONG)
                    .show()
            }
        }


    }

}