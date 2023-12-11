package com.project.sns.controller.response;

import com.project.sns.model.User;
import com.project.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    private Integer     id;
    private String      userName;
    private UserRole    role;

    /**
     * User(서비스에서 사용하는 DTO) -> UserJoinResponse 변환
     * @param user
     * @return UserJoinResponse
     */
    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
