package com.project.sns.model;

import com.project.sns.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer     id;
    private String      userName;
    private String      password;
    private UserRole    role;
    private Timestamp   registeredAt;
    private Timestamp   updatedAt;
    private Timestamp   deletedAt;

    /**
     * UserEntity (DB에 접근할 때 사용) -> User(서비스에서 사용하는 DTO) 변환
     * @param entity
     * @return User
     */
    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
