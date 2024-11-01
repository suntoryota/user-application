package com.example.demo.controller;

import com.example.demo.constant.AppConstant;
import com.example.demo.constant.BaseResponse;
import com.example.demo.domain.dto.request.UserRequest;
import com.example.demo.domain.dto.response.UserResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<UserResponse>> create(
            @Valid @RequestBody UserRequest request) {
        log.info("Create user with request: {}", request);
        BaseResponse<UserResponse> response = userService.createUser(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getById(@PathVariable Long id) {
        log.info("Get user by id: {}", id);
        BaseResponse<UserResponse> response = userService.getUserById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<UserResponse>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get all users with search: {}, page: {} and size: {}", search, page, size);
        Pageable pageable = PageRequest.of(page, size);
        BaseResponse<Page<UserResponse>> response = userService.getAllUsers(search, pageable);

        if (!response.getStatus().equals(AppConstant.SUCCESS)) {
            return ResponseEntity.status(response.getCode())
                    .body(BaseResponse.error(response.getCode(), response.getMessage()));
        }

        return ResponseEntity.ok()
                .headers(setPaginationHeaders(response.getData()))
                .body(BaseResponse.ok(response.getData().getContent()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        log.info("Update user with id: {} and request: {}", id, request);
        BaseResponse<UserResponse> response = userService.updateUser(id, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        log.info("Delete user with id: {}", id);
        BaseResponse<Void> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    private HttpHeaders setPaginationHeaders(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.getTotalPages()));
        return headers;
    }
}