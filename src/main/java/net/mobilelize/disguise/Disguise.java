package net.mobilelize.disguise;

import dev.iiahmed.disguise.DisguiseManager;
import net.mobilelize.disguise.commands.DisguiseCommand;
import net.mobilelize.disguise.listeners.OnPlayerRender;
import net.mobilelize.disguise.tabCompleter.tabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Disguise extends JavaPlugin {

    @Override
    public void onEnable() {

        DisguiseManager.initialize(this, true);

        // Register the command
        this.getCommand("disguise").setExecutor(new DisguiseCommand(this));
        this.getCommand("disguise").setTabCompleter(new tabCompleter());

        this.getServer().getPluginManager().registerEvents(new OnPlayerRender(), this);


        getLogger().info("DisguisePlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DisguisePlugin has been disabled!");
    }
}
