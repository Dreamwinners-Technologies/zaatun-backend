package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.AffiliateUserModel;
import com.zaatun.zaatunecommerce.model.AffiliateWithdrawModel;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.repository.AffiliateUserRepository;
import com.zaatun.zaatunecommerce.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShopAffiliateService {
    private final ProfileRepository profileRepository;
    private final AffiliateUserRepository affiliateUserRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<String>> beAffiliate(String token) {
        String username = jwtProvider.getUserNameFromJwt(token);
        String id = UUID.randomUUID().toString();

        System.out.println(username);
        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(username);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();
            if ((profileModel.getIsAffiliate() != null && profileModel.getIsAffiliate()) || profileModel.getAffiliateUser() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already an affiliate user");
            }

            Long createdOn = System.currentTimeMillis();

            List<AffiliateWithdrawModel> affiliateWithdrawModels = new ArrayList<>();
            AffiliateUserModel affiliateUserModel = new AffiliateUserModel(id, createdOn, null, null, false,
                    username, null, 0, 0, 0, affiliateWithdrawModels, profileModel);

            profileModel.setAffiliateUser(affiliateUserModel);

            profileRepository.save(profileModel);

            return new ResponseEntity<>(new ApiResponse<>(201, "Be affiliate requested.Wait for Approval",
                    null), HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile Not Found");
    }

    public ResponseEntity<ApiResponse<String>> makeWithdrawRequest(String token, Integer amount) {
        String username = jwtProvider.getUserNameFromJwt(token);

        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(username);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            if (profileModel.getAffiliateUser() != null) {
                AffiliateUserModel affiliateUserModel = profileModel.getAffiliateUser();

                if (profileModel.getIsAffiliate() != null && !profileModel.getIsAffiliate()) {
                    if (affiliateUserModel.getIsApproved()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your Affiliate id is postponed for some reason." +
                                "Please Contact with Admin.");
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please, Wait for admins approval of your id");
                    }
                } else {
                    Integer balanceOnAccount = affiliateUserModel.getAffiliateBalance();

                    if (balanceOnAccount < amount) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance on your account");
                    } else {
                        AffiliateWithdrawModel affiliateWithdrawModel = new AffiliateWithdrawModel();
                        affiliateWithdrawModel.setAffiliateUserModel(affiliateUserModel);
                        affiliateWithdrawModel.setCreatedOn(System.currentTimeMillis());
                        affiliateWithdrawModel.setWithdrawAmount(amount);
                        affiliateWithdrawModel.setIsApproved(false);
                        affiliateWithdrawModel.setIsCompleted(false);

                        List<AffiliateWithdrawModel> affiliateWithdrawModels = affiliateUserModel.getAffiliateWithdrawModels();
                        affiliateWithdrawModels.add(affiliateWithdrawModel);

                        profileRepository.save(profileModel);

                        return new ResponseEntity<>(new ApiResponse<>(201, "Withdraw Request is Placed. Wait for admin Approval.", null), HttpStatus.CREATED);
                    }
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not an affiliate user");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Profile Found");
        }
    }
}
