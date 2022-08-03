package com.alexmurz.composetexter.modules.topic.remote

import android.content.Context
import androidx.work.*
import com.alexmurz.composetexter.network.checkPOST
import com.alexmurz.composetexter.network.connectivity.ConnectivityWatcher
import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.model.Topic
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

const val CREATE_TOPIC_TAG = "CreateTopic"
const val CREATE_TOPIC_WORK_NAME = "CreateTopic"

private const val BACKOFF_DELAY_SECONDS = 60L
private const val KEY_ID = "id"
private const val KEY_DATE = "DATE"
private const val KEY_TITLE = "title"
private const val KEY_MESSAGE = "msg"

private val workConstraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

private fun TopicNetworkV1DTO.toData() = Data.Builder()
    .putLong(KEY_ID, id)
    .putLong(KEY_DATE, date)
    .putString(KEY_TITLE, title)
    .putString(KEY_MESSAGE, message)
    .build()

private fun Data.toTopicNetworkV1DTO() = TopicNetworkV1DTO().apply {
    id = getLong(KEY_ID, 0)
    date = getLong(KEY_DATE, 0)
    title = getString(KEY_TITLE) ?: ""
    message = getString(KEY_MESSAGE) ?: ""
}

internal class CreateTopicWorkImpl(
    private val appContext: Context,
) : TopicAPI.CreateTopic {
    override suspend fun createTopic(title: String, message: String): Topic {
        val wm = WorkManager.getInstance(appContext)

        val work = OneTimeWorkRequestBuilder<CreateTopicWorker>()
            .addTag(CREATE_TOPIC_TAG)
            .setInputData(
                Data.Builder()
                    .putString(KEY_TITLE, title)
                    .putString(KEY_MESSAGE, message)
                    .build()
            )
            .setExpedited(
                OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS,
            )
            .setConstraints(workConstraints)
            .build()

        wm.enqueueUniqueWork(
            CREATE_TOPIC_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            work
        )

        val workInfo = wm.getWorkInfoById(work.id).await()
        return workInfo.outputData
            .toTopicNetworkV1DTO()
            .toTopic()
    }
}


class CreateTopicWorker private constructor(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    private val api by inject<TopicNetworkAPI>()
    private val connectivityWatcher by inject<ConnectivityWatcher>()

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)!!
        val message = inputData.getString(KEY_MESSAGE)!!
        return try {
            val topic = connectivityWatcher.checkPOST {
                api.create(title, message)
            }
            Result.success(topic.toData())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

