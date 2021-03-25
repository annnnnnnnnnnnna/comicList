package jp.annnnnnna.comicList.service

fun String.replaceVariable(data: Map<String, String>): String {
    var ret = this
    data.keys.forEach {
        ret = ret.replace("#$it#".toRegex(), data[it] ?: error(""))
    }
    return ret
}