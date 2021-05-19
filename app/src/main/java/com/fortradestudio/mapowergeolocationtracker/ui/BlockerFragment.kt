package com.fortradestudio.mapowergeolocationtracker.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.databinding.FragmentBlockerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class BlockerFragment : Fragment() {

    private lateinit var blockerFragmentBinding: FragmentBlockerBinding

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


        val auth = FirebaseAuth.getInstance();
        val database = Firebase.database
        val myRef = database.getReference("key")

//        Blocker key
        myRef.setValue("1234")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
//                Log.d("key", "Value is: $value")

                blockerFragmentBinding.startExperienceBtn.setOnClickListener {
                    val keyValue = blockerFragmentBinding.keyValue.text.toString()
//                    key checking
                    if (value == keyValue) {
                        if (auth.currentUser != null) {
                            findNavController().navigate(R.id.action_blockerFragment_to_homeFragment)

                        } else {
                            findNavController().navigate(
                                R.id.action_blockerFragment_to_loginFragment
                            )
                        }
                    } else {
                        Toast.makeText(activity, "Enter valid key!", Toast.LENGTH_SHORT).show()
                    }
                }
//                Toast.makeText(this,"done",L)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("key", "Failed to read value.", error.toException())
            }
        })


    }


}