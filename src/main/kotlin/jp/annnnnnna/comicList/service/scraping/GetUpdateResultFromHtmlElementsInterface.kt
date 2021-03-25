package jp.annnnnnna.comicList.service.scraping

import jp.annnnnnna.comicList.model.UpdateInfo
import org.jsoup.nodes.Document

interface GetUpdateResultFromHtmlElementsInterface: ScrapingApiImplBase {
    fun Document.getUpdateDateFromHtmlElement(platformSetting: Map<String, String>): String
    fun Document.getLatestLinkFromHtmlElement(platformSetting: Map<String, String>): String

    fun Document.getUpdateResultFromHtmlElements(platformSetting: Map<String, String>): UpdateInfo {
        return makeUpdateInfo(
                getUpdateDateFromHtmlElement(platformSetting),
                makeParamMap(getLatestLinkFromHtmlElement(platformSetting)),
                platformSetting
        )
    }
    private fun makeParamMap(latestLink:String): Map<String, String> {
        val mp = mutableMapOf<String, String>()
        // このキーの責任範囲をちゃんとしたい
        mp["latestUrl"] = latestLink
        return mp
    }
}