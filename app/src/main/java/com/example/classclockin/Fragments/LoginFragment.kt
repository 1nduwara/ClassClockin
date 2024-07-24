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
import com.example.classclockin.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.txtLinkReg.setOnClickListener{
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener{
            loginUser()
//            it.findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

//        binding.btnDelete.setOnClickListener {
//            clearAllUserData()
//        }


        return binding.root
    }

    private fun loginUser() {
        val username = binding.txtUsername.text.toString().trim()
        val password = binding.txtPwd.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            // Display a message to the user
            Toast.makeText(context, "Please enter both username and password!", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val storedFirstName = sharedPref?.getString("first_name", "")
        val storedTeacherId = sharedPref?.getString("teacher_id", "")
        val storedEmail = sharedPref?.getString("email_address", "")
        val storedPassword = sharedPref?.getString("password", "")

        if ((username == storedFirstName || username == storedTeacherId || username == storedEmail) && password == storedPassword) {
            // Navigate to HomeFragment
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
        } else {
            // Display a message to the user
            Toast.makeText(context, "Invalid username or password!", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun clearAllUserData() {
//        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//        val editor = sharedPref?.edit()
//
//        // Clear all data
//        editor?.clear()
//
//        // Commit the changes
//        editor?.apply()
//
//        Toast.makeText(context, "All user data cleared", Toast.LENGTH_SHORT).show()
//    }


}