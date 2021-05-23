package jp.annnnnnna.comicList.service.scraping

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jp.annnnnnna.comicList.mapper.PlatformMapper
import jp.annnnnnna.comicList.mapper.TitleMapper
import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import jp.annnnnnna.comicList.model.UpdateInfo
import jp.annnnnnna.comicList.service.getNowInJST
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
            val titles = mutableListOf<Title>().apply{
                addAll(titleMapper.findByPlatformId(platform.id))
            }

            val titleList = getTitlesApiCustom(platform, platformSetting, titles)

            var id = titleMapper.findAll().size + 1
            var updateCount = 0
            var notFound = titles.map{ it.id }
            titleList.forEach { title ->
                if (insertIfNotExist(id, title, titles)) {
                    id++
                    updateCount++
                } else {
                    notFound = notFound.minus(titles.find {it.name == title.name}?.id?: -1)
                }
            }

            val now = getNowInJST().time
            platformMapper.updateLastCheckDate(platform.id, now)
            notFound.forEach {
                titleMapper.finish(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getTitlesApiCustom(platform: Platform, platformSetting: Map<String, String>, titles: MutableList<Title>): List<Title>
    fun insertIfNotExist(id: Int, newTitle: Title, titles: MutableList<Title>): Boolean {
        val title = titles.find { it.name  == newTitle.name }
        if (title == null) {
            titleMapper.insert(
                Title(
                    id,
                    newTitle.name,
                    newTitle.platform,
                    0,
                    null,
                    null,
                    newTitle.latestUrl,
                    newTitle.url,
                    newTitle.updateCheckUrl,
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

    private fun needCheck(lastCheckedAt: Date?, days: Int, platform: Platform): Boolean {
        if (lastCheckedAt == null) return true
        val next = Calendar.getInstance().apply {
            time = lastCheckedAt
            add(Calendar.DATE, days)
            set(Calendar.HOUR_OF_DAY, platform.updateTime)
            set(Calendar.MINUTE, 5) // 更新後5分まで一応あける
        }.time
        val now = getNowInJST().time
        return next < now
    }
    fun getUpdateDateApi(platform: Platform, title: Title, platformSetting: Map<String, String>) {
        if (title.finished || !needCheck(title.lastCheckedAt, title.frequency, platform)) {
            return
        }
        val today = getNowInJST()

        val updateInfo = getUpdateDateApiCustom(platform, title, platformSetting)

        Thread.sleep(10000) // 各作品の情報取得はとりあえず10秒間隔にしておく
        if (updateInfo.updateDate == null) {
            if (title.lastUpdatedAt != null) title.finished = true
            title.lastCheckedAt = getNowInJST().time
            if (title.frequency == 0) title.frequency = 1
        } else {
            val diff = when (title.lastUpdatedAt) {
                null -> dateDiff(updateInfo.updateDate, getNowInJST().time)
                updateInfo.updateDate -> dateDiff(updateInfo.updateDate, title.lastCheckedAt!!)
                else -> dateDiff(updateInfo.updateDate, title.lastUpdatedAt!!)
            }
            title.frequency = when(diff/7) {
                0 -> 1
                1 -> 7
                2 -> 14
                else -> 35 // 1年以上更新がないようなものの扱いについて要考慮
            }

            val lastCheckedAt = Calendar.getInstance().apply {
                time = updateInfo.updateDate
            }
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