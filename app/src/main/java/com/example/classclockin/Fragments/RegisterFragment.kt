package com.example.classclockin.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_register, container, false)

        // Initializing Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {

            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnReg.setOnClickListener{
            registerUser()
//            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
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

//        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//        val editor = sharedPref?.edit()
//        editor?.putString("first_name", firstName)
//        editor?.putString("last_name", lastName)
//        editor?.putString("teacher_id", teacherId)
//        editor?.putString("phone_number", phoneNumber)
//        editor?.putString("email_address", emailAddress)
//        editor?.putString("password", password)
//        editor?.apply()
//
//        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
//        view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)

        auth.createUserWithEmailAndPassword(emailAddress, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Registration successful, save additional user info
                    val user = auth.currentUser
                    user?.let {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = firstName // Optionally save additional profile information
                        }

                        user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    }



