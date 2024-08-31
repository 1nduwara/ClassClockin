package com.example.classclockin.fragments.navPages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentNotificationBinding
import com.example.classclockin.fragments.dataModels.Notification
import com.google.firebase.database.DatabaseReference


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var database: DatabaseReference
    private lateinit var notificationContainer: LinearLayout

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
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        // Initialize notificationContainer after inflating the layout
        notificationContainer = view.findViewById(R.id.notification_container)

        // Example of adding a notification item
        addNotification("Remember to mark today’s attendance.", "7:30 AM")

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

    private fun addNotification(message: String, time: String) {
        // Inflate the notification item layout
        val inflater = LayoutInflater.from(context)
        val notificationView = inflater.inflate(R.layout.notification_item, notificationContainer, false)

        // Find and set the TextViews in the notification item layout
        val messageTextView = notificationView.findViewById<TextView>(R.id.notification_message)
        val timeTextView = notificationView.findViewById<TextView>(R.id.notification_time)

        messageTextView.text = message
        timeTextView.text = time

        // Add the notification item view to the container
        notificationContainer.addView(notificationView)
    }





}