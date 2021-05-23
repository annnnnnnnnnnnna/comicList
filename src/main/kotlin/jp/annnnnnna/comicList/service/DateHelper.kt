package jp.annnnnnna.comicList.service

import java.util.*

fun getNowInJST(): Calendar =
    Calendar.getInstance().apply {
        add(Calendar.MILLISECOND, TimeZone.getTimeZone("JST").rawOffset - TimeZone.getDefault().rawOffset)
    }
