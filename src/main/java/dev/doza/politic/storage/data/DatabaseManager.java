package dev.doza.politic.storage.data;

import dev.doza.politic.Plugin;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.region.Region;
import org.bukkit.Location;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {
    private static Connection connection;
    private static ExecutorService executor;

    @FunctionalInterface
    private interface SqlTask {
        void run(Connection connection) throws SQLException;
    }

    public static void init() {
        Plugin plugin = Plugin.getInstance();
        plugin.getDataFolder().mkdirs();
        File file = new File(plugin.getDataFolder(), "data.db");

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            createTables();
            executor = Executors.newSingleThreadExecutor();
        } catch (SQLException e) {
            plugin.getLogger().severe("Не удалось открыть базу данных: " + e.getMessage());
            connection = null;
        }
    }

    public static void close() {
        if (executor == null) {
            return;
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
            connection.close();
        } catch (InterruptedException | SQLException e) {
            Plugin.getInstance().getLogger().warning("Не удалось корректно закрыть базу данных: " + e.getMessage());
        }
        executor = null;
        connection = null;
    }

    private static void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS countries (
                        name TEXT PRIMARY KEY,
                        resident TEXT NOT NULL,
                        capital TEXT NOT NULL,
                        balance REAL NOT NULL,
                        color INTEGER NOT NULL,
                        friendly_fire INTEGER NOT NULL,
                        pvp INTEGER NOT NULL,
                        center_world TEXT,
                        center_x REAL,
                        center_y REAL,
                        center_z REAL,
                        center_yaw REAL,
                        center_pitch REAL
                    )""");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS country_players (
                        country TEXT NOT NULL,
                        uuid TEXT NOT NULL,
                        PRIMARY KEY (country, uuid)
                    )""");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS regions (
                        world TEXT NOT NULL,
                        x INTEGER NOT NULL,
                        z INTEGER NOT NULL,
                        country TEXT NOT NULL,
                        town TEXT,
                        PRIMARY KEY (world, x, z)
                    )""");
        }
    }

    private static void submit(SqlTask task) {
        if (executor == null) {
            return;
        }

        executor.submit(() -> {
            try {
                task.run(connection);
            } catch (SQLException e) {
                Plugin.getInstance().getLogger().severe("Ошибка записи в базу данных: " + e.getMessage());
            }
        });
    }

    private static void execute(Connection connection, String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < params.length; index++) {
                statement.setObject(index + 1, params[index]);
            }
            statement.executeUpdate();
        }
    }

    public static void saveCountry(Country country) {
        String name = country.getName();
        String resident = country.getResident().toString();
        String capital = country.getCapital().getName();
        double balance = country.getBalance();
        int color = country.getColor().asRGB();
        int friendlyFire = country.isFriendlyFire() ? 1 : 0;
        int pvp = country.isPvp() ? 1 : 0;
        Object[] center = centerValues(country.getCenter());
        List<UUID> players = new ArrayList<>(country.getPlayers());

        submit(connection -> {
            execute(connection, "INSERT OR REPLACE INTO countries VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    name, resident, capital, balance, color, friendlyFire, pvp,
                    center[0], center[1], center[2], center[3], center[4], center[5]);
            replacePlayers(connection, name, players);
        });
    }

    public static void deleteCountry(String name) {
        submit(connection -> {
            execute(connection, "DELETE FROM countries WHERE name = ?", name);
            execute(connection, "DELETE FROM country_players WHERE country = ?", name);
            execute(connection, "DELETE FROM regions WHERE country = ?", name);
        });
    }

    public static void renameCountry(String oldName, String newName) {
        submit(connection -> {
            execute(connection, "UPDATE countries SET name = ? WHERE name = ?", newName, oldName);
            execute(connection, "UPDATE country_players SET country = ? WHERE country = ?", newName, oldName);
            execute(connection, "UPDATE regions SET country = ? WHERE country = ?", newName, oldName);
        });
    }

    public static void updateCountryField(String countryName, String column, Object value) {
        submit(connection -> execute(connection, "UPDATE countries SET " + column + " = ? WHERE name = ?", value, countryName));
    }

    public static void updateCountryCenter(String countryName, Location location) {
        Object[] center = centerValues(location);

        submit(connection -> execute(connection,
                "UPDATE countries SET center_world = ?, center_x = ?, center_y = ?, center_z = ?, center_yaw = ?, center_pitch = ? WHERE name = ?",
                center[0], center[1], center[2], center[3], center[4], center[5], countryName));
    }

    public static void updateCountryPlayers(String countryName, List<UUID> players) {
        List<UUID> snapshot = new ArrayList<>(players);
        submit(connection -> replacePlayers(connection, countryName, snapshot));
    }

    public static void saveRegion(Region region) {
        String world = region.getChunk().getWorld().getName();
        int x = region.getChunk().getX();
        int z = region.getChunk().getZ();
        String country = region.getCountry().getName();
        String town = region.getTown() == null ? null : region.getTown().getName();

        submit(connection -> execute(connection, "INSERT OR REPLACE INTO regions VALUES (?, ?, ?, ?, ?)",
                world, x, z, country, town));
    }

    public static void deleteRegion(String world, int x, int z) {
        submit(connection -> execute(connection, "DELETE FROM regions WHERE world = ? AND x = ? AND z = ?", world, x, z));
    }

    private static void replacePlayers(Connection connection, String countryName, List<UUID> players) throws SQLException {
        execute(connection, "DELETE FROM country_players WHERE country = ?", countryName);

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO country_players VALUES (?, ?)")) {
            for (UUID uuid : players) {
                statement.setString(1, countryName);
                statement.setString(2, uuid.toString());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static Object[] centerValues(Location location) {
        if (location == null || location.getWorld() == null) {
            return new Object[]{null, null, null, null, null, null};
        }

        return new Object[]{
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                (double) location.getYaw(),
                (double) location.getPitch()
        };
    }
}