package com.alexmurz.composetexter.mviapp.components.topiclist.presenter

import com.alexmurz.composetexter.mviapp.components.topiclist.TopicListPresenter
import com.alexmurz.composetexter.mviapp.components.topiclist.TopicListState
import com.alexmurz.composetexter.mviapp.components.topiclist.TopicListView
import com.alexmurz.composetexter.mviapp.components.topiclist.model.TopicListStateImpl
import com.alexmurz.composetexter.mviapp.nav_host.AppNav
import com.alexmurz.composetexter.mviapp.nav_host.Destination
import com.alexmurz.composetexter.mviapp.utils.BitField
import com.alexmurz.composetexter.mviapp.utils.runSingleTaskForBusyFlag
import com.alexmurz.topic.TopicUseCase
import com.alexmurz.topic.TopicsContext
import com.alexmurz.topic.model.Topic
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.rx3.rxSingle
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val LIMIT = 25

private inline val TopicsContext.createSortedTopicList: List<Topic>
    get() = data.sortedByDescending { it.date }

@Suppress("NOTHING_TO_INLINE")
private inline fun TopicsContext.toState(
    isLoadingNewer: Boolean = false,
    isLoadingOlder: Boolean = false,
): TopicListState = TopicListStateImpl(
    isLoadingNewer,
    isLoadingOlder,
    topics = createSortedTopicList
)

class TopicListPresenterImpl : TopicListPresenter, KoinComponent {

    private val nav by inject<AppNav>()
    private val useInitialize by inject<TopicUseCase.Initialize>()
    private val useUpdate by inject<TopicUseCase.Update>()
    private val useLoadMore by inject<TopicUseCase.LoadMore>()

    private val context by lazy {
        TopicsContext(LIMIT)
    }

    override fun subscribe(view: TopicListView, disposable: CompositeDisposable) {
        disposable.addAll(
            observeShowTopicIntent(view.topicClickIntent()),
            observeLoadTopicsIntent(view),
        )
    }

    private fun observeShowTopicIntent(intent: Observable<Topic>): Disposable {
        return intent.subscribe {
            nav.open(
                Destination.MessageList(it.messageChainParent)
            )
        }
    }

    private fun observeLoadTopicsIntent(
        view: TopicListView,
    ): Disposable {
        return initialize(view)
            .flatMapObservable {
                Observable.combineLatest(
                    observeUpdateTopicsIntent(view.loadNewerTopicsIntent()).startWithItem(false),
                    observeLoadOlderTopicsIntent(view.loadOlderTopicsIntent()).startWithItem(false),
                    ::BitField,
                )
            }
            .distinctUntilChanged()
            .doOnError {
                it.printStackTrace()
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { flag ->
                val (isLoadingNewer, isLoadingOlder) = flag
                view.showState(
                    context.toState(
                        isLoadingNewer,
                        isLoadingOlder,
                    )
                )
            }
    }

    private fun observeUpdateTopicsIntent(intent: Observable<*>): Observable<Boolean> {
        return intent.runSingleTaskForBusyFlag {
            rxSingle { useUpdate.update(context) }
        }
    }

    private fun observeLoadOlderTopicsIntent(intent: Observable<*>): Observable<Boolean> {
        return intent.runSingleTaskForBusyFlag {
            rxSingle { useLoadMore.loadMore(context) }
        }
    }

    private fun initialize(view: TopicListView): Single<*> {
        view.showState(
            context.toState(
                isLoadingNewer = true,
            )
        )

        return rxSingle<Unit> { useInitialize.initialize(context) }
            .doOnError {
                it.printStackTrace()
            }
            .retry(3)
            .onErrorResumeNext {
                Single.just(Unit)
            }
            .doOnSuccess {
                view.showState(context.toState())
            }
    }
}
