package dev.mariany.genesis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.mariany.genesis.Genesis;

import java.io.*;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/genesis.json5");
    private static GenesisConfig config = new GenesisConfig();

    public static GenesisConfig getConfig() {
        return config;
    }

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, GenesisConfig.class);
            } catch (IOException error) {
                Genesis.LOGGER.error("Failed to load config: {}", error.getMessage());
            }
        } else {
            saveConfig();
        }
    }

    private static void saveConfig() {
        try {
            if (CONFIG_FILE.getParentFile().mkdirs()) {
                Genesis.LOGGER.info("Creating parent directory for {} config", Genesis.MOD_ID);
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException error) {
            Genesis.LOGGER.error("Failed to save config: {}", error.getMessage());
        }
    }
}
