package com.example.classclockin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentHomeBinding


class MarkAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
<<<<<<< HEAD:app/src/main/java/com/example/classclockin/Fragments/HomeFragment.kt
        return inflater.inflate(R.layout.fragment_home, container, false)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
=======
        return inflater.inflate(R.layout.fragment_mark_attendance, container, false)
>>>>>>> origin/master:app/src/main/java/com/example/classclockin/fragments/MarkAttendanceFragment.kt
    }

}