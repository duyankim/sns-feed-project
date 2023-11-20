package com.project.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

    private String  resultCode;
    private T       result;

    /**
     * 응답 에러
     * @param errorCode
     * @return void
     */
    public static Response<Void> error(String errorCode) {
        // 응답 에러일 경우 에러코드 전달 받음
        return new Response<>(errorCode, null);
    }

    /**
     * 응답 성공
     * @param result
     * @param <T>
     * @return </T>
     */
    public static <T> Response<T> success(T result) {
        // 응답 성공일 경우 결과가 각각 다르기 때문에 제네릭 사용
        return new Response<>("SUCCESS", result);
    }
}
