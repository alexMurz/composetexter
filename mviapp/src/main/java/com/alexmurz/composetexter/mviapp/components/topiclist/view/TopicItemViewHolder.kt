package com.alexmurz.composetexter.mviapp.components.topiclist.view

import androidx.recyclerview.widget.RecyclerView
import com.alexmurz.composetexter.libcore.ext.timeStringSince
import com.alexmurz.composetexter.mviapp.components.topiclist.view.topicview.TopicView

class TopicItemViewHolder(view: TopicView) : RecyclerView.ViewHolder(view) {
    fun bind(topicItemModel: TopicItemModel) {
        val view = itemView as TopicView
        val topic = topicItemModel.topic
        view.title.text = topic.title
        view.messageTimeText.apply {
            leftContent = topic.message
            rightContent = topic.date.timeStringSince()
            requestLayout()
        }
        view.setOnClickListener {
            topicItemModel.onClickListener?.invoke(topic)
        }
    }
}
