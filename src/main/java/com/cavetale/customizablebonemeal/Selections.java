package com.cavetale.customizablebonemeal;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Selections {
    private final CustomizableBonemealPlugin plugin;
    @Getter private int maxSelections;
    public static final String META = "selection";
    private final File folder;

    Selections(final CustomizableBonemealPlugin plugin) {
        this.plugin = plugin;
        folder = new File(plugin.getDataFolder(), "selections");
    }

    void load() {
        maxSelections = plugin.getConfig().getInt("MaxSelections");
        folder.mkdirs();
    }

    public Selection of(Player player) {
        return plugin.meta.get(player, META, Selection.class, () -> load(player));
    }

    public void clear(Player player) {
        plugin.meta.remove(player, META);
    }

    public Selection load(Player player) {
        UUID uuid = player.getUniqueId();
        String filename = uuid + ".json";
        File file = new File(folder, filename);
        if (!file.exists()) return new Selection();
        return plugin.json.load(file, Selection.class, Selection::new);
    }

    public void save(Player player) {
        Selection selection = plugin.meta.get(player, META, Selection.class, () -> null);
        if (selection == null) return;
        UUID uuid = player.getUniqueId();
        String filename = uuid + ".json";
        File file = new File(folder, filename);
        plugin.json.save(file, selection, true);
    }

    void openGUI(Player player) {
        List<Plant> plants = plugin.plants.all();
        int size = ((plants.size() - 1) / 9 + 1) * 9;
        if (size == 0) return;
        GUI gui = new GUI(plugin);
        gui.createInventory(size, ChatColor.BLUE + "Bonemeal Plants");
        makeGUI(player, gui, plants);
        gui.open(player);
    }

    void makeGUI(Player player, GUI gui, List<Plant> plants) {
        Selection selection = of(player);
        for (Iterator<String> iter = selection.plants.iterator(); iter.hasNext();) {
            String it = iter.next();
            if (plugin.plants.get(it) == null) {
                iter.remove();
                gui.dirty = true;
            }
        }
        gui.clear();
        int i = 0;
        for (Plant plant : plants) {
            int index = i++;
            ItemStack item = new ItemStack(plant.material, 1, (short) plant.data);
            ItemMeta meta = item.getItemMeta();
            boolean isSelected = selection.plants.contains(plant.key);
            List<String> lore = new ArrayList<>();
            if (isSelected) {
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                lore.add(ChatColor.AQUA + "Active");
                item.setAmount(plant.material.getMaxStackSize());
            } else {
                lore.add(ChatColor.DARK_GRAY + "Not Active");
            }
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
            gui.setItem(index, item, click -> {
                    if (isSelected) {
                        selection.plants.remove(plant.key);
                        gui.dirty = true;
                    } else {
                        if (selection.plants.size() < maxSelections) {
                            selection.plants.add(plant.key);
                            gui.dirty = true;
                        }
                    }
                    makeGUI(player, gui, plants);
                    player.updateInventory();
                });
        }
        gui.onClose = () -> {
            if (!gui.dirty) return;
            save(player);
        };
    }
}
