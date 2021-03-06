package jp.annnnnnna.comicList.mapper

import jp.annnnnnna.comicList.model.ScrapingHistory
import org.apache.ibatis.annotations.InsertProvider
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.SelectProvider

@Mapper
interface ScrapingHistoryMapper {
    @InsertProvider(type = ScrapingHistoryMapperProvider::class, method ="insert")
    fun insert(data: ScrapingHistory): Int

    @SelectProvider(type = ScrapingHistoryMapperProvider::class, method ="findLatest")
    fun findLatest(): ScrapingHistory

}

@Suppress("unused")
internal class ScrapingHistoryMapperProvider{
    fun insert(): String = ScrapingHistory::class.insert().toString()

    fun findLatest(): String = ScrapingHistory::class.select().apply{
        ORDER_BY("last_checked_at desc")
        LIMIT(1)
    }.toString()
}
