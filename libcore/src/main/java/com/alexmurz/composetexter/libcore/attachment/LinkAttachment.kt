package com.alexmurz.composetexter.libcore.attachment

data class LinkAttachment(
    val linkText: String,
    val uri: String,
) : Attachment()
