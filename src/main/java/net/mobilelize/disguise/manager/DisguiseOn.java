package net.mobilelize.disguise.manager;

import dev.iiahmed.disguise.*;
import net.mobilelize.disguise.listeners.OnPlayerRender;
import net.mobilelize.disguise.tabCompleter.DisguiseTabCompleter;
import net.mobilelize.disguise.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class DisguiseOn {

    private final DisguiseProvider provider = DisguiseManager.getProvider();
    private final DisguiseOff disguiseOff = new DisguiseOff();
    private final OnPlayerRender onPlayerRender = new OnPlayerRender();

    final String disguiseHelpMenu = ChatColor.AQUA + "Disguise Command Help Menu\n"
            + ChatColor.DARK_GRAY + "===========================\n\n"
            + ChatColor.YELLOW + "Command: "
            + ChatColor.GREEN + "/disguise <targetDisguise/player/entity/flags/commands> <TargetDisguise/entity/flags> <flags>\n\n"
            + ChatColor.YELLOW + "Description:\n"
            + ChatColor.WHITE + "The /disguise command allows you to change your appearance or the appearance of other players in the game.\n\n"
            + ChatColor.YELLOW + "Commands:\n"
            + ChatColor.AQUA + "  help: " + ChatColor.WHITE + "Show this help menu.\n"
            + ChatColor.AQUA + "  reload: " + ChatColor.WHITE + "Reload the plugin configuration.\n\n"
            + ChatColor.YELLOW + "Flags:\n"
            + ChatColor.LIGHT_PURPLE + "  -m: " + ChatColor.WHITE + "Set the disguise as an entity.\n"
            + ChatColor.LIGHT_PURPLE + "  -n: " + ChatColor.WHITE + "Name only (disguise as the name without changing the appearance).\n"
            + ChatColor.LIGHT_PURPLE + "  -s: " + ChatColor.WHITE + "Skin only (change the skin without changing the name).\n"
            + ChatColor.LIGHT_PURPLE + "  -r: " + ChatColor.WHITE + "Real name (shows the real name of the player).\n"
            + ChatColor.LIGHT_PURPLE + "  -f: " + ChatColor.WHITE + "Force (force the disguise even if it normally wouldn't apply).\n"
            + ChatColor.LIGHT_PURPLE + "  -c: " + ChatColor.WHITE + "Commands (execute disguise commands).\n\n"
            + ChatColor.YELLOW + "For more information, visit the website!\n";


    public void asPlayer(String nameOfPlayer, String targetDisguise, CommandSender sender, String[] args){

        if (Bukkit.getPlayerExact(nameOfPlayer) == null){
            sender.sendMessage(ChatColor.RED + "Disguise failed: " + DisguiseResponse.FAIL_PROFILE_NOT_FOUND.toString().toLowerCase().replace('_', ' '));
            return;
        }

        final Disguise.Builder builder = Disguise.builder();
        final Player player = Bukkit.getPlayerExact(nameOfPlayer);
        final Disguise.Builder oldDisguise = Disguise.builder().setName(player.getName()).setSkin(Skin.of(player)).setEntityType(provider.getInfo(player).getEntityType());
        boolean isDisguise = false;
        UUID uuid;
        boolean isTypeOfEntity = false;
        boolean changedOnlyName = false;
        boolean changedOnlySkin = false;

        if (player.isDead()){
            sender.sendMessage(ChatColor.GOLD + "The player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " is dead and can't be disguised");
            return;
        }

        if (ishandleCommand(args) && !Arrays.stream(args).toList().getLast().equalsIgnoreCase("-f")){
            switch (Arrays.stream(args).toList().getLast().toLowerCase()){
                case "-m" -> {
                    if (provider.isDisguised(player)){
                        builder.setSkin(Skin.of(player));
                        builder.setName(player.getName());
                    }
                    isTypeOfEntity = true;
                    final DisguiseTabCompleter tabCompleter = new DisguiseTabCompleter();
                    if (tabCompleter.entityNames.contains(targetDisguise)){
                        builder.setEntityType(EntityType.valueOf(targetDisguise.toLowerCase()));
                    } else {
                        builder.setEntityType(EntityType.BLOCK_DISPLAY);
                    }
                }
                case "-n" -> {
                    if (provider.isDisguised(player)){
                        builder.setSkin(Skin.of(player));
                    }
                    if (provider.isDisguisedAsEntity(player)) {
                        builder.setEntityType(provider.getInfo(player).getEntityType());
                    }
                    changedOnlyName = true;
                    builder.setName(targetDisguise);}
                case "-s" -> {
                    if (provider.isDisguisedAsEntity(player)) {
                        builder.setEntityType(provider.getInfo(player).getEntityType());
                    }
                    if (provider.isDisguised(player)){
                        builder.setName(player.getName());
                    }
                    changedOnlySkin =true;
                    uuid = UUIDFetcher.getUUID(targetDisguise);
                    if (uuid != null){
                        builder.setSkin(SkinAPI.MOJANG, uuid);
                    }else {
                        sender.sendMessage(ChatColor.GOLD + "Skin not Found");
                        return;
                    }}
                case "-c" -> {
                    executeCommands(nameOfPlayer, sender);
                    return;}
                case "-r" -> {
                    if (provider.isDisguised(player)){
                        sender.sendMessage(ChatColor.RED + provider.getInfo(player).getName() + ChatColor.GOLD + " is disguised as " + ChatColor.RED + player.getName());
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Player is not disguised");
                    }
                    return;}
            }
        } else {
            uuid = UUIDFetcher.getUUID(targetDisguise);
            if (uuid != null){
                builder.setSkin(SkinAPI.MOJANG, uuid);
            }
            builder.setName(targetDisguise);
        }
        if (blacklist(targetDisguise)){
            sender.sendMessage(ChatColor.GOLD + "That name is blacklisted");
            return;
        }
        if (provider.isDisguised(player)){
            isDisguise =true;
        }
        provider.undisguise(player);
        DisguiseResponse response = provider.disguise(player, builder.build());

        if (response == DisguiseResponse.SUCCESS) {
            if (isTypeOfEntity) {
                sender.sendMessage(ChatColor.RED + provider.getInfo(player).getName() + ChatColor.GOLD + " has been disguised as an " + ChatColor.RED + targetDisguise);
            } else if (changedOnlyName) {
                sender.sendMessage(ChatColor.RED + provider.getInfo(player).getName() + ChatColor.GOLD + " name is now " + ChatColor.RED + targetDisguise);
            } else if (changedOnlySkin) {
                sender.sendMessage(ChatColor.RED + provider.getInfo(player).getName() + ChatColor.GOLD + " skin is of player " + ChatColor.RED + targetDisguise);
            } else {
                sender.sendMessage(ChatColor.RED + provider.getInfo(player).getName() + ChatColor.GOLD + " has been disguised as " + ChatColor.RED + targetDisguise);
            }
        } else {
            if(isDisguise){
                provider.disguise(player, oldDisguise.build());
            }
            sender.sendMessage(ChatColor.RED + "Disguise failed: " + response.toString().toLowerCase().replace('_', ' '));
        }

        if (provider.isDisguisedAsEntity(player)){
            getServer().getScheduler().runTaskAsynchronously(net.mobilelize.disguise.Disguise.getInstance(), () -> {
                // This code will run on a separate thread
                getRenderingPlayers(player);
            });
        }
    }

    public void asSender(String targetDisguise, CommandSender sender, String[] args){

        if (Bukkit.getPlayerExact(targetDisguise) != null && Arrays.stream(args).toList().getLast().equalsIgnoreCase("-f") && args.length == 2){
            if (sender.getName().equalsIgnoreCase(args[0]) && Bukkit.getPlayerExact(args[0]).equals(sender)){
                disguiseOff.asSender(sender);
                return;
            }
            disguiseOff.asPlayer(targetDisguise, sender);
            return;
        }

        final Disguise.Builder builder = Disguise.builder();
        final Player player = (Player) sender;
        UUID uuid;
        final Disguise.Builder oldDisguise = Disguise.builder().setName(player.getName()).setSkin(Skin.of(player)).setEntityType(provider.getInfo(player).getEntityType());
        boolean isDisguised = false;
        boolean isTypeOfEntity = false;
        boolean changedOnlyName = false;
        boolean changedOnlySkin = false;

        if (player.isDead()){
            sender.sendMessage(ChatColor.GOLD + "You're dead and can't be disguised");
            return;
        }

        if (ishandleCommand(args) && !Arrays.stream(args).toList().getLast().equalsIgnoreCase("-f")){
            switch (Arrays.stream(args).toList().getLast().toLowerCase()){
                case "-m" -> {
                    if (provider.isDisguised(player)){
                        builder.setSkin(Skin.of(player));
                        builder.setName(player.getName());
                    }
                    isTypeOfEntity = true;
                    final DisguiseTabCompleter tabCompleter = new DisguiseTabCompleter();
                    if (tabCompleter.entityNames.contains(targetDisguise)){
                        builder.setEntityType(EntityType.valueOf(targetDisguise.toLowerCase()));
                    } else {
                        builder.setEntityType(EntityType.BLOCK_DISPLAY);
                    }
                }
                case "-n" -> {
                    if (provider.isDisguised(player)){
                        builder.setSkin(Skin.of(player));
                    }
                    if (provider.isDisguisedAsEntity(player)) {
                        builder.setEntityType(provider.getInfo(player).getEntityType());
                    }
                    changedOnlyName = true;
                    builder.setName(targetDisguise);}
                case "-s" -> {
                    if (provider.isDisguisedAsEntity(player)) {
                        builder.setEntityType(provider.getInfo(player).getEntityType());
                    }
                    if (provider.isDisguised(player)){
                        builder.setName(player.getName());
                    }
                    changedOnlySkin =true;
                    uuid = UUIDFetcher.getUUID(targetDisguise);
                    if (uuid != null){
                        builder.setSkin(SkinAPI.MOJANG, uuid);
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Skin not Found");
                        return;
                    }
                }
                case "-c" -> {
                    executeCommands(targetDisguise, sender);
                return;}
                case "-r" -> {
                    if (provider.isDisguised(player)){
                        sender.sendMessage(ChatColor.GOLD + "Your real name is " + ChatColor.RED + provider.getInfo(player).getName() + ChatColor.GOLD + " disguised as " + ChatColor.RED + player.getName());
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Your not disguised");
                    }
                return;}
            }
        } else {
            uuid = UUIDFetcher.getUUID(targetDisguise);
            if (uuid != null){
                builder.setSkin(SkinAPI.MOJANG, uuid);
            }
            builder.setName(targetDisguise);
        }
        if (blacklist(targetDisguise)){
            sender.sendMessage(ChatColor.GOLD + "That name is blacklisted");
            return;
        }
        if (provider.isDisguised(player)){
            isDisguised = true;
        }
        provider.undisguise(player);
        DisguiseResponse response = provider.disguise(player, builder.build());

        if (response == DisguiseResponse.SUCCESS) {
            if (isTypeOfEntity) {
                sender.sendMessage(ChatColor.GOLD + "You're now disguised as an " + ChatColor.RED + targetDisguise);
            } else if (changedOnlyName) {
                sender.sendMessage(ChatColor.GOLD + "You're name is now " + ChatColor.RED + targetDisguise);
            } else if (changedOnlySkin) {
                sender.sendMessage(ChatColor.GOLD + "You're skin is now of player " + ChatColor.RED + targetDisguise);
            } else {
                sender.sendMessage(ChatColor.GOLD + "You're now disguised as " + ChatColor.RED + targetDisguise);
            }
        } else {
            if (isDisguised){
                provider.disguise(player, oldDisguise.build());
            }
            sender.sendMessage(ChatColor.RED + "Disguise failed: " + response.toString().toLowerCase().replace('_', ' '));
        }

        if (provider.isDisguisedAsEntity(player)){
            getServer().getScheduler().runTaskAsynchronously(net.mobilelize.disguise.Disguise.getInstance(), () -> {
                // This code will run on a separate thread
                getRenderingPlayers(player);
            });
        }
    }

    public void all(String targetDisguise, CommandSender sender, String[] args){

        final Disguise.Builder builder = Disguise.builder();
        UUID uuid;
        boolean isTypeOfEntity = false;
        boolean changedOnlyName = false;
        boolean changedOnlySkin = false;
        final Disguise.Builder oldDisguise = Disguise.builder();

        if (ishandleCommand(args) && !Arrays.stream(args).toList().getLast().equalsIgnoreCase("-f")){
            switch (Arrays.stream(args).toList().getLast().toLowerCase()){
                case "-m" -> {
                    isTypeOfEntity = true;
                    builder.setEntityType(EntityType.valueOf(targetDisguise.toLowerCase()));
                }
                case "-n" -> {
                    changedOnlyName = true;
                    builder.setName(targetDisguise);}
                case "-s" -> {
                    changedOnlySkin =true;
                    uuid = UUIDFetcher.getUUID(targetDisguise);
                    if (uuid != null){
                        builder.setSkin(SkinAPI.MOJANG, uuid);
                    }else {
                        sender.sendMessage(ChatColor.GOLD + "Skin not Found");
                        return;
                    }}
                case "-c" -> {sender.sendMessage(ChatColor.GOLD + "You can't -c all");
                    return;}
                case "-r" -> {
                    // Handle the `-r` flag for listing disguised players
                    StringBuilder disguisedPlayersList = new StringBuilder();
                    int disguisedCount = 0;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (provider.isDisguised(player)) {
                            disguisedCount++;
                            disguisedPlayersList.append(ChatColor.WHITE + provider.getInfo(player).getName())
                                    .append(ChatColor.WHITE + " -> ")
                                    .append(ChatColor.GOLD + player.getName())
                                    .append(ChatColor.GOLD + ", ");
                        }
                    }

                    if (disguisedCount > 0) {
                        // Remove trailing comma and space
                        disguisedPlayersList.setLength(disguisedPlayersList.length() - 2);

                        // Send formatted message to the sender
                        sender.sendMessage(ChatColor.RED + "" + disguisedCount + ChatColor.GOLD + " players are disguised:\n" + ChatColor.WHITE + disguisedPlayersList);
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "No players are currently disguised.");
                    }
                    return;
                }
            }
        } else {
            uuid = UUIDFetcher.getUUID(targetDisguise);
            if (uuid != null){
                builder.setSkin(SkinAPI.MOJANG, uuid);
            }
            builder.setName(targetDisguise);
        }
        if (blacklist(targetDisguise)){
            sender.sendMessage(ChatColor.GOLD + "That name is blacklisted");
            return;
        }
        int totalPlayers = 0;
        int successfulActions = 0;

        if (!isTypeOfEntity && !changedOnlySkin){
            List<String> modifiedNames = generateModifiedNames(targetDisguise, Bukkit.getOnlinePlayers().size());

            for (Player player : Bukkit.getOnlinePlayers()) {
                totalPlayers++;
                DisguiseResponse response;
                boolean isDisguised = false;
                if (!player.isDead()){
                    if (provider.isDisguised(player)){
                        isDisguised = true;
                        if (changedOnlyName){
                            builder.setEntityType(provider.getInfo(player).getEntityType());
                            builder.setSkin(Skin.of(player));
                        }
                    }
                    oldDisguise.setName(player.getName()).setSkin(Skin.of(player)).setEntityType(provider.getInfo(player).getEntityType());
                    provider.undisguise(player);
                    response = provider.disguise(player, builder.setName(modifiedNames.getFirst()).build());
                    if (!response.equals(DisguiseResponse.SUCCESS) && isDisguised){
                        provider.disguise(player, oldDisguise.build());
                    }
                } else {
                    response = DisguiseResponse.FAIL_PROFILE_NOT_FOUND;
                }
                modifiedNames.removeFirst();
                if (response == DisguiseResponse.SUCCESS){
                    successfulActions++;
                }
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                totalPlayers++;
                DisguiseResponse response;
                boolean isDisguised = false;
                if (!player.isDead()){
                    if (provider.isDisguised(player)){
                        isDisguised = true;
                        if (isTypeOfEntity){
                            builder.setName(player.getName());
                            builder.setSkin(Skin.of(player));
                        }
                        if (changedOnlySkin){
                            builder.setEntityType(provider.getInfo(player).getEntityType());
                            builder.setName(player.getName());
                        }
                    }
                    oldDisguise.setName(player.getName()).setSkin(Skin.of(player)).setEntityType(provider.getInfo(player).getEntityType());
                    provider.undisguise(player);
                    response = provider.disguise(player, builder.build());
                    if (!response.equals(DisguiseResponse.SUCCESS) && isDisguised){
                        provider.disguise(player, oldDisguise.build());
                    }
                } else {
                    response = DisguiseResponse.FAIL_PROFILE_NOT_FOUND;
                }
                if (response == DisguiseResponse.SUCCESS){
                    successfulActions++;
                }
            }
        }
        if (isTypeOfEntity){
            sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully disguised as an " + ChatColor.RED + targetDisguise);
        }
        else if (changedOnlyName) {
            sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully named " + ChatColor.RED + targetDisguise);
        } else if (changedOnlySkin) {
            sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players skin's has been successfully changed to of player " + ChatColor.RED + targetDisguise);
        } else {
            sender.sendMessage(ChatColor.RED + "" +  successfulActions + ChatColor.GOLD + " out of " + ChatColor.RED + totalPlayers + ChatColor.GOLD + " players has been successfully disguised as " + ChatColor.RED + targetDisguise);
        }

        if (isTypeOfEntity){
            // This code will run on a separate thread
            getServer().getScheduler().runTaskAsynchronously(net.mobilelize.disguise.Disguise.getInstance(), this::getRenderingPlayersAll);
        }
    }

    public final List<String> generateModifiedNames(String baseName, int count) {
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

    public final Boolean ishandleCommand(String[] args) {
        return switch (Arrays.stream(args).toList().getLast().toLowerCase()) {
            case "-m", "-n", "-s", "-c", "-r", "-f" -> true;
            default -> false;
        };
    }

    public void executeCommands(String command, CommandSender sender){
        if (command.equalsIgnoreCase("help")){
            sender.sendMessage(disguiseHelpMenu);
        } else if (command.equalsIgnoreCase("reload")){
            sender.sendMessage(net.mobilelize.disguise.Disguise.getConfigManager().reloadConfig());
        } else {
            sender.sendMessage(ChatColor.GOLD + "Not a Command");
        }
    }

    private void getRenderingPlayers(Player target) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(target)) continue; // Skip the target player themselves

            // Check if the target player is within the player's view
            if (player.canSee(target)) {
                onPlayerRender.updatePlayerModel(player, target);
            }
        }
    }

    private void getRenderingPlayersAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (player.equals(target)) continue; // Skip if the player is the same as the target

                // Check if the player can see the target player
                if (player.canSee(target) && provider.isDisguisedAsEntity(target)) {
                    onPlayerRender.updatePlayerModel(player, target);
                }
            }
        }
    }

    private Boolean blacklist(String name){
        if (!net.mobilelize.disguise.Disguise.getConfigManager().isNameBlacklistEnabled()) {return false;}
        List<String> blacklisedWords = net.mobilelize.disguise.Disguise.getConfigManager().getNameBlacklist();
        for (String blacklistWord : blacklisedWords){
            if(blacklistWord.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
