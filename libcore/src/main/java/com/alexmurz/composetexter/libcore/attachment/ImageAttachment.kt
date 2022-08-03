package com.alexmurz.composetexter.libcore.attachment

import com.alexmurz.composetexter.libcore.file.FileRef

/**
 * Attachment with image
 */
data class ImageAttachment(
    val imageDescription: String,
    val images: Image,
) {

    /**
     * Reference to remote image
     */
    class Image(
        imageFile: FileRef,
        width: Int,
        height: Int,
    )
}