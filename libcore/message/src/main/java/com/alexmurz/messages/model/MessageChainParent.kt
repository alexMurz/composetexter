package com.alexmurz.messages.model

/**
 * Parent of message chain or message folder
 */
interface MessageChainParent {
    val sourceId: Int
    val sourceType: Int

    val packedId: Long
        get() = (sourceType.toLong() shl 32) + sourceId.toLong()
}
