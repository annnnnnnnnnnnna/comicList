package jp.annnnnnna.comicList.mapper

import jp.annnnnnna.comicList.model.Title
import org.apache.ibatis.annotations.InsertProvider
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.SelectProvider
import org.apache.ibatis.annotations.UpdateProvider
import org.apache.ibatis.jdbc.SQL
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

@Mapper
interface TitleMapper {
    @InsertProvider(type = TitleMapperProvider::class, method ="insert")
    fun insert(data: Title): Int

    @UpdateProvider(type = TitleMapperProvider::class, method ="update")
    fun update(data: Title): Int

    @UpdateProvider(type = TitleMapperProvider::class, method ="finish")
    fun finish(id: Int): Int

    @SelectProvider(type = TitleMapperProvider::class, method ="findAll")
    fun findAll(): List<Title>

    @SelectProvider(type = TitleMapperProvider::class, method ="findByName")
    fun findByName(platformId: Int, titleName: String): List<Title>

    @SelectProvider(type = TitleMapperProvider::class, method ="findByPlatformId")
    fun findByPlatformId(id: Int): List<Title>

    @SelectProvider(type = TitleMapperProvider::class, method ="findByIds")
    fun findByIds(ids: List<Int>): List<Title>
}

@Suppress("unused")
internal class TitleMapperProvider{
    fun insert(): String = Title::class.insert().toString()

    fun update(): String =SQL().apply{
        UPDATE("title")
        SET(
                "last_updated_at = #{lastUpdatedAt}",
                "latest_url = #{latestUrl}",
                "frequency = #{frequency}",
                "finished = #{finished}",
                "last_checked_at = #{lastCheckedAt}")
        WHERE("id = #{id}")
    }.toString()

    fun finish(): String =SQL().apply{
        UPDATE("title")
        SET("finished = true")
        WHERE("id = #{id}")
    }.toString()

    fun findAll(): String = Title::class.select().toString()

    fun findByName(): String = Title::class.select().apply{
        WHERE( "platform = #{platformId}", "name = #{titleName}")
    }.toString()

    fun findByPlatformId(): String = Title::class.select().apply{
        WHERE( "platform = #{id}", "finished = false")
        ORDER_BY("finished", "name")
    }.toString()

    fun findByIds(ids: List<Int>): String = Title::class.select().apply{
        WHERE( "id in (${ids.joinToString(",")})", "finished = false")
        ORDER_BY("last_updated_at desc", "name")
    }.toString()
}
