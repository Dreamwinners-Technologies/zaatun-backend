package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopStaticPageResponse;
import com.zaatun.zaatunecommerce.model.StaticPageModel;
import com.zaatun.zaatunecommerce.repository.StaticPageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ShopStaticPageService {
    private final StaticPageRepository staticPageRepository;


    public ResponseEntity<ApiResponse<List<ShopStaticPageResponse>>> getStaticPageList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        List<StaticPageModel> staticPageModels = staticPageRepository.findAll(sort);

        List<ShopStaticPageResponse> shopStaticPageResponses = new ArrayList<>();
        for (StaticPageModel staticPageModel: staticPageModels){
            ShopStaticPageResponse shopStaticPageResponse = new ShopStaticPageResponse(staticPageModel.getTitle(),
                    staticPageModel.getPageSlug(), staticPageModel.getPosition(), null);
            shopStaticPageResponses.add(shopStaticPageResponse);
        }

        return new ResponseEntity<>(new ApiResponse<>(200, "Static Page Found", shopStaticPageResponses), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<ShopStaticPageResponse>> getStaticPageBySlug(String pageSlug) {
        Optional<StaticPageModel> staticPageModelOptional = staticPageRepository.findByPageSlug(pageSlug);
        if(staticPageModelOptional.isPresent()){
            StaticPageModel staticPageModel = staticPageModelOptional.get();

            ShopStaticPageResponse shopStaticPageResponse = new ShopStaticPageResponse(staticPageModel.getTitle(),
                    staticPageModel.getPageSlug(), staticPageModel.getPosition(), staticPageModel.getContent());

            return new ResponseEntity<>(new ApiResponse<>(200, "Page Found", shopStaticPageResponse), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Page found with that id");
        }
    }
}
