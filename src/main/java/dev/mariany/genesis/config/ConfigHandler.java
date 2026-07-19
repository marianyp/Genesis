package dev.mariany.genesis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler<T> {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final File file;
    protected final T defaultConfig;

    @Nullable
    protected T config;

    public ConfigHandler(String id, @NotNull T defaultConfig) {
        this.defaultConfig = defaultConfig;
        this.config = defaultConfig;
        this.file = new File("config/" + id + ".json5");
    }

    public T getConfig() {
        if (this.config == null) {
            return this.defaultConfig;
        }

        return this.config;
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        if (this.file.exists()) {
            try (FileReader reader = new FileReader(this.file)) {
                this.config = (T) GSON.fromJson(reader, this.defaultConfig.getClass());
            } catch (IOException error) {
                this.config = this.defaultConfig;
                LOGGER.error("Failed to load config: {}", error.getMessage());
            }
        }

        this.saveConfig();
    }

    public void saveConfig() {
        try {
            if (this.file.getParentFile().mkdirs()) {
                LOGGER.info("Creating parent directory for config");
            }

            try (FileWriter writer = new FileWriter(this.file)) {
                GSON.toJson(this.config, writer);
            }
        } catch (IOException error) {
            LOGGER.error("Failed to save config: {}", error.getMessage());
        }
    }
}
