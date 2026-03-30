package com.example.blogStudy.dto.create;

import com.example.blogStudy.entity.Post;
import com.example.blogStudy.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreate {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자를 초과해서 입력하실 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 6000, message = "본문은 6000자를 초과해서 입력하실 수 없습니다.")
    private String content;


    public Post toEntity(User user) {
        return Post.create(this.title, this.content, user);
    }
}
