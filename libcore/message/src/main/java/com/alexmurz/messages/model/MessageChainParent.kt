package com.alexmurz.messages.model

/**
 * Parent of message chain or message folder
 */
interface MessageChainParent {
    val id: Long

    companion object {
        fun of(type: MessageChainParentType, parentId: Long): MessageChainParent = of(
            (type.typeId shl 32) + parentId
        )

        fun of(id: Long): MessageChainParent = ConstMessageChainParent(id)
    }
}

private data class ConstMessageChainParent(override val id: Long) : MessageChainParent

enum class MessageChainParentType(
    val typeId: Int
) {
    Topic(1)
    ;

    fun create(id: Long) = MessageChainParent.of(this, id)
}
