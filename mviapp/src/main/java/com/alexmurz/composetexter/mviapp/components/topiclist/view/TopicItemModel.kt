package com.alexmurz.composetexter.mviapp.components.topiclist.view

import com.alexmurz.topic.model.Topic

class TopicItemModel(
    val topic: Topic,
    val onClickListener: ((Topic) -> Unit)? = null,
)
