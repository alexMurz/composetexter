package com.alexmurz.composetexter.mviapp.components.topiclist.model

import com.alexmurz.composetexter.mviapp.components.topiclist.TopicListState
import com.alexmurz.topic.model.Topic

class TopicListStateImpl(
    override val isLoadingNewer: Boolean,
    override val isLoadingOlder: Boolean,
    override val topics: List<Topic>
) : TopicListState
