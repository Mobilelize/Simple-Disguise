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
import java.util.stream.Collectors;

public class tabCompleter implements TabCompleter {

    List<String> entityNames = Arrays.asList(
            "item",
            "experience_orb",
            "area_effect_cloud",
            "elder_guardian",
            "wither_skeleton",
            "stray",
            "egg",
            "leash_knot",
            "painting",
            "arrow",
            "snowball",
            "fireball",
            "small_fireball",
            "ender_pearl",
            "eye_of_ender",
            "potion",
            "experience_bottle",
            "item_frame",
            "wither_skull",
            "tnt",
            "falling_block",
            "firework_rocket",
            "husk",
            "spectral_arrow",
            "shulker_bullet",
            "dragon_fireball",
            "zombie_villager",
            "skeleton_horse",
            "zombie_horse",
            "armor_stand",
            "donkey",
            "mule",
            "evoker_fangs",
            "evoker",
            "vex",
            "vindicator",
            "illusioner",
            "command_block_minecart",
            "boat",
            "minecart",
            "chest_minecart",
            "furnace_minecart",
            "tnt_minecart",
            "hopper_minecart",
            "spawner_minecart",
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
            "glow_item_frame",
            "glow_squid",
            "goat",
            "marker",
            "allay",
            "chest_boat",
            "frog",
            "tadpole",
            "warden",
            "camel",
            "block_display",
            "interaction",
            "item_display",
            "sniffer",
            "text_display",
            "breeze",
            "wind_charge",
            "breeze_wind_charge",
            "armadillo",
            "bogged",
            "ominous_item_spawner",
            "fishing_bobber",
            "lightning_bolt",
            "player"
    );


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase(); // Input text the user is typing

            // Add the "*" option only if it matches the input
            if ("*".startsWith(input)) {
                completions.add("*");
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
            if ("-m".startsWith(input)) {
                completions.add("-m");
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
        }
        return Collections.emptyList();
    }

}