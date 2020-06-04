package me.qKing12.HandyOrbs.NMS;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class NMS_v1_8_R3 implements NMS {
    Method block=null;
    Method blockByte=null;
    Field fishCatchTime=null;

    Material grass=Material.getMaterial("GRASS");
    Material sugar_cane=Material.getMaterial("SUGAR_CANE_BLOCK");
    Material crops=Material.getMaterial("CROPS");
    Material carrots=Material.CARROT;
    Material potatoes=Material.POTATO;
    Material water=Material.getMaterial("STATIONARY_WATER");
    Material nether_warts=Material.getMaterial("NETHER_WARTS");
    Material soil=Material.getMaterial("SOIL");

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
        if(b.getType().toString().contains("LOG") || b.getType().toString().contains("LEAVES")){
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
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particles), b, x, y, z, offset1, offset2, offset3, velocity, cantitate);
        for(Player p : loc.getWorld().getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void changeBlockData(Block b, byte nr){
        try {
            if(block==null)
                block = Block.class.getMethod("setData", byte.class);
            block.invoke(b, nr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeBlockData(Block b, byte nr, boolean type){
        try {
            if(blockByte==null)
                blockByte = Block.class.getMethod("setData", byte.class, boolean.class);
            blockByte.invoke(b, nr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBiteTime(FishHook hook, int time){
        net.minecraft.server.v1_8_R3.EntityFishingHook hookCopy = (EntityFishingHook) ((CraftEntity) hook).getHandle();

        if(fishCatchTime==null) {
            try {
                fishCatchTime = net.minecraft.server.v1_8_R3.EntityFishingHook.class.getDeclaredField("aw");
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
