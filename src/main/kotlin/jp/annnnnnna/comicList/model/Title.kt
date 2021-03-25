package jp.annnnnnna.comicList.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Title(
    val id: Int,
    val name: String,
    val platform: Int,
    var frequency: Int,
    var lastUpdatedAt: Date?,
    var lastCheckedAt: Date?,
    var latestUrl: String?,
    val url: String,
    val updateCheckUrl: String,
    var finished: Boolean
): Serializable {
    fun getUpdateDate(): String = if (lastUpdatedAt == null) "" else "最終更新日：" + SimpleDateFormat("yyyy/MM/dd").format(lastUpdatedAt)
}
