package net.mobilelize.disguise.listeners;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OnPlayerRender implements Listener {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    @EventHandler
    public void onPlayerTrack(PlayerTrackEntityEvent event) {
        if (event.getEntity() instanceof Player loadPlayer){
            if (provider.isDisguisedAsEntity(loadPlayer))
            {
                provider.refreshAsEntity(loadPlayer, false, event.getPlayer());
                updatePlayerModel(event.getPlayer(), loadPlayer);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event){
        if (event.getRightClicked() instanceof Player player){
            if (provider.isDisguisedAsEntity(player) && provider.getInfo(player).getEntityType() == EntityType.ALLAY){
                updatePlayerModel(event.getPlayer(), player);
                event.setCancelled(true);
            }
        }
    }

    final public void updatePlayerModel(Player player, Player loadPlayer){
        // Resend main hand and offhand items
        player.sendEquipmentChange(loadPlayer, EquipmentSlot.HAND, loadPlayer.getInventory().getItemInMainHand());
        player.sendEquipmentChange(loadPlayer, EquipmentSlot.OFF_HAND, loadPlayer.getInventory().getItemInOffHand());

        // Resend armor pieces
        player.sendEquipmentChange(loadPlayer, EquipmentSlot.HEAD, loadPlayer.getInventory().getHelmet());
        player.sendEquipmentChange(loadPlayer, EquipmentSlot.CHEST, loadPlayer.getInventory().getChestplate());
        player.sendEquipmentChange(loadPlayer, EquipmentSlot.LEGS, loadPlayer.getInventory().getLeggings());
        player.sendEquipmentChange(loadPlayer, EquipmentSlot.FEET, loadPlayer.getInventory().getBoots());

        //Resend Size value doesn't work any other way for some reason
        final double lowestValue = .000000000000001;
        final double baseValue = loadPlayer.getAttribute(Attribute.GENERIC_SCALE).getValue();

        //To make sure the value is set for some reason
        loadPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(baseValue + lowestValue);
        loadPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(baseValue - lowestValue);
    }
}
