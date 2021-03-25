package jp.annnnnnna.comicList.service.scraping

import com.fasterxml.jackson.databind.JsonNode
import jp.annnnnnna.comicList.model.UpdateInfo

interface GetUpdateResultFromJsonNodeInterface: ScrapingApiImplBase {
    fun JsonNode.getUpdateDateFromJsonNode(platformSetting: Map<String, String>): String
    fun JsonNode.getLatestLinkFromJsonNode(platformSetting: Map<String, String>): String

    fun JsonNode.getUpdateResultFromJsonNode(platformSetting: Map<String, String>): UpdateInfo {
        return makeUpdateInfo(
                getUpdateDateFromJsonNode(platformSetting),
                makeParamMap(getLatestLinkFromJsonNode(platformSetting)),
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