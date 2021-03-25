package jp.annnnnnna.comicList.controller

import jp.annnnnnna.comicList.model.Title
import jp.annnnnnna.comicList.service.TitleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TitleSelectApiController(
        private val titleService: TitleService
) {
    @GetMapping("/titleSelect/getTitles")
    fun get(
            @RequestParam(name = "platformId", required = false) platformId: Int
    ) : List<Title> {
        return titleService.getTitlesByPlatformId(platformId)
    }

}