package com.arielaviv.studentsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arielaviv.studentsapp.adapter.StudentAdapter
import com.arielaviv.studentsapp.model.StudentRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentsListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private lateinit var fabAddStudent: FloatingActionButton

    private val newStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshList()
        }
    }

    private val studentDetailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_list)

        title = getString(R.string.students_list_title)

        recyclerView = findViewById(R.id.recyclerViewStudents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StudentAdapter(
            students = StudentRepository.getAll(),
            onItemClick = { student ->
                val intent = Intent(this, StudentDetailsActivity::class.java)
                intent.putExtra(EXTRA_STUDENT_ID, student.id)
                studentDetailsLauncher.launch(intent)
            },
            onCheckboxClick = { student ->
                StudentRepository.toggleChecked(student.id)
            }
        )
        recyclerView.adapter = adapter

        fabAddStudent = findViewById(R.id.fabAddStudent)
        fabAddStudent.setOnClickListener {
            val intent = Intent(this, NewStudentActivity::class.java)
            newStudentLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        adapter.updateStudents(StudentRepository.getAll())
    }

    companion object {
        const val EXTRA_STUDENT_ID = "extra_student_id"
    }
}
