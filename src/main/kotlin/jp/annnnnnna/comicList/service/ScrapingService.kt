package jp.annnnnnna.comicList.service

import com.fasterxml.jackson.databind.ObjectMapper
import jp.annnnnnna.comicList.mapper.PlatformMapper
import jp.annnnnnna.comicList.mapper.ScrapingHistoryMapper
import jp.annnnnnna.comicList.mapper.TitleMapper
import jp.annnnnnna.comicList.model.ScrapingHistory
import jp.annnnnnna.comicList.service.scraping.ScrapingApiImplBase
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.util.*
import kotlin.reflect.full.createInstance

@Service
class ScrapingService (
        private val scrapingHistoryMapper: ScrapingHistoryMapper,
        private val platformMapper: PlatformMapper,
        private val titleMapper: TitleMapper
) {
    companion object{
        const val classNamePrefix = "jp.annnnnnna.comicList.service.scraping."
        const val classNameSuffix = "ScrapingApiImpl"
    }
    private val objectMapper = ObjectMapper()

    fun getClassName(type: String): String = "${classNamePrefix}${type}${classNameSuffix}"
    fun getScrapingClass(type: String): ScrapingApiImplBase {
        return (Class.forName(getClassName(type)).kotlin.createInstance() as ScrapingApiImplBase).also {
            it.platformMapper = platformMapper
            it.titleMapper = titleMapper
            it.objectMapper = objectMapper
        }
    }

    fun update() {
        if (Calendar.getInstance(TimeZone.getDefault()).apply {
                add(Calendar.MILLISECOND, TimeZone.getTimeZone("JST").rawOffset - TimeZone.getDefault().rawOffset)
                add(Calendar.MINUTE, -30)
        }.time < scrapingHistoryMapper.findLatest().lastCheckedAt) {
            // 30分以内の再起動はしない
            return
        }

        val now = Calendar.getInstance().apply {
            add(Calendar.MILLISECOND, TimeZone.getTimeZone("JST").rawOffset - TimeZone.getDefault().rawOffset)
        }.time
        scrapingHistoryMapper.insert(ScrapingHistory(now))

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.MILLISECOND, TimeZone.getTimeZone("JST").rawOffset - TimeZone.getDefault().rawOffset)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val platforms = platformMapper.findAll()
        platforms.forEach { platform ->
            val scrapingService = getScrapingClass(platform.dataType)
            val settingMap = scrapingService.checkSetting(objectMapper.readTree(platform.settingsJson))

            if ((platform.lastCheckedAt == null ||  platform.lastCheckedAt < yesterday)
                    && Calendar.getInstance(TimeZone.getTimeZone("JST")).get(Calendar.HOUR_OF_DAY) >= platform.updateTime) {
                try {
                    scrapingService.getTitlesApi(platform, settingMap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                titleMapper.findByPlatformId(platform.id).forEach {
                    try {
                        scrapingService.getUpdateDateApi(platform, it, settingMap)
                    } catch (hse : HttpStatusException) {
                        if (hse.statusCode == 404) titleMapper.finish(it.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                platformMapper.updateLastCheckDate(platform.id, now)
            }
        }
    }

    fun test() {
        val doc: String = Jsoup.connect("https://www.ganganonline.com/contents/watashiga/")
                .ignoreContentType(true)
                .execute()
                .body()
        println(doc)
    }
}
