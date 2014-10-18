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

    private static final int MAX_BLOCKS = 500;

    private Entity entity;

    private float horizontalTick = (float) ( Math.random() * 2 * Math.PI );
    private float verticalTick = 0f;

    public TornadoBlock( Location location, Material material ) {
        if ( location.getBlock().getType() != Material.AIR && location.getBlock().getType().isSolid() ) {
            Block block = location.getBlock();
            entity = location.getWorld().spawnFallingBlock( location, block.getType(), block.getData() );
        } else {
            entity = location.getWorld().spawnFallingBlock( location, material, (byte) 0 );
        }

        ((FallingBlock)entity).setDropItem( false );
    }

    public HashSet<TornadoBlock> tick() {
        double radius = Math.sin( getVerticalDistance() ) * 2;
        float horizontal = getHorizontalDistance();

        Vector vector = new Vector( radius * Math.cos( horizontal ), 0.5, radius * Math.sin( horizontal ) );

        HashSet<TornadoBlock> newBlocks = new HashSet<>();

        Block block = entity.getLocation().add( vector.clone().normalize() ).getBlock();
        if ( block.getType() != Material.AIR ) {
            newBlocks.add( new TornadoBlock( block.getLocation(), block.getType() ) );
        }

        entity.setVelocity( vector );

        return newBlocks;
    }

    private float getHorizontalDistance() {
        return horizontalTick += 0.8;
    }

    private float getVerticalDistance() {
        if ( verticalTick < 1 ) {
            verticalTick += 0.05;
        }
        return verticalTick;
    }

    public void remove() {
        entity.remove();
    }

    public static void spawnTornado( Vector direction, Location location, Material material ) {
        direction.normalize().multiply( 3 ).setY( 0 );

        HashSet<TornadoBlock> totalBlocks = new HashSet<>();

        new BukkitRunnable() {
            private List<TornadoBlock> blocks = new ArrayList<>();

            @Override
            public void run() {
                location.add( direction );

                for ( int i = 0; i < 10; i++ ) {
                    checkList();
                    TornadoBlock tornadoBlock = new TornadoBlock( location, material );
                    blocks.add( tornadoBlock );
                    totalBlocks.add( tornadoBlock );
                }

                List<TornadoBlock> spinningBlocks = new ArrayList<>();

                for ( TornadoBlock tornadoBlock : blocks ) {
                    HashSet<TornadoBlock> newBlocks = tornadoBlock.tick();
                    spinningBlocks.addAll( newBlocks.stream().collect( Collectors.toList() ) );
                }

                for ( TornadoBlock tornadoBlock : spinningBlocks ) {
                    checkList();
                    blocks.add( tornadoBlock );
                    totalBlocks.add( tornadoBlock );
                }
            }

            private void checkList() {
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
                for ( TornadoBlock tornadoBlock : totalBlocks ) {
                    tornadoBlock.remove();
                }
            }

        }.runTaskLater( Tornados.getInstance(), 200 );
    }

}
