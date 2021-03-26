package jp.annnnnnna.comicList.service.scraping

import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

interface GetTitlesFromHtmlElementsInterface: ScrapingApiImplBase {
    fun Element.getTitleNameFromHtmlElement(platformSetting: Map<String, String>): String
    fun Element.getPathFromHtmlElement(platformSetting: Map<String, String>): String
    fun Element.getComicIdFromHtmlElement(platformSetting: Map<String, String>): String

    fun Elements.getTitlesFromHtmlElements(platform: Platform, platformSetting: Map<String, String>, idBegin:Int, titles: MutableList<Title>): List<Int> {
        var id = idBegin
        var updateCount = 0
        var ret = titles.map{ it.id }
        forEach {
            val titleName = it.getTitleNameFromHtmlElement(platformSetting)
            val path = it.getPathFromHtmlElement(platformSetting)
            val comicId = it.getComicIdFromHtmlElement(platformSetting)

            val mp = makeParamMap(path, comicId)

            if (insertIfNotExist(titleName, id, mp, platform, platformSetting, titles)) {
                id++
                updateCount++
            } else {
                ret = ret.minus(titles.find {it.name == titleName}?.id?: -1)
            }
        }
        return ret
    }

    private fun makeParamMap(path:String, comicId: String): Map<String, String> {
        val mp = mutableMapOf<String, String>()
        // このキーの責任範囲をちゃんとしたい
        mp["titleUrl"] = path
        mp["id"] = comicId
        return mp
    }
}