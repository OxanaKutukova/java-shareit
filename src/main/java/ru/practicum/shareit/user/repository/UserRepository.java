package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import java.util.Collection;

public interface UserRepository {

    User getUserById(Long userId);

    Collection<User> getAllUsers();

    User createUser(User toUser);

    User updateUser(Long userId,User toUser);

    void deleteUser(Long userId);

}
