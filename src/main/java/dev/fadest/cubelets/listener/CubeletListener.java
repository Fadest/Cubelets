package dev.fadest.cubelets.listener;

import dev.fadest.cubelets.Cubelets;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CubeletListener implements Listener {

    private final Cubelets plugin;

    public CubeletListener(Cubelets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
            plugin.getCubeletManager().openCubelet(event.getPlayer(), event.getClickedBlock().getLocation());
        }
    }

    @EventHandler
    public void onEntityDamageByLighting(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING && event.getEntityType() == EntityType.DROPPED_ITEM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerManipulateArmorStand(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }
}
