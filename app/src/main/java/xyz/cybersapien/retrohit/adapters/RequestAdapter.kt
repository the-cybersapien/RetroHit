package xyz.cybersapien.retrohit.adapters

import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import okhttp3.Headers
import xyz.cybersapien.retrohit.R

/**
 * Created by the-cybersapien on 6/8/17.
 * This is the recycler Adapter used for the RequestFragment.
 */

class RequestAdapter(var headers: Headers? = null, val isEditable: Boolean = false) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val VIEW_TYPE_HEADER = 1
    val VIEW_TYPE_BODY = 2
    var show_body = true
    var requestBody: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // If it's the header, inflate the header List Item View
        if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.headers_list_item_view, parent, false)
            return HeaderItemViewHolder(view)
        }
        // Otherwise it has to be the request body
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.request_body_edit_text, parent, false)
        view.findViewById<TextInputEditText>(R.id.request_body_edit_text)
                .addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        requestBody = p0?.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        // KUCH NA KR BC!
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        // KUCH NA KR BC!
                    }

                })
        return BodyTextViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_HEADER -> {
                var pos = holder.adapterPosition
                val viewHolder = holder as HeaderItemViewHolder
                val headerName = headers?.name(pos)
                val values = headers?.values(headerName)
                viewHolder.headerNameTextView.text = headerName
                viewHolder.headerValueTextView.text = values.toString()
                if (isEditable) {
                    viewHolder.deleteButton.visibility = View.VISIBLE
                    viewHolder.deleteButton.setOnClickListener {
                        headers = headers?.newBuilder()?.removeAll(headerName)?.build()
                        notifyDataSetChanged()
                    }
                } else {
                    viewHolder.deleteButton.visibility = View.GONE
                }
            }
            VIEW_TYPE_BODY -> {
                val viewHolder = holder as BodyTextViewHolder
                viewHolder.bodyTextView.setText("HELLO!!!")
            }
        }
    }

    override fun getItemCount(): Int {
        val headCount = headers?.names()?.size ?: 0
        return if (show_body) (headCount + 1) else headCount
    }

    override fun getItemViewType(position: Int): Int {
        if (show_body && position == 0)
            return VIEW_TYPE_BODY
        return VIEW_TYPE_HEADER
    }

    inner class HeaderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerNameTextView = itemView.findViewById<TextView>(R.id.header_name_text_view)
        val headerValueTextView = itemView.findViewById<TextView>(R.id.header_value_text_view)
        val deleteButton = itemView.findViewById<Button>(R.id.delete_button)
    }

    inner class BodyTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bodyTextView = itemView.findViewById<TextInputEditText>(R.id.request_body_edit_text)
    }
}