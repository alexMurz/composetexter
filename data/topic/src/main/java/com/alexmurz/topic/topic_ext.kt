package com.alexmurz.topic

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.model.Topic

/**
 * Get reference date for loading from Topic
 */
internal val Topic.referenceDate: CATime
    inline get() = date
