package jp.annnnnnna.comicList.mapper

import org.apache.ibatis.jdbc.SQL
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

fun <T : Any> KClass<T>.select(): SQL {
    val ret = SQL()
    primaryConstructor?.parameters?.asSequence()?.map {
        it.name?.toSnakeCase()
    }?.forEach {
       ret.SELECT(it)
    }
    ret.FROM(this.simpleName?.toSnakeCase())
    return ret
}

fun <T : Any> KClass<T>.insert(): SQL {
    val ret = SQL()
    ret.INSERT_INTO(this.simpleName?.toSnakeCase())

    primaryConstructor?.parameters?.asSequence()?.map {
        it.name?.toSnakeCase() to it.name
    }?.forEach {(column, value) ->
        ret.VALUES(column, "#{$value}")
    }

    return ret
}

fun String.toSnakeCase(): String =
        mapIndexed { index, c ->
            if (index != 0 && c.isUpperCase()) {
                "_" + c.toLowerCase()
            } else {
                c.toString().toLowerCase()
            }
        }.joinToString("")
