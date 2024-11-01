package com.example.demo.service;

import com.example.demo.constant.BaseResponse;
import com.example.demo.domain.dto.request.UserRequest;
import com.example.demo.domain.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    BaseResponse<UserResponse> createUser(UserRequest request);
    BaseResponse<UserResponse> getUserById(Long id);
    BaseResponse<Page<UserResponse>> getAllUsers(String searchText, Pageable pageable);
    BaseResponse<UserResponse> updateUser(Long id, UserRequest request);
    BaseResponse<Void> deleteUser(Long id);
}