package com.alexmurz.composetexter.mviapp.components.messagelist

import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface MessageListPresenter {
    fun subscribe(view: MessageListView, disposable: CompositeDisposable)
}

interface MessageListState {
    val messages: List<Message>
}

interface MessageListView {
    val messageChainParent: MessageChainParent
    fun showState(topicListState: MessageListState)
}
