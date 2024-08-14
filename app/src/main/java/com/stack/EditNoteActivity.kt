package com.stack

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.properties.Delegates

class EditNoteActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var backBtn: FloatingActionButton
    private var noteId by Delegates.notNull<Int>()
    private var noteContent: String? = null
    private var noteTitle: String? = null
    private var noteTimeStampCreated: String? = null
    private var noteTimeStampUpdated: String? = null
    private lateinit var editTitle: EditText
    private lateinit var editNote: EditText

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
        noteId = intent.getIntExtra("note_id", -1)
        noteContent = intent.getStringExtra("note_content")
        noteTitle = intent.getStringExtra("note_title")
        noteTimeStampCreated = intent.getStringExtra("note_timestamp_created")
        noteTimeStampUpdated = intent.getStringExtra("note_timestamp_updated")

        Log.e("debug", "onCreate: $noteTitle $noteContent")
        editTitle = findViewById(R.id.edit_title)
        editNote = findViewById(R.id.edit_note)

        setSupportActionBar(toolbar)
        editTitle.setText(noteTitle)
        editNote.setText(noteContent)

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
        if ((editTitle.text.isNotEmpty() || editNote.text.isNotEmpty())) {
            val database = DatabaseHelper(this@EditNoteActivity)
            DatabaseHelper.setUserLoggedIn(true)

            if (intent.getBooleanExtra("editable", false)) {
                database.updateNote(noteId, noteTitle.text.toString(), noteContent.text.toString())
            } else {
                database.addNote(noteTitle.text.toString(), noteContent.text.toString())
            }

        } else {
            Log.e("tag", "saveNote: illegal")
        }
    }


}