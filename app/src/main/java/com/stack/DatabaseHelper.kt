package com.stack

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // Create Note Table
        val CREATE_NOTE_TABLE = ("CREATE TABLE " + TABLE_NOTE + "("
                + NOTE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOTE_COLUMN_TITLE + " TEXT,"
                + NOTE_COLUMN_CONTENT + " TEXT,"
                + NOTE_COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")")
        db.execSQL(CREATE_NOTE_TABLE)

        // Create Todo Table
        val CREATE_TODO_TABLE = ("CREATE TABLE " + TABLE_TODO + "("
                + TODO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TODO_COLUMN_TASK + " TEXT,"
                + TODO_COLUMN_STATUS + " INTEGER,"
                + TODO_COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")")
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODO")
        // Create tables again
        onCreate(db)
    }

    private val isUserLoggedIn: Boolean
        // Check if User is Logged In
        get() = Companion.isUserLoggedIn

    // Note CRUD Operations
    fun addNote(title: String?, content: String?) {
        if (!this.isUserLoggedIn) return
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOTE_COLUMN_TITLE, title)
        values.put(NOTE_COLUMN_CONTENT, content)
        db.insert(TABLE_NOTE, null, values)
        db.close()
    }

    val notes: Cursor?
        get() {
            if (!this.isUserLoggedIn) return null
            val db = this.readableDatabase
            val query = "SELECT * FROM " + TABLE_NOTE
            return db.rawQuery(query, null)
        }

    fun updateNote(id: Int, title: String?, content: String?) {
        if (!this.isUserLoggedIn) return
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOTE_COLUMN_TITLE, title)
        values.put(NOTE_COLUMN_CONTENT, content)
        values.put(NOTE_COLUMN_TIMESTAMP, System.currentTimeMillis())
        db.update(TABLE_NOTE, values, NOTE_COLUMN_ID + " = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteNote(id: Int) {
        if (!this.isUserLoggedIn) return
        val db = this.writableDatabase
        db.delete(TABLE_NOTE, NOTE_COLUMN_ID + " = ?", arrayOf(id.toString()))
        db.close()
    }

    // Todo CRUD Operations
    fun addTodo(task: String?, status: Int) {
        if (!this.isUserLoggedIn) return
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TODO_COLUMN_TASK, task)
        values.put(TODO_COLUMN_STATUS, status)
        db.insert(TABLE_TODO, null, values)
        db.close()
    }

    val todos: Cursor?
        get() {
            if (!this.isUserLoggedIn) return null
            val db = this.readableDatabase
            val query = "SELECT * FROM " + TABLE_TODO
            return db.rawQuery(query, null)
        }

    fun updateTodo(id: Int, task: String?, status: Int) {
        if (!this.isUserLoggedIn) return
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TODO_COLUMN_TASK, task)
        values.put(TODO_COLUMN_STATUS, status)
        values.put(TODO_COLUMN_TIMESTAMP, System.currentTimeMillis())
        db.update(TABLE_TODO, values, TODO_COLUMN_ID + " = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTodo(id: Int) {
        if (!this.isUserLoggedIn) return
        val db = this.writableDatabase
        db.delete(TABLE_TODO, TODO_COLUMN_ID + " = ?", arrayOf(id.toString()))
        db.close()
    }

    companion object {
        // Database Name and Version
        private const val DATABASE_NAME = "data.db"
        private const val DATABASE_VERSION = 1

        // Note Table
        private const val TABLE_NOTE = "note_table"
        private const val NOTE_COLUMN_ID = "id"
        private const val NOTE_COLUMN_TITLE = "title"
        private const val NOTE_COLUMN_CONTENT = "content"
        private const val NOTE_COLUMN_TIMESTAMP = "time_stamp"

        // Todo Table
        private const val TABLE_TODO = "todo_table"
        private const val TODO_COLUMN_ID = "id"
        private const val TODO_COLUMN_TASK = "task"
        private const val TODO_COLUMN_STATUS = "status"
        private const val TODO_COLUMN_TIMESTAMP = "time_stamp"

        // User Login Status
        private var isUserLoggedIn = false

        // Set User Login Status
        fun setUserLoggedIn(isLoggedIn: Boolean) {
            isUserLoggedIn = isLoggedIn
        }
    }
}
