package xyz.cybersapien.retrohit.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import okhttp3.Headers
import xyz.cybersapien.retrohit.R

/**
 * Created by ogcybersapien on 6/8/17.
 */

class HeaderViewAdapter(var headers: Headers? = null, val isEditable: Boolean = false) : RecyclerView.Adapter<HeaderViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.headers_list_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val headerName = headers?.name(position)
        val values = headers?.values(headerName)
        holder.headerNameTextView.text = headerName
        holder.headerValueTextView.text = values.toString()
        if (isEditable) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                headers = headers?.newBuilder()?.removeAll(headerName)?.build()
                notifyDataSetChanged()
            }
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return (headers?.names()?.size) ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerNameTextView = itemView.findViewById<TextView>(R.id.header_name_text_view)
        val headerValueTextView = itemView.findViewById<TextView>(R.id.header_value_text_view)
        val deleteButton = itemView.findViewById<Button>(R.id.delete_button)
    }
}