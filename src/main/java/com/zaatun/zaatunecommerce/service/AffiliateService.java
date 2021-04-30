package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.AffiliateUserSort;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.AffiliateUserModel;
import com.zaatun.zaatunecommerce.model.AffiliateWithdrawModel;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.repository.AffiliateWithdrawRepository;
import com.zaatun.zaatunecommerce.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AffiliateService {
    private final ProfileRepository profileRepository;
    private final AffiliateWithdrawRepository affiliateWithdrawRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<PaginationResponse<List<ProfileModel>>>> getNewAffiliateUsers(int pageSize, int pageNo) {
        ProfileModel exampleProfile = ProfileModel.builder()
                .affiliateUser(AffiliateUserModel.builder().isApproved(false).build())
                .build();

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("affiliateUser.createdOn").descending());
        Page<ProfileModel> profileModelPage = profileRepository.findAll(Example.of(exampleProfile), pageable);
        System.out.println(profileModelPage.getNumberOfElements());

        PaginationResponse<List<ProfileModel>> paginationResponse = new PaginationResponse<>(pageSize, pageNo, profileModelPage.getContent().size(),
                profileModelPage.isLast(), profileModelPage.getTotalElements(), profileModelPage.getTotalPages(),
                profileModelPage.getContent());
        System.out.println(paginationResponse.getData().size());
        if (profileModelPage.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "New Affiliate Users Not Found", paginationResponse),
                    HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "New Affiliate Users Found", paginationResponse),
                    HttpStatus.ACCEPTED);
        }

    }

    public ResponseEntity<ApiResponse<String>> approveAffiliate(String token, String id) {
        ProfileModel exampleProfile = ProfileModel.builder()
                .affiliateUser(AffiliateUserModel.builder().id(id).build())
                .build();

        Optional<ProfileModel> profileModelOptional = profileRepository.findOne(Example.of(exampleProfile));

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            if (profileModel.getIsAffiliate() != null && profileModel.getIsAffiliate()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already an affiliate user");
            }

            AffiliateUserModel affiliateUserModel = profileModel.getAffiliateUser();

            String affiliateUserSlug = "ZTN-A-"+affiliateUserModel.getId().substring(0, 8).toUpperCase();

            profileModel.setIsAffiliate(true);
            affiliateUserModel.setIsApproved(true);
            affiliateUserModel.setAffiliateUserSlug(affiliateUserSlug);
            profileRepository.save(profileModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Affiliate User is Approved", id), HttpStatus.ACCEPTED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Affiliate User Not Found");
        }
    }

    public ResponseEntity<ApiResponse<PaginationResponse<List<ProfileModel>>>>
    getAffiliateUserList(int pageNo, int pageSize, String name, String phoneNo, String affiliateUserSlug,
                         AffiliateUserSort sortBy, Sort.Direction sortDirection) {

        ProfileModel exampleProfile = ProfileModel.builder()
                .name(name)
                .phoneNo(phoneNo)
                .affiliateUser(AffiliateUserModel.builder().affiliateUserSlug(affiliateUserSlug).isApproved(true).build())
                .build();

        Pageable pageable;
        Sort sort = Sort.by(sortDirection, "affiliateUser."+sortBy.toString()); //OrderBy is Column name and sortBy is Direction

        pageable = PageRequest.of(pageNo, pageSize, sort); //Make pageable object for pagination

        //Example matcher logics for advance searching
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<ProfileModel> profileModelPage = profileRepository.findAll(Example.of(exampleProfile, matcher), pageable);

        PaginationResponse<List<ProfileModel>> paginationResponse = new PaginationResponse<>(pageSize, pageNo,
                profileModelPage.getSize(), profileModelPage.isLast(), profileModelPage.getTotalElements(),
                profileModelPage.getTotalPages(), profileModelPage.getContent());

        System.out.println(paginationResponse.getData().get(0).getName());

        for (ProfileModel profileModel: paginationResponse.getData()){
            System.out.println(profileModel.getName());
            System.out.println(profileModel.getIsAffiliate());
        }
        if (profileModelPage.isEmpty()) {
            //If there is no profile found
            return new ResponseEntity<>(new ApiResponse<>(200, "No Affiliate User Found", paginationResponse), HttpStatus.OK);
        } else {
            //If products are found
            return new ResponseEntity<>(new ApiResponse<>(200, "Affiliate User Found", paginationResponse), HttpStatus.OK);
        }
    }

    public ResponseEntity getWithdrawRequests() {

        AffiliateWithdrawModel exampleWithdraw = AffiliateWithdrawModel.builder()
                .isApproved(false)
                .build();

        Pageable pageable = PageRequest.of(0, 50);

        Page<AffiliateWithdrawModel> affiliateWithdrawModels = affiliateWithdrawRepository.findAll(Example.of(exampleWithdraw), pageable);

        return new ResponseEntity(affiliateWithdrawModels, HttpStatus.OK);
    }

    public ResponseEntity approveWithdrawRequest(String token, Long withdrawId, String massage, Boolean isApproved) {
        String updatedBy = jwtProvider.getNameFromJwt(token);
        Long updatedOn = System.currentTimeMillis();

        Optional<AffiliateWithdrawModel> affiliateWithdrawModelOptional = affiliateWithdrawRepository.findById(withdrawId);
        if(affiliateWithdrawModelOptional.isPresent()){
            AffiliateWithdrawModel affiliateWithdrawModel = affiliateWithdrawModelOptional.get();
            AffiliateUserModel affiliateUserModel = affiliateWithdrawModel.getAffiliateUserModel();

            affiliateWithdrawModel.setUpdatedBy(updatedBy);
            affiliateWithdrawModel.setUpdatedOn(updatedOn);
            affiliateWithdrawModel.setIsApproved(isApproved);
            affiliateWithdrawModel.setMassage(massage);
            affiliateWithdrawModel.setIsCompleted(true);

            if(isApproved){
                affiliateUserModel.setAffiliateBalance(affiliateUserModel.getAffiliateBalance() -
                        affiliateWithdrawModel.getWithdrawAmount());
            }

            affiliateUserModel.setUpdatedBy(updatedBy);
            affiliateUserModel.setUpdatedOn(updatedOn);

            affiliateWithdrawRepository.save(affiliateWithdrawModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Withdraw Request Approved", null), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Withdrawal Data Found");
        }
    }
}
