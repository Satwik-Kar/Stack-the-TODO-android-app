package com.stack

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class EditNoteActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_note)
        toolbar = findViewById(R.id.note_toolbar)
        toolbar.title = ""

        setSupportActionBar(toolbar)
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