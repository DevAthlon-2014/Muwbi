package com.muwbi.devathlon;

import com.muwbi.devathlon.entity.RidableEnderDragon;
import com.muwbi.devathlon.entity.RidableEntity;
import com.muwbi.devathlon.listener.PlayerInteractEntityListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Moritz
 */
@Getter
public class DragonRide extends JavaPlugin {

    private final Random random = new Random();

    @Getter
    private static DragonRide instance;

    private List<RidableEntity> registeredEntities = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        registeredEntities.add( new RidableEnderDragon() );

        getCommand( "rideadragon" ).setExecutor( ( sender, command, s, strings ) -> {
            if ( !( sender instanceof Player ) ) {
                sender.sendMessage( "Nur Spieler dürfen Drachen reiten!" );
                return true;
            }

            Player player = (Player) sender;

            final EnderDragon enderDragon = ( EnderDragon ) player.getWorld().spawnEntity( player.getLocation(), EntityType.ENDER_DRAGON );
            enderDragon.setPassenger( player );

            AtomicInteger ticks = new AtomicInteger( 0 );
            Bukkit.getScheduler().runTaskTimerAsynchronously( this, () -> {
                enderDragon.setVelocity( player.getVelocity().normalize() );
            }, 0, 1 );

            return true;
        } );

        Bukkit.getPluginManager().registerEvents( new PlayerInteractEntityListener(), this );
    }

}
