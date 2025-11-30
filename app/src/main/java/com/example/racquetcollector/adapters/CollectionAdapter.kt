package com.example.racquetcollector.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.racquetcollector.R
import com.example.racquetcollector.api.RacquetCollectionItem

class CollectionAdapter(private val collection: List<RacquetCollectionItem>) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val racquetName: TextView = view.findViewById(R.id.racquet_name)
        val racquetNotes: TextView = view.findViewById(R.id.racquet_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = collection[position]
        holder.racquetName.text = "${item.racquet.model_name} (${item.racquet.model_year})"
        holder.racquetNotes.text = item.notes ?: ""
    }

    override fun getItemCount() = collection.size
}
