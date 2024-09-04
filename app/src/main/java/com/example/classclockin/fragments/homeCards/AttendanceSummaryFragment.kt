package com.example.classclockin.fragments.homeCards


import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentAttendanceSummaryBinding
import com.example.classclockin.fragments.AttendanceSummaryAdapter
import com.example.classclockin.fragments.dataModels.AttendanceSummary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date
import java.util.Locale


class AttendanceSummaryFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceSummaryBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var attendanceSummaryAdapter: AttendanceSummaryAdapter
    private lateinit var recyclerView: RecyclerView
    private val attendanceSummaries = mutableListOf<AttendanceSummary>()


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
        databaseReference = FirebaseDatabase.getInstance().reference

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_attendanceSummaryFragment_to_homeFragment)
        }

        binding.btnTodaySummary.setOnClickListener {
            it.findNavController().navigate(R.id.action_attendanceSummaryFragment_to_TodaysSummary)
        }

        // Initialize RecyclerView
        recyclerView = binding.recyclerView // Use binding to find the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        attendanceSummaryAdapter = AttendanceSummaryAdapter(attendanceSummaries)
        recyclerView.adapter = attendanceSummaryAdapter

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("students/attendanceSummary")

        // Load Attendance Summaries
        loadAttendanceSummaries()

        return binding.root

    }

    private fun loadAttendanceSummaries() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                attendanceSummaries.clear()
                val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                for (dateSnapshot in snapshot.children) {
                    val summary = dateSnapshot.getValue(AttendanceSummary::class.java)
                    summary?.let {
                        attendanceSummaries.add(it)
                    }
                }

                // Sort the list by date, with today's date at the top
                attendanceSummaries.sortWith(compareByDescending<AttendanceSummary> {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)
                }.thenByDescending {
                    it.date == today
                })

                attendanceSummaryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}