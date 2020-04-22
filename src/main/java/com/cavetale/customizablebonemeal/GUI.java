package com.cavetale.customizablebonemeal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter @RequiredArgsConstructor
public final class GUI implements InventoryHolder {
    final JavaPlugin plugin;
    Inventory inventory;
    Map<Integer, Consumer<InventoryClickEvent>> clicks = new HashMap<>();
    Runnable onClose = null;
    boolean dirty = false;

    public Inventory createInventory(final int size,
                                     final String title) {
        inventory = Bukkit.getServer().createInventory(this, size, title);
        return inventory;
    }

    public void onInventoryOpen(final InventoryOpenEvent event) {
        Objects.requireNonNull(inventory, "inventory is null");
    }

    public void onInventoryClose(final InventoryCloseEvent event) {
        Objects.requireNonNull(inventory, "inventory is null");
        if (onClose != null) {
            Bukkit.getScheduler().runTask(plugin, onClose);
        }
    }

    public void onInventoryClick(final InventoryClickEvent event) {
        Objects.requireNonNull(inventory, "inventory is null");
        event.setCancelled(true);
        if (event.getClickedInventory() == null
            || !event.getClickedInventory().equals(inventory)) {
            return;
        }
        Consumer<InventoryClickEvent> run = clicks.get(event.getSlot());
        if (run != null) {
            Bukkit.getScheduler().runTask(plugin, () -> run.accept(event));
        }
    }

    public void onInventoryDrag(final InventoryDragEvent event) {
        Objects.requireNonNull(inventory, "inventory is null");
        event.setCancelled(true);
    }

    public void setClick(final int slot,
                         final Consumer<InventoryClickEvent> callback) {
        clicks.put(slot, callback);
    }

    public void setItem(final int slot,
                        final ItemStack item) {
        Objects.requireNonNull(inventory).setItem(slot, item);
    }

    public void setItem(final int slot,
                        final ItemStack item,
                        final Consumer<InventoryClickEvent> callback) {
        Objects.requireNonNull(inventory).setItem(slot, item);
        clicks.put(slot, callback);
    }

    public InventoryView open(Player player) {
        return player.openInventory(inventory);
    }

    public void clear() {
        if (inventory != null) inventory.clear();
        clicks.clear();
        onClose = null;
    }
}
