package com.fortradestudio.mapowergeolocationtracker.ui

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentOneTimPasswordBinding
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment.OneTimePassViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment.OneTimePassViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OneTimPasswordFragment : Fragment() {

    companion object{
        private const val TAG = "OneTimPasswordFragment"
        private const val number_cache_key = "phoneNumber"
    }

    private lateinit var oneTimeFragmentBinding : FragmentOneTimPasswordBinding
    private lateinit var oneTimePassViewModel: OneTimePassViewModel
    private val mainScope= CoroutineScope(Dispatchers.Main)

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

        val fromCache = Utils(requireActivity()).getFromCache(number_cache_key)

        if(fromCache!=null){
            oneTimeFragmentBinding.phoneNumberTextView.text = "${getString(R.string.headerText)} \n \t\t\t\t\t\t\t\t +91 $fromCache"
        }


        with(oneTimeFragmentBinding) {
            verifyButton.setOnClickListener {
                oneTimePassViewModel.showDialog()
                val otp = otpView.otp
                oneTimePassViewModel.verifyOtp(otp){
                    if(!it){
                        mainScope.launch { otpView.showError() }
                    }else{
                        mainScope.launch { otpView.showSuccess() }
                    }
                }
            }
        }
    }

}