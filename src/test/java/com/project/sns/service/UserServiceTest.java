package com.project.sns.service;

import com.project.sns.exception.ErrorCode;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.fixture.UserEntityFixture;
import com.project.sns.model.entity.UserEntity;
import com.project.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService          userService;

    @MockBean
    private UserEntityRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";
        Integer userId = 1;

        UserEntity fixture = UserEntityFixture.get(userName, password, userId);

        // mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty()); // userName으로 찾았을 때 없어야 함.
        when(encoder.encode(password)).thenReturn("encrypted_password");
        when(userRepository.save(any())).thenReturn(fixture); // UserEntity 클래스 mock의 fixture

        Assertions.assertDoesNotThrow(() -> userService.join(fixture.getUserName(), fixture.getPassword()));
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는_경우() {
        String userName = "userName";
        String password = "password";
        Integer userId = 1;

        UserEntity fixture = UserEntityFixture.get(userName, password, userId);

        // mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(encoder.encode(password)).thenReturn("encrypted_password");
        when(userRepository.save(any())).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";
        Integer userId = 1;

        UserEntity fixture = UserEntityFixture.get(userName, password, userId);

        // mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 로그인시_password가_틀린_경우() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";
        Integer userId = 1;

        UserEntity fixture = UserEntityFixture.get(userName, password, userId);

        // mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}
