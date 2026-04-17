package com.peciatech.ibold.storage;

import com.peciatech.ibold.exception.StorageException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonFileStorage {
    private final ObjectMapper objectMapper;
    private final Path dataDir;

    public JsonFileStorage(ObjectMapper objectMapper, @Value("${storage.data-dir:data}") String dataDir) {
        this.objectMapper = objectMapper;
        this.dataDir = Path.of(dataDir);
        try {
            Files.createDirectories(this.dataDir);
        } catch (IOException ex) {
            throw new StorageException("Unable to initialize JSON storage directory");
        }
    }

    public synchronized <T> T read(String fileName, TypeReference<T> typeReference, Supplier<T> defaultSupplier) {
        Path filePath = dataDir.resolve(fileName);
        if (Files.notExists(filePath)) {
            T defaultValue = defaultSupplier.get();
            write(fileName, defaultValue);
            return defaultValue;
        }

        try {
            if (Files.size(filePath) == 0) {
                return defaultSupplier.get();
            }
            return objectMapper.readValue(filePath.toFile(), typeReference);
        } catch (Exception ex) {
            throw new StorageException("Unable to read JSON file: " + fileName);
        }
    }

    public synchronized <T> void write(String fileName, T content) {
        Path filePath = dataDir.resolve(fileName);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), content);
        } catch (Exception ex) {
            throw new StorageException("Unable to write JSON file: " + fileName);
        }
    }
}
