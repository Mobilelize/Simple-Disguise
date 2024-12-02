package net.mobilelize.disguise.manager;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.UndisguiseResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisguiseOff {

    private static final DisguiseProvider provider = DisguiseManager.getProvider();

    public static void asPlayer(String nameOfPlayer, CommandSender sender){

        Player player = Bukkit.getServer().getPlayerExact(nameOfPlayer);
        UndisguiseResponse response = provider.undisguise(player);

        switch (response){
            case SUCCESS -> {
                sender.sendMessage(ChatColor.GOLD + "The player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " has been successfully undisguised");
            } case null, default -> {
                sender.sendMessage(ChatColor.RED + "Undisguise failed: " + response.toString().toLowerCase().replace('_', ' '));
            }
        }
    }

    public static void asSender(CommandSender sender){

        Player player = (Player) sender;
        UndisguiseResponse response = provider.undisguise(player);

        switch (response){
            case SUCCESS -> {
                sender.sendMessage(ChatColor.GOLD + "You have been successfully undisguised");
            } case null, default -> {
                sender.sendMessage(ChatColor.RED + "Undisguise failed: " + response.toString().toLowerCase().replace('_', ' '));
            }
        }
    }

    public static void all(CommandSender sender){

        int totalPlayers = 0;
        int successfulActions = 0;


        for (Player player : Bukkit.getOnlinePlayers()) {
            totalPlayers++;

            UndisguiseResponse response = provider.undisguise(player);
            if (response == UndisguiseResponse.SUCCESS){
                successfulActions++;
            }
        }

        sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully Undisguised");

    }
}
