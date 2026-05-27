package project.piuda.domain.community.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.PostCategory;

@Getter
public class PostRequest {
    private String title;
    private String content;
    private PostCategory category;
    private String imageUrl;
}
