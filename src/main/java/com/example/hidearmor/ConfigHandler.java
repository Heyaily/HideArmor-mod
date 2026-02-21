package com.example.hidearmor;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class ConfigHandler {
    public static Configuration config;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        config.load();
    }

    public static boolean[] getSettings(String target) {
        String category = target.equals("Global") ? "global" : "player_" + target;
        boolean h = config.getBoolean("hideHelmet", category, false, "");
        boolean c = config.getBoolean("hideChestplate", category, false, "");
        boolean l = config.getBoolean("hideLeggings", category, false, "");
        boolean b = config.getBoolean("hideBoots", category, false, "");
        boolean a = config.getBoolean("hideArrows", category, false, ""); 
        boolean i = config.getBoolean("hideIcons", category, false, ""); 
        return new boolean[]{b, l, c, h, a, i}; 
    }

    public static void setSetting(String target, int slot, boolean value) {
        String category = target.equals("Global") ? "global" : "player_" + target;
        String key = slot == 5 ? "hideIcons" : slot == 4 ? "hideArrows" : slot == 3 ? "hideHelmet" : slot == 2 ? "hideChestplate" : slot == 1 ? "hideLeggings" : "hideBoots";
        config.get(category, key, false).set(value);
        config.save();
    }
}