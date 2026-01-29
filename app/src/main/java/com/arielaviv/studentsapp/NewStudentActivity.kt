package com.arielaviv.studentsapp

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.arielaviv.studentsapp.model.Student
import com.arielaviv.studentsapp.model.StudentRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NewStudentActivity : AppCompatActivity() {

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutId: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextId: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var checkBoxChecked: CheckBox
    private lateinit var buttonCancel: Button
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_student)

        title = getString(R.string.new_student_title)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        textInputLayoutName = findViewById(R.id.textInputLayoutName)
        textInputLayoutId = findViewById(R.id.textInputLayoutId)
        editTextName = findViewById(R.id.editTextName)
        editTextId = findViewById(R.id.editTextId)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextAddress = findViewById(R.id.editTextAddress)
        checkBoxChecked = findViewById(R.id.checkBoxChecked)
        buttonCancel = findViewById(R.id.buttonCancel)
        buttonSave = findViewById(R.id.buttonSave)
    }

    private fun setupListeners() {
        buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        buttonSave.setOnClickListener {
            if (validateInput()) {
                saveStudent()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        val name = editTextName.text.toString().trim()
        val id = editTextId.text.toString().trim()

        if (name.isEmpty()) {
            textInputLayoutName.error = getString(R.string.error_empty_name)
            isValid = false
        } else {
            textInputLayoutName.error = null
        }

        if (id.isEmpty()) {
            textInputLayoutId.error = getString(R.string.error_empty_id)
            isValid = false
        } else if (StudentRepository.exists(id)) {
            textInputLayoutId.error = getString(R.string.error_duplicate_id)
            isValid = false
        } else {
            textInputLayoutId.error = null
        }

        return isValid
    }

    private fun saveStudent() {
        val student = Student(
            id = editTextId.text.toString().trim(),
            name = editTextName.text.toString().trim(),
            phone = editTextPhone.text.toString().trim(),
            address = editTextAddress.text.toString().trim(),
            isChecked = checkBoxChecked.isChecked
        )

        StudentRepository.add(student)
        Toast.makeText(this, "Student saved", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
}
