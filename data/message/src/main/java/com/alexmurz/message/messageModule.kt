package com.alexmurz.message

import com.alexmurz.message.local.RoomMessageStorage
import com.alexmurz.message.mapper.MessageEntityMapper
import com.alexmurz.message.remote.NoopRemote
import com.alexmurz.messages.api.MessageAPI
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

private val LOCAL_QUALIFIER = named("local")
private val REMOTE_QUALIFIER = named("remote")

val messageModule = module {
    single { MessageEntityMapper } bind MessageEntityMapper::class

    single(LOCAL_QUALIFIER) {
        RoomMessageStorage(get(), get())
    } binds arrayOf(
        MessageAPI.LoadNewest::class,
        MessageAPI.LoadNewer::class,
        MessageAPI.LoadOlder::class,
        MessageAPI.Save::class,
    )

    single(REMOTE_QUALIFIER) {
        NoopRemote
    } binds arrayOf(
        MessageAPI.LoadNewest::class,
        MessageAPI.LoadNewer::class,
        MessageAPI.LoadOlder::class,
        MessageAPI.Post::class,
    )
}
