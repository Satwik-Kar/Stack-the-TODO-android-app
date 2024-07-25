package com.stack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

public class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todosRecyclerView = view.findViewById<RecyclerView>(R.id.todosRecyclerView)
        val layoutManager = LinearLayoutManager(view.context)
        todosRecyclerView.layoutManager = layoutManager
        val items = listOf<Todo>(
            Todo("he", "description", Date()), Todo("he", "description", Date())
        )
        val adapter = Adapter(items)
        todosRecyclerView.adapter = adapter


    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title_todo)
    val description: TextView = itemView.findViewById(R.id.desc_todo)
    val dateTime: TextView = itemView.findViewById(R.id.date_todo)
    val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_todo)
    val image: ImageView = itemView.findViewById(R.id.image_todo)

}

class Adapter(private var itemList: List<Todo>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_items_recycler_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.dateTime.text = item.dateTime.date.toString()
    }

}
