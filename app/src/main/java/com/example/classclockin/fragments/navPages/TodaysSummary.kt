package com.example.classclockin.fragments.navPages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentTodaysSummaryBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import java.util.*

class TodaysSummary : Fragment() {

    private lateinit var binding: FragmentTodaysSummaryBinding
    private lateinit var database: DatabaseReference

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
        binding = FragmentTodaysSummaryBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("attendanceSummary")

        setDefaultDateTime()
        setupDateTimePickers()

        // Fetch the total student count
        getStudentCount()

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_todaysSummary_to_attendanceSummaryFragment)
        }

        binding.navHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_todaysSummary_to_homeFragment)
        }

        binding.navNotification.setOnClickListener {
            it.findNavController().navigate(R.id.action_todaysSummary_to_notificationFragment)
        }

        binding.navAccount.setOnClickListener {
            it.findNavController().navigate(R.id.action_todaysSummary_to_accountFragment)
        }

        binding.button2.setOnClickListener {
            saveAttendanceSummary()
        }

        return binding.root
    }

    private fun setDefaultDateTime() {
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.format("dd/MM/yyyy", calendar).toString()
        val currentTime = DateFormat.format("HH:mm", calendar).toString()

        binding.outDate.setText(currentDate)
        binding.outTime.setText(currentTime)
    }

    private fun setupDateTimePickers() {
        binding.outDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    binding.outDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.outTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    binding.outTime.setText(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(requireContext())
            ).show()
        }
    }

    private fun getStudentCount() {
        database = FirebaseDatabase.getInstance().getReference("students") // Reference to the students table
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalStudents = 0L

                for (studentSnapshot in snapshot.children) {
                    val studentName = studentSnapshot.child("studentName").getValue(String::class.java)
                    val studentId = studentSnapshot.child("studentId").getValue(String::class.java)
                    val studentAttendance = studentSnapshot.child("studentAttendance").getValue(Float::class.java)

                    // Only count the student if all necessary fields are present and valid
                    if (studentName != null && studentId != null && studentAttendance != null) {
                        totalStudents++
                    }
                }

                // Update the TextView with the total student count
                binding.txtNoStu.text = totalStudents.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                Log.e("FirebaseError", "Error: ${error.message}")
            }
        })
    }


    private fun saveAttendanceSummary() {
        val attDate = binding.outDate.text.toString() // Expected format: "02/09/2024"
        val time = binding.outTime.text.toString()
        val totalPresent = binding.outTotPresent.text.toString()
        val totalAbsent = binding.outTotAbsent.text.toString()
        val totalLate = binding.outTotLate.text.toString()

        // Ensure the date is in the correct format
        if (attDate.isEmpty() || time.isEmpty() || totalPresent.isEmpty() || totalAbsent.isEmpty() || totalLate.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map to store the summary
        val attendanceSummary = mapOf(
            "date" to attDate,
            "time" to time,
            "totalPresent" to totalPresent,
            "totalAbsent" to totalAbsent,
            "totalLate" to totalLate
        )

        val date = binding.outDate.text.toString().replace("/", "-")
        database.child("attendanceSummary").child(date).setValue(attendanceSummary)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Attendance summary saved successfully", Toast.LENGTH_SHORT).show()
                    //redirect to the attendance summary page
                    view?.findNavController()?.navigate(R.id.action_todaysSummary_to_attendanceSummaryFragment)
                } else {
                    Toast.makeText(context, "Failed to save attendance summary", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseError", "Error: ${task.exception}")
                }
            }
    }
}