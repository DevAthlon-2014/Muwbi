package com.muwbi.devathlon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Moritz
 */
public class TornadoBlock {

    /**
     * The maximum amount of blocks per tornado
     */
    private static final int MAX_BLOCKS = 500;

    /**
     * The FallingBlock entity
     */
    private Entity entity;

    /**
     * The current horizontal distance between each block
     */
    private float horizontalTick = (float) ( Math.random() * 2 * Math.PI );
    /**
     * The current vertical distance between each block
     */
    private float verticalTick = 0f;

    /**
     * Construct a new TornadoBlock object
     *
     * @param location The location where the block should be spawned
     * @param material The default material for the tornado
     */
    public TornadoBlock( Location location, Material material ) {
        if ( location.getBlock().getType() != Material.AIR && location.getBlock().getType().isSolid() ) {
            Block block = location.getBlock();
            entity = location.getWorld().spawnFallingBlock( location, block.getType(), block.getData() );
        } else {
            entity = location.getWorld().spawnFallingBlock( location, material, (byte) 0 );
        }

        ( (FallingBlock) entity ).setDropItem( false );
    }

    /**
     * Called when the tornado is being ticked
     *
     * @return A HashSet containing all TornadoBlock objects
     */
    public HashSet<TornadoBlock> tick() {
        double radius = Math.sin( getVerticalDistance() ) * 2;
        float horizontal = getHorizontalDistance();

        // Construct a vector with the new horizontal and vertical distances
        Vector vector = new Vector( radius * Math.cos( horizontal ), 0.5, radius * Math.sin( horizontal ) );

        HashSet<TornadoBlock> newBlocks = new HashSet<>();

        // Add a new block to the list of new blocks
        Block block = entity.getLocation().add( vector.clone().normalize() ).getBlock();
        if ( block.getType() != Material.AIR ) {
            newBlocks.add( new TornadoBlock( block.getLocation(), block.getType() ) );
        }

        // Set the FallingBlock entity's velocity
        entity.setVelocity( vector );

        return newBlocks;
    }

    /**
     * Calculates and returns the current horizontal distance
     *
     * @return The horizontal distance between each block
     */
    private float getHorizontalDistance() {
        return horizontalTick += 0.8;
    }

    /**
     * Calculates and returns the current vertical distance
     *
     * @return The vertical distance between each block
     */
    private float getVerticalDistance() {
        if ( verticalTick < 1 ) {
            verticalTick += 0.05;
        }
        return verticalTick;
    }

    /**
     * Removes the entity
     */
    public void remove() {
        entity.remove();
    }

    /**
     * Spawns a tornado consisting out of TornadoBlocks
     *
     * @param direction The direction where the tornado should move towards
     * @param location  The initial location where the tornado should spawn
     * @param material  The default material for the tornado
     */
    public static void spawnTornado( Vector direction, Location location, Material material ) {
        // Normalize the given direction, apply speed and set y to 0
        direction.normalize().multiply( 3 ).setY( 0 );

        HashSet<TornadoBlock> totalBlocks = new HashSet<>();

        new BukkitRunnable() {
            private List<TornadoBlock> blocks = new ArrayList<>();

            @Override
            public void run() {
                location.add( direction );

                // Spawn 10 new TornadoBlocks
                for ( int i = 0; i < 10; i++ ) {
                    checkList();
                    TornadoBlock tornadoBlock = new TornadoBlock( location, material );
                    blocks.add( tornadoBlock );
                    totalBlocks.add( tornadoBlock );
                }

                List<TornadoBlock> spinningBlocks = new ArrayList<>();

                // Add each TornadoBlock's spinning blocks
                for ( TornadoBlock tornadoBlock : blocks ) {
                    HashSet<TornadoBlock> newBlocks = tornadoBlock.tick();
                    spinningBlocks.addAll( newBlocks.stream().collect( Collectors.toList() ) );
                }

                // Add all new TornadoBlocks to the list of total blocks
                for ( TornadoBlock tornadoBlock : spinningBlocks ) {
                    checkList();
                    blocks.add( tornadoBlock );
                    totalBlocks.add( tornadoBlock );
                }
            }

            private void checkList() {
                // Determine whether the amount of TornadoBlocks is higher than the maximum allowed number
                while ( blocks.size() >= MAX_BLOCKS ) {
                    TornadoBlock tornadoBlock = blocks.get( 0 );
                    tornadoBlock.remove();
                    blocks.remove( tornadoBlock );
                    totalBlocks.remove( tornadoBlock );
                }
            }

        }.runTaskTimer( Tornados.getInstance(), 5, 5 );

        new BukkitRunnable() {

            @Override
            public void run() {
                // Remove all TornadoBlocks of a tornado
                for ( TornadoBlock tornadoBlock : totalBlocks ) {
                    tornadoBlock.remove();
                }
            }

        }.runTaskLater( Tornados.getInstance(), 200 );
    }

}
