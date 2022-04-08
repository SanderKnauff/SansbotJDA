package ooo.sansk.sansbot.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class AbstractJsonRepository<I, T extends Identifyable<I>> implements Repository<I, T> {

    private static Logger logger = LoggerFactory.getLogger(AbstractJsonRepository.class);
    private final Path storagePath;
    private Map<I, T> objectCache;
    private ObjectMapper objectMapper;

    public AbstractJsonRepository(Path storagePath) {
        this.storagePath = storagePath;
    }

    public void loadAll() {
        if (!storageFileExists())
            createEmptyStorageFile();
        objectCache = readObjectsFromStorage();
    }

    public Collection<T> getAll() {
        if (!isCacheLoaded())
            loadAll();
        return objectCache.values();
    }

    public Optional<T> findOne(I id) {
        return Optional.ofNullable(objectCache.get(id));
    }

    public void addOne(T object) {
        objectCache.put(object.getId(), object);
        save();
    }

    public void save() {
        try {
            createObjectMapper().writeValue(Files.newOutputStream(storagePath), objectCache);
        } catch (IOException e) {
            logger.error("Exception while while saving Json to {} ({}: {})", storagePath.toAbsolutePath(), e.getClass().getSimpleName(), e.getMessage());
        }
    }


    private Map<I, T> readObjectsFromStorage() {
        try (InputStream inputStream = Files.newInputStream(storagePath)){
            Class idClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Class objectClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            return createObjectMapper().readValue(inputStream, objectMapper.getTypeFactory().constructMapType(HashMap.class, idClass, objectClass));
        } catch (IOException e) {
            logger.error("Exception while while reading Json from {}. List will not be initialized to prevent overwriting storage to an empty file ({}: {})", storagePath.toAbsolutePath(), e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private boolean storageFileExists() {
        return storagePath.toFile().exists();
    }

    private boolean isCacheLoaded() {
        return objectCache != null;
    }


    protected ObjectMapper createObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
            objectMapper.enable(JsonParser.Feature.IGNORE_UNDEFINED);
            objectMapper.enable(JsonParser.Feature.ALLOW_MISSING_VALUES);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    public void createEmptyStorageFile() {
        try {
            if (storagePath.toAbsolutePath().getParent() != null && !storagePath.toAbsolutePath().getParent().toFile().exists()) {
                Files.createDirectories(storagePath.toAbsolutePath().getParent());
            }

            if (!storagePath.toFile().exists()) {
                if (storagePath.toFile().isDirectory()) {
                    Files.createDirectory(storagePath);
                } else {
                    Files.createFile(storagePath);
                    Files.write(storagePath, "{}".getBytes());
                }
            }
        } catch (IOException ioe) {
            logger.error("An exception occurred while creating config files ({}: {})", ioe.getClass().getSimpleName(), ioe.getLocalizedMessage());
            ioe.printStackTrace();
        }
    }
}
