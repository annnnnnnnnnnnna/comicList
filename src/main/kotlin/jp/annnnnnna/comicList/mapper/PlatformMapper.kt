package jp.annnnnnna.comicList.mapper

import jp.annnnnnna.comicList.model.Platform
import jp.annnnnnna.comicList.model.Title
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.SelectProvider
import org.apache.ibatis.annotations.UpdateProvider
import org.apache.ibatis.jdbc.SQL
import java.util.*
import kotlin.reflect.full.memberProperties

@Mapper
interface PlatformMapper{
    @SelectProvider(type = PlatformMapperProvider::class, method ="findAll")
    fun findAll(): List<Platform>
    @UpdateProvider(type = PlatformMapperProvider::class, method ="updateLastCheckDate")
    fun updateLastCheckDate(platformId: Int, lastCheckedAt: Date): Int
}

@Suppress("unused")
internal class PlatformMapperProvider{
    fun findAll(): String = Platform::class.select().apply{
        ORDER_BY("name")
    }.toString()

    fun updateLastCheckDate(): String =SQL().apply{
        UPDATE("platform")
        SET("last_checked_at = #{lastCheckedAt}")
        WHERE("id = #{platformId}")
    }.toString()
}
