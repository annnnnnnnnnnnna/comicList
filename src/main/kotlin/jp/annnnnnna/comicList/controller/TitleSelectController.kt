package jp.annnnnnna.comicList.controller

import jp.annnnnnna.comicList.service.TitleService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class TitleSelectController(
        private val titleService: TitleService
) {
    @GetMapping("/titleSelect")
    fun get() : ModelAndView =
            ModelAndView("allComicList").apply {
                addObject("platforms", titleService.getPlatforms())
            }

}