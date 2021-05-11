package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.StaticPageRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.StaticPageModel;
import com.zaatun.zaatunecommerce.repository.StaticPageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StaticPageService {
    private final StaticPageRepository staticPageRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<String>> addStaticPage(String token, StaticPageRequest staticPageRequest) {
        String pageId = UUID.randomUUID().toString();
        String pageSlug = staticPageRequest.getTitle().replace(" ", "-") + "-" + pageId.substring(0, 4);
        String username = jwtProvider.getUserNameFromJwt(token);

        StaticPageModel staticPageModel = new StaticPageModel(pageId, staticPageRequest.getTitle(), pageSlug,
                System.currentTimeMillis(), username, null, null, staticPageRequest.getPosition(),
                staticPageRequest.getContent());

        staticPageRepository.save(staticPageModel);

        return new ResponseEntity<>(new ApiResponse<>(201, "Static Page Created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse<List<StaticPageModel>>> getStaticPages() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        List<StaticPageModel> staticPageModels = staticPageRepository.findAll(sort);

        return new ResponseEntity<>(new ApiResponse<>(200, "Static Pages Found", staticPageModels), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<String>> editStaticPage(String pageId, String token, StaticPageRequest staticPageRequest) {
        String pageSlug = staticPageRequest.getTitle().replace(" ", "-") + "-" + pageId.substring(0, 4);
        String username = jwtProvider.getUserNameFromJwt(token);

        Optional<StaticPageModel> staticPageModelOptional = staticPageRepository.findById(pageId);
        if(staticPageModelOptional.isPresent()){
            StaticPageModel staticPageModel = staticPageModelOptional.get();
            staticPageModel.setPageSlug(pageSlug);
            staticPageModel.setTitle(staticPageRequest.getTitle());
            staticPageModel.setPosition(staticPageRequest.getPosition());
            staticPageModel.setContent(staticPageRequest.getContent());

            staticPageRepository.save(staticPageModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Static Page Edit Successful", null), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Page found with that is");
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteStaticPage(String pageId) {
        Optional<StaticPageModel> staticPageModelOptional = staticPageRepository.findById(pageId);
        if(staticPageModelOptional.isPresent()){
            staticPageRepository.deleteById(pageId);

            return new ResponseEntity<>(new ApiResponse<>(200, "Static Page Delete Successful", null), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Page found with that is");
        }
    }
}
