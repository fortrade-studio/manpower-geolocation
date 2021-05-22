package com.fortradestudio.mapowergeolocationtracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentClockBinding
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourRecord
import com.fortradestudio.mapowergeolocationtracker.room.User
import com.fortradestudio.mapowergeolocationtracker.utils.CacheUtils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ClockFragment : Fragment() {

    lateinit var clockFragmentViewModel: ClockFragmentViewModel
    lateinit var clockFragmentViewBinding: FragmentClockBinding


    companion object {
        private const val TAG = "ClockFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                            animateClock()
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
            } else if (it == 2) {
                // we have to show clock out
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockOutButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.clockOutData({
                        clockFragmentViewModel.postState(1)
                        animateClock()
                    }) {
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        clockFragmentViewModel.filterClockInOrClockOut({

            if (it) {
                // means that result don't exists -> user needs to clock In
                Toast.makeText(context, "Please Clock In", Toast.LENGTH_SHORT).show()
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockInButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.uploadData({
                        if (it) {
                            // upload success
                                animateClock()
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
                Toast.makeText(context, "Please Clock Out", Toast.LENGTH_SHORT).show()
                clockFragmentViewBinding.clockButton.text = getString(R.string.clockOutButton)
                clockFragmentViewBinding.clockButton.setOnClickListener {
                    clockFragmentViewModel.clockOutData({
                        animateClock()
                    }) {
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }) {
            Log.e(TAG, "onViewCreated: ", it)
        }


    }

    fun animateClock() {
     //   clockFragmentViewBinding.imageView2.setBackgroundResource(R.drawable.check)
        clockFragmentViewBinding.imageView2.animate()
            .rotationY(360F)
            .setDuration(500)
            .withEndAction {
                clockFragmentViewBinding.imageView2.setBackgroundResource(R.drawable.ic_clock)
            }
    }


}