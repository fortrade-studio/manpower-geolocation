package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentOneTimPasswordBinding
import com.fortradestudio.mapowergeolocationtracker.utils.ErrorUtils
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.SharedViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment.LoginFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment.OneTimePassViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment.OneTimePassViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class OneTimPasswordFragment : Fragment() , Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "OneTimPasswordFragment"
        private const val number_cache_key = "phoneNumber"
    }

    private lateinit var oneTimeFragmentBinding: FragmentOneTimPasswordBinding
    private lateinit var oneTimePassViewModel: OneTimePassViewModel
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        oneTimeFragmentBinding = FragmentOneTimPasswordBinding.inflate(inflater, container, false)
        return oneTimeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler(this)

        oneTimePassViewModel = ViewModelProvider(
            this,
            OneTimePassViewModelFactory(requireActivity(), requireView())
        ).get(OneTimePassViewModel::class.java)

        val fromCache = Utils(requireActivity()).getFromCache(number_cache_key)

        if (fromCache != null) {
            oneTimeFragmentBinding.phoneNumberTextView.text =
                "${getString(R.string.headerText)} \n \t\t\t\t\t\t\t\t +91 $fromCache"
        }


        with(oneTimeFragmentBinding) {
            verifyButton.setOnClickListener {
                oneTimePassViewModel.showDialog()
                val otp = otpView.otp
                oneTimePassViewModel.verifyOtp(otp) {
                    if (!it) {
                        mainScope.launch { otpView.showError() }
                    } else {
                        mainScope.launch { otpView.showSuccess() }
                    }
                }
            }
        }

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                oneTimeFragmentBinding.include2.countdown.setText("Try again after: " + millisUntilFinished / 1000 + "s")
                oneTimeFragmentBinding.include2.resendOtp.setVisibility(View.GONE)
            }

            override fun onFinish() {
                oneTimeFragmentBinding.include2.countdown.setText("Don't receive the OTP ?")
                oneTimeFragmentBinding.include2.resendOtp.setVisibility(View.VISIBLE)
            }
        }.start()

        oneTimeFragmentBinding.include2.resendOtp.setOnClickListener {

            val phoneNumber = sharedViewModel.phNumber.value.toString()
            oneTimePassViewModel.checkIfMobileNumberIsValid(phoneNumber) {
                if (it) {

                        oneTimePassViewModel.sendNumberForOTP(phoneNumber)
                }
            }


            Toast.makeText(activity, "OTP sent again to " + phoneNumber, Toast.LENGTH_SHORT)
                .show()

            object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    oneTimeFragmentBinding.include2.countdown.text = "Try again after: " + millisUntilFinished / 1000 + "s"
                    oneTimeFragmentBinding.include2.resendOtp.visibility = View.INVISIBLE
                }

                override fun onFinish() {
                    oneTimeFragmentBinding.include2.countdown.text = "Don't receive the OTP ?"
                    oneTimeFragmentBinding.include2.resendOtp.visibility = View.VISIBLE
                }
            }.start()
        }


    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        ErrorUtils()
    }


}