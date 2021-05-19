package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.shop.ShopDeliveryAddressRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.repository.DeliveryAddressRepository;
import com.zaatun.zaatunecommerce.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DeliveryAddressService {
    private final ProfileRepository profileRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<Long>> addDeliveryAddress(String token, ShopDeliveryAddressRequest shopDeliveryAddressRequest) {
        String userName = jwtProvider.getUserNameFromJwt(token);

        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(userName);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            DeliveryAddressModel deliveryAddressModel = DeliveryAddressModel.builder()
                    .fullName(shopDeliveryAddressRequest.getFullName())
                    .phoneNo(shopDeliveryAddressRequest.getPhoneNo())
                    .email(shopDeliveryAddressRequest.getEmail())
                    .address(shopDeliveryAddressRequest.getAddress())
                    .area(shopDeliveryAddressRequest.getArea())
                    .city(shopDeliveryAddressRequest.getCity())
                    .region(shopDeliveryAddressRequest.getRegion())
                    .build();

            profileModel.getDeliveryAddresses().add(deliveryAddressModel);

            profileRepository.save(profileModel);

            return new ResponseEntity<>(new ApiResponse<>(201, "Address Added", null), HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Profile Found");
    }

    public ResponseEntity<ApiResponse<List<DeliveryAddressModel>>> getDeliveryAddress(String token) {
        String userName = jwtProvider.getUserNameFromJwt(token);

        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(userName);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            List<DeliveryAddressModel> deliveryAddressModelList = profileModel.getDeliveryAddresses();

            if (deliveryAddressModelList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(200, "No Address Found",
                        deliveryAddressModelList), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>(200, "Address Found",
                        deliveryAddressModelList), HttpStatus.OK);
            }

        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Profile Found");
    }

    public ResponseEntity<ApiResponse<List<DeliveryAddressModel>>>
    editDeliveryAddress(String token, ShopDeliveryAddressRequest shopDeliveryAddressRequest, Long addressId) {
        String userName = jwtProvider.getUserNameFromJwt(token);

        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(userName);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            List<DeliveryAddressModel> deliveryAddressModels = profileModel.getDeliveryAddresses();

            Optional<DeliveryAddressModel> deliveryAddressModelOptional = deliveryAddressRepository.findById(addressId);

            if (deliveryAddressModelOptional.isPresent()) {
                DeliveryAddressModel deliveryAddressModel = deliveryAddressModelOptional.get();

                if (deliveryAddressModels.contains(deliveryAddressModel)){
                    deliveryAddressModel.setFullName(shopDeliveryAddressRequest.getFullName());
                    deliveryAddressModel.setPhoneNo(shopDeliveryAddressRequest.getPhoneNo());
                    deliveryAddressModel.setEmail(shopDeliveryAddressRequest.getEmail());
                    deliveryAddressModel.setAddress(shopDeliveryAddressRequest.getAddress());
                    deliveryAddressModel.setArea(shopDeliveryAddressRequest.getArea());
                    deliveryAddressModel.setCity(shopDeliveryAddressRequest.getCity());
                    deliveryAddressModel.setRegion(shopDeliveryAddressRequest.getRegion());

                    deliveryAddressRepository.save(deliveryAddressModel);

                    return new ResponseEntity<>(new ApiResponse<>(200, "Address Edit SuccessFul", null), HttpStatus.OK);
                }
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery Address is not matched with profile");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery Address is not Found");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile is not Found");
    }
}
