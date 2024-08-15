package com.example.classclockin.fragments.homeCards

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.classclockin.R
import com.example.classclockin.Student
import com.example.classclockin.databinding.FragmentViewStudentListBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale

class ViewStudentList : Fragment() {

    private lateinit var binding: FragmentViewStudentListBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewStudentListBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance().reference

        // Handle back button click
        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_viewStudentList_to_homeFragment)
        }

        // Handle add student button click
        binding.fabAddStudent.setOnClickListener {
            addStudent()
        }

        // Load students from Firebase
        loadStudents()

        return binding.root
    }

    private fun addStudent() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_students, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Student")

        val alertDialog = dialogBuilder.show()

        val studentNameEditText = dialogView.findViewById<EditText>(R.id.editTextStudentName)
        val studentIdEditText = dialogView.findViewById<EditText>(R.id.editTextStudentId)
        val selectPhotoButton = dialogView.findViewById<Button>(R.id.buttonSelectPhoto)
        val studentPhotoImageView = dialogView.findViewById<ImageView>(R.id.imageViewStudentPhoto)
        val addStudentButton = dialogView.findViewById<Button>(R.id.buttonAddStudent)

        var selectedPhotoUri: Uri? = null

        // Handle photo selection
        selectPhotoButton.setOnClickListener {
            selectPhotoFromGallery { uri ->
                selectedPhotoUri = uri
                studentPhotoImageView.setImageURI(uri)
            }
        }

        // Handle adding the student
        addStudentButton.setOnClickListener {
            val studentName = studentNameEditText.text.toString().trim()
            val studentId = studentIdEditText.text.toString().trim()

            if (studentName.isEmpty() || studentId.isEmpty()) {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Upload photo to Firebase Storage if selected
            if (selectedPhotoUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference.child("student_photos/$studentId.jpg")
                storageRef.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            saveStudentToDatabase(studentId, studentName, uri.toString())
                            alertDialog.dismiss()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // No photo selected, just save student info without photo URL
                saveStudentToDatabase(studentId, studentName, "")
                alertDialog.dismiss()
            }
        }
    }

    private fun saveStudentToDatabase(studentId: String, studentName: String, studentPhotoUrl: String) {
        val student = Student(
            studentPhoto = studentPhotoUrl,
            studentId = studentId,
            studentName = studentName,
            studentAttendance = 0.0f
        )
        database.child("students").child(studentId).setValue(student)
    }

    private fun selectPhotoFromGallery(callback: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
        this.photoSelectionCallback = callback
    }

    private val PICK_IMAGE_REQUEST = 1
    private var photoSelectionCallback: ((Uri) -> Unit)? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                photoSelectionCallback?.invoke(uri)
            }
        }
    }



    private fun loadStudents() {
        database.child("students").orderByChild("studentName").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.studentListLayout.removeAllViews()
                var currentLetter = ""

                val studentsList = mutableListOf<Student>()

                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    student?.let { studentsList.add(it) }
                }

                studentsList.sortBy { it.studentName?.toUpperCase(Locale.getDefault()) }

                for (student in studentsList) {
                    val firstLetter = student.studentName?.firstOrNull()?.toUpperCase().toString()
                    if (firstLetter != currentLetter) {
                        currentLetter = firstLetter
                        val letterView = LayoutInflater.from(context).inflate(R.layout.letter_separator, null)
                        val letterText = letterView.findViewById<TextView>(R.id.letterText)
                        letterText.text = currentLetter
                        binding.studentListLayout.addView(letterView)
                    }

                    val studentView = createStudentView(student)
                    binding.studentListLayout.addView(studentView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun createStudentView(student: Student?): View {
        val studentView = LayoutInflater.from(context).inflate(R.layout.student_item, null)
        val studentName = studentView.findViewById<TextView>(R.id.studentName)
        val studentId = studentView.findViewById<TextView>(R.id.studentId)
        val studentAttendance = studentView.findViewById<TextView>(R.id.studentAttendance)
        val studentPhoto = studentView.findViewById<ImageView>(R.id.studentPhoto)

        studentName.text = student?.studentName
        studentId.text = "ID: ${student?.studentId}"
        studentAttendance.text = "${student?.studentAttendance?.toInt()}%"

        // Load student photo using Glide or similar library
        if (!student?.studentPhoto.isNullOrEmpty()) {
            Glide.with(this).load(student?.studentPhoto).into(studentPhoto)
        } else {
            studentPhoto.setImageResource(R.drawable.placeholder_image) // default image if no photo
        }

        return studentView
    }

}
