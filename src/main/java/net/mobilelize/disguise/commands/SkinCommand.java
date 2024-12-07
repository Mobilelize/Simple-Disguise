package net.mobilelize.disguise.commands;

import net.mobilelize.disguise.manager.DisguiseOff;
import net.mobilelize.disguise.manager.DisguiseOn;
import net.mobilelize.disguise.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkinCommand implements CommandExecutor {

    private final DisguiseOn disguiseOn = new DisguiseOn();
    private final DisguiseOff disguiseOff = new DisguiseOff();

    private final Cooldown cooldown = new Cooldown();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }

        if (!cooldown.checkCooldown(sender, "mobilelize.skin", "mobilelize.skin.self")) {
            return true; // Cooldown not satisfied
        }

        switch (args.length) {
            case 0 -> {
                // Remove disguise for the sender
                if (!player.hasPermission("mobilelize.skin.self")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                    return false;
                }
                disguiseOff.asSender(player);
                return true;
            }
            case 1 -> {
                if (args[0].equals("*")) {
                    // Remove disguise for all players
                    if (!player.hasPermission("mobilelize.skin.all")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                        return false;
                    }
                    disguiseOff.all(player);
                } else if (Bukkit.getPlayerExact(args[0]) != null) {
                    // Remove disguise for a specific player
                    if (!player.hasPermission("mobilelize.skin.all")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to modify other players.");
                        return false;
                    }
                    if (isSender(player, args)) {
                        disguiseOff.asSender(player);
                    } else {
                        disguiseOff.asPlayer(args[0], player);
                    }
                } else {
                    // Apply skin disguise for the sender
                    if (!player.hasPermission("mobilelize.skin.self")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                        return false;
                    }
                    disguiseOn.asSender(args[0], player, new String[]{args[0], "-s"});
                }
                return true;
            }
            case 2 -> {
                if (args[0].equals("*")) {
                    // Apply skin disguise to all players
                    if (!player.hasPermission("mobilelize.skin.all")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                        return false;
                    }
                    String[] newArgs = new String[]{args[0], args[1], "-s"};
                    disguiseOn.all(args[1], player, newArgs);
                } else if (isSender(player, args)) {
                    // Apply skin disguise to the sender
                    if (!player.hasPermission("mobilelize.skin.self")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                        return false;
                    }
                    disguiseOn.asSender(args[1], player, new String[]{args[1], "-s"});
                } else {
                    // Apply skin disguise to a specific player
                    if (!player.hasPermission("mobilelize.skin.all")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to modify other players.");
                        return false;
                    }
                    disguiseOn.asPlayer(args[0], args[1], player, new String[]{args[0], args[1], "-s"});
                }
                return true;
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Too many arguments.");
                return false;
            }
        }
    }

    private Boolean isSender(Player player, String[] args) {
        if (args[0].equals("*")) {
            return false;
        }
        return player.getName().equalsIgnoreCase(args[0]) && Bukkit.getPlayerExact(args[0]).equals(player);
    }
}
