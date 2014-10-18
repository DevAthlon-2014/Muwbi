package com.muwbi.devathlon.listener;

import com.muwbi.devathlon.DragonRide;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author Moritz
 */
public class PlayerInteractEntityListener implements Listener {

    public void onPlayerInteractEntity( PlayerInteractEntityEvent event ) {
        DragonRide.getInstance().getRegisteredEntities().forEach(
            ridableEntity -> ridableEntity.onInteract( event )
        );
    }

}
