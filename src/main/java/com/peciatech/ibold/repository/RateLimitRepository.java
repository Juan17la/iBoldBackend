package com.peciatech.ibold.repository;

import com.peciatech.ibold.domain.model.RateLimitState;
import com.peciatech.ibold.storage.JsonFileStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

@Repository
public class RateLimitRepository {
    private static final String FILE_NAME = "rate-limits.json";

    private final JsonFileStorage storage;

    public RateLimitRepository(JsonFileStorage storage) {
        this.storage = storage;
    }

    public synchronized Optional<RateLimitState> findByUserId(String userId) {
        return loadStates().stream()
                .filter(state -> state.getUserId().equals(userId))
                .findFirst();
    }

    public synchronized void save(RateLimitState rateLimitState) {
        List<RateLimitState> states = loadStates();
        states.removeIf(state -> state.getUserId().equals(rateLimitState.getUserId()));
        states.add(rateLimitState);
        saveAll(states);
    }

    public synchronized List<RateLimitState> findAll() {
        return new ArrayList<>(loadStates());
    }

    public synchronized void clear() {
        saveAll(new ArrayList<>());
    }

    private List<RateLimitState> loadStates() {
        return storage.read(FILE_NAME, new TypeReference<List<RateLimitState>>() {
        }, ArrayList::new);
    }

    private void saveAll(List<RateLimitState> states) {
        storage.write(FILE_NAME, states);
    }
}
