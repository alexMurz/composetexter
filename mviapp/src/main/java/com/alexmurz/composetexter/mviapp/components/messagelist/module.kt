package com.alexmurz.composetexter.mviapp.components.messagelist

import com.alexmurz.composetexter.mviapp.components.messagelist.presenter.MessageListPresenterImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val appMessageListModule = module {
    scope<MessageListFragment> {
        scoped { MessageListPresenterImpl() } bind MessageListPresenter::class
    }
}
