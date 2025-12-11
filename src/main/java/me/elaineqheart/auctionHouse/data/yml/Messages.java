package me.elaineqheart.auctionHouse.data.yml;

import com.google.common.base.Charsets;
import me.elaineqheart.auctionHouse.AuctionHouse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(AuctionHouse.getPlugin().getDataFolder(), "messages.yml");

        if (!file.exists()) {
            AuctionHouse.getPlugin().saveResource("messages.yml", false);
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        // load the messages.yml file from the jar file and update missing keys with
        // defaults
        final InputStream defConfigStream = AuctionHouse.getPlugin().getResource("messages.yml");
        if (defConfigStream == null) {
            return;
        }
        customFile.setDefaults(
                YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            AuctionHouse.getPlugin().getLogger().severe("Couldn't save messages.yml file");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    private static String format(String message) {
        // Replace &n with \n first to avoid it becoming underline
        message = message.replace("&n", "\n");

        // MiniMessage
        Component component = MiniMessage.miniMessage().deserialize(message);
        String legacy = LegacyComponentSerializer.legacySection().serialize(component);

        // Legacy colors
        return ChatColor.translateAlternateColorCodes('&', legacy);
    }

    private static String getValue(String key) {
        if (customFile.isList(key)) {
            List<String> list = customFile.getStringList(key);
            return format(String.join("\n", list));
        }
        String message = customFile.getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return format(message);
    }

    // this is to replace placeholders like %player%
    public static String getFormatted(String key, String... replacements) {
        String message = getValue(key);
        message = replacePlaceholders(key, message, replacements);
        return message;
    }

    public static List<String> getLoreList(String key, String... replacements) {
        if (customFile.isList(key)) {
            List<String> list = customFile.getStringList(key);
            List<String> formattedList = new ArrayList<>();
            for (String line : list) {
                String formatted = format(line);
                formatted = replacePlaceholders(key, formatted, replacements);
                formattedList.add(formatted);
            }
            return formattedList;
        }

        String message = getValue(key);
        message = replacePlaceholders(key, message, replacements);
        return List.of(message.split("\n"));
    }

    private static String replacePlaceholders(String key, String message, String... replacements) {
        if (replacements.length % 2 != 0) {
            return ChatColor.RED + "Invalid placeholder replacements for key: " + key;
        }
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

}
