package com.example.blogStudy.dto.update;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PasswordUpdate {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+=-]+$",
            message = "비밀번호에 공백은 포함될 수 없습니다."
    )
    private String currentPassword;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+=-]+$",
            message = "비밀번호에 공백은 포함될 수 없습니다."
    )
    private String newPassword;
}
