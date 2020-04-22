package com.cavetale.customizablebonemeal;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class BonemealCommand implements CommandExecutor {
    private final CustomizableBonemealPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String alias, String[] args) {
        if (sender instanceof Player) {
            return onCommand((Player) sender, args);
        }
        sender.sendMessage("Player expected");
        return true;
    }

    boolean onCommand(Player player, String[] args) {
        if (args.length > 0) {
            String arg = Stream.of(args).collect(Collectors.joining(" "));
            Plant plant = plugin.plants.get(arg);
            if (plant == null) return false;
            Set<String> plants = plugin.selections.of(player).plants;
            plants.removeIf(s -> plugin.plants.get(s) == null);
            if (plants.contains(plant.key)) {
                plants.remove(plant.key);
                plugin.selections.save(player);
            } else {
                int max = plugin.selections.getMaxSelections();
                if (plants.size() < max) {
                    plants.add(plant.key);
                    plugin.selections.save(player);
                } else {
                    player.sendMessage(ChatColor.RED
                                       + "You can only select " + max + "!");
                    return true;
                }
            }
        }
        showMenu(player);
        return true;
    }

    void showMenu(Player player) {
        player.sendMessage(ChatColor.BLUE + "Bonemeal Plants");
        ComponentBuilder cb = new ComponentBuilder("Click: ");
        List<Plant> active = plugin.plants.of(player);
        for (Plant plant : plugin.plants.all()) {
            cb.append(" ").reset();
            cb.append("[" + plant.key + "]");
            if (active.contains(plant)) {
                cb.color(ChatColor.GREEN);
            } else {
                cb.color(ChatColor.DARK_GRAY);
            }
            cb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/bonemeal " + plant.key));
            cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    TextComponent.fromLegacyText(plant.key)));
        }
        player.spigot().sendMessage(cb.create());
    }
}
