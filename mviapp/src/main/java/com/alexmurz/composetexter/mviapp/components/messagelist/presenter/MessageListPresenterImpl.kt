@file:Suppress("NOTHING_TO_INLINE")

package com.alexmurz.composetexter.mviapp.components.messagelist.presenter

import android.util.LruCache
import androidx.core.util.lruCache
import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.mviapp.components.messagelist.MessageListPresenter
import com.alexmurz.composetexter.mviapp.components.messagelist.MessageListState
import com.alexmurz.composetexter.mviapp.components.messagelist.MessageListView
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.service.MessageService
import com.alexmurz.messages.service.MessageServiceContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

private const val LIMIT = 25

private inline fun MessageServiceContext.toState(): MessageListState = MessageListStateImpl(
    data.sortedByDescending { it.dateCreated }
)

class MessageListPresenterImpl : MessageListPresenter, KoinComponent {

    private val service by inject<MessageService>()
    private val contextMap = LruCache<Long, MessageServiceContext>(3)

    override fun subscribe(view: MessageListView, disposable: CompositeDisposable) {
        val parent = view.messageChainParent
        val key = parent.id
        val context = contextMap[key] ?: run {
            MessageServiceContext(LIMIT, parent).also {
                contextMap.put(key, it)
            }
        }

        disposable.addAll(
            runList(view, context)
        )
    }

    private fun runList(view: MessageListView, context: MessageServiceContext): Disposable {
        return Single
            .fromCallable {
                val idCounter = AtomicInteger(0)
                val baseDate = CATime.now().timestamp - 1_000
                fun message(
                    text: String
                ): Message {
                    val id = idCounter.getAndIncrement()
                    val date = CATime.of(baseDate + id)
                    return Message(
                        id = id,
                        message = text,
                        dateCreated = date,
                        dateUpdated = date,
                    )
                }

                val items = (0 until 100).map {
                    val r = (Math.random() * 20).toInt()
                    message("Message $it. " + "123 ".repeat(r))
                }
                context.addItems(items)
            }
            .doOnError { it.printStackTrace() }
            .retry()
            .map { context.toState() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::showState)
    }

}
