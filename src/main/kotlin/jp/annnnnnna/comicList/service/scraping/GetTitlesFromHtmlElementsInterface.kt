package jp.annnnnnna.comicList.service.scraping

import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

interface GetTitlesFromHtmlElementsInterface: ScrapingApiImplBase {
    fun Element.getTitleNameFromHtmlElement(platformSetting: Map<String, String>): String
    fun Element.getPathFromHtmlElement(platformSetting: Map<String, String>): String
    fun Element.getComicIdFromHtmlElement(platformSetting: Map<String, String>): String

    fun Elements.getTitlesFromHtmlElements(
        platform: Platform,
        platformSetting: Map<String, String>
    ): List<Title> {
        return map {
            val titleName = it.getTitleNameFromHtmlElement(platformSetting)
            val path = it.getPathFromHtmlElement(platformSetting)
            val comicId = it.getComicIdFromHtmlElement(platformSetting)

            val mp = makeParamMap(path, comicId)

            Title(
                -1,
                titleName,
                platform.id,
                0,
                null,
                null,
                makeUrl(platformSetting, mp),
                makeUrl(platformSetting, mp),
                makeDataUrl(platformSetting, mp),
                false
            )
        }
    }

    private fun makeParamMap(path:String, comicId: String): Map<String, String> {
        val mp = mutableMapOf<String, String>()
        // このキーの責任範囲をちゃんとしたい
        mp["titleUrl"] = path
        mp["id"] = comicId
        return mp
    }
}