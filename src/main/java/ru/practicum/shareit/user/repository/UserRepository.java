package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long userId);

    List<User> findAll();

    User create(User toUser);

    User update(Long userId, User toUser);

    void delete(Long userId);

}
