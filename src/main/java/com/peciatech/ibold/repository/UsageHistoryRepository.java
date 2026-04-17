package com.peciatech.ibold.repository;

import com.peciatech.ibold.domain.model.DailyUsageRecord;
import com.peciatech.ibold.storage.JsonFileStorage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

@Repository
public class UsageHistoryRepository {
    private static final String FILE_NAME = "usage-history.json";

    private final JsonFileStorage storage;

    public UsageHistoryRepository(JsonFileStorage storage) {
        this.storage = storage;
    }

    public synchronized List<DailyUsageRecord> findByUserId(String userId) {
        Map<String, List<DailyUsageRecord>> usageByUser = loadAll();
        List<DailyUsageRecord> usage = usageByUser.getOrDefault(userId, new ArrayList<>());
        usage.sort(Comparator.comparing(DailyUsageRecord::getDate));
        return new ArrayList<>(usage);
    }

    public synchronized void addTokens(String userId, LocalDate date, int tokens) {
        Map<String, List<DailyUsageRecord>> usageByUser = loadAll();
        List<DailyUsageRecord> usage = usageByUser.computeIfAbsent(userId, ignored -> new ArrayList<>());

        Optional<DailyUsageRecord> existing = usage.stream()
                .filter(record -> date.equals(record.getDate()))
                .findFirst();

        if (existing.isPresent()) {
            DailyUsageRecord record = existing.get();
            record.setUsedTokens(record.getUsedTokens() + tokens);
        } else {
            usage.add(new DailyUsageRecord(date, tokens));
        }

        LocalDate oldestAllowedDate = LocalDate.now().minusDays(60);
        usage.removeIf(record -> record.getDate().isBefore(oldestAllowedDate));
        usage.sort(Comparator.comparing(DailyUsageRecord::getDate));

        saveAll(usageByUser);
    }

    private Map<String, List<DailyUsageRecord>> loadAll() {
        return storage.read(FILE_NAME, new TypeReference<Map<String, List<DailyUsageRecord>>>() {
        }, HashMap::new);
    }

    private void saveAll(Map<String, List<DailyUsageRecord>> usageByUser) {
        storage.write(FILE_NAME, usageByUser);
    }
}
