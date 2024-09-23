package com.example.classclockin.fragments.navPages

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentAccountBinding
import com.example.classclockin.fragments.dataModels.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth
//    val profileImageView = binding.imageView3

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Set selected image to ImageView
            binding.imageView3.setImageURI(it)

            // Save image to local storage
            saveImageLocally(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()


        binding.btnUploadpicture.setOnClickListener {
            // Open image picker
            pickImage.launch("image/*")
        }

        loadImageFromStorage()

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_accountFragment_to_homeFragment)
        }

        binding.navNotification.setOnClickListener {
            it.findNavController().navigate(R.id.action_accountFragment_to_notificationFragment)
        }

        binding.navHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_accountFragment_to_homeFragment)
        }

        binding.btnResetPwd.setOnClickListener {
            showResetPasswordDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                fetchTeacherInfo() { teacher ->
                    showEditProfileDialog(teacher)
                }
            } else {
                Toast.makeText(context, "No authenticated user found", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch and display teacher info
        fetchTeacherInfo()

        return binding.root
    }


    private fun saveImageLocally(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "profile_picture.png")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun loadImageFromStorage() {
        val file = File(requireContext().filesDir, "profile_picture.png")
        if (file.exists()) {
            binding.imageView3.setImageURI(Uri.fromFile(file))
        } else {
            // Set default placeholder if no image is saved
            binding.imageView3.setImageResource(R.drawable.placeholder_image)
        }
    }

    private fun showResetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.et_current_password)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.et_new_password)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.et_confirm_password)

        val tvCurrentPasswordError = dialogView.findViewById<TextView>(R.id.tv_current_password_error)
        val tvNewPasswordError = dialogView.findViewById<TextView>(R.id.tv_new_password_error)
        val tvConfirmPasswordError = dialogView.findViewById<TextView>(R.id.tv_confirm_password_error)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Reset Password")
            .setView(dialogView)
            .setPositiveButton("Reset", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val resetButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            resetButton.setOnClickListener {
                val currentPassword = etCurrentPassword.text.toString().trim()
                val newPassword = etNewPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                var isValid = true

                // Reset error messages
                tvCurrentPasswordError.visibility = View.GONE
                tvNewPasswordError.visibility = View.GONE
                tvConfirmPasswordError.visibility = View.GONE

                // Validate current password
                if (currentPassword.isEmpty()) {
                    tvCurrentPasswordError.text = "Please enter your current password"
                    tvCurrentPasswordError.visibility = View.VISIBLE
                    isValid = false
                }

                // Validate new password
                if (newPassword.isEmpty()) {
                    tvNewPasswordError.text = "Please enter a new password"
                    tvNewPasswordError.visibility = View.VISIBLE
                    isValid = false
                } else if (!isPasswordValid(newPassword)) {
                    tvNewPasswordError.text = "Password must be at least 8 characters long, contain upper and lowercase letters, numbers, and special characters"
                    tvNewPasswordError.visibility = View.VISIBLE
                    isValid = false
                } else if (newPassword == currentPassword) {
                    tvNewPasswordError.text = "New password cannot be the same as the current password"
                    tvNewPasswordError.visibility = View.VISIBLE
                    isValid = false
                }

                // Validate confirm password
                if (confirmPassword.isEmpty()) {
                    tvConfirmPasswordError.text = "Please confirm your new password"
                    tvConfirmPasswordError.visibility = View.VISIBLE
                    isValid = false
                } else if (confirmPassword != newPassword) {
                    tvConfirmPasswordError.text = "Passwords do not match"
                    tvConfirmPasswordError.visibility = View.VISIBLE
                    isValid = false
                }

                // If all validations pass, proceed with updating the password
                if (isValid) {
                    updatePassword(currentPassword, newPassword, tvCurrentPasswordError, dialog)
                }
            }
        }

        dialog.show()
    }

    // Helper function to validate the new password
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
        return password.matches(passwordPattern)
    }



    private fun updatePassword(
        currentPassword: String,
        newPassword: String,
        tvCurrentPasswordError: TextView,
        dialog: AlertDialog
    ) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            Toast.makeText(context, "Password reset successful", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Password reset failed: ${passwordTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    tvCurrentPasswordError.text = "Current password is incorrect"
                    tvCurrentPasswordError.visibility = View.VISIBLE
                }
            }
        } else {
            Toast.makeText(context, "No authenticated user found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTeacherInfo(callback: ((User) -> Unit)? = null) {
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email

            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")

            // Query to find the user with the matching email
            val query = usersRef.orderByChild("emailAddress").equalTo(userEmail)

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result.exists()) {
                        for (snapshot in result.children) {
                            val teacher = snapshot.getValue(User::class.java)
                            teacher?.let {
                                populateTeacherInfo(it)
                                if (callback != null) {
                                    callback(it)
                                } // Pass the teacher object back
                            }
                        }
                    } else {
                        Toast.makeText(context, "No matching user found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(context, "Failed to load teacher info: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            Toast.makeText(context, "No authenticated user found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun populateTeacherInfo(teacher: User) {
        binding.txtCTName.text = teacher.firstName
        binding.outCtId.text = teacher.teacherId
        binding.outCtPhone.text = teacher.phoneNumber
        binding.outCtEmail.text = teacher.emailAddress
        // Display the user's birthday
        binding.outCtDob.text = teacher.birthday
    }

    @SuppressLint("MissingInflatedId")
    private fun showEditProfileDialog(teacher: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val etFirstName = dialogView.findViewById<TextInputEditText>(R.id.et_first_name)
        val etLastName = dialogView.findViewById<TextInputEditText>(R.id.et_last_name)
        val etPhoneNumber = dialogView.findViewById<TextInputEditText>(R.id.et_phone_number)
        val etBirthday = dialogView.findViewById<TextInputEditText>(R.id.et_birthDay)

        // Pre-fill the fields with the current data
        etFirstName.setText(teacher.firstName)
        etLastName.setText(teacher.lastName)
        etPhoneNumber.setText(teacher.phoneNumber)
        etBirthday.setText(teacher.birthday)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val newFirstName = etFirstName.text.toString().trim()
                val newLastName = etLastName.text.toString().trim()
                val newPhoneNumber = etPhoneNumber.text.toString().trim()
                val newBirthday = etBirthday.text.toString().trim()

                if (newFirstName.isEmpty() || newLastName.isEmpty() || newPhoneNumber.isEmpty() || newBirthday.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Update the user's information in the database
                updateTeacherInfo(
                    teacher.emailAddress,
                    newFirstName,
                    newLastName,
                    newPhoneNumber,
                    newBirthday
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }



    private fun updateTeacherInfo(
        email: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        birthday: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        val query = usersRef.orderByChild("emailAddress").equalTo(email)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (result.exists()) {
                    for (snapshot in result.children) {
                        val userId = snapshot.key
                        userId?.let {
                            val userRef = usersRef.child(it)
                            userRef.child("firstName").setValue(firstName)
                            userRef.child("lastName").setValue(lastName)
                            userRef.child("phoneNumber").setValue(phoneNumber)
                            userRef.child("birthday").setValue(birthday).addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT)
                                        .show()
                                    // Fetch and refresh the teacher info on the screen
                                    fetchTeacherInfo()
                                } else {
                                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "No matching user found", Toast.LENGTH_SHORT).show()
                }
            } else {
                val errorMessage = task.exception?.message
                Toast.makeText(context, "Failed to update profile: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

