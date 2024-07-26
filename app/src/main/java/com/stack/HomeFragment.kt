package com.stack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date

public class HomeFragment : Fragment() {
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var fabAddTodo: FloatingActionButton
    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todos
        val todosRecyclerView = view.findViewById<RecyclerView>(R.id.todosRecyclerView)
        val todo_layoutManager = LinearLayoutManager(view.context)
        todosRecyclerView.layoutManager = todo_layoutManager
        val todos = listOf<Todo>(
            Todo("he", "description", Date()), Todo("he", "description", Date())
        )
        val todoAdapter = TodoAdapter(todos)
        todosRecyclerView.adapter = todoAdapter
        //notes
        val notesRecyclerView = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.HORIZONTAL,false)
        notesRecyclerView.layoutManager = layoutManager
        val notes = listOf<Note>(
            Note("he", "description"), Note("he", "description")
        )
        val noteAdapter = NoteAdapter(notes)
        notesRecyclerView.adapter = noteAdapter
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
            // Handle add note click
        }

        fabAddTodo.setOnClickListener {
            // Handle add todo click
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
}

class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title_todo)
    val description: TextView = itemView.findViewById(R.id.desc_todo)
    val dateTime: TextView = itemView.findViewById(R.id.date_todo)

    val image: ImageView = itemView.findViewById(R.id.image_todo)

}

class TodoAdapter(private var itemList: List<Todo>) : RecyclerView.Adapter<TodoViewHolder>() {
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
        holder.title.text = item.title
        holder.description.text = item.description
        holder.dateTime.text = item.dateTime.date.toString()
    }

}

class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.note_title)
    val content: TextView = itemView.findViewById(R.id.note_content)



}

class NoteAdapter(private var itemList: List<Note>) : RecyclerView.Adapter<NoteViewHolder>() {
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

    }

}