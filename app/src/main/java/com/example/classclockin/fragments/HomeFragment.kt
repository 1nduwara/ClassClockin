package com.example.classclockin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Get the current date
        val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())

        // Setting the current date to the TextView
        binding.crd1TxtDate.text = "Today is $currentDate"

        binding.crd2TxtMarkAttendance.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_markAttendanceFragment)
        }
        binding.crd2ImgCalendar.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_markAttendanceFragment)
        }
        binding.crd2MarkAttendance.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_markAttendanceFragment)
        }

        binding.crd2ViewStudentList.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_viewStudentList)
        }

        binding.crd4ImgStudentList.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_viewStudentList)
        }

        binding.crd4TxtViewStuList.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_viewStudentList)
        }

        binding.navNotification.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        //navigation to account fragment
        binding.navAccount.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_accountFragment)
        }

        return binding.root
    }
}
