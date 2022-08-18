package com.alexmurz.composetexter.mviapp.components.topiclist.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.alexmurz.composetexter.mviapp.components.topiclist.view.topicview.TopicView

object TopicItemCallback : DiffUtil.ItemCallback<TopicItemModel>() {
    override fun areItemsTheSame(oldItem: TopicItemModel, newItem: TopicItemModel): Boolean =
        oldItem.topic == newItem.topic

    override fun areContentsTheSame(oldItem: TopicItemModel, newItem: TopicItemModel): Boolean =
        oldItem.topic.isSameContent(newItem.topic)
}

class TopicListAdapter : ListAdapter<TopicItemModel, TopicItemViewHolder>(TopicItemCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).topic.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TopicItemViewHolder(
        TopicView(parent.context)
    )

    override fun onBindViewHolder(holder: TopicItemViewHolder, position: Int) =
        holder.bind(getItem(position))
}
