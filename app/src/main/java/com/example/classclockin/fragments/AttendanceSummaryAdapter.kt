package com.example.classclockin.fragments

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.classclockin.R
import com.example.classclockin.fragments.dataModels.AttendanceSummary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class AttendanceSummaryAdapter(
    private val attendanceSummaries: List<AttendanceSummary>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_summary, parent, false)
        return AttendanceSummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val attendanceSummary = attendanceSummaries[position]
        (holder as AttendanceSummaryViewHolder).bind(attendanceSummary)
    }

    override fun getItemCount(): Int {
        return attendanceSummaries.size
    }

    class AttendanceSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.txtDate)
        private val tvTotalPresent: TextView = itemView.findViewById(R.id.txtTotalPresent)
        private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("students")

        fun bind(attendanceSummary: AttendanceSummary) {
            val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val targetFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

            val date = originalFormat.parse(attendanceSummary.date)
            val formattedDate = targetFormat.format(date)

            tvDate.text = formattedDate

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalStudents = 0L

                    for (studentSnapshot in snapshot.children) {
                        val studentName = studentSnapshot.child("studentName").getValue(String::class.java)
                        val studentId = studentSnapshot.child("studentId").getValue(String::class.java)
                        val studentAttendance = studentSnapshot.child("studentAttendance").getValue(Float::class.java)

                        if (studentName != null && studentId != null && studentAttendance != null) {
                            totalStudents++
                        }
                    }

                    tvTotalPresent.text = "Total Present: ${attendanceSummary.totalPresent} / $totalStudents"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(itemView.context, "Failed to load student count", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
