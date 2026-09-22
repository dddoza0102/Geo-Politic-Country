package dev.doza.politic.bukkit.util;

import dev.doza.politic.Plugin;
import dev.doza.politic.bukkit.manager.ConfigManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChatUtil {
    public static String format(String text) {
        if (text == null) return "";
        return translate(ChatColor.translateAlternateColorCodes('&', text));
    }

    public static String translate(String input) {
        if (input == null || input.isEmpty()) return "";
        var serializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build();
        var component = serializer.deserialize(input);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static void send(CommandSender player, String path, Map<String, String> args) {
        var config = ConfigManager.getConfig(Plugin.getInstance().getConfig().getString("lang_file"));
        Object value = config.get(path);

        if (value == null) {
            player.sendMessage(path);
            return;
        }

        if (value instanceof List) {
            for (String string : config.getStringList(path)) {
                player.sendMessage(applyArgs(format(string), args));
            }
        } else {
            String string = config.getString(path);
            if (string != null) {
                player.sendMessage(applyArgs(format(string), args));
            }
        }
    }

    public static void send(String path, Map<String, String> args) {
        var config = ConfigManager.getConfig(Plugin.getInstance().getConfig().getString("lang_file"));
        Object value = config.get(path);

        if (value == null) {
            Plugin.getInstance().getLogger().warning("Путь локализации '" + path + "' не найден для вебхуков!");
            return;
        }

        if (value instanceof List) {
            for (String string : config.getStringList(path)) {
                String readyMessage = applyArgs(format(string), args);

                Plugin.getInstance().getWebhookManager().sendNotification(readyMessage);
            }
        } else {
            String string = config.getString(path);
            if (string != null) {
                String readyMessage = applyArgs(format(string), args);
                Plugin.getInstance().getWebhookManager().sendNotification(readyMessage);
            }
        }
    }


    public static void send(CommandSender player, String path){
        var config = ConfigManager.getConfig(Plugin.getInstance().getConfig().getString("lang_file"));
        Object value = config.get(path);

        if (value == null) {
            player.sendMessage(path);
            return;
        }

        if (value instanceof List) {
            for (String string : config.getStringList(path)) {
                player.sendMessage(format(string));
            }
        } else {
            String string = config.getString(path);
            if (string != null) {
                player.sendMessage(format(string));
            }
        }
    }

    public static void send(String path){
        var config = ConfigManager.getConfig(Plugin.getInstance().getConfig().getString("lang_file"));
        Object value = config.get(path);

        if (value == null) {
            Plugin.getInstance().getLogger().warning("Путь локализации '" + path + "' не найден для отправки в вебхуки!");
            return;
        }

        if (value instanceof List) {
            for (String string : config.getStringList(path)) {
                Plugin.getInstance().getWebhookManager().sendNotification(format(string));
            }
        } else {
            String string = config.getString(path);
            if (string != null) {
                Plugin.getInstance().getWebhookManager().sendNotification(format(string));
            }
        }
    }

    public static String applyArgs(String text, Map<String, String> args){
        String result = null;
        for (String arg: args.keySet()){
            assert false;
            result = result.replace(arg, args.get(arg));
        }
        return result;
    }

    public static String getConfigString(String configPath) {
        return getConfigString(configPath, Collections.emptyMap());
    }

    public static String getConfigString(String configPath, Map<String, String> args) {
        String lang = Plugin.getInstance().getConfig().getString("lang");
        String message = ConfigManager.getConfig(lang).getString(configPath);

        if (message == null) {
            if (configPath.toLowerCase().contains("not_country")) return "§cNot country";
            if (configPath.toLowerCase().contains("not_town")) return "§cNot town";
            return "";
        }

        for (Map.Entry<String, String> entry : args.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return format(message);
    }
}