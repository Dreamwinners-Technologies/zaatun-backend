package com.zaatun.zaatunecommerce.jwt.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.jwt.dto.request.EditProfile;
import com.zaatun.zaatunecommerce.jwt.dto.request.LoginForm;
import com.zaatun.zaatunecommerce.jwt.dto.request.PassChangeRequest;
import com.zaatun.zaatunecommerce.jwt.dto.request.SignUpForm;
import com.zaatun.zaatunecommerce.jwt.dto.response.JwtResponse;
import com.zaatun.zaatunecommerce.jwt.dto.response.ProfileResponse;
import com.zaatun.zaatunecommerce.jwt.services.SignUpAndSignInService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SignUpAndSignInService signUpAndSignInService;
//    private final ForgetPasswordService forgetPasswordService;


    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<String>> signUpUser(@Valid @RequestBody SignUpForm signUpRequest) {
        return signUpAndSignInService.signUp(signUpRequest);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<JwtResponse>> signInUser(@Valid @RequestBody LoginForm loginRequest) {
        return signUpAndSignInService.signIn(loginRequest);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getLoggedInProfile(@RequestHeader(name = "Authorization", required = true)
                                                                                   String jwtToken) {
        return signUpAndSignInService.getLoggedUserProfile(jwtToken);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> editLoggedInProfile(@RequestHeader(name = "Authorization",
            required = true) String jwtToken,@Valid @RequestBody EditProfile editProfile) {

        return signUpAndSignInService.editLoggedInProfile(jwtToken, editProfile);
    }

    @PutMapping("/changePass")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestHeader(name = "Authorization",
            required = true) String jwtToken, @Valid @RequestBody PassChangeRequest passChangeRequest) {

        return signUpAndSignInService.changePassword(jwtToken, passChangeRequest);
    }

//    @PostMapping("/forgetPass/otp")
//    public String generateOTP(@RequestBody GenerateOTPRequest generateOTPRequest) throws IOException, MessagingException {
//        return forgetPasswordService.generateOTP(generateOTPRequest);
//    }

    //
//    @PostMapping("/verifyOTP")
//    public String verifyOTP(@RequestBody GenerateOTPRequest1 generateOTPRequest) {
//        return forgetPasswordService.verifyOTP(generateOTPRequest);
//    }
//
//    @PostMapping("/forgetPass")
//    public String forgetPassChange(@RequestBody GenerateOTPRequest2 generateOTPRequest) throws IOException, MessagingException {
//        return forgetPasswordService.forgetPassChange(generateOTPRequest);
//    }
//
//
    @GetMapping("/serverCheck")
    public String getServerStatStatus() {
        return "The Server is Running";
    }

}
