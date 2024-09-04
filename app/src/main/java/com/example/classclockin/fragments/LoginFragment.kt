package com.example.classclockin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.txtLinkReg.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        return binding.root
    }

    private fun loginUser() {
        val identifier = binding.txtUsername.text.toString().trim()
        val password = binding.txtPwd.text.toString().trim()

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please enter both email or teacher ID and password!", Toast.LENGTH_SHORT).show()
            return
        }

        if (identifier.contains('@')) {
            // If the identifier contains '@', treat it as an email
            signInWithEmail(identifier, password)
        } else {
            // Treat the identifier as teacher ID and find associated email
            retrieveEmailForTeacherId(identifier, password)
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun retrieveEmailForTeacherId(teacherId: String, password: String) {
        database.child("users").child(teacherId).child("emailAddress").get()
            .addOnSuccessListener { snapshot ->
                val email = snapshot.value as? String
                if (email != null) {
                    signInWithEmail(email, password)
                } else {
                    Toast.makeText(context, "Teacher ID not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to retrieve email", Toast.LENGTH_SHORT).show()
            }
    }
}