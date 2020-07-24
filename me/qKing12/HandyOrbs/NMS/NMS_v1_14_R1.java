package me.qKing12.HandyOrbs.NMS;

import net.minecraft.server.v1_14_R1.EntityFishingHook;
import net.minecraft.server.v1_14_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_14_R1.ParticleType;
import net.minecraft.server.v1_14_R1.Particles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class NMS_v1_14_R1 implements NMS {
    Field fishCatchTime=null;

    Material grass=Material.GRASS_BLOCK;
    Material sugar_cane=Material.SUGAR_CANE;
    Material crops=Material.WHEAT;
    Material carrots=Material.CARROTS;
    Material potatoes=Material.POTATOES;
    Material beetroot = Material.BEETROOTS;
    Material water=Material.WATER;
    Material nether_warts=Material.NETHER_WART;
    Material soil=Material.FARMLAND;

    public Material getGrass(){
        return grass;
    }

    public Material getSugarCane(){
        return sugar_cane;
    }

    public Material getCrops(){
        return crops;
    }

    public Material getCarrots() { return carrots; }

    public Material getPotatoes() { return potatoes; }

    public Material getBeetroot() { return beetroot; }

    public Material getWater(){
        return water;
    }

    public Material getNetherWarts(){
        return nether_warts;
    }

    public Material getSoil(){
        return soil;
    }

    public void treeSaver(ArrayList<BlockState> blocks, Location loc){
        Block b = loc.getBlock();
        if(b.getType().toString().contains("WOOD") || b.getType().toString().contains("LOG") || b.getType().toString().contains("LEAVES")){
            BlockState bState = b.getState();
            bState.update();
            blocks.add(bState);
            loc.getBlock().setType(Material.AIR);
            treeSaver(blocks, loc.clone().add(1, 0, 0));
            treeSaver(blocks, loc.clone().add(-1, 0, 0));
            treeSaver(blocks, loc.clone().add(0, 1, 0));
            treeSaver(blocks, loc.clone().add(0, -1, 0));
            treeSaver(blocks, loc.clone().add(0, 0, 1));
            treeSaver(blocks, loc.clone().add(0, 0, -1));
        }
    }

    @Override
    public void sendParticle(String particles, boolean b, float x, float y, float z, float offset1, float offset2, float offset3, int velocity, int cantitate, Location loc){
        ParticleType particule;
        if(particles.equalsIgnoreCase("FIREWORKS_SPARK"))
            particule=Particles.FIREWORK;
        else if(particles.equalsIgnoreCase("VILLAGER_HAPPY"))
            particule=Particles.HAPPY_VILLAGER;
        else if(particles.equalsIgnoreCase("VILLAGER_ANGRY"))
            particule=Particles.ANGRY_VILLAGER;
        else
            particule=Particles.FLAME;
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particule, b, x, y, z, offset1, offset2, offset3, velocity, cantitate);
        for(Player p : loc.getWorld().getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void changeBlockData(Block b, byte nr){
        try {
            BlockState blockState = b.getState();
            blockState.setRawData(nr);
            blockState.update(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeBlockData(Block b, byte nr, boolean type){
        Bisected data = (Bisected) b.getBlockData();
        if(nr==(byte)0)
            data.setHalf(Bisected.Half.BOTTOM);
        else
            data.setHalf(Bisected.Half.TOP);

        b.setBlockData(data);
    }

    @Override
    public void setBiteTime(FishHook hook, int time){
        net.minecraft.server.v1_14_R1.EntityFishingHook hookCopy = (EntityFishingHook) ((CraftEntity) hook).getHandle();

        if(fishCatchTime==null) {
            try {
                fishCatchTime = net.minecraft.server.v1_14_R1.EntityFishingHook.class.getDeclaredField("as");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        fishCatchTime.setAccessible(true);

        try {
            fishCatchTime.setInt(hookCopy, time);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        fishCatchTime.setAccessible(false);
    }
}
