package com.project.sns.service;

import com.project.sns.exception.ErrorCode;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.fixture.PostEntityFixture;
import com.project.sns.fixture.UserEntityFixture;
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

    @Test
    void 포스트_수정_성공() {
        String  title    = "title";
        String  body     = "body";
        String  userName = "userName";
        Integer postId   = 1;
        Integer userId   = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postRepository.saveAndFlush(any())).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트_수정시_포스트가_존재하지_않는_경우() {
        String  title    = "title";
        String  body     = "body";
        String  userName = "userName";
        Integer postId   = 1;
        Integer userId   = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트_수정_권한이_없는_경우() {
        String  title    = "title";
        String  body     = "body";
        String  userName = "userName";
        Integer postId   = 2;
        Integer userId   = 2;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity writer     = UserEntityFixture.get("userName1", "password", 1);

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }
}
