package com.cavetale.customizablebonemeal;

import lombok.Value;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

@Value
public final class Plant {
    public final String key;
    public final Material material;
    public final int data;

    Plant(final ConfigurationSection config) {
        key = config.getName();
        String tmp = config.getString("Material");
        try {
            material = Material.valueOf(tmp.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new IllegalStateException(iae);
        }
        data = config.getInt("Data");
    }
}
