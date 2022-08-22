package com.alexmurz.composetexter.mviapp.components.topiclist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexmurz.composetexter.mviapp.components.topiclist.view.TopicItemModel
import com.alexmurz.composetexter.mviapp.components.topiclist.view.TopicListAdapter
import com.alexmurz.composetexter.mviapp.components.topiclist.view.TopicListUi
import com.alexmurz.composetexter.mviapp.utils.ui.loadMores
import com.alexmurz.topic.model.Topic
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

private const val LOAD_MORE_THRESHOLD = 3

class TopicListFragment : Fragment(), TopicListView, KoinScopeComponent {
    override val scope: Scope = createScope()

    private val presenter by inject<TopicListPresenter>()
    private val disposable = CompositeDisposable()
    private val adapter = TopicListAdapter()
    private lateinit var ui: TopicListUi

    private val topicClickSubject = PublishSubject.create<Topic>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TopicListUi(requireContext()).apply {
            ui = this
            recyclerView.let {
                it.adapter = adapter
                it.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe(this, disposable)
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    override fun topicClickIntent(): Observable<Topic> = topicClickSubject

    override fun loadNewerTopicsIntent(): Observable<*> =
        ui.refreshLayout.refreshes().subscribeOn(AndroidSchedulers.mainThread())

    override fun loadOlderTopicsIntent(): Observable<*> =
        ui.recyclerView.loadMores(LOAD_MORE_THRESHOLD).subscribeOn(AndroidSchedulers.mainThread())

    override fun showState(topicListState: TopicListState) {
        adapter.submitList(topicListState.topics.map {
            TopicItemModel(it, topicClickSubject::onNext)
        })
        ui.refreshLayout.isRefreshing = topicListState.isLoadingNewer
    }

    companion object {
        private const val TAG = "TopicList"
        const val FRAGMENT_TAG = "TLF"

        fun create(): Fragment {
            return TopicListFragment()
        }
    }
}