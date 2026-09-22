package dev.doza.politic.bukkit.manager;

import dev.doza.politic.Plugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final Map<String, FileConfiguration> configs = new HashMap<>();
    private static final Map<String, File> files = new HashMap<>();

    public static void init(String... fileNames) {
        Plugin plugin = Plugin.getInstance();

        for (String fileName : fileNames) {
            String fullPath = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
            File file = new File(plugin.getDataFolder(), fullPath);

            File parentFolder = file.getParentFile();
            if (parentFolder != null && !parentFolder.exists()) {
                parentFolder.mkdirs();
            }

            if (!file.exists()) {
                try {
                    plugin.saveResource(fullPath, false);
                } catch (Exception e) {
                    plugin.getLogger().warning("Не удалось распаковать ресурс: " + fullPath);
                }
            }

            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                configs.put(fileName, config);
                files.put(fileName, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static FileConfiguration getConfig(String fileName) {
        return configs.get(fileName);
    }

    public static File getFile(String fileName) {
        return files.get(fileName);
    }

    public static void save(String fileName) {
        FileConfiguration config = configs.get(fileName);
        File file = files.get(fileName);

        if (config == null || file == null) {
            Plugin.getInstance().getLogger().warning("Конфиг не найден для сохранения: " + fileName);
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Plugin.getInstance().getLogger().warning("Не удалось сохранить конфиг: " + fileName);
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        for (String fileName : configs.keySet()) {
            save(fileName);
        }
    }

    public static void reload(String fileName) {
        File file = files.get(fileName);

        if (file == null) {
            Plugin.getInstance().getLogger().warning("Конфиг не найден для перезагрузки: " + fileName);
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(fileName, config);
    }

    public static void reloadAll() {
        for (String fileName : new ArrayList<>(configs.keySet())) {
            reload(fileName);
        }
    }

    public static void initSchems(String... files) {
        Plugin plugin = Plugin.getInstance();

        for (String fileName : files) {
            String fullPath = fileName.endsWith(".schem") ? fileName : fileName + ".schem";
            File file = new File(plugin.getDataFolder(), fullPath);

            File parentFolder = file.getParentFile();
            if (parentFolder != null && !parentFolder.exists()) {
                parentFolder.mkdirs();
            }

            if (!file.exists()) {
                try {
                    plugin.saveResource(fullPath, false);
                } catch (Exception e) {
                    plugin.getLogger().warning("Не удалось распаковать ресурс: " + fullPath);
                }
            }
        }
    }

    public static String getString(String config, String path){
        return ConfigManager.getConfig(config).getString(path);
    }

    public static double getDouble(String config, String path){
        return ConfigManager.getConfig(config).getDouble(path);
    }

    public static int getInteger(String config, String path){
        return ConfigManager.getConfig(config).getInt(path);
    }
}