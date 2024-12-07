package net.mobilelize.disguise.listeners;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.DisguiseResponse;
import net.mobilelize.disguise.Disguise;
import net.mobilelize.disguise.manager.DisguiseOn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class OnPlayerKick implements Listener {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    private final HashMap<String, UUID> playerHashMap = new HashMap<>();

    @EventHandler
    public void PreLogin(AsyncPlayerPreLoginEvent event){
        playerHashMap.put(event.getName(), event.getUniqueId());
    }

    @EventHandler
    public void Login(PlayerLoginEvent event){
        // Schedule the next part of the task on the main server thread
        getServer().getScheduler().runTask(net.mobilelize.disguise.Disguise.getInstance(), () -> {
            try {
                Thread.sleep(Disguise.getConfigManager().getDelayAmount() + 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            playerHashMap.remove(event.getPlayer().getName());
        });
    }


    @EventHandler
    public void PlayerKickEvent(PlayerKickEvent event) {
        if (event.getCause().equals(PlayerKickEvent.Cause.DUPLICATE_LOGIN) && Disguise.getConfigManager().isAllowDuplicateLogin()){
            if (!Disguise.getConfigManager().isCheckRealUsername() || !event.getPlayer().getName().equals(provider.getInfo(event.getPlayer()).getName())){
                switch (Disguise.getConfigManager().getDuplicateLoginAction()){
                    case "none":
                        break;
                    case "disguise-joining-player":
                        getServer().getScheduler().runTaskAsynchronously(net.mobilelize.disguise.Disguise.getInstance(), () -> {
                            // This code will run on a separate thread
                            try {
                                Thread.sleep(Disguise.getConfigManager().getDelayAmount()); // Simulate a delay (1 second)
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            // Schedule the next part of the task on the main server thread
                            getServer().getScheduler().runTask(net.mobilelize.disguise.Disguise.getInstance(), () -> {
                                Player player = Bukkit.getPlayer(playerHashMap.get(event.getPlayer().getName()));
                                if (player != null) {
                                    disguiseJoiningPlayer(player);
                                }
                                playerHashMap.remove(event.getPlayer().getName());
                            });
                        });
                        break;
                    case "undisguise-existing-player":
                        provider.undisguise(event.getPlayer());
                        break;
                }
                event.setCancelled(true);
            }
        }
    }

    private void disguiseJoiningPlayer(Player player){
        if (!Disguise.getConfigManager().isRandomNameEnabled()){
            return;
        }

        if (player.isDead()){
            Bukkit.getConsoleSender().sendMessage( "Player " + player.getName() + " disguised failed");
            return;
        }

        DisguiseOn disguiseOn = new DisguiseOn();

        if (!Disguise.getConfigManager().isForceUseRandomNameList() && player.getName().length() <= Disguise.getConfigManager().getMaxModifiableLength()){

            List<String> names = disguiseOn.generateModifiedNames(player.getName(), 1);
            DisguiseResponse response;

            for (String name : names) {
                response = provider.disguise(player, dev.iiahmed.disguise.Disguise.builder().setName(name).build());
                if (response == DisguiseResponse.SUCCESS) {
                    return;
                }
            }
        }

        List<String> shuffledNames = new ArrayList<>(Disguise.getConfigManager().getRandomNameList());
        Collections.shuffle(shuffledNames);

        for (String name : shuffledNames) {
            // Attempt to use the name in the generator
            List<String> generatedNames = disguiseOn.generateModifiedNames(name, 1);

            for (String generatedName : generatedNames) {
                // Try to disguise the player with the generated name
                DisguiseResponse response = provider.disguise(
                        player,
                        dev.iiahmed.disguise.Disguise.builder().setName(generatedName).build()
                );

                if (response == DisguiseResponse.SUCCESS) {
                    return;
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage( "Player " + player.getName() + " disguised failed");
    }
}
