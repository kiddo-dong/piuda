package project.piuda.domain.community.application.dto;

import lombok.Getter;

@Getter
public class PostRequest {
    private String title;
    private String content;
    private String imageUrl;
}
