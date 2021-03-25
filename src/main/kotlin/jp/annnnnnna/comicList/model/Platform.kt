package jp.annnnnnna.comicList.model

import java.io.Serializable
import java.util.*

data class Platform(
    val id: Int,
    val name: String,
    val url: String,
    val settingsJson: String,
    val updateTime: Int,
    val dataType: String,
    val lastCheckedAt: Date?
): Serializable
