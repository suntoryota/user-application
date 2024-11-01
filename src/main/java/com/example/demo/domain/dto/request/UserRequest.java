package com.example.demo.domain.dto.request;

import com.example.demo.constant.AppConstant;
import com.example.demo.constant.UserStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = AppConstant.ValidationMessage.NAME_REQUIRED)
    @Pattern(regexp = "^[a-zA-Z\\s]{2,50}$", message = "First name must be 2-50 characters and contain only letters")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = AppConstant.ValidationMessage.NAME_REQUIRED)
    @Pattern(regexp = "^[a-zA-Z\\s]{2,50}$", message = "Last name must be 2-50 characters and contain only letters")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = AppConstant.ValidationMessage.EMAIL_REQUIRED)
    @Email(message = AppConstant.ValidationMessage.INVALID_EMAIL)
    @Size(max = 50, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = AppConstant.ValidationMessage.PHONE_INVALID)
    private String phoneNumber;

    @NotNull(message = "Status is required")
    private UserStatus status;
}
