package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentLoginBinding
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment.LoginFragmentViewModel
import com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment.LoginFragmentViewModelFactory


class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "LoginFragment"

        private const val number_cache_key = "phoneNumber"
    }

    private lateinit var loginFragmentBinding: FragmentLoginBinding
    private lateinit var loginFragmentViewModel: LoginFragmentViewModel

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
                loginFragmentViewModel.checkIfMobileNumberIsValid(phoneNumber) {
                    if (it) {
                        loginFragmentViewModel.showDialog()
                        loginFragmentViewModel.sendNumberForOTP(phoneNumber)
                    }
                    else {
                        phoneNumberEditText.error = getString(R.string.invalidNumber)
                    }
                }
            }
        }
    }
}