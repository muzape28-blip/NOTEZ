package com.zaba.notez

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onOpen: (Note) -> Unit,
    private val onDelete: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.Holder>() {

    private var items: List<Note> = emptyList()
    private val fmt = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())

    fun submit(list: List<Note>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(h: Holder, position: Int) {
        val n = items[position]
        h.title.text = n.title.ifBlank { "(tanpa judul)" }
        h.preview.text = n.content.lines().take(2).joinToString(" ").take(120)
        h.meta.text = "${fmt.format(Date(n.updatedAt))} • ${n.content.length} karakter"
        h.itemView.setOnClickListener { onOpen(n) }
        h.delete.setOnClickListener { onDelete(n) }
    }

    override fun getItemCount() = items.size

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.item_title)
        val preview: TextView = v.findViewById(R.id.item_preview)
        val meta: TextView = v.findViewById(R.id.item_meta)
        val delete: ImageButton = v.findViewById(R.id.item_delete)
    }
}
