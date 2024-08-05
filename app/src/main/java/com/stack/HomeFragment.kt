package com.stack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date

public class HomeFragment : Fragment() {
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabAddNote: ExtendedFloatingActionButton
    private lateinit var fabAddTodo: ExtendedFloatingActionButton
    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private lateinit var motionLayout: MotionLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todos
        val notesRecyclerView = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        notesRecyclerView.layoutManager = layoutManager
        val todosRecyclerView = view.findViewById<RecyclerView>(R.id.todosRecyclerView)
        val todoLayoutmanager = LinearLayoutManager(view.context)
        todosRecyclerView.layoutManager = todoLayoutmanager

        val notes = arrayListOf<Note>()
        val todos = arrayListOf<Todo>()

        val db = DatabaseHelper(view.context)
        DatabaseHelper.setUserLoggedIn(true)
        val notesCursor = db.notes
        val todosCursor = db.todos
        val noteAdapter = NoteAdapter(notes)
        val todoAdapter = TodoAdapter(todos)
        notesRecyclerView.adapter = noteAdapter
        todosRecyclerView.adapter = todoAdapter

        if (notesCursor != null) {
            if (notesCursor.moveToFirst()) {
                if (notesCursor.moveToFirst()) {
                    do { // Use ado-while loop to process all items, including the first
                        val idIndex = notesCursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_ID)
                        val titleIndex =
                            notesCursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_TITLE)
                        val contentIndex =
                            notesCursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CONTENT)
                        val timestampIndex =
                            notesCursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_TIMESTAMP)

                        val id = notesCursor.getInt(idIndex)
                        val title = notesCursor.getString(titleIndex)
                        val content = notesCursor.getString(contentIndex)
                        val timestamp = notesCursor.getString(timestampIndex)
                        Log.e("TAG", "onViewCreated: $id")

                        notes.add(Note(id, title, content, timestamp))

                    } while (notesCursor.moveToNext())
                    noteAdapter.notifyDataSetChanged()
                }


            } else {
                notes.add(
                    Note(
                        123,
                        getString(R.string.demoNoteTitle),
                        getString(R.string.demoNoteContent),
                        "timestamp"
                    )
                )
                noteAdapter.notifyDataSetChanged()
            }


        } else {
            Log.e("TAG", "onViewCreated: " + "No Notes Found")

        }

        if (todosCursor != null) {
            if (todosCursor.moveToFirst()) {
                do {
                    val idIndex = todosCursor.getColumnIndex(DatabaseHelper.TODO_COLUMN_ID)
                    val taskIndex = todosCursor.getColumnIndex(DatabaseHelper.TODO_COLUMN_TASK)
                    val statusIndex = todosCursor.getColumnIndex(DatabaseHelper.TODO_COLUMN_STATUS)
                    val timestampIndex =
                        todosCursor.getColumnIndex(DatabaseHelper.TODO_COLUMN_TIMESTAMP)

                    val id = todosCursor.getInt(idIndex)
                    val task = todosCursor.getString(taskIndex)
                    val status = todosCursor.getString(statusIndex)
                    val timestamp = todosCursor.getString(timestampIndex)
                    Log.e("TAG", "onViewCreated: $id")

                    todos.add(Todo(id, task, status, timestamp))

                } while (todosCursor.moveToNext())
                todoAdapter.notifyDataSetChanged()


            } else {
                todos.add(
                    Todo(
                        123,
                        getString(R.string.demoTodoTask),
                        Todo.STATUS_DONE,
                        getString(R.string.demoTodoDateTime)
                    )
                )
                todoAdapter.notifyDataSetChanged()
            }


        } else {
            Log.e("TAG", "onViewCreated: " + "No Todos Found")

        }






        fabMain = view.findViewById(R.id.fab_main)
        fabAddNote = view.findViewById(R.id.fab_add_note)
        fabAddTodo = view.findViewById(R.id.fab_add_todo)

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)



        fabMain.setOnClickListener {
            animateFAB()
        }

        fabAddNote.setOnClickListener {
            val intent = Intent(view.context, EditNoteActivity::class.java)
            startActivity(intent)
        }

        fabAddTodo.setOnClickListener {
            showTaskInputDialog(view.context)
        }


    }

    private fun showTaskInputDialog(context: Context) {
        val builder = MaterialAlertDialogBuilder(context)
        val view = EditText(context)
        builder.setTitle("Add Task")
            .setView(view)
            .setPositiveButton("Add") { dialog, _ ->
                val task = view.text.toString()
                val db = DatabaseHelper(context)
                DatabaseHelper.setUserLoggedIn(true)

                db.addTodo(task, Todo.STATUS_PENDING)

            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun animateFAB() {
        if (isFabOpen) {
            fabMain.startAnimation(rotateBackward)
            fabAddNote.startAnimation(fabClose)
            fabAddTodo.startAnimation(fabClose)
            fabAddNote.visibility = View.GONE
            fabAddTodo.visibility = View.GONE
            fabAddNote.isClickable = false
            fabAddTodo.isClickable = false
            isFabOpen = false
        } else {
            fabMain.startAnimation(rotateForward)
            fabAddNote.startAnimation(fabOpen)
            fabAddTodo.startAnimation(fabOpen)
            fabAddNote.visibility = View.VISIBLE
            fabAddTodo.visibility = View.VISIBLE
            fabAddNote.isClickable = true
            fabAddTodo.isClickable = true
            isFabOpen = true
        }
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.findViewById(R.id.task_todo)
        val status: ImageView = itemView.findViewById(R.id.logo_status)
        val dateTime: TextView = itemView.findViewById(R.id.date_todo)

        val image: ImageView = itemView.findViewById(R.id.image_todo)

    }

    inner class TodoAdapter(private var itemList: List<Todo>) :
        RecyclerView.Adapter<TodoViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_items_recycler_layout, parent, false)
            return TodoViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
            val item = itemList[position]
            holder.task.text = item.task
            if (item.status == Todo.STATUS_PENDING) {
                holder.status.setImageResource(R.drawable.baseline_pending_actions_24)

            } else if (item.status == Todo.STATUS_DONE) {
                holder.status.setImageResource(R.drawable.baseline_done_outline_24)

            }
            holder.dateTime.text = item.timeStamp
        }

    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.note_title)
        val content: TextView = itemView.findViewById(R.id.note_content)
        val card: CardView = itemView.findViewById(R.id.TODO)


    }

    inner class NoteAdapter(private var itemList: List<Note>) :
        RecyclerView.Adapter<NoteViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_items_recycler_layout, parent, false)
            return NoteViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            val item = itemList[position]
            holder.title.text = item.title
            holder.content.text = item.content
            holder.card.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditNoteActivity::class.java)
                startActivity(intent)


            }

        }

    }
}

