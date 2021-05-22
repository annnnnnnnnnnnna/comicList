package jp.annnnnnna.comicList.service.scraping

import com.fasterxml.jackson.databind.JsonNode
import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title

interface GetTitlesFromJsonNodeInterface: ScrapingApiImplBase {

    fun JsonNode.getTitleNameFromJsonNode(platformSetting: Map<String, String>): String
    fun JsonNode.getPathFromFromJsonNode(platformSetting: Map<String, String>): String
    fun JsonNode.getComicIdFromFromJsonNode(platformSetting: Map<String, String>): String

    fun JsonNode.getTitlesFromJsonNode(platform: Platform, platformSetting: Map<String, String>): List<Title> {
        return map {
            val titleName = it.getTitleNameFromJsonNode(platformSetting)
            val path = it.getPathFromFromJsonNode(platformSetting)
            val comicId = it.getComicIdFromFromJsonNode(platformSetting)

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