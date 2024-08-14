package com.stack


import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


public class HomeFragment : Fragment() {
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabAddNote: ExtendedFloatingActionButton
    private lateinit var fabAddTodo: ExtendedFloatingActionButton

    private lateinit var filterAll: Button
    private lateinit var filterDone: Button
    private lateinit var filterPending: Button

    private lateinit var todos: ArrayList<Todo>
    private lateinit var notes: ArrayList<Note>

    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var todosRecyclerView: RecyclerView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var noteAdapter: NoteAdapter

    override fun onResume() {
        super.onResume()
        refreshLayout()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filterPending = view.findViewById(R.id.filter_pending)
        filterDone = view.findViewById(R.id.filter_done)
        filterAll = view.findViewById(R.id.filter_all)
        filterAll.setBackgroundColor(resources.getColor(R.color.primary))
        filterAll.setTextColor(resources.getColor(android.R.color.white))

        notesRecyclerView = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        notesRecyclerView.layoutManager = layoutManager
        todosRecyclerView = view.findViewById<RecyclerView>(R.id.todosRecyclerView)
        val todoLayoutmanager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        todosRecyclerView.layoutManager = todoLayoutmanager


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
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // Drag directions (not used in this case)
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Swipe directions
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Not handling drag and drop in this example
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                Log.e("posi", "onSwiped: $position")
                when (direction) {
                    ItemTouchHelper.LEFT -> {

                        todoAdapter.notifyItemRemoved(position)
                    }

                    ItemTouchHelper.RIGHT -> {

                        val db = DatabaseHelper(view.context)
                        val todo = todos[position]
                        db.updateTodo(todo.id, todo.task, Todo.STATUS_DONE)
                        todos[position].status = Todo.STATUS_DONE
                        todoAdapter.notifyItemRemoved(position)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()

                // Clear the canvas with the default background color
                c.drawColor(Color.TRANSPARENT)

                val backgroundColor: Int
                val icon: Drawable?

                if (dX > 0) { // Swiping to the right
                    backgroundColor = ContextCompat.getColor(
                        recyclerView.context,
                        R.color.todoDone
                    ) // Green color
                    icon = ContextCompat.getDrawable(
                        recyclerView.context,
                        R.drawable.baseline_done_outline_24
                    )
                } else { // Swiping to the left
                    backgroundColor = Color.parseColor("#D32F2F") // Red color
                    icon = ContextCompat.getDrawable(
                        recyclerView.context,
                        R.drawable.baseline_delete_outline_24
                    )
                }

                // Draw the background color
                paint.color = backgroundColor

                if (dX > 0) { // Right swipe
                    c.drawRect(
                        itemView.left.toFloat(), itemView.top.toFloat(),
                        dX, itemView.bottom.toFloat(), paint
                    )
                } else { // Left swipe
                    c.drawRect(
                        itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                    )
                }

                // Calculate the position of the icon
                val itemHeight = itemView.height
                val iconMargin = (itemHeight - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX > 0) { // Right swipe
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = iconLeft + icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                } else if (dX < 0) { // Left swipe
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = iconRight - icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                // Draw the icon
                icon.draw(c)

                // Reset translation to avoid leaving the black background
                itemView.translationX = dX
            }


        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(todosRecyclerView)


        filterAll.setOnClickListener {


            todosRecyclerView.adapter = TodoAdapter(todos)



            filterAll.setBackgroundColor(resources.getColor(R.color.primary))
            filterAll.setTextColor(resources.getColor(android.R.color.white))
            filterDone.setBackgroundColor(resources.getColor(R.color.primarySurface))
            filterDone.setTextColor(resources.getColor(android.R.color.black))
            filterPending.setBackgroundColor(resources.getColor(R.color.primarySurface))
            filterPending.setTextColor(resources.getColor(android.R.color.black))

        }
        filterDone.setOnClickListener {
            val filtered = todos.filter { it.status == Todo.STATUS_DONE }
            todosRecyclerView.adapter = TodoAdapter(filtered)


            filterAll.setBackgroundColor(resources.getColor(R.color.primarySurface))
            filterAll.setTextColor(resources.getColor(android.R.color.black))
            filterDone.setBackgroundColor(resources.getColor(R.color.primary))
            filterDone.setTextColor(resources.getColor(android.R.color.white))
            filterPending.setBackgroundColor(resources.getColor(R.color.primarySurface))
            filterPending.setTextColor(resources.getColor(android.R.color.black))
        }
        filterPending.setOnClickListener {

            val filtered = todos.filter { it.status == Todo.STATUS_PENDING }
            todosRecyclerView.adapter = TodoAdapter(filtered)

            filterAll.setBackgroundColor(resources.getColor(R.color.primarySurface))
            filterAll.setTextColor(resources.getColor(android.R.color.black))
            filterDone.setBackgroundColor(resources.getColor(R.color.primarySurface))
            filterDone.setTextColor(resources.getColor(android.R.color.black))
            filterPending.setBackgroundColor(resources.getColor(R.color.primary))
            filterPending.setTextColor(resources.getColor(android.R.color.white))
        }


    }

    private fun refreshLayout() {

        notes = arrayListOf()
        todos = arrayListOf()
        val db = DatabaseHelper(requireView().context)
        DatabaseHelper.setUserLoggedIn(true)
        val notesCursor = db.notes
        val todosCursor = db.todos
        noteAdapter = NoteAdapter(notes)
        todoAdapter = TodoAdapter(todos)
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
                    notes.sort()
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
                todos.sort()
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


    }

    private fun showTaskInputDialog(context: Context) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setCancelable(false)
        val view = EditText(context)
        builder.setTitle("Add Task")
            .setView(view)
            .setPositiveButton("Add") { dialog, _ ->
                val task = view.text.toString()
                val db = DatabaseHelper(context)
                DatabaseHelper.setUserLoggedIn(true)

                db.addTodo(task, Todo.STATUS_PENDING)

                refreshLayout()

            }
            .setNegativeButton("Cancel", null)
            .show()
        view.post {
            view.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
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

    private fun formatTimestamp(timeStamp: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(timeStamp)

        val calendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()
        val yesterdayCalendar = Calendar.getInstance()

        currentCalendar.time = Date()

        // Set yesterday's date
        yesterdayCalendar.time = Date()
        yesterdayCalendar.add(Calendar.DATE, -1)

        calendar.time = date

        return when {
            // Check if the date is today
            currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    currentCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) -> {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                "Today, ${timeFormat.format(date!!)}"
            }

            yesterdayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    yesterdayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) -> {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                "Yesterday, ${timeFormat.format(date!!)}"
            }

            else -> {
                val dateFormatOutput = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                dateFormatOutput.format(date!!)
            }
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
            holder.dateTime.text = formatTimestamp(item.timeStamp)
        }

    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.note_title)
        val content: TextView = itemView.findViewById(R.id.note_content)
        val card: CardView = itemView.findViewById(R.id.TODO)
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraint)


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
            if (item.title.isNotEmpty()) {
                holder.title.text = item.title

            } else {
                val constraintSet = ConstraintSet()
                // Clone the existing layout
                constraintSet.clone(holder.constraintLayout)

                // Change the constraint
                constraintSet.connect(
                    R.id.note_content,
                    ConstraintSet.TOP,
                    R.id.constraint,
                    ConstraintSet.TOP,
                    16
                )

                // Apply the changes to the ConstraintLayout
                constraintSet.applyTo(holder.constraintLayout)

            }
            holder.content.text = item.content
            holder.card.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditNoteActivity::class.java)
                intent.putExtra("note_id", item.id)
                intent.putExtra("note_content", item.content)
                intent.putExtra("note_title", item.title)
                intent.putExtra("note_timestamp", item.timeStamp)
                intent.putExtra("editable", true)
                startActivity(intent)


            }

        }

    }

}

