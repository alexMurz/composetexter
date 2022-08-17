package com.alexmurz.composetexter.mviapp.components.messagelist.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

object MessageItemCallback : DiffUtil.ItemCallback<MessageItemModel>() {
    override fun areItemsTheSame(oldItem: MessageItemModel, newItem: MessageItemModel): Boolean =
        oldItem.message == newItem.message

    override fun areContentsTheSame(oldItem: MessageItemModel, newItem: MessageItemModel): Boolean =
        oldItem.message.isSameContent(newItem.message)
}

class MessageListAdapter : ListAdapter<MessageItemModel, MessageItemViewHolder>(MessageItemCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).message.id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MessageItemViewHolder(
        MessageView(parent.context)
    )

    override fun onBindViewHolder(holder: MessageItemViewHolder, position: Int) =
        holder.bind(getItem(position))
}
