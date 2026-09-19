package dev.doza.politic.webhook.manager;

import dev.doza.politic.webhook.config.WebhookConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebhookManager {

    private final JavaPlugin plugin;
    private final WebhookConfig config;

    public WebhookManager(JavaPlugin plugin, WebhookConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Отправляет сообщение во все включенные мессенджеры асинхронно.
     * @param rawMessage Строка сообщения (может содержать цвета Minecraft)
     */
    public void sendNotification(String rawMessage) {
        // Очищаем текст от внутренних цветовых кодов параграфа (§)
        String cleanMessage = ChatColor.stripColor(rawMessage);

        // Переводим выполнение в асинхронный поток Bukkit
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (config.isDiscordEnabled()) {
                sendToDiscord(config.getDiscordUrl(), cleanMessage);
            }
            if (config.isTelegramEnabled()) {
                sendToTelegram(config.getTelegramToken(), config.getTelegramChatId(), cleanMessage);
            }
        });
    }

    private void sendToDiscord(String webhookUrl, String text) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = "{\"content\": \"" + text.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }
            
            int code = conn.getResponseCode();
            if (code != 204) {
                plugin.getLogger().warning("Discord Webhook вернул код ошибки: " + code);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Не удалось отправить сообщение в Discord: " + e.getMessage());
        }
    }

    private void sendToTelegram(String token, String chatId, String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String urlStr = String.format("https://telegram.org", 
                    token, chatId, encodedText);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            if (code != 200) {
                plugin.getLogger().warning("Telegram API вернул код ошибки: " + code);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Не удалось отправить сообщение в Telegram: " + e.getMessage());
        }
    }
}