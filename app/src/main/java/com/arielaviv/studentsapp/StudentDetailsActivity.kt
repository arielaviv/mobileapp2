package com.arielaviv.studentsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.arielaviv.studentsapp.model.Student
import com.arielaviv.studentsapp.model.StudentRepository

class StudentDetailsActivity : AppCompatActivity() {

    private lateinit var imageViewAvatar: ImageView
    private lateinit var textViewName: TextView
    private lateinit var textViewId: TextView
    private lateinit var textViewPhone: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var buttonEdit: Button

    private var studentId: String? = null

    private val editStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                loadStudentData()
                setResult(RESULT_OK)
            }
            RESULT_STUDENT_DELETED -> {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        title = getString(R.string.student_details_title)

        studentId = intent.getStringExtra(StudentsListActivity.EXTRA_STUDENT_ID)
        Log.d("StudentDetails", "Opening student: $studentId")

        imageViewAvatar = findViewById(R.id.imageViewAvatar)
        textViewName = findViewById(R.id.textViewName)
        textViewId = findViewById(R.id.textViewId)
        textViewPhone = findViewById(R.id.textViewPhone)
        textViewAddress = findViewById(R.id.textViewAddress)
        textViewStatus = findViewById(R.id.textViewStatus)
        buttonEdit = findViewById(R.id.buttonEdit)

        loadStudentData()

        buttonEdit.setOnClickListener {
            val intent = Intent(this, EditStudentActivity::class.java)
            intent.putExtra(StudentsListActivity.EXTRA_STUDENT_ID, studentId)
            editStudentLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadStudentData()
    }

    private fun loadStudentData() {
        val student = studentId?.let { StudentRepository.getById(it) }

        if (student == null) {
            finish()
            return
        }

        imageViewAvatar.setImageResource(student.avatarResId)
        textViewName.text = student.name
        textViewId.text = student.id
        textViewPhone.text = student.phone.ifEmpty { "-" }
        textViewAddress.text = student.address.ifEmpty { "-" }
        textViewStatus.text = if (student.isChecked) {
            getString(R.string.checked_status)
        } else {
            getString(R.string.unchecked_status)
        }
    }

    companion object {
        const val RESULT_STUDENT_DELETED = 2
    }
}
