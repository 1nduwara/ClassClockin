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
import android.widget.Toast
import com.example.classclockin.R
import com.example.classclockin.databinding.FragmentHomeBinding
import com.example.classclockin.databinding.FragmentMarkAttendanceBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MarkAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentMarkAttendanceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMarkAttendanceBinding.inflate(inflater, container, false)

        return binding.root
    }

    //date picker
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//________________________________________________________________________________________________________________________________
        // Initialize the TextInputEditText
        val dateInput = view.findViewById<TextInputEditText>(R.id.dateInput)

        // Set an onClickListener to show the DatePickerDialog
        dateInput.setOnClickListener {
            // Get current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create a DatePickerDialog and show it
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the date and set it to the EditText
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    dateInput.setText(formattedDate)
                },
                year, month, day
            )

            datePickerDialog.show()
        }


        // Initialize the Spinner
        val spinner: Spinner = view.findViewById(R.id.spinner)

        // Create a list of items for the Spinner
        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5") //<================================ Add Items

        // Create an ArrayAdapter using the string list and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set an item selected listener for the spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                val selectedItem = parent.getItemAtPosition(position).toString()

                // Show the selected item in a Toast message
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing when nothing is selected
            }
        }
    }








}