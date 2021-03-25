package jp.annnnnnna.comicList.controller


import jp.annnnnnna.comicList.service.ScrapingService
import jp.annnnnnna.comicList.service.TitleService
import kotlinx.coroutines.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ScrapingController(
        private val titleService: TitleService,
        private val scrapingService: ScrapingService
) {
    @GetMapping("/scraping")
    fun get() :String {
        GlobalScope.launch {
            scrapingService.update()
            println("done")
        }
        return "done"
    }
    @GetMapping("/scraping/test")
    fun test() :String {
        scrapingService.test()
        return "done"
    }

}