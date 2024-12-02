package net.mobilelize.disguise.listeners;

import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPlayerRender implements Listener {

    private static final DisguiseProvider provider = DisguiseManager.getProvider();

    @EventHandler
    public void onPlayerTrack(PlayerTrackEntityEvent event) {
        if (event.getEntity() instanceof Player loadPlayer){
            if (provider.isDisguisedAsEntity(loadPlayer))
            {
                provider.disguise(loadPlayer, Disguise.builder().setEntityType(provider.getInfo(loadPlayer).getEntityType()).build());
                event.setCancelled(true);
            }
        }
    }
}
