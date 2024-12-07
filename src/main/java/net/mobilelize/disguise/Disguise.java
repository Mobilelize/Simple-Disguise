package net.mobilelize.disguise;

import dev.iiahmed.disguise.DisguiseManager;
import net.mobilelize.disguise.commands.DisguiseCommand;
import net.mobilelize.disguise.commands.NameCommand;
import net.mobilelize.disguise.commands.SkinCommand;
import net.mobilelize.disguise.listeners.OnPlayerKick;
import net.mobilelize.disguise.listeners.OnPlayerRender;
import net.mobilelize.disguise.manager.ConfigManager;
import net.mobilelize.disguise.tabCompleter.DisguiseTabCompleter;
import net.mobilelize.disguise.tabCompleter.NameTabCompleter;
import net.mobilelize.disguise.tabCompleter.SkinTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Disguise extends JavaPlugin {

    private static ConfigManager configManager;

    private static Disguise instance;

    private static final HashMap<UUID, Double> cooldown = new HashMap<>();

    public static Disguise getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        // Save the default config if it doesn't exist
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        // Check the configuration and log a recommendation if needed
        if (!configManager.isCheckRealUsername() && configManager.isAllowDuplicateLogin()) {
            getLogger().warning("It is recommended to enable 'check-real-username' to prevent conflicts and abuse.");
        }

        DisguiseManager.initialize(this, true);

        // Register the command
        this.getCommand("disguise").setExecutor(new DisguiseCommand());
        this.getCommand("disguise").setTabCompleter(new DisguiseTabCompleter());

        if (configManager.isAllowSkinCommand()){
            this.getCommand("skin").setExecutor(new SkinCommand());
            this.getCommand("skin").setTabCompleter(new SkinTabCompleter());
        }

        if (configManager.isAllowNameCommand()){
            this.getCommand("name").setExecutor(new NameCommand());
            this.getCommand("name").setTabCompleter(new NameTabCompleter());
        }


        this.getServer().getPluginManager().registerEvents(new OnPlayerRender(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerKick(), this);


        getLogger().info("Simple Disguise Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Simple Disguise Plugin has been disabled!");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static HashMap<UUID, Double> getCooldown(){return cooldown;}

    public static void setCooldown(UUID playerId, Double currentTime) {
        Disguise.cooldown.put(playerId, currentTime);
    }
}
