package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentBlockerBinding

class BlockerFragment : Fragment() {

    private lateinit var blockerFragmentBinding : FragmentBlockerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        blockerFragmentBinding = FragmentBlockerBinding.inflate(inflater,container,false)
        return blockerFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        blockerFragmentBinding.startExperienceBtn.setOnClickListener { findNavController().navigate(R.id.action_blockerFragment_to_loginFragment) }

    }


}