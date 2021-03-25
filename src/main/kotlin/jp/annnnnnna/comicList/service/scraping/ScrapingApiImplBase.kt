package jp.annnnnnna.comicList.service.scraping

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jp.annnnnnna.comicList.mapper.PlatformMapper
import jp.annnnnnna.comicList.mapper.TitleMapper
import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import jp.annnnnnna.comicList.model.UpdateInfo
import java.util.*

interface ScrapingApiImplBase {

    var titleMapper: TitleMapper
    var platformMapper: PlatformMapper
    var objectMapper: ObjectMapper

    var settingKeys: Set<String>
    fun checkSetting(platformSetting: JsonNode): Map<String, String> {
        val ret = mutableMapOf<String, String>()
        settingKeys.forEach {
            if (platformSetting[it].isArray) {
                ret[it] = platformSetting[it].joinToString(" ")
            } else {
                ret[it] = platformSetting[it].textValue()
            }
        }
        return ret
    }

    fun getTitlesApi(platform: Platform, platformSetting: Map<String, String>) {
        try {
            if (getTitlesApiCustom(platform, platformSetting) > 0) {
                platformMapper.updateLastCheckDate(platform.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getTitlesApiCustom(platform: Platform, platformSetting: Map<String, String>): Int
    fun insertIfNotExist(titleName: String, id: Int, paramMap: Map<String,String>, platform: Platform, platformSetting: Map<String, String>): Boolean {
        if (titleMapper.findByName(platform.id, titleName).isEmpty()) {
            titleMapper.insert(
                    Title(
                            id,
                            titleName,
                            platform.id,
                            0,
                            null,
                            null,
                            makeUrl(platformSetting, paramMap),
                            makeUrl(platformSetting, paramMap),
                            makeDataUrl(platformSetting, paramMap),
                            false
                    )
            )
            return true
        }
        return false
    }
    fun makeUrl(platformSetting: Map<String, String>, paramMap: Map<String, String>): String
    fun makeLatestUrl(platformSetting: Map<String, String>, paramMap: Map<String, String>): String
    fun makeDataUrl(platformSetting: Map<String, String>, paramMap: Map<String, String>): String
    fun makeUpdateDate(platformSetting: Map<String, String>, updateDate:String?): Date?

    private fun needCheck(lastCheckedAt: Date?, days: Int): Boolean {
        if (lastCheckedAt == null) return true
        val next = Calendar.getInstance().apply {
            time = lastCheckedAt
            add(Calendar.DATE, days) }.time
        val now = Calendar.getInstance().time
        return next <= now
    }
    fun getUpdateDateApi(platform: Platform, title: Title, platformSetting: Map<String, String>) {
        if (title.finished || !needCheck(title.lastCheckedAt, title.frequency)) {
            return
        }
        val today = Calendar.getInstance()
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < platform.updateTime) {
            // 10時更新のサイトとかは10時まではチェックしに行かない
            return
        }

        val updateInfo = getUpdateDateApiCustom(platform, title, platformSetting)

        Thread.sleep(10000) // 各作品の情報取得はとりあえず10秒間隔にしておく
        if (updateInfo.updateDate == null) {
            if (title.lastUpdatedAt != null) title.finished = true
            title.lastCheckedAt = Calendar.getInstance().time
            if (title.frequency == 0) title.frequency = 1
        } else {
//            if (title.lastUpdatedAt == updateInfo.updateDate) return
            val diff = when (title.lastUpdatedAt) {
                null -> dateDiff(updateInfo.updateDate, Calendar.getInstance().time)
                updateInfo.updateDate -> dateDiff(updateInfo.updateDate, title.lastCheckedAt!!)
                else -> dateDiff(updateInfo.updateDate, title.lastUpdatedAt!!)
            }
            title.frequency = when(diff/7) {
                0 -> 1
                1 -> 7
                2 -> 14
                else -> 35 // 1年以上更新がないようなものの扱いについて要考慮
            }
            val lastCheckedAt = Calendar.getInstance()
            lastCheckedAt.time = updateInfo.updateDate
            while (lastCheckedAt <= today) { lastCheckedAt.add(Calendar.DATE, title.frequency) }
            if (lastCheckedAt > today) {
                lastCheckedAt.add(Calendar.DATE, -1 * title.frequency)
            }
            title.lastUpdatedAt = updateInfo.updateDate
            title.lastCheckedAt = lastCheckedAt.time
        }
        title.latestUrl = updateInfo.latestUrl
        titleMapper.update(title)
    }
    fun getUpdateDateApiCustom(platform: Platform, title: Title, platformSetting: Map<String, String>): UpdateInfo
    fun makeUpdateInfo(updateDate: String?, paramMap: Map<String, String>, platformSetting: Map<String, String>): UpdateInfo {
        return UpdateInfo(
                makeUpdateDate(platformSetting, updateDate),
                makeLatestUrl(platformSetting, paramMap)
        )
    }
    private fun dateDiff(from: Date, to: Date): Int {
        val dateTimeTo: Long = to.time
        val dateTimeFrom: Long = from.time
        val dayDiff = (dateTimeTo - dateTimeFrom) / (1000 * 60 * 60 * 24)
        return kotlin.math.abs(dayDiff.toInt())
    }
}