package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long generateUserId = 0L;

    @Override
    public Optional<User> findById(Long userId) {

        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(++generateUserId);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(Long userId, User user) {
        users.put(userId, user);

        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

}
