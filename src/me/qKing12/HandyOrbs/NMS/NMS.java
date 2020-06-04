package me.qKing12.HandyOrbs.NMS;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FishHook;

import java.util.ArrayList;


public interface NMS {

    Material getGrass();

    Material getNetherWarts();

    Material getCrops();

    Material getCarrots();

    Material getPotatoes();

    Material getSugarCane();

    Material getWater();

    Material getSoil();

    void treeSaver(ArrayList<BlockState> blocks, Location loc);

    void sendParticle(String particles, boolean b, float x, float y, float z, float offset1, float offset2, float offset3, int velocity, int cantitate, Location loc);

    void changeBlockData(Block b, byte nr);

    void changeBlockData(Block b, byte nr, boolean type);

    void setBiteTime(FishHook hook, int time);
}
