package jp.annnnnnna.comicList.service.scraping

import com.fasterxml.jackson.databind.ObjectMapper
import jp.annnnnnna.comicList.mapper.PlatformMapper
import jp.annnnnnna.comicList.mapper.TitleMapper
import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import jp.annnnnnna.comicList.model.UpdateInfo
import jp.annnnnnna.comicList.service.replaceVariable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*

class SundayScrapingApiImpl:
        ScrapingApiImplBase,
        GetTitlesFromHtmlElementsInterface,
        GetUpdateResultFromHtmlElementsInterface
{
    override lateinit var titleMapper: TitleMapper
    override lateinit var platformMapper: PlatformMapper
    override lateinit var objectMapper: ObjectMapper

    private val titleListUrl = "titleListUrl"
    private val titleListKey = "titleListKey"
    private val titleKey = "titleKey"
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

    override fun getTitlesApiCustom(platform: Platform, platformSetting: Map<String, String>): Int {
        val id = titleMapper.findAll().size + 1
        val doc: Document = Jsoup.connect(platformSetting[titleListUrl]!!).get()

        val node = doc.select(platformSetting[titleListKey]!!)

        return node.getTitlesFromHtmlElements(platform, platformSetting, id)
    }

    override fun getUpdateDateApiCustom(platform: Platform, title: Title, platformSetting: Map<String, String>): UpdateInfo {
        val doc: Document = Jsoup.connect(title.updateCheckUrl).get()
        return doc.getUpdateResultFromHtmlElements(platformSetting)
    }

    override fun Element.getTitleNameFromHtmlElement(platformSetting: Map<String, String>): String {
        return select(platformSetting[titleKey]!!).text()
    }
    override fun Element.getPathFromHtmlElement(platformSetting: Map<String, String>): String {
        return attr("href")
    }
    override fun Element.getComicIdFromHtmlElement(platformSetting: Map<String, String>): String {
        return ""
    }
    override fun Document.getUpdateDateFromHtmlElement(platformSetting: Map<String, String>): String {
        return select(platformSetting[updateDateItem]!!).text()
    }
    override fun Document.getLatestLinkFromHtmlElement(platformSetting: Map<String, String>): String {
        return select(platformSetting[latestLinkItem]!!).attr("href")
    }

}