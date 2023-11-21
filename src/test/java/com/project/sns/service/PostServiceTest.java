package com.project.sns.service;

import com.project.sns.exception.ErrorCode;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.model.entity.PostEntity;
import com.project.sns.model.entity.UserEntity;
import com.project.sns.repository.PostEntityRepository;
import com.project.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postRepository;

    @MockBean
    private UserEntityRepository userRepository;

    @Test
    void 포스트_작성_성공() {
        String title    = "title";
        String body     = "body";
        String userName = "userName";

        //mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
    }

    @Test
    void 포스트_작성시_요청한_유저가_존재하지_않는_경우() {
        String title    = "title";
        String body     = "body";
        String userName = "userName";

        //mocking
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
}
