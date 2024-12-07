package net.mobilelize.disguise.tabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisguiseTabCompleter implements TabCompleter {

    public final List<String> entityNames = Arrays.asList(
            "elder_guardian",
            "wither_skeleton",
            "stray",
            "husk",
            "zombie_villager",
            "skeleton_horse",
            "zombie_horse",
            "donkey",
            "mule",
            "evoker",
            "vex",
            "vindicator",
            "illusioner",
            "creeper",
            "skeleton",
            "spider",
            "giant",
            "zombie",
            "slime",
            "ghast",
            "zombified_piglin",
            "enderman",
            "cave_spider",
            "silverfish",
            "blaze",
            "magma_cube",
            "ender_dragon",
            "wither",
            "bat",
            "witch",
            "endermite",
            "guardian",
            "shulker",
            "pig",
            "sheep",
            "cow",
            "chicken",
            "squid",
            "wolf",
            "mooshroom",
            "snow_golem",
            "ocelot",
            "iron_golem",
            "horse",
            "rabbit",
            "polar_bear",
            "llama",
            "llama_spit",
            "parrot",
            "villager",
            "end_crystal",
            "turtle",
            "phantom",
            "trident",
            "cod",
            "salmon",
            "pufferfish",
            "tropical_fish",
            "drowned",
            "dolphin",
            "cat",
            "panda",
            "pillager",
            "ravager",
            "trader_llama",
            "wandering_trader",
            "fox",
            "bee",
            "hoglin",
            "piglin",
            "strider",
            "zoglin",
            "piglin_brute",
            "axolotl",
            "glow_squid",
            "goat",
            "allay",
            "frog",
            "tadpole",
            "warden",
            "camel",
            "sniffer",
            "breeze",
            "breeze_wind_charge",
            "armadillo",
            "bogged",
            "player"
    );


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase(); // Input text the user is typing

            List<String> commands = Arrays.asList("help", "reload");

            // Add the "*" option only if it matches the input
            if ("*".startsWith(input)) {
                completions.add("*");
            }

            if ("-r".startsWith(input)) {
                completions.add("-r");
            }

            for (String thisCommand : commands) {
                if (thisCommand.startsWith(input)) {
                    completions.add(thisCommand);
                }
            }

            // Filter and add entity names that match the input
            for (String entityName : entityNames) { // Assume entityNames is predefined
                if (entityName.toLowerCase().startsWith(input)) {
                    completions.add(entityName);
                }
            }

            // Filter and add player names that match the input
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String playerName = onlinePlayer.getName();
                if (playerName.toLowerCase().startsWith(input)) {
                    completions.add(playerName);
                }
            }
            return completions;
        } else if (args.length > 1) {
            List<String> completions = new ArrayList<>();
            String input = Arrays.stream(args).toList().getLast().toLowerCase(); // Input text the user is typing

            // Add the "-m" option only if it matches the input
            List<String> flags = Arrays.asList("-m", "-n", "-s", "-c", "-f", "-r");

            // Add matching flags to completions
            for (String flag : flags) {
                if (flag.startsWith(input)) {
                    completions.add(flag);
                }
            }

            if (args.length < 3){
                // Filter and add entity names that match the input
                for (String entityName : entityNames) { // Assume entityNames is predefined
                    if (entityName.toLowerCase().startsWith(input)) {
                        completions.add(entityName);
                    }
                }

                // Filter and add player names that match the input
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    String playerName = onlinePlayer.getName();
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }

}