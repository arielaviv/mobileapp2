package com.arielaviv.studentsapp

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arielaviv.studentsapp.model.Student
import com.arielaviv.studentsapp.model.StudentRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditStudentActivity : AppCompatActivity() {

    private lateinit var imageViewAvatar: ImageView
    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutId: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextId: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var checkBoxChecked: CheckBox
    private lateinit var buttonCancel: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonSave: Button

    private var originalStudentId: String? = null
    private var originalStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        title = getString(R.string.edit_student_title)

        originalStudentId = intent.getStringExtra(StudentsListActivity.EXTRA_STUDENT_ID)

        initViews()
        loadStudentData()
        setupListeners()
    }

    private fun initViews() {
        imageViewAvatar = findViewById(R.id.imageViewAvatar)
        textInputLayoutName = findViewById(R.id.textInputLayoutName)
        textInputLayoutId = findViewById(R.id.textInputLayoutId)
        editTextName = findViewById(R.id.editTextName)
        editTextId = findViewById(R.id.editTextId)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextAddress = findViewById(R.id.editTextAddress)
        checkBoxChecked = findViewById(R.id.checkBoxChecked)
        buttonCancel = findViewById(R.id.buttonCancel)
        buttonDelete = findViewById(R.id.buttonDelete)
        buttonSave = findViewById(R.id.buttonSave)
    }

    private fun loadStudentData() {
        originalStudent = originalStudentId?.let { StudentRepository.getById(it) }

        if (originalStudent == null) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        originalStudent?.let { student ->
            imageViewAvatar.setImageResource(student.avatarResId)
            editTextName.setText(student.name)
            editTextId.setText(student.id)
            editTextPhone.setText(student.phone)
            editTextAddress.setText(student.address)
            checkBoxChecked.isChecked = student.isChecked
        }
    }

    private fun setupListeners() {
        buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        buttonDelete.setOnClickListener {
            showDeleteConfirmation()
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
        } else if (id != originalStudentId && StudentRepository.exists(id)) {
            textInputLayoutId.error = getString(R.string.error_duplicate_id)
            isValid = false
        } else {
            textInputLayoutId.error = null
        }

        return isValid
    }

    private fun saveStudent() {
        val updatedStudent = Student(
            id = editTextId.text.toString().trim(),
            name = editTextName.text.toString().trim(),
            phone = editTextPhone.text.toString().trim(),
            address = editTextAddress.text.toString().trim(),
            isChecked = checkBoxChecked.isChecked,
            avatarResId = originalStudent?.avatarResId ?: R.drawable.ic_student_avatar
        )

        originalStudentId?.let { oldId ->
            StudentRepository.update(oldId, updatedStudent)
        }

        setResult(RESULT_OK)
        finish()
    }

    // TODO: maybe add undo option instead of dialog
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Delete") { _, _ ->
                deleteStudent()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStudent() {
        originalStudentId?.let { id ->
            StudentRepository.delete(id)
        }

        setResult(StudentDetailsActivity.RESULT_STUDENT_DELETED)
        finish()
    }
}
