package com.example.demo.service.impl;

import com.example.demo.constant.BaseResponse;
import com.example.demo.constant.ErrorCode;
import com.example.demo.constant.UserStatus;
import com.example.demo.domain.dto.request.UserRequest;
import com.example.demo.domain.dto.response.UserResponse;
import com.example.demo.domain.entity.user.User;
import com.example.demo.domain.entity.user.UserRepository;
import com.example.demo.exception.UserException;
import com.example.demo.service.UserService;
import com.example.demo.util.Helper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Helper helper;

    public UserServiceImpl(UserRepository userRepository, Helper helper) {
        this.userRepository = userRepository;
        this.helper = helper;
    }

    @Override
    public BaseResponse<UserResponse> createUser(UserRequest request) {
        try {
            log.info("Creating new user");

            if (userRepository.existsByEmail(request.getEmail())) {
                return BaseResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
                );
            }

            User user = helper.getModelMapper().map(request, User.class);
            user.setStatus(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE);
            User savedUser = userRepository.save(user);

            return BaseResponse.ok(helper.getModelMapper().map(savedUser, UserResponse.class));
        } catch (Exception e) {
            log.error("Error creating user: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error creating user"
            );
        }
    }

    @Override
    public BaseResponse<UserResponse> getUserById(Long id) {
        try {
            log.info("Getting user by id: {}", id);

            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return BaseResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        ErrorCode.USER_NOT_FOUND.getMessage()
                );
            }

            return BaseResponse.ok(helper.getModelMapper().map(userOptional.get(), UserResponse.class));
        } catch (Exception e) {
            log.error("Error getting user: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error getting user"
            );
        }
    }

    @Override
    public BaseResponse<Page<UserResponse>> getAllUsers(String searchText, Pageable pageable) {
        try {
            Page<User> users;
            if (searchText == null || searchText.trim().isEmpty()) {
                users = userRepository.findAll(pageable);
            } else {
                users = userRepository.search(searchText.trim(), pageable);
            }

            log.info("Found {} users", users.getTotalElements());

            Page<UserResponse> responses = users.map(user ->
                    helper.getModelMapper().map(user, UserResponse.class)
            );

            return BaseResponse.ok(responses);
        } catch (Exception e) {
            log.error("Error getting users: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error getting users"
            );
        }
    }

    @Override
    public BaseResponse<UserResponse> updateUser(Long id, UserRequest request) {
        try {
            log.info("Updating user with id: {}", id);

            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return BaseResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        ErrorCode.USER_NOT_FOUND.getMessage()
                );
            }

            User user = userOptional.get();
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                return BaseResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
                );
            }

            helper.getModelMapper().map(request, user);
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }

            User savedUser = userRepository.save(user);
            return BaseResponse.ok(helper.getModelMapper().map(savedUser, UserResponse.class));
        } catch (Exception e) {
            log.error("Error updating user: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error updating user"
            );
        }
    }

    @Override
    public BaseResponse<Void> deleteUser(Long id) {
        try {
            log.info("Deleting user with id: {}", id);

            if (!userRepository.existsById(id)) {
                return BaseResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        ErrorCode.USER_NOT_FOUND.getMessage()
                );
            }

            userRepository.deleteById(id);
            return BaseResponse.ok(null);
        } catch (Exception e) {
            log.error("Error deleting user: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error deleting user"
            );
        }
    }
}