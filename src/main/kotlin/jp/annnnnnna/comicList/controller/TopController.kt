package jp.annnnnnna.comicList.controller

import jp.annnnnnna.comicList.service.ScrapingService
import jp.annnnnnna.comicList.service.TitleService
import kotlinx.coroutines.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView


@Controller
class TopController(
        private val titleService: TitleService,
        private val scrapingService: ScrapingService
) {
    @GetMapping("/")
    fun get(
            @CookieValue("titles") cookieValue: String?,
    ) : ModelAndView {
        GlobalScope.launch {
            scrapingService.update()
        }

        return ModelAndView("comicList").apply {
            addObject("comics", titleService.getTitles(cookieValue))
        }
    }
}