package com.example.classclockin.fragments.homeCards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentAttendanceSummaryBinding


class AttendanceSummaryFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceSummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceSummaryBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_attendanceSummary_to_homeFragment)
        }


        return binding.root

    }
}