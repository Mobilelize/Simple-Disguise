package net.mobilelize.disguise.manager;

import dev.iiahmed.disguise.*;
import net.mobilelize.disguise.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class DisguiseOn {

    private static final DisguiseProvider provider = DisguiseManager.getProvider();

    public static void asPlayer(String nameOfPlayer, String targetDisguise, CommandSender sender, String[] args){

        Disguise.Builder builder = Disguise.builder();
        Player player;
        UUID uuid;
        boolean isTypeOfEntity = false;

        player = Bukkit.getServer().getPlayerExact(nameOfPlayer);
        if (player == null){
            sender.sendMessage(ChatColor.RED + "Disguise failed: " + DisguiseResponse.FAIL_PROFILE_NOT_FOUND.toString().toLowerCase().replace('_', ' '));
            return;
        }

        if (Arrays.stream(args).toList().getLast().equalsIgnoreCase("-m")){
            isTypeOfEntity = true;
            builder.setEntityType(EntityType.valueOf(targetDisguise));
        } else {
            uuid = UUIDFetcher.getUUID(targetDisguise);
            if (uuid != null){
                builder.setSkin(SkinAPI.MOJANG, uuid);
            }
            builder.setName(targetDisguise);
        }

        DisguiseResponse response = provider.disguise(player, builder.build());

        if (response == DisguiseResponse.SUCCESS) {
            if (isTypeOfEntity) {
                sender.sendMessage(ChatColor.RED + provider.getInfo(Bukkit.getPlayerExact(args[0])).getName() + ChatColor.GOLD + " has been disguised as an " + ChatColor.RED + targetDisguise);
            } else {
                sender.sendMessage(ChatColor.RED + provider.getInfo(Bukkit.getPlayerExact(args[0])).getName() + ChatColor.GOLD + " has been disguised as " + ChatColor.RED + targetDisguise);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Disguise failed: " + response.toString().toLowerCase().replace('_', ' '));
        }
    }

    public static void asSender(String targetDisguise, CommandSender sender, String[] args){

        Disguise.Builder builder = Disguise.builder();
        Player player = (Player) sender;
        UUID uuid = null;
        boolean isTypeOfEntity = false;

        if (Arrays.stream(args).toList().getLast().equalsIgnoreCase("-m")){
            isTypeOfEntity = true;
            builder.setEntityType(EntityType.valueOf(targetDisguise));
        } else {
            uuid = UUIDFetcher.getUUID(targetDisguise);
            if (uuid != null){
                builder.setSkin(SkinAPI.MOJANG, uuid);
            }
            builder.setName(targetDisguise);
        }

        DisguiseResponse response = provider.disguise(player, builder.build());

        if (response == DisguiseResponse.SUCCESS) {
            if (isTypeOfEntity) {
                sender.sendMessage(ChatColor.GOLD + "You're now disguised as an " + ChatColor.RED + targetDisguise);
                ;
            } else {
                sender.sendMessage(ChatColor.GOLD + "You're now disguised as " + ChatColor.RED + targetDisguise);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Disguise failed: " + response.toString().toLowerCase().replace('_', ' '));
        }
    }

    public static void all(String targetDisguise, CommandSender sender, String[] args){

        Disguise.Builder builder = Disguise.builder();
        UUID uuid = null;
        boolean isTypeOfEntity = false;

        if (Arrays.stream(args).toList().getLast().equalsIgnoreCase("-m")){
            isTypeOfEntity = true;
            builder.setEntityType(EntityType.valueOf(targetDisguise));
        } else {
            uuid = UUIDFetcher.getUUID(targetDisguise);
            if (uuid != null){
                builder.setSkin(SkinAPI.MOJANG, uuid);
            }
            builder.setName(targetDisguise);
        }

        int totalPlayers = 0;
        int successfulActions = 0;

        if (!isTypeOfEntity){
            List<String> modifiedNames = generateModifiedNames(targetDisguise, Bukkit.getOnlinePlayers().size());

            for (Player player : Bukkit.getOnlinePlayers()) {
                totalPlayers++;

                DisguiseResponse response = provider.disguise(player, builder.setName(modifiedNames.getFirst()).build());
                modifiedNames.removeFirst();
                if (response == DisguiseResponse.SUCCESS){
                    successfulActions++;
                }
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                totalPlayers++;

                DisguiseResponse response = provider.disguise(player, builder.build());
                if (response == DisguiseResponse.SUCCESS){
                    successfulActions++;
                }
            }
        }
        if (isTypeOfEntity){
            sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully disguised as an " + targetDisguise);
        }
        else {
            sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully disguised as " + targetDisguise);
        }

    }

    public static List<String> generateModifiedNames(String baseName, int count) {
        // Get all online player names
        Set<String> onlinePlayerNames = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toSet());

        // Determine the cap for the random number
        int onlinePlayersCount = onlinePlayerNames.size();
        int randomCap = Math.max(200, onlinePlayersCount + 10); // Cap is 200 or players online + 10

        Random random = new Random();
        List<String> modifiedNames = new ArrayList<>();
        Set<String> usedNumbers = onlinePlayerNames.stream()
                .filter(name -> name.startsWith(baseName))
                .map(name -> name.replace(baseName, ""))
                .collect(Collectors.toSet());

        while (modifiedNames.size() < count) {
            int randomNumber = random.nextInt(randomCap) + 1; // Numbers start from 1
            String potentialName = baseName + randomNumber;

            // Add the name only if it's unique
            if (!onlinePlayerNames.contains(potentialName) && !usedNumbers.contains(String.valueOf(randomNumber))) {
                modifiedNames.add(potentialName);
                usedNumbers.add(String.valueOf(randomNumber)); // Track used numbers
            }
        }

        return modifiedNames;
    }
}
