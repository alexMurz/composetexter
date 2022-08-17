package com.alexmurz.composetexter.mviapp.components.messagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alexmurz.composetexter.mviapp.components.messagelist.view.MessageItemModel
import com.alexmurz.composetexter.mviapp.components.messagelist.view.MessageListAdapter
import com.alexmurz.composetexter.mviapp.components.messagelist.view.MessageListFragmentUi
import com.alexmurz.messages.model.MessageChainParent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class MessageListFragment(
    override val messageChainParent: MessageChainParent
) : Fragment(), MessageListView, KoinScopeComponent {
    override val scope: Scope = createScope()

    private val presenter by inject<MessageListPresenter>()

    private val adapter = MessageListAdapter()
    private lateinit var ui: MessageListFragmentUi
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MessageListFragmentUi(requireContext()).apply {
            ui = this
            recycler.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe(this, compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun showState(topicListState: MessageListState) {
        adapter.submitList(topicListState.messages.map {
            MessageItemModel(it)
        })
    }

    companion object {
        fun nameFor(parent: MessageChainParent) = "MessageListFragment(${parent.id})"
    }
}
