package com.fortradestudio.mapowergeolocationtracker.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentBlockerBinding
import com.fortradestudio.mapowergeolocationtracker.utils.ErrorUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class BlockerFragment : Fragment(), Thread.UncaughtExceptionHandler {

    private lateinit var blockerFragmentBinding: FragmentBlockerBinding

    companion object {
        private const val TAG = "BlockerFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        blockerFragmentBinding = FragmentBlockerBinding.inflate(inflater, container, false)
        return blockerFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler(this)
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_blockerFragment_to_homeFragment)

        } else {
            findNavController().navigate(
                R.id.action_blockerFragment_to_loginFragment
            )
        }

    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        ErrorUtils().report(e)
    }

}