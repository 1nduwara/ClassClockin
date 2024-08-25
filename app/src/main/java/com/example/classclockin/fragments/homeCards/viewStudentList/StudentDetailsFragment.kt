package com.example.classclockin.fragments.homeCards.viewStudentList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentStudentDetailsBinding


class StudentDetailsFragment : Fragment() {
    lateinit var binding: FragmentStudentDetailsBinding


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
        binding = FragmentStudentDetailsBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_studentDetailsFragment_to_viewStudentList)
        }

        return binding.root
    }


}