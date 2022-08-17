package com.alexmurz.composetexter.mviapp.components.topiclist.view

import androidx.recyclerview.widget.RecyclerView
import com.alexmurz.composetexter.libcore.ext.timeStringSince
import com.alexmurz.topic.model.Topic

class TopicItemViewHolder(view: TopicView) : RecyclerView.ViewHolder(view) {
    fun bind(topicItemModel: TopicItemModel) {
        val view = itemView as TopicView
        val topic = topicItemModel.topic
        view.title.text = topic.title
        view.message.text = topic.message
        view.time.text = topic.date.timeStringSince()

        view.setOnClickListener {
            topicItemModel.onClickListener?.invoke(topic)
        }
    }
}
