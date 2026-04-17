package com.peciatech.ibold.repository;

import com.peciatech.ibold.domain.model.QuotaState;
import com.peciatech.ibold.storage.JsonFileStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

@Repository
public class QuotaRepository {
    private static final String FILE_NAME = "quota-states.json";

    private final JsonFileStorage storage;

    public QuotaRepository(JsonFileStorage storage) {
        this.storage = storage;
    }

    public synchronized Optional<QuotaState> findByUserId(String userId) {
        return loadStates().stream()
                .filter(state -> state.getUserId().equals(userId))
                .findFirst();
    }

    public synchronized List<QuotaState> findAll() {
        return new ArrayList<>(loadStates());
    }

    public synchronized void save(QuotaState quotaState) {
        List<QuotaState> states = loadStates();
        states.removeIf(state -> state.getUserId().equals(quotaState.getUserId()));
        states.add(quotaState);
        saveAll(states);
    }

    public synchronized void saveAll(List<QuotaState> states) {
        storage.write(FILE_NAME, states);
    }

    private List<QuotaState> loadStates() {
        return storage.read(FILE_NAME, new TypeReference<List<QuotaState>>() {
        }, ArrayList::new);
    }
}
