package net.mobilelize.disguise.commands;

import dev.iiahmed.disguise.*;
import net.mobilelize.disguise.manager.DisguiseOff;
import net.mobilelize.disguise.manager.DisguiseOn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class DisguiseCommand implements CommandExecutor {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    final Plugin plugin;

    public DisguiseCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        switch (args.length) {
            case 0 -> {
                DisguiseOff.asSender(sender);
                return true;
            }
            case 1 -> {
                if (Bukkit.getPlayerExact(args[0]) != null) {
                    if (isSender(sender, args)) {
                        DisguiseOff.asSender(sender);
                    } else {
                        DisguiseOff.asPlayer(args[0], sender);
                    }
                } else if (args[0].equals("*")) {
                    DisguiseOff.all(sender);
                } else {
                    DisguiseOn.asSender(args[0], sender, args);
                }
                return true;
            }
            case 2 -> {
                if (args[0].equals("*")) {
                    DisguiseOn.all(args[1], sender, args);
                } else if (Arrays.stream(args).toList().getLast().equalsIgnoreCase("-m")) {
                    DisguiseOn.asSender(args[0], sender, args);
                } else if (isSender(sender, args)) {
                    DisguiseOn.asSender(args[1], sender, args);
                } else {
                    DisguiseOn.asPlayer(args[0], args[1], sender, args);
                }
                return true;
            }
            case 3 -> {
                if (isSender(sender, args)) {
                    DisguiseOn.asSender(args[1], sender, args);
                } else if (args[0].equals("*")) {
                    DisguiseOn.all(args[1], sender, args);
                }else {
                    DisguiseOn.asPlayer(args[0], args[1], sender, args);
                }
                return true;
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Too much args for now that is");
                return true;
            }
        }
    }

    private Boolean isSender(CommandSender sender, String[] args){
        if (args[0].equals("*")){
            return false;
        }
        return sender.getName().equalsIgnoreCase(args[0]) || provider.getInfo((Player) sender).getName().equalsIgnoreCase(args[0]);
    }
}
