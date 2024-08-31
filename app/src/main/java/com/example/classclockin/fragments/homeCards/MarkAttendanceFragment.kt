package com.example.classclockin.fragments.homeCards

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.fragments.dataModels.Student
import com.example.classclockin.databinding.FragmentMarkAttendanceBinding
import com.example.classclockin.fragments.dataModels.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MarkAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentMarkAttendanceBinding
    private lateinit var database: DatabaseReference
    private var studentList: MutableList<Student> = mutableListOf()
    private var selectedDate: String = getCurrentDate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarkAttendanceBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference

        loadStudents()
        setupDateInput()

        // Handle back button click
        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_markAttendanceFragment_to_homeFragment)
        }

        binding.fabSaveAttendance.setOnClickListener {
            saveAttendance()
        }

        binding.navNotification.setOnClickListener{
            it.findNavController().navigate(R.id.action_markAttendanceFragment_to_notificationFragment)
        }

        binding.navAccount.setOnClickListener{
            it.findNavController().navigate(R.id.action_markAttendanceFragment_to_accountFragment)
        }


        return binding.root
    }

    private fun setupDateInput() {
        binding.dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Set the selected date in the TextInputEditText
                    selectedDate = "$selectedYear-${(selectedMonth + 1).toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
                    binding.dateInput.setText(selectedDate)
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        // Set current date as default
        binding.dateInput.setText(selectedDate)
    }

    private fun loadStudents() {
        database.child("students").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    student?.let { studentList.add(it) }
                }
                displayStudents()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load students", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayStudents() {
        binding.studentListLayout.removeAllViews()

        for (student in studentList) {
            val studentView = createStudentView(student)
            binding.studentListLayout.addView(studentView)
        }

        setupAllPresentButton()
    }

    private fun setupAllPresentButton() {
        binding.btnAllPresent.setOnClickListener {
            for (student in studentList) {
                student.studentAttendance = 100.0f // Mark all as present
                updateAttendance(student)
            }
        }
    }

    private fun createStudentView(student: Student): View {
        val studentView = LayoutInflater.from(context).inflate(R.layout.student_item_with_attendance, null)
        val nameTextView = studentView.findViewById<TextView>(R.id.txt_student_name)
        val attendanceSpinner = studentView.findViewById<Spinner>(R.id.spinner_attendance)

        val attendanceOptions = resources.getStringArray(R.array.attendance_options)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            attendanceOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        attendanceSpinner.adapter = adapter

        when (student.studentAttendance) {
            100.0f -> attendanceSpinner.setSelection(0) // Present
            0.0f -> attendanceSpinner.setSelection(1)   // Absent
            else -> attendanceSpinner.setSelection(2)   // Late
        }

        attendanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val newAttendance = when (position) {
                    0 -> 100.0f // Present
                    1 -> 0.0f   // Absent
                    else -> 50.0f // Late
                }

                if (student.studentAttendance != newAttendance) {
                    student.studentAttendance = newAttendance
                    updateAttendance(student)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        nameTextView.text = student.studentName

        return studentView
    }

    private fun saveAttendance() {
        for (student in studentList) {
            updateAttendance(student)
        }
//        addNotification("You have marked the attendance for $selectedDate", marked = true)
        // Display a Toast message after saving attendance
        Toast.makeText(requireContext(), "Attendance saved successfully!", Toast.LENGTH_SHORT).show()
    }

//    private fun addNotification(message: String, marked: Boolean = false) {
//        val notificationId = database.child("notifications").push().key ?: return
//        val notification = Notification(notificationId, message, System.currentTimeMillis(), marked)
//        database.child("notifications").child(notificationId).setValue(notification)
//    }

    private fun updateAttendance(student: Student) {
        val attendanceRef = database.child("students").child(student.studentId!!).child("attendanceRecords").child(selectedDate)

        attendanceRef.setValue(student.studentAttendance)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Attendance updated for ${student.studentName} on $selectedDate", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update attendance", Toast.LENGTH_SHORT).show()
            }

        displayStudents()
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
}
