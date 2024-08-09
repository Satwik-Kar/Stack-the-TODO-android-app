package com.stack

import android.util.Log
import com.stack.Todo.Companion.dateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Todo(val id: Int, val task: String, val status: String, val timeStamp: String) :
    Comparable<Todo> {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val STATUS_PENDING = "pending"
        val STATUS_DONE = "done"


    }

    override fun compareTo(other: Todo): Int {
        val date1 = dateFormat.parse(this.timeStamp)
        val date2 = dateFormat.parse(other.timeStamp)
        return date2?.compareTo(date1) ?: 0
    }


}

class Note(val id: Int, val title: String, val content: String, val timeStamp: String) :
    Comparable<Note> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun compareTo(other: Note): Int {
        val date1 = dateFormat.parse(this.timeStamp)
        val date2 = dateFormat.parse(other.timeStamp)
        return date2?.compareTo(date1) ?: 0
    }

}
