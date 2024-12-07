package net.mobilelize.disguise.commands;

import net.mobilelize.disguise.Disguise;
import net.mobilelize.disguise.manager.DisguiseOff;
import net.mobilelize.disguise.manager.DisguiseOn;
import net.mobilelize.disguise.tabCompleter.DisguiseTabCompleter;
import net.mobilelize.disguise.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DisguiseCommand implements CommandExecutor {

    private final DisguiseTabCompleter tabCompleter = new DisguiseTabCompleter();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        final DisguiseOff disguiseOff = new DisguiseOff();
        final DisguiseOn disguiseOn = new DisguiseOn();

        switch (args.length) {
            case 0 -> {
                disguiseOff.asSender(sender);
                return true;
            }
            case 1 -> {
                if (Disguise.getConfigManager().isAutoCommandEnabled() && isCommand(args[0])){
                  disguiseOn.executeCommands(args[0], sender);
                } else if (Disguise.getConfigManager().isRealNameBehaviorEnabled() && Bukkit.getPlayerExact(args[0]) != null) {
                    final String[] newArgs = new String[] { args[0], "-r"};
                    if (isSender(sender, args)){
                        disguiseOn.asSender(args[0], sender, newArgs);
                    } else {
                        disguiseOn.asPlayer(args[0], args[0], sender, newArgs);
                    }
                } else if (Disguise.getConfigManager().isMobAutoDetect() && tabCompleter.entityNames.contains(args[0].toLowerCase()) && mobCheck()) {
                    final String[] newArgs = new String[] { args[0], "-m"};
                    disguiseOn.asSender(args[0].toLowerCase(), sender, newArgs);
                } else if (Bukkit.getPlayerExact(args[0]) != null) {
                    if (isSender(sender, args)) {
                        disguiseOff.asSender(sender);
                    } else {
                        disguiseOff.asPlayer(args[0], sender);
                    }
                } else if (args[0].equals("*")) {
                    if (Disguise.getConfigManager().isRealNameApplyToAll()){
                        final String[] newArgs = new String[] { args[0], "-r"};
                        disguiseOn.all("all", sender, newArgs);
                    } else {
                        disguiseOff.all(sender);
                    }
                } else {
                    if (Disguise.getConfigManager().isRealNameBehaviorForce()){
                        final String[] newArgs = new String[] { args[0], "-r"};
                        disguiseOn.asPlayer(args[0], args[0], sender, newArgs);
                    } else {
                        disguiseOn.asSender(args[0], sender, args);
                    }

                }
                return true;
            }
            case 2 -> {
                if (args[0].equals("*")) {
                    if (args[1].equalsIgnoreCase("-f")){
                        disguiseOff.all(sender);
                    }
                    else if (Disguise.getConfigManager().isMobAutoDetect() && tabCompleter.entityNames.contains(args[1])){
                        final String[] newArgs = new String[] { args[0], args[1], "-m"};
                        disguiseOn.all(args[1], sender, newArgs);
                    } else {
                        disguiseOn.all(args[1], sender, args);
                    }
                } else if (disguiseOn.ishandleCommand(args) && !Arrays.stream(args).toList().getLast().equalsIgnoreCase("-r")) {
                    disguiseOn.asSender(args[0], sender, args);
                } else if (Disguise.getConfigManager().isMobAutoDetect() && tabCompleter.entityNames.contains(args[1])) {
                    final String[] newArgs = new String[] { args[0], args[1], "-m"};
                    if (isSender(sender, args)){
                        disguiseOn.asSender(args[1], sender, newArgs);
                    } else {
                        disguiseOn.asPlayer(args[0], args[1], sender, newArgs);
                    }
                } else if (isSender(sender, args)) {
                    disguiseOn.asSender(args[1], sender, args);
                } else {
                    disguiseOn.asPlayer(args[0], args[1], sender, args);
                }
                return true;
            }
            case 3 -> {
                if (isSender(sender, args)) {
                    disguiseOn.asSender(args[1], sender, args);
                } else if (args[0].equals("*")) {
                    disguiseOn.all(args[1], sender, args);
                }else {
                    disguiseOn.asPlayer(args[0], args[1], sender, args);
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
        return sender.getName().equalsIgnoreCase(args[0]) && Bukkit.getPlayerExact(args[0]).equals(sender);
    }

    private Boolean isCommand(String command){
        return switch (command.toLowerCase()){
            case "help", "reload" -> true;
            default -> false;
        };
    }

    private Boolean mobCheck(){
        if (Disguise.getConfigManager().isRealNameBehaviorForce()){
            return Disguise.getConfigManager().isMobOverrideRealName();
        }
        return true;
    }
}
