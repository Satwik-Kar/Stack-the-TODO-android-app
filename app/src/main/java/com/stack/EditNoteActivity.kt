package com.stack

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditNoteActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var backBtn: FloatingActionButton

    override fun onDestroy() {
        super.onDestroy()
        saveNote()
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_note)
        backBtn = findViewById(R.id.backBtn)
        toolbar = findViewById(R.id.note_toolbar)
        toolbar.title = ""

        setSupportActionBar(toolbar)
        backBtn.setOnClickListener {

            saveNote()
            finish()
        }
        onBackPressedDispatcher.addCallback(this) {
            saveNote()

            finish()
        }

    }


    private fun saveNote(): Unit {
        val noteTitle = findViewById<EditText>(R.id.edit_title)
        val noteContent = findViewById<EditText>(R.id.edit_note)
        if (noteTitle.text.isNotEmpty() || noteContent.text.isNotEmpty()) {
            val database = DatabaseHelper(this@EditNoteActivity)
            DatabaseHelper.setUserLoggedIn(true)
            database.addNote(noteTitle.text.toString(), noteContent.text.toString())
        }
    }


}