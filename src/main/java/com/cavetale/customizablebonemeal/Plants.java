package com.cavetale.customizablebonemeal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;

@RequiredArgsConstructor
public final class Plants {
    private final CustomizableBonemealPlugin plugin;
    private Map<String, Plant> map = new HashMap<>();

    void load(ConfigurationSection config) {
        map.clear();
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            try {
                Plant plant = new Plant(section);
                map.put(plant.key, plant);
            } catch (IllegalStateException ise) {
                plugin.getLogger().warning("config.yml: Invalid config: " + key);
            }
        }
    }

    List<Plant> all() {
        return new ArrayList<>(map.values());
    }

    List<Plant> of(Player player) {
        return plugin.selections.of(player).plants.stream()
            .map(map::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    Plant get(String key) {
        return map.get(key);
    }

    boolean plant(Player player, Block grassBlock, Plant plant) {
        Block block = grassBlock.getRelative(0, 1, 0);
        if (!block.isEmpty()) return false;
        Block above = null;
        Material blockMaterial;
        int blockData;
        Material aboveMaterial = null;
        int aboveData = 0;
        // Set and test above block if necessary
        if (plant.material == Material.DOUBLE_PLANT) {
            above = block.getRelative(0, 1, 0);
            if (!above.isEmpty()) return false;
            aboveMaterial = plant.material;
            aboveData = 10;
        }
        blockMaterial = plant.material;
        blockData = plant.data;
        // Call event(s)
        EntityChangeBlockEvent event = new EntityChangeBlockEvent(player, block, blockMaterial,
                                                                  (byte) blockData);
        plugin.getServer().getPluginManager().callEvent(event);
        if (above != null) {
            if (event.isCancelled()) return false;
            event = new EntityChangeBlockEvent(player, above, aboveMaterial, (byte) aboveData);
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }
        // Update blocks
        block.setTypeIdAndData(blockMaterial.getId(), (byte) blockData, false);
        if (above != null) {
            above.setTypeIdAndData(aboveMaterial.getId(), (byte) aboveData, false);
        }
        // Audio-visual feedback
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().playSound(loc, Sound.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 0.5f, 1.25f);
        block.getWorld().spawnParticle(Particle.SPELL, loc, 6, 0.1, 0.1, 0.1, 1.0);
        return true;
    }
}
