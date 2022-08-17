package com.alexmurz.composetexter.mviapp.components.messagelist.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MessageItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(itemModel: MessageItemModel) {
        val view = itemView as MessageView
        val message = itemModel.message
        view.message.text = message.message
    }
}
