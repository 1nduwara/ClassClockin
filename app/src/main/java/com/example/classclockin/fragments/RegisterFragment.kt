package com.example.classclockin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentRegisterBinding
import com.example.classclockin.fragments.dataModels.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initializing Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnReg.setOnClickListener {
            registerUser()
        }

        return binding.root
    }

    private fun registerUser() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val teacherId = binding.etTeacherId.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val emailAddress = binding.etEmailAddress.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || teacherId.isEmpty() || phoneNumber.isEmpty() || emailAddress.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(emailAddress, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = firstName // saving additional profile information
                        }

                        user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                saveUserData(firstName, lastName, teacherId, phoneNumber, emailAddress)
                            }
                        }
                    }
                } else {
                    // Show error message as a toast if the registration failed
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(firstName: String, lastName: String, teacherId: String, phoneNumber: String, emailAddress: String) {
        val user = User(
            firstName = firstName,
            lastName = lastName,
            teacherId = teacherId,
            phoneNumber = phoneNumber,
            emailAddress = emailAddress
        )

        database.child("users").child(teacherId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "User data saved successfully", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Toast.makeText(context, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}



