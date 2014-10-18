package com.muwbi.devathlon;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * @author Moritz
 */
@Getter
public class Tornados extends JavaPlugin {

    /**
     * Represents the plugin's random generator
     */
    private final Random random = new Random();

    /**
     * Represents the plugin instance
     */
    @Getter
    private static Tornados instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand( "tornado" ).setExecutor( ( sender, command, s, strings ) -> {
            if ( !( sender instanceof Player ) ) {
                sender.sendMessage( "Du darfst keine Tornados spawnen!" );
                return true;
            }

            Player player = (Player) sender;

            TornadoBlock.spawnTornado( player.getLocation().getDirection(), player.getLocation(), player.getLocation().subtract( 0, 1, 0 ).getBlock().getType() );

            return true;
        } );

        getServer().getPluginManager().registerEvents( new EntityChangeBlockListener(), this );
    }

}
