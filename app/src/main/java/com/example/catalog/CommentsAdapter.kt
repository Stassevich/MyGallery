package com.example.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        //Заполнение комментария по шаблону
        val comment = comments[position]
        holder.usernameTextView.text = comment.username
        holder.ratingBar.rating = comment.rating.toFloat()
        holder.commentTextView.text = comment.text
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}