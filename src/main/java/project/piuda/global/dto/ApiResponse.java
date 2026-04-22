package project.piuda.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("OK", data, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>("FAIL", null, message);
    }
}