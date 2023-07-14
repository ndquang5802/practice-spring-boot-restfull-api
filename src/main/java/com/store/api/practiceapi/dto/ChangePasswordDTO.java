package com.store.api.practiceapi.dto;

import lombok.*;

@Getter
@Setter
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
