package com.example.classclockin.fragments.homeCards.viewStudentList

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.classclockin.R
import com.example.classclockin.fragments.dataModels.Student
import com.example.classclockin.databinding.FragmentViewStudentListBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale

class ViewStudentList : Fragment() {

    private lateinit var binding: FragmentViewStudentListBinding
    private lateinit var database: DatabaseReference
    private var studentList = mutableListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewStudentListBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference

        // Setup search functionality
        setupSearchView()

        // Handling all the button clicks
        setupButtonListeners()

        // Load students from Firebase
        loadStudents()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterStudents(newText ?: "")
                return true
            }
        })
    }

    private fun filterStudents(query: String) {
        val filteredList = studentList.filter {
            it.studentName?.contains(query, ignoreCase = true) == true
        }
        displayStudents(filteredList)
    }

    private fun loadStudents() {
        database.child("students").orderByChild("studentName").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()  // Clear the list before adding new data
                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    // Ensure student ID is not null or empty
                    if (!student?.studentId.isNullOrEmpty()) {
                        student?.let { studentList.add(it) }
                    }
                }
                displayStudents(studentList)  // Display all students initially
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load students", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayStudents(students: List<Student>) {
        binding.studentListLayout.removeAllViews()
        var currentLetter = ""

        for (student in students) {
            val firstLetter = student.studentName.firstOrNull()?.uppercase(Locale.getDefault()).toString()
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

    private fun setupButtonListeners() {
        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_viewStudentList_to_homeFragment)
        }

        binding.navNotification.setOnClickListener{
            it.findNavController().navigate(R.id.action_viewStudentList_to_notificationFragment)
        }

        binding.navHome.setOnClickListener{
            it.findNavController().navigate(R.id.action_viewStudentList_to_homeFragment)
        }

        binding.navAccount.setOnClickListener{
            it.findNavController().navigate(R.id.action_viewStudentList_to_accountFragment)
        }

        // Handle add student button click
        binding.fabAddStudent.setOnClickListener {
            addStudent()
        }

        // Handle filter by attendance button click
        binding.btnFilter.setOnClickListener {
            filterByAttendance()
        }
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

            // Save student info with photo URI directly
            if (selectedPhotoUri != null) {
                // Upload the image to Firebase Storage
                uploadImageToFirebaseStorage(selectedPhotoUri!!, studentId) { downloadUrl ->
                    // After image is uploaded, save the student info with the download URL
                    saveStudentToDatabase(studentId, studentName, downloadUrl)
                    alertDialog.dismiss()
                }
            } else {
                Toast.makeText(context, "Please select a photo", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun uploadAndSaveStudent(imageUri: Uri, studentId: String, studentName: String) {
        // Upload image to Firebase Storage
        uploadImageToFirebaseStorage(imageUri, studentId) { downloadUrl ->
            // Once the image URL is available, save student data along with the image URL
            saveStudentToDatabase(studentId, studentName, downloadUrl)
        }
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, studentId: String, onSuccess: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().getReference("student_photos/$studentId.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL after successful upload
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())  // Return the download URL
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Failed to upload image", exception)
            }
    }

    private fun saveStudentToDatabase(studentId: String, studentName: String, studentPhotoUrl: String) {
        // Create student object with all relevant information
        val student = Student(
            studentPhoto = studentPhotoUrl,
            studentId = studentId,
            studentName = studentName,
            studentAttendance = 0.0f,
        )

        // Save student to Firebase Realtime Database
        database.child("students").child(studentId).setValue(student)
            .addOnSuccessListener {
                Log.d("FirebaseDatabase", "Student saved successfully!")
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseDatabase", "Failed to save student", exception)
            }
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

    private fun filterByAttendance() {
        val sortedList = studentList.sortedBy { it.studentAttendance }
        displayStudents(sortedList)
    }
}


