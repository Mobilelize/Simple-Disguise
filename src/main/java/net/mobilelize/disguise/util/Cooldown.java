package net.mobilelize.disguise.util;

import net.mobilelize.disguise.Disguise;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Cooldown {

    public final boolean checkCooldown(CommandSender sender, String permissionBase, String permissionSelf) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }

        UUID playerId = player.getUniqueId();
        double currentTime = System.currentTimeMillis() / 1000.0; // Current time in seconds

        // Check if player is on cooldown
        if (Disguise.getCooldown().containsKey(playerId)) {
            double lastUse = Disguise.getCooldown().get(playerId);

            // Set cooldown duration here
            int cooldownSeconds = Disguise.getConfigManager().getCommandCooldown();
            if (currentTime - lastUse < cooldownSeconds) {
                // Check if player has the base permission (bypass cooldown)
                if (player.hasPermission(permissionBase)) {
                    return true; // Allow bypass for players with the base permission
                }

                // Check if player has the specific permission (no bypass)
                if (player.hasPermission(permissionSelf)) {
                    double timeLeft = cooldownSeconds - (currentTime - lastUse);
                    player.sendMessage(ChatColor.RED + "You must wait " + String.format("%.1f", timeLeft) + " seconds before using this command again.");
                    return false;
                }
            }
        }

        // Update cooldown using setCooldown method
        Disguise.setCooldown(playerId, currentTime);
        return true;
    }
}
