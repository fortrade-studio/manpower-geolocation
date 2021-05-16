package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentOneTimPasswordBinding
import com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment.OneTimePassViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment.OneTimePassViewModelFactory

class OneTimPasswordFragment : Fragment() {

    private lateinit var oneTimeFragmentBinding : FragmentOneTimPasswordBinding
    private lateinit var oneTimePassViewModel: OneTimePassViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        oneTimeFragmentBinding = FragmentOneTimPasswordBinding.inflate(inflater,container,false)
        return oneTimeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oneTimePassViewModel = ViewModelProvider(this,OneTimePassViewModelFactory(requireActivity(),requireView())).get(OneTimePassViewModel::class.java)

        with(oneTimeFragmentBinding) {
            button.setOnClickListener {
                val otp = editTextNumber.text.toString()
                oneTimePassViewModel.verifyOtp(otp)
            }
        }
    }

}