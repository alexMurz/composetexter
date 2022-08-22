package com.alexmurz.composetexter.mviapp.components.messagelist.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alexmurz.composetexter.libcore.ext.timeStringSince

class MessageItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(itemModel: MessageItemModel) {
        val view = itemView as MessageView
        val message = itemModel.message
        view.message.apply {
            leftContent = message.message
            rightContent = message.dateCreated.timeStringSince()
        }
    }
}
