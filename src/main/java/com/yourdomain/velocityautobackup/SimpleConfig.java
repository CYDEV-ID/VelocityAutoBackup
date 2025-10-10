package com.yourdomain.velocityautobackup;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SimpleConfig {

    private final Map<String, Object> data;
    private final Logger logger;

    public SimpleConfig(Path configFile, Logger logger) {
        this.logger = logger;
        Yaml yaml = new Yaml();
        Map<String, Object> loadedData = null;
        try (InputStream in = Files.newInputStream(configFile)) {
            loadedData = yaml.load(in);
        } catch (IOException e) {
            logger.error("Could not read config file: {}", configFile, e);
        }
        this.data = loadedData;
    }

    public String getString(String key, String defaultValue) {
        if (data != null && data.containsKey(key) && data.get(key) instanceof String) {
            return (String) data.get(key);
        }
        return defaultValue;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public int getInt(String key, int defaultValue) {
        if (data != null && data.containsKey(key) && data.get(key) instanceof Integer) {
            return (Integer) data.get(key);
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key) {
        if (data != null && data.containsKey(key) && data.get(key) instanceof List) {
            try {
                return (List<String>) data.get(key);
            } catch (ClassCastException e) {
                logger.error("Config key '{}' is not a list of strings.", key);
            }
        }
        return Collections.emptyList();
    }
}