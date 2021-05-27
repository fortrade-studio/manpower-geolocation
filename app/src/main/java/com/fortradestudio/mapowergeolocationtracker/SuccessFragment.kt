package com.fortradestudio.mapowergeolocationtracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.sackcentury.shinebuttonlib.ShineButton


class SuccessFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<ShineButton>(R.id.po_image2)
        val backBtn = view.findViewById<Button>(R.id.backBtn)
        Handler(Looper.getMainLooper()).postDelayed({
            button.performClick()
        },300)

        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_successFragment_to_clockFragment)
        }

    }

}