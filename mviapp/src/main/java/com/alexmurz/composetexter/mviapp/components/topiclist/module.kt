package com.alexmurz.composetexter.mviapp.components.topiclist

import com.alexmurz.composetexter.mviapp.components.topiclist.presenter.TopicListPresenterImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val appTopicListModule = module {
    scope<TopicListFragment> {
        scoped {
            TopicListPresenterImpl()
        } bind TopicListPresenter::class
    }
}
