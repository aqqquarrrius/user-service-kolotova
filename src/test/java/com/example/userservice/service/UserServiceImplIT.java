package com.example.userservice.service;

import com.example.userservice.dto.UserCreateRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserUpdateRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceImplIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_persistsAndReturnsDto() {
        // given
        UserCreateRequest req = new UserCreateRequest("Pedro", "pedro@example.com", 45);

        // when
        UserDto dto = userService.createUser(req);

        // then
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo("Pedro");
        assertThat(dto.getEmail()).isEqualTo("pedro@example.com");
        assertThat(dto.getAge()).isEqualTo(45);
        assertThat(dto.getCreatedAt()).isNotNull();

        // а в базе действительно есть запись
        assertThat(userRepository.count()).isEqualTo(1);
        Optional<User> saved = userRepository.findById(dto.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getEmail()).isEqualTo("pedro@example.com");
    }

    @Test
    void getUserById_returnsExisting() {
        // given
        User u = userRepository.save(new User("Vangogh", "vangogh@example.com", 92));

        // when
        Optional<UserDto> found = userService.getUserById(u.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Vangogh");
    }

    @Test
    void getAllUsers_returnsList() {
        // given
        userRepository.saveAll(List.of(
                new User("Leo", "leo@example.com", 20),
                new User("Pablo", "pablo@example.com", 21)
        ));

        // when
        List<UserDto> all = userService.getAllUsers();

        // then
        assertThat(all).hasSize(2);
        assertThat(all).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("leo@example.com", "pablo@example.com");
    }

    @Test
    void updateUser_updatesFields() {
        // given
        User u = userRepository.save(new User("Gustav", "gustav@example.com", 22));
        UserUpdateRequest req = new UserUpdateRequest("Gustav_upd", "gustav_upd@example.com", 23);

        // when
        Optional<UserDto> updated = userService.updateUser(u.getId(), req);

        // then
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Gustav_upd");
        assertThat(updated.get().getEmail()).isEqualTo("gustav_upd@example.com");
        assertThat(updated.get().getAge()).isEqualTo(23);

        // и в базе обновилось
        User fromDb = userRepository.findById(u.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("Gustav_upd");
        assertThat(fromDb.getEmail()).isEqualTo("gustav_upd@example.com");
        assertThat(fromDb.getAge()).isEqualTo(23);
    }

    @Test
    void updateUser_returnsEmpty_whenNotFound() {
        // when
        Optional<UserDto> updated = userService.updateUser(999L, new UserUpdateRequest("Salvador", "dali@example.com", 18));

        // then
        assertThat(updated).isEmpty();
    }


    @Test
    void deleteUser_removesEntity() {
        // given
        User u = userRepository.save(new User("Sandro", "sandro@example.com", 35));
        Long id = u.getId();

        // when
        userService.deleteUser(id);

        // then
        assertThat(userRepository.findById(id)).isEmpty();
        assertThat(userRepository.count()).isZero();
    }

    @Test
    void deleteUser_throws_whenNotFound() {
        // then
        assertThatThrownBy(() -> userService.deleteUser(123456L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
