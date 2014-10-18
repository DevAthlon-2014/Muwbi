package com.muwbi.devathlon.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author Moritz
 */
public class RidableEnderDragon extends RidableEntity {

    @Override
    public void onInteract( PlayerInteractEntityEvent event ) {
        if ( event.getRightClicked().getType() == EntityType.ENDER_DRAGON ) {
            event.getRightClicked().setPassenger( event.getPlayer() );
        }
    }

}
