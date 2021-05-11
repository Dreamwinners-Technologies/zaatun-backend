package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.StaticPageRequest;
import com.zaatun.zaatunecommerce.model.StaticPageModel;
import com.zaatun.zaatunecommerce.service.StaticPageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/staticPages")
public class StaticPageController {
    private final StaticPageService staticPageService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addStaticPage(@RequestHeader(name = "Authorization", required = false) String token,
                                                             @RequestBody StaticPageRequest staticPageRequest) {

        return staticPageService.addStaticPage(token, staticPageRequest);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StaticPageModel>>> getStaticPages() {
        return staticPageService.getStaticPages();
    }

    @PutMapping("/{pageId}")
    public ResponseEntity<ApiResponse<String>> editStaticPage(
            @PathVariable String pageId,
            @RequestHeader(name = "Authorization", required = false) String token,
            @RequestBody StaticPageRequest staticPageRequest) {

        return staticPageService.editStaticPage(pageId, token, staticPageRequest);
    }

    @DeleteMapping("/{pageId}")
    public ResponseEntity<ApiResponse<String>> deleteStaticPage(@PathVariable String pageId) {

        return staticPageService.deleteStaticPage(pageId);
    }
}
