package com.zaatun.zaatunecommerce.jwt.services;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.jwt.dto.request.EditProfile;
import com.zaatun.zaatunecommerce.jwt.dto.request.LoginForm;
import com.zaatun.zaatunecommerce.jwt.dto.request.PassChangeRequest;
import com.zaatun.zaatunecommerce.jwt.dto.request.SignUpForm;
import com.zaatun.zaatunecommerce.jwt.dto.response.JwtResponse;
import com.zaatun.zaatunecommerce.jwt.dto.response.ProfileResponse;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.jwt.model.Role;
import com.zaatun.zaatunecommerce.jwt.model.RoleName;
import com.zaatun.zaatunecommerce.jwt.model.UserModel;
import com.zaatun.zaatunecommerce.model.ShortStatisticsModel;
import com.zaatun.zaatunecommerce.repository.ProfileRepository;
import com.zaatun.zaatunecommerce.jwt.repository.RoleRepository;
import com.zaatun.zaatunecommerce.jwt.repository.UserRepository;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.repository.ShortStatisticsRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SignUpAndSignInService {

    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final ShortStatisticsRepository shortStatisticsRepository;

    public ResponseEntity<ApiResponse<String>> signUp(SignUpForm signUpRequest) {
        String emailOrPhone = signUpRequest.getEmailOrPhone();
        String email = null;
        String phone = null;
        if (EmailValidator.getInstance().isValid(emailOrPhone)) {
            email = emailOrPhone;
            if (profileRepository.existsByEmail(email)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Already Exists");
            }
        } else {
            phone = emailOrPhone;
            if (profileRepository.existsByPhoneNo(phone)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone No Already Exists");
            }
        }

        Set<String> rolesString = new HashSet<>();
        rolesString.add("USER");

        String userName = "ZTN-U-" + signUpRequest.getEmailOrPhone();

        ProfileModel profileModel = ProfileModel.builder()
                .id(UUID.randomUUID().toString())
                .name(signUpRequest.getName())
                .createdOn(System.currentTimeMillis())
                .phoneNo(phone)
                .email(email)
                .username(userName)
                .totalOrderAmounts(0)
                .totalOrders(0)
                .build();

        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID().toString())
                .username(userName)
                .roles(getRolesFromStringRoles(rolesString))
                .profileModel(profileModel)
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();

        profileRepository.save(profileModel);
        userRepository.saveAndFlush(userModel);

        ShortStatisticsModel shortStatisticsModel = shortStatisticsRepository.findById(0).get();
        shortStatisticsModel.setTotalUsers(shortStatisticsModel.getTotalUsers() + 1);
        shortStatisticsRepository.save(shortStatisticsModel);

        return new ResponseEntity<>(new ApiResponse<String>(201, "Account Created", null), HttpStatus.CREATED);
    }


    public ResponseEntity<ApiResponse<JwtResponse>> signIn(LoginForm loginRequest) {
        String userName = "ZTN-U-" + loginRequest.getEmailOrPhone();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userName,
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);

        return new ResponseEntity<>(new ApiResponse<>(200, "Login Successful",
                new JwtResponse(jwt, "Bearer")), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<ProfileResponse>> getLoggedUserProfile(String jwtToken) {

        String userName = jwtProvider.getUserNameFromJwt(jwtToken);
        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(userName);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();


            ProfileResponse profileResponse = new ProfileResponse(profileModel.getUsername(), profileModel.getEmail(),
                    profileModel.getName(), profileModel.getPhoneNo(), profileModel.getAddress(),
                    profileModel.getDateOfBirth(), profileModel.getThana(), profileModel.getDistrict());

            return new ResponseEntity<>(new ApiResponse<>(200, "User Found", profileResponse), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User Not Found");
        }

    }

    public ResponseEntity<ApiResponse<ProfileResponse>> editLoggedInProfile(String jwtToken, EditProfile editProfile) {
        String userName = jwtProvider.getUserNameFromJwt(jwtToken);
        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(userName);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            if (profileModel.getEmail() == null) {
                profileModel.setEmail(editProfile.getEmail());
            }
            else if (profileModel.getEmail().equals(editProfile.getEmail())) {

            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't Change Default Email");
            }


            if (profileModel.getPhoneNo() == null) {
                profileModel.setPhoneNo(editProfile.getPhoneNo());
            }
            else if (profileModel.getPhoneNo().equals(editProfile.getPhoneNo())) {

            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't Change Default Phone No");
            }

            profileModel.setName(editProfile.getName());
            profileModel.setAddress(editProfile.getAddress());
            profileModel.setThana(editProfile.getThana());
            profileModel.setDistrict(editProfile.getDistrict());
            profileModel.setDateOfBirth(editProfile.getDateOfBirth());

            profileRepository.save(profileModel);

            ProfileResponse profileResponse = new ProfileResponse(profileModel.getUsername(), profileModel.getEmail(),
                    profileModel.getName(), profileModel.getPhoneNo(), profileModel.getAddress(),
                    profileModel.getDateOfBirth(), profileModel.getThana(), profileModel.getDistrict());

            return new ResponseEntity<>(new ApiResponse<>(200, "Profile Edit Successful", profileResponse), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User Not Found");
        }

    }

    public ResponseEntity<ApiResponse<String>> changePassword(String jwtToken, PassChangeRequest passChangeRequest) {
        String userName = jwtProvider.getUserNameFromJwt(jwtToken);
        Optional<UserModel> userModelOptional = userRepository.findByUsername(userName);

        if(userModelOptional.isPresent()){
            UserModel userModel = userModelOptional.get();

            if(encoder.matches(passChangeRequest.getOldPass(), userModel.getPassword())){
                userModel.setPassword(encoder.encode(passChangeRequest.getNewPass()));

                userRepository.save(userModel);

                return new ResponseEntity<>(new ApiResponse<>(200,
                        "Password Change Successful", null), HttpStatus.OK);
            }
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password Doesn't match");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No User Found");
        }
    }

    public Set<Role> getRolesFromStringRoles(Set<String> roles2) {
        Set<Role> roles = new HashSet<>();
        for (String role : roles2) {
            Optional<Role> roleOptional = roleRepository.findByName(RoleName.valueOf(role));
            if (roleOptional.isEmpty()) {
                throw new ValidationException("Role '" + role + "' does not exist.");
            }
            roles.add(roleOptional.get());
        }
        return roles;
    }

    private Set<String> getRolesStringFromRole(Set<Role> roles2) {
        Set<String> roles = new HashSet<>();
        for (Role role : roles2) {
            roles.add(role.getName().toString());
        }
        return roles;
    }



//
//    public ResponseEntity changePass(PassChangeRequest passChangeRequest) {
//
//        Optional<User> userOptional = userRepository.findByUsername(getLoggedAuthUser().getBody().getUsername());
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (encoder.matches(passChangeRequest.getOldPass(), user.getPassword())) {
//                user.setPassword(encoder.encode(passChangeRequest.getNewPass()));
//
//                userRepository.save(user);
//
//                MessageResponse messageResponse = new MessageResponse("Pass Changed Successful", 200);
//                return new ResponseEntity(messageResponse, HttpStatus.OK);
//            } else {
//                MessageResponse messageResponse = new MessageResponse("Old Pass Not Matched", 400);
//                return new ResponseEntity(messageResponse, HttpStatus.BAD_REQUEST);
//            }
//        } else {
//            MessageResponse messageResponse = new MessageResponse("No User Found", 204);
//            return new ResponseEntity(messageResponse, HttpStatus.NO_CONTENT);
//        }
//    }

//    public String deleteUser(String email) {
//
//        if (userRepository.findByEmail(email).isPresent()) {
//
//            userRepository.deleteById(userRepository.findByEmail(email).get().getId());
//            return "Deleted";
//        } else {
//            return "Not Found";
//        }
//
//    }
//
//    public String editProfile(EditProfile editProfile) {
//        String username = getLoggedAuthUserName();
//
//        if (!username.isEmpty()) {
//            //System.out.println(username);
//            Optional<User> userOptional = userRepository.findByUsername(username);
//
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                if (!editProfile.getName().isEmpty()) {
//                    user.setName(editProfile.getName());
//                }
//                if (!editProfile.getPhoneNo().isEmpty()) {
//                    user.setPhoneNo(editProfile.getPhoneNo());
//                }
//                if (!editProfile.getNewPassword().isEmpty() && !editProfile.getCurrentPassword().isEmpty()) {
//                    if (encoder.matches(editProfile.getCurrentPassword(), userOptional.get().getPassword())) {
//
//                        user.setPassword(encoder.encode(editProfile.getNewPassword()));
//                    } else {
//                        return "Wrong Current Password";
//                    }
//                }
//
//                userRepository.save(user);
//                return "Saved Successfully";
//            } else {
//                return "User Not Found";
//            }
//
//        } else {
//            return "Unsuccessful";
//        }
//
//
//    }
//
//    public String addAreaList(AreaNameRequestsResponse areaNameRequestsResponse) {
//        for (String names : areaNameRequestsResponse.getAreaNames()) {
//            AreaNames areaNames = new AreaNames(names);
//            areaNameRepository.save(areaNames);
//        }
//        return "Saved";
//    }
//
//    public AreaNameRequestsResponse getAreaList() {
//        List<AreaNames> areaNamesOptional = areaNameRepository.findAll();
//
//        AreaNameRequestsResponse areaNameRequestsResponse = new AreaNameRequestsResponse();
//        List<String> areaNamesList = new ArrayList<>();
//        for (AreaNames areaNames : areaNamesOptional) {
//            areaNamesList.add(areaNames.getAreaName());
//        }
//        areaNameRequestsResponse.setAreaNames(areaNamesList);
//        return areaNameRequestsResponse;
//    }

//    public LoggedUserDetailsResponse getLoggedUserDetails(Authentication authentication) {
//
//        System.out.println(authentication.toString());
//        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
//        List<String> userRoleList = new ArrayList<>();
//        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
//            userRoleList.add(grantedAuthority.getAuthority());
//        }
//        LoggedUserDetailsResponse loggedUserDetailsResponse = new LoggedUserDetailsResponse();
//        loggedUserDetailsResponse.setUserName(authentication.getName());
//        loggedUserDetailsResponse.setUserRole(userRoleList);
//        loggedUserDetailsResponse.setIsAuthenticated(authentication.isAuthenticated());
//        return loggedUserDetailsResponse;
//    }


}
