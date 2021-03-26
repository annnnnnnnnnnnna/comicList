package jp.annnnnnna.comicList.service.scraping

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jp.annnnnnna.comicList.mapper.PlatformMapper
import jp.annnnnnna.comicList.mapper.TitleMapper
import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import jp.annnnnnna.comicList.model.UpdateInfo
import jp.annnnnnna.comicList.service.replaceVariable
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class CrossScrapingApiImpl :
        ScrapingApiImplBase,
        GetTitlesFromJsonNodeInterface,
        GetUpdateResultFromJsonNodeInterface
{
    override lateinit var titleMapper: TitleMapper
    override lateinit var platformMapper: PlatformMapper
    override lateinit var objectMapper: ObjectMapper

    private val titleListUrl = "titleListUrl"
    private val titleListKey = "titleListKey"
    private val titleKey = "titleKey"
    private val titlePathKey = "titlePathKey"
    private val titleUrlStringFormat = "titleUrlStringFormat"
    private val titleLatestUrlStringFormat = "titleLatestUrlStringFormat"
    private val titleDataUrlStringFormat = "titleDataUrlStringFormat"
    private val updateDateItem = "updateDateItem"
    private val updateDateFormat = "updateDateFormat"
    private val latestLinkItem = "latestLinkItem"

    override var settingKeys: Set<String> = arrayOf(
            titleListUrl,
            titleListKey,
            titleKey,
            titlePathKey,
            titleUrlStringFormat,
            titleLatestUrlStringFormat,
            titleDataUrlStringFormat,
            updateDateItem,
            updateDateFormat,
            latestLinkItem
    ).toSet()

    override fun makeUrl(platformSetting: Map<String, String>, paramMap: Map<String, String>): String {
        return platformSetting[titleUrlStringFormat]!!.replaceVariable(paramMap)
    }
    override fun makeLatestUrl(platformSetting: Map<String, String>, paramMap: Map<String, String>): String {
        return platformSetting[titleLatestUrlStringFormat]!!.replaceVariable(paramMap)
    }
    override fun makeDataUrl(platformSetting: Map<String, String>, paramMap: Map<String, String>): String {
        return platformSetting[titleDataUrlStringFormat]!!.replaceVariable(paramMap)
    }
    override fun makeUpdateDate(platformSetting: Map<String, String>, updateDate:String?): Date? {
        return if (updateDate.isNullOrEmpty()) null else SimpleDateFormat(platformSetting[updateDateFormat]!!).parse(updateDate)
    }

    override fun getTitlesApiCustom(platform: Platform, platformSetting: Map<String, String>, titles: MutableList<Title>): List<Int> {
        val id = titleMapper.findAll().size + 1
        val doc: String = Jsoup.connect(platformSetting[titleListUrl]!!)
                .ignoreContentType(true)
                .execute()
                .body()

        val node: JsonNode = objectMapper.readTree(doc)
        val ret = mutableListOf<Int>()
        ret.addAll(node[platformSetting[titleListKey]!!].getTitlesFromJsonNode(platform, platformSetting, id, titles))
        return ret
    }

    override fun getUpdateDateApiCustom(platform: Platform, title: Title, platformSetting: Map<String, String>): UpdateInfo {
        val doc: String = Jsoup.connect(title.updateCheckUrl)
                .ignoreContentType(true)
                .execute()
                .body()
        val node: JsonNode = objectMapper.readTree(doc)
        return node.getUpdateResultFromJsonNode(platformSetting)
    }

    override fun JsonNode.getTitleNameFromJsonNode(platformSetting: Map<String, String>): String {
        return this[platformSetting[titleKey]!!].textValue()
    }
    override fun JsonNode.getPathFromFromJsonNode(platformSetting: Map<String, String>): String {
        return this[platformSetting[titlePathKey]!!].textValue()
    }
    override fun JsonNode.getComicIdFromFromJsonNode(platformSetting: Map<String, String>): String {
        return ""
    }
    override fun JsonNode.getUpdateDateFromJsonNode(platformSetting: Map<String, String>): String {
        var updateDate = this
        platformSetting[updateDateItem]!!.split(".").forEach {
            updateDate = try {
                updateDate[it.toInt()]
            } catch(_: Exception) {
                updateDate[it]
            }
        }
        return updateDate.textValue()
    }
    override fun JsonNode.getLatestLinkFromJsonNode(platformSetting: Map<String, String>): String {
        var latestLink = this
        platformSetting[latestLinkItem]!!.split(".").forEach {
            latestLink = try {
                latestLink[it.toInt()]
            } catch(_: Exception) {
                latestLink[it]
            }
        }
        return latestLink.textValue()
    }
}