package com.muwbi.devathlon;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * @author Moritz
 */
public class EntityChangeBlockListener implements Listener {

    @EventHandler
    public void onEntityChangeBlock( EntityChangeBlockEvent event ) {
        if ( event.getEntityType() == EntityType.FALLING_BLOCK ) {
            event.setCancelled( true );
        }
    }

}
