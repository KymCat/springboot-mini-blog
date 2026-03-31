package com.example.blogStudy.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentUpdate {

    @NotBlank(message = "댓글을 입력해주세요.")
    @Size(max = 6000, message = "댓글은 6000자를 초과해서 입력하실 수 없습니다.")
    private String content;
}
