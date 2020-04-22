package com.cavetale.customizablebonemeal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
final class EventListener implements Listener {
    private final CustomizableBonemealPlugin plugin;
    public static final String PERMISSION = "cbm.cbm";

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        plugin.exit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(PERMISSION)) return;
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null) return;
        if (hand.getType() != Material.INK_SACK || hand.getDurability() != 15) return;
        if (player.isSneaking()) {
            switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                break;
            default:
                return;
            }
            event.setCancelled(true);
            plugin.selections.openGUI(player);
        } else {
            switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return;
            }
            Block block = event.getClickedBlock();
            if (block == null || block.getType() != Material.GRASS) return;
            List<Plant> plants = plugin.plants.of(player);
            if (plants.isEmpty()) return;
            if (event.isCancelled()) return;
            event.setCancelled(true);
            int r = 3;
            int amount = 0;
            for (int dz = -r; dz <= r; dz += 1) {
                for (int dx = -r; dx <= r; dx += 1) {
                    for (int dy = -r; dy <= r; dy += 1) {
                        if (dx * dx + dy * dy + dz * dz > r * r + 1) continue;
                        Block grassBlock = block.getRelative(dx, dy, dz);
                        if (grassBlock.getType() != Material.GRASS) continue;
                        if (plugin.random.nextInt(2) > 0) continue;
                        Plant plant = plants.get(plugin.random.nextInt(plants.size()));
                        if (plugin.plants.plant(player, grassBlock, plant)) {
                            amount += 1;
                        }
                    }
                }
            }
            if (amount > 0 && player.getGameMode() != GameMode.CREATIVE) {
                hand.setAmount(hand.getAmount() - 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder())
                .onInventoryOpen(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder())
                .onInventoryClose(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder())
                .onInventoryClick(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder())
                .onInventoryDrag(event);
        }
    }
}
