package com.stack


class Todo(val id: Int, val task: String, val status: String, val timeStamp: String) {
    companion object{

        val STATUS_PENDING = "pending"
        val STATUS_DONE = "done"

    }
}

class Note(val id: Int, val title: String, val content: String, val timeStamp: String) {
}
