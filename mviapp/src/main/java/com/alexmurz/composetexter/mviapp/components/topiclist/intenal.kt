package com.alexmurz.composetexter.mviapp.components.topiclist

import com.alexmurz.topic.model.Topic
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface TopicListPresenter {
    fun subscribe(view: TopicListView, disposable: CompositeDisposable)
}

interface TopicListState {
    val isLoadingNewer: Boolean
    val isLoadingOlder: Boolean
    val topics: List<Topic>
}

interface TopicListView {
    fun topicClickIntent(): Observable<Topic>
    fun loadOlderTopicsIntent(): Observable<*>
    fun loadNewerTopicsIntent(): Observable<*>

    fun showState(topicListState: TopicListState)
}
