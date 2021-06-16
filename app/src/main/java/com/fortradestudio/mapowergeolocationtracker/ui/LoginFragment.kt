package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentLoginBinding
import com.fortradestudio.mapowergeolocationtracker.utils.ErrorUtils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.SharedViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment.LoginFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment.LoginFragmentViewModelFactory


class LoginFragment : Fragment() , Thread.UncaughtExceptionHandler{

    companion object {
        private const val TAG = "LoginFragment"

        private const val number_cache_key = "phoneNumber"
    }

    private lateinit var loginFragmentBinding: FragmentLoginBinding
    private lateinit var loginFragmentViewModel: LoginFragmentViewModel

    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        loginFragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return loginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler(this)

        loginFragmentViewModel =
            ViewModelProvider(
                this,
                LoginFragmentViewModelFactory(requireActivity(), requireView())
            ).get(
                LoginFragmentViewModel::class.java
            )

        with(loginFragmentBinding) {
            verifyButton.setOnClickListener {
                val phoneNumber = phoneNumberEditText.text.toString()
//                val phoneNumber = "1234567890"
                sharedViewModel.savePhoneNumber(phoneNumber)
                loginFragmentViewModel.checkIfMobileNumberIsValid(phoneNumber) {
                    if (it) {
                        loginFragmentViewModel.showDialog()
                        loginFragmentViewModel.sendNumberForOTP(phoneNumber)
                    } else {
                        phoneNumberEditText.error = getString(R.string.invalidNumber)
                    }
                }
            }
        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {

        ErrorUtils().report(e)
    }
}

