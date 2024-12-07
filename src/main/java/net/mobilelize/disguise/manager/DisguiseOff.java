package net.mobilelize.disguise.manager;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.UndisguiseResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisguiseOff {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    public void asPlayer(String nameOfPlayer, CommandSender sender){

        final Player player = Bukkit.getPlayerExact(nameOfPlayer);

        if (player.isDead()){
            sender.sendMessage(ChatColor.GOLD + "The player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " is dead and can't be disguised");
            return;
        }

        UndisguiseResponse response = provider.undisguise(player);

        if (response == UndisguiseResponse.SUCCESS) {
            sender.sendMessage(ChatColor.GOLD + "The player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " has been successfully undisguised");
        } else {
            sender.sendMessage(ChatColor.RED + "Undisguise failed: " + response.toString().toLowerCase().replace('_', ' '));
        }
    }

    public void asSender(CommandSender sender){

        final Player player = (Player) sender;

        if (player.isDead()){
            sender.sendMessage(ChatColor.GOLD + "You are dead and can't be undisguised");
            return;
        }

        UndisguiseResponse response = provider.undisguise(player);

        if (response == UndisguiseResponse.SUCCESS) {
            sender.sendMessage(ChatColor.GOLD + "You have been successfully undisguised");
        } else {
            sender.sendMessage(ChatColor.RED + "Undisguise failed: " + response.toString().toLowerCase().replace('_', ' '));
        }
    }

    public void all(CommandSender sender){

        int totalPlayers = 0;
        int successfulActions = 0;


        for (Player player : Bukkit.getOnlinePlayers()) {
            totalPlayers++;

            UndisguiseResponse response;
            if (!player.isDead()){
                response = provider.undisguise(player);
            } else {
                response = UndisguiseResponse.FAIL_PROFILE_NOT_FOUND;
            }
            if (response == UndisguiseResponse.SUCCESS){
                successfulActions++;
            }
        }

        sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully Undisguised");

    }
}
