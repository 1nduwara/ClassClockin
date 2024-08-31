package com.example.classclockin.fragments.navPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentAccountBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

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

        return binding.root
    }

    private fun showResetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.et_current_password)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.et_new_password)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.et_confirm_password)

        val tvCurrentPasswordError =
            dialogView.findViewById<TextView>(R.id.tv_current_password_error)
        val tvNewPasswordError = dialogView.findViewById<TextView>(R.id.tv_new_password_error)
        val tvConfirmPasswordError =
            dialogView.findViewById<TextView>(R.id.tv_confirm_password_error)

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

                // Validation logic
                if (currentPassword.isEmpty()) {
                    tvCurrentPasswordError.text = "Please enter your current password"
                    tvCurrentPasswordError.visibility = View.VISIBLE
                    isValid = false
                }

                if (newPassword.isEmpty()) {
                    tvNewPasswordError.text = "Please enter a new password"
                    tvNewPasswordError.visibility = View.VISIBLE
                    isValid = false
                } else if (newPassword.length < 6) {
                    tvNewPasswordError.text = "New password must be at least 6 characters long"
                    tvNewPasswordError.visibility = View.VISIBLE
                    isValid = false
                } else if (newPassword == currentPassword) {
                    tvNewPasswordError.text =
                        "New password cannot be the same as the current password"
                    tvNewPasswordError.visibility = View.VISIBLE
                    isValid = false
                }

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

}

