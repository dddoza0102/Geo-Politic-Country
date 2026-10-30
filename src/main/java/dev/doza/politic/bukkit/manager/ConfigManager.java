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

    // ИСПРАВЛЕНО (1): новый метод, приводит "config.yml" и "config" к одному ключу "config"
    private static String normalize(String fileName) {
        return fileName.endsWith(".yml") ? fileName.substring(0, fileName.length() - 4) : fileName;
    }

    public static void init(String... fileNames) {
        Plugin plugin = Plugin.getInstance();

        for (String fileName : fileNames) {
            // ИСПРАВЛЕНО (1): ключ в мапах теперь всегда без ".yml"
            String key = normalize(fileName);
            String fullPath = key + ".yml";
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

            // ИСПРАВЛЕНО (2): если файла всё равно нет, пишем ошибку и не регистрируем пустой конфиг,
            // чтобы getConfig вернул null вместо пустой секции и проблема была видна сразу
            if (!file.exists()) {
                plugin.getLogger().severe("Файл конфигурации отсутствует: " + fullPath);
                continue;
            }

            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                configs.put(key, config);
                files.put(key, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static FileConfiguration getConfig(String fileName) {
        // ИСПРАВЛЕНО (1): нормализация имени, плюс защита от null (например, если lang_file не задан)
        if (fileName == null) {
            return null;
        }
        return configs.get(normalize(fileName));
    }

    public static File getFile(String fileName) {
        // ИСПРАВЛЕНО (1)
        if (fileName == null) {
            return null;
        }
        return files.get(normalize(fileName));
    }

    public static void save(String fileName) {
        // ИСПРАВЛЕНО (1)
        String key = normalize(fileName);
        FileConfiguration config = configs.get(key);
        File file = files.get(key);

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
        // ИСПРАВЛЕНО (1)
        String key = normalize(fileName);
        File file = files.get(key);

        if (file == null) {
            Plugin.getInstance().getLogger().warning("Конфиг не найден для перезагрузки: " + fileName);
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(key, config);
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