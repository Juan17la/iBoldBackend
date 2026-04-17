package com.peciatech.ibold.repository;

import com.peciatech.ibold.domain.model.UserAccount;
import com.peciatech.ibold.storage.JsonFileStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

@Repository
public class UserRepository {
    private static final String FILE_NAME = "users.json";

    private final JsonFileStorage storage;

    public UserRepository(JsonFileStorage storage) {
        this.storage = storage;
    }

    public synchronized Optional<UserAccount> findByUserId(String userId) {
        return loadUsers().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    public synchronized Optional<UserAccount> findByEmail(String email) {
        return loadUsers().stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public synchronized List<UserAccount> findAll() {
        return new ArrayList<>(loadUsers());
    }

    public synchronized void save(UserAccount userAccount) {
        List<UserAccount> users = loadUsers();
        users.removeIf(user -> user.getUserId().equals(userAccount.getUserId()));
        users.add(userAccount);
        saveUsers(users);
    }

    private List<UserAccount> loadUsers() {
        return storage.read(FILE_NAME, new TypeReference<List<UserAccount>>() {
        }, ArrayList::new);
    }

    private void saveUsers(List<UserAccount> users) {
        storage.write(FILE_NAME, users);
    }
}
