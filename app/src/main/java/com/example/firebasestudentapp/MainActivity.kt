package com.example.firebasestudentapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var studentList: MutableList<Student>
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val course = findViewById<EditText>(R.id.etCourse)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        database = FirebaseDatabase.getInstance().getReference("Students")
        studentList = mutableListOf()
        adapter = StudentAdapter(studentList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // SAVE DATA
        btnSave.setOnClickListener {
            val studentId = database.push().key!!

            val student = Student(
                studentId,
                name.text.toString(),
                email.text.toString(),
                course.text.toString()
            )

            database.child(studentId).setValue(student).addOnSuccessListener {
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

        // RETRIEVE DATA
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                studentList.clear()
                for (data in snapshot.children) {
                    val student = data.getValue(Student::class.java)
                    if (student != null) {
                        studentList.add(student)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}