package com.example.classclockin.fragments.navPages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

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
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
        }
        binding.navHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
        }
        binding.navAccount.setOnClickListener {
            it.findNavController().navigate(R.id.action_notificationFragment_to_accountFragment)
        }

        return binding.root

    }


}