package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.ProfileSort;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<List<ProfileModel>>>>
    getUserList(@RequestParam(defaultValue = "0") Integer pageNo,
                @RequestParam(defaultValue = "50") Integer pageSize,
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String username,
                @RequestParam(required = false) String phone,
                @RequestParam(required = false) String email,
                @RequestParam(defaultValue = "createdOn") ProfileSort sortBy,
                @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return userService.getUserList(pageNo, pageSize, name, username, phone, email, sortBy, direction);
    }
}
