package net.mobilelize.disguise.listeners;

import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

import java.util.Objects;
import java.util.logging.Logger;

public class OnPlayerRender implements Listener {

    private static final DisguiseProvider provider = DisguiseManager.getProvider();

    @EventHandler
    public void onPlayerTrack(PlayerTrackEntityEvent event) {
        if (event.getEntity() instanceof Player loadPlayer){
            provider.getInfo(loadPlayer).getEntityType();
            if (provider.isDisguisedAsEntity(loadPlayer))
            {
                provider.disguise(loadPlayer, Disguise.builder().setEntityType(provider.getInfo(loadPlayer).getEntityType()).build());
                event.setCancelled(true);
            }
        }
    }
}
