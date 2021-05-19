package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.ProfileSort;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final ProfileRepository profileRepository;

    public ResponseEntity<ApiResponse<PaginationResponse<List<ProfileModel>>>> getUserList(Integer pageNo, Integer pageSize, String name,
                                                           String username, String phone, String email,
                                                           ProfileSort sortBy, Sort.Direction direction) {

        ProfileModel exampleProfile = ProfileModel.builder()
                .name(name)
                .username(username)
                .phoneNo(phone)
                .email(email)
                .build();

        Sort sort = Sort.by(direction, sortBy.toString());
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<ProfileModel> profileModelPage = profileRepository.findAll(Example.of(exampleProfile, matcher), pageable);

        PaginationResponse<List<ProfileModel>> paginationResponse = new PaginationResponse<>(pageSize, pageNo,
                profileModelPage.getContent().size(), profileModelPage.isLast(), profileModelPage.getTotalElements(),
                profileModelPage.getTotalPages(), profileModelPage.getContent());

        return new ResponseEntity<>(new ApiResponse<>(200, "Profile Found", paginationResponse), HttpStatus.OK);
    }
}
