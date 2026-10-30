package dev.doza.politic.bukkit.util;

import dev.doza.politic.Plugin;
import dev.doza.politic.bukkit.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CraftUtil {
    private static final String CONFIG_NAME = "crafts";
    private static final String RECIPE_KEY_PREFIX = "craft_";
    private static final int RECIPE_SIZE = 3;

    private static final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    public static void load() {
        unload();

        FileConfiguration config = ConfigManager.getConfig(CONFIG_NAME);
        if (config == null || !config.getBoolean("enabled")) {
            return;
        }

        ConfigurationSection crafts = config.getConfigurationSection("crafts");
        if (crafts == null) {
            return;
        }

        for (String id : crafts.getKeys(false)) {
            ConfigurationSection section = crafts.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            registerCraft(id, section);
        }
    }

    public static void unload() {
        for (NamespacedKey key : registeredRecipes) {
            Bukkit.removeRecipe(key);
        }
        registeredRecipes.clear();
    }

    public static ItemStack createItem(String id) {
        return createItem(id, Collections.emptyMap(), null);
    }

    public static ItemStack createItem(String id, Map<String, String> args) {
        return createItem(id, args, null);
    }

    public static ItemStack createItem(String id, Map<String, String> args, Color color) {
        ConfigurationSection section = getCraftSection(id);
        if (section == null) {
            return null;
        }

        Material material = resolveMaterial(section, color);
        if (material == null) {
            logWarning(id, "неизвестный материал");
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = section.getString("name");
        if (name != null) {
            meta.setDisplayName(ChatUtil.applyArgs(ChatUtil.format(name), args));
        }

        List<String> lore = new ArrayList<>();
        for (String line : section.getStringList("lore")) {
            lore.add(ChatUtil.applyArgs(ChatUtil.format(line), args));
        }
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(getDataKey(id, section), PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isCraftItem(ItemStack item, String id) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ConfigurationSection section = getCraftSection(id);
        if (section == null) {
            return false;
        }

        return item.getItemMeta().getPersistentDataContainer().has(getDataKey(id, section), PersistentDataType.BOOLEAN);
    }

    public static boolean requiresCountry(String id) {
        ConfigurationSection section = getCraftSection(id);
        return section != null && section.getBoolean("requires_country");
    }

    public static String getCraftId(Recipe recipe) {
        if (!(recipe instanceof Keyed keyed) || !registeredRecipes.contains(keyed.getKey())) {
            return null;
        }
        return keyed.getKey().getKey().substring(RECIPE_KEY_PREFIX.length());
    }

    private static void registerCraft(String id, ConfigurationSection section) {
        ItemStack result = createItem(id);
        if (result == null) {
            return;
        }

        ConfigurationSection itemsSection = section.getConfigurationSection("items");
        ConfigurationSection recipeSection = section.getConfigurationSection("recipe");
        if (itemsSection == null || recipeSection == null) {
            logWarning(id, "нет секции items или recipe");
            return;
        }

        String[] shape = new String[RECIPE_SIZE];
        for (int row = 0; row < RECIPE_SIZE; row++) {
            String line = recipeSection.getString(String.valueOf(row + 1));
            if (line == null) {
                logWarning(id, "нет строки рецепта " + (row + 1));
                return;
            }

            String[] cells = line.split(";");
            if (cells.length != RECIPE_SIZE) {
                logWarning(id, "в строке рецепта " + (row + 1) + " должно быть " + RECIPE_SIZE + " ячейки");
                return;
            }

            StringBuilder builder = new StringBuilder();
            for (String cell : cells) {
                String symbol = cell.trim();
                if (symbol.length() != 1 || !itemsSection.isString(symbol)) {
                    logWarning(id, "символ '" + symbol + "' не описан в items");
                    return;
                }
                builder.append(symbol);
            }
            shape[row] = builder.toString();
        }

        NamespacedKey recipeKey = new NamespacedKey(Plugin.getInstance(), RECIPE_KEY_PREFIX + id);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape(shape);

        String joinedShape = String.join("", shape);
        for (String symbol : itemsSection.getKeys(false)) {
            Material material = Material.matchMaterial(itemsSection.getString(symbol, ""));
            if (material == null) {
                logWarning(id, "неизвестный материал для символа '" + symbol + "'");
                return;
            }
            if (joinedShape.contains(symbol)) {
                recipe.setIngredient(symbol.charAt(0), material);
            }
        }

        Bukkit.addRecipe(recipe);
        registeredRecipes.add(recipeKey);
    }

    private static Material resolveMaterial(ConfigurationSection section, Color color) {
        if (section.getBoolean("requires_country")) {
            return color == null ? Material.WHITE_BANNER : getNearestBanner(color);
        }
        return Material.matchMaterial(section.getString("material", ""));
    }

    private static Material getNearestBanner(Color color) {
        DyeColor nearest = DyeColor.WHITE;
        int nearestDistance = Integer.MAX_VALUE;

        for (DyeColor dye : DyeColor.values()) {
            Color dyeColor = dye.getColor();
            int red = color.getRed() - dyeColor.getRed();
            int green = color.getGreen() - dyeColor.getGreen();
            int blue = color.getBlue() - dyeColor.getBlue();
            int distance = red * red + green * green + blue * blue;

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = dye;
            }
        }

        return Material.valueOf(nearest.name() + "_BANNER");
    }

    private static ConfigurationSection getCraftSection(String id) {
        FileConfiguration config = ConfigManager.getConfig(CONFIG_NAME);
        if (config == null) {
            return null;
        }
        return config.getConfigurationSection("crafts." + id);
    }

    private static NamespacedKey getDataKey(String id, ConfigurationSection section) {
        return new NamespacedKey(Plugin.getInstance(), section.getString("data_container", id));
    }

    private static void logWarning(String id, String reason) {
        Plugin.getInstance().getLogger().warning("Крафт '" + id + "' пропущен: " + reason);
    }
}