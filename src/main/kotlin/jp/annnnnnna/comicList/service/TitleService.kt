package jp.annnnnnna.comicList.service

import jp.annnnnnna.comicList.mapper.PlatformMapper
import jp.annnnnnna.comicList.mapper.TitleMapper
import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import org.springframework.stereotype.Service

@Service
class TitleService (
        private val titleMapper: TitleMapper,
        private val platformMapper: PlatformMapper
) {
    fun getTitles(cookieStr: String?):List<Title> {
        return if (cookieStr.isNullOrEmpty()) return listOf()
        else titleMapper.findByIds(cookieStr.split("-").mapNotNull { try { it.toInt() } catch(_: Exception) { null } })
    }
    fun getTitlesByPlatformId(platformId: Int):List<Title> {
        return titleMapper.findByPlatformId(platformId)
    }
    fun getPlatforms():List<Platform> {
        return platformMapper.findAll()
    }
}