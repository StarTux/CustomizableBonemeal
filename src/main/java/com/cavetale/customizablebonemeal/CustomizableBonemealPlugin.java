package com.cavetale.customizablebonemeal;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomizableBonemealPlugin extends JavaPlugin {
    final Plants plants = new Plants(this);
    final Selections selections = new Selections(this);
    final Metadata meta = new Metadata(this);
    final Json json = new Json(this);
    final Random random = ThreadLocalRandom.current();
    final EventListener listener = new EventListener(this);
    final BonemealCommand bonemealCommand = new BonemealCommand(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        readConfig();
        getServer().getPluginManager().registerEvents(listener, this);
        getCommand("bonemeal").setExecutor(bonemealCommand);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(this::exit);
    }

    void readConfig() {
        reloadConfig();
        plants.load(getConfig().getConfigurationSection("Plants"));
        selections.load();
    }

    void exit(Player player) {
        selections.clear(player);
        InventoryView view = player.getOpenInventory();
        if (view != null && view.getTopInventory().getHolder() instanceof GUI) {
            player.closeInventory();
        }
    }
}
