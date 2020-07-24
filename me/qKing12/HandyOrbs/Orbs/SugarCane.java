package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SugarCane {

    public static boolean verifyWater(Block toVerify){
        Material water=Main.plugin.getNms().getWater();
        if(toVerify.getWorld().getBlockAt(toVerify.getX()+1, toVerify.getY(), toVerify.getZ()).getType().equals(water))
            return true;
        if(toVerify.getWorld().getBlockAt(toVerify.getX()-1, toVerify.getY(), toVerify.getZ()).getType().equals(water))
            return true;
        if(toVerify.getWorld().getBlockAt(toVerify.getX(), toVerify.getY(), toVerify.getZ()+1).getType().equals(water))
            return true;
        return toVerify.getWorld().getBlockAt(toVerify.getX(), toVerify.getY(), toVerify.getZ()).getType().equals(water);
    }

    public static void sugarManager(ArmorStand armorStand, Orb runnable){
        final Location finalLoc = armorStand.getLocation().getBlock().getLocation();
        if(!ConfigLoad.sugarCaneType.containsKey(finalLoc)){
            ArrayList<Location> locations = new ArrayList<>();
            World world = finalLoc.getWorld();
            int radius = Main.plugin.getConfig().getInt("permanent-orbs.sugar-cane.action-radius");
            Material water=Main.plugin.getNms().getWater();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = world.getBlockAt(finalLoc.getBlockX() + x, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z);
                        if (block.getType().equals(water)) {
                            Block b1 = world.getBlockAt(finalLoc.getBlockX() + x + 1, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z);
                            if (b1.getType().equals(Material.SAND)) {
                                if (!locations.contains(b1.getLocation()))
                                    locations.add(b1.getLocation());
                            }
                            Block b2 = world.getBlockAt(finalLoc.getBlockX() + x, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z + 1);
                            if (b2.getType().equals(Material.SAND)) {
                                if (!locations.contains(b2.getLocation()))
                                    locations.add(b2.getLocation());
                            }
                            Block b3 = world.getBlockAt(finalLoc.getBlockX() + x - 1, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z);
                            if (b3.getType().equals(Material.SAND)) {
                                if (!locations.contains(b3.getLocation()))
                                    locations.add(b3.getLocation());
                            }
                            Block b4 = world.getBlockAt(finalLoc.getBlockX() + x, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z - 1);
                            if (b4.getType().equals(Material.SAND)) {
                                if (!locations.contains(b4.getLocation()))
                                    locations.add(b4.getLocation());
                            }
                        }
                    }
                }
            }
            ConfigLoad.sugarCaneType.put(finalLoc, locations);
        }
        runnable.setActivity(new BukkitRunnable() {
            @Override
            public void run() {
                /*if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    //ConfigLoad.crystals.remove(armorStand);
                    //armorStand.remove();
                    cancel();
                    return;
                }*/
                try {
                    ArrayList<Location> locatii = new ArrayList<>();
                    if (!ConfigLoad.sugarCaneType.get(finalLoc).isEmpty()) {
                        Iterator<Location> toIterate = ConfigLoad.sugarCaneType.get(finalLoc).iterator();
                        while (toIterate.hasNext()) {
                            Location loc = toIterate.next();
                            if (!loc.getBlock().getType().equals(Material.SAND) && verifyWater(loc.getBlock()))
                                toIterate.remove();
                            else if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getType().equals(Material.AIR))
                                locatii.add(loc);
                        }
                        int marime = locatii.size();
                        Location randomLoc;
                        Location loc = armorStand.getLocation().clone();
                        loc.setY(loc.getY() + 1.3);
                        if (marime == 1) {
                            randomLoc = locatii.get(0).clone();
                            randomLoc.setY(randomLoc.getY() + 1.3);
                            particlePath3(loc, randomLoc, 0);
                        } else if (marime > 1) {
                            Random rand = new Random();
                            randomLoc = locatii.get(rand.nextInt(marime)).clone();
                            randomLoc.setY(randomLoc.getY() + 1);
                            particlePath3(loc, randomLoc, 0);
                        }
                    }
                }catch(Exception x){
                    Main.plugin.getLogger().info("[DEBUG] Orb activity forcefully removed by an internal error. SugarCane");
                    cancel();
                    runnable.setActivity(null);
                }
            }
        }.runTaskTimer(Main.plugin, 20*Main.plugin.getConfig().getInt("permanent-orbs.sugar-cane.ability-cooldown"), 20*Main.plugin.getConfig().getInt("permanent-orbs.sugar-cane.ability-cooldown")));
    }

    public static void particlePath3(Location start, Location end, float procent){
        if(Math.floor(procent)==1) {
            end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Main.plugin.getNms().getSugarCane());
            return;
        }
        float locX = (float)start.getBlockX()+(float)(end.getBlockX()-start.getBlockX())*procent;
        float locY = (float)start.getY()+(float)(end.getY()-start.getY())*procent;
        float locZ = (float)start.getBlockZ()+(float)(end.getBlockZ()-start.getBlockZ())*procent;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () ->{
            Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, locX+(float)0.5, locY, locZ+(float)0.5, 0, 0, 0, 0, 1, start);
            particlePath3(start, end, procent+(float)0.05);
        }, 1);
    }

    public static void hyperActivity(ArmorStand am, Player p){
        Location loc = am.getLocation();
        ArrayList<Location> locatii = new ArrayList<>();
        Iterator<Location> toIterate = ConfigLoad.sugarCaneType.get(am.getLocation().getBlock().getLocation()).iterator();
        while (toIterate.hasNext()) {
            Location loc2 = toIterate.next();
            if (!loc2.getBlock().getType().equals(Material.SAND) && verifyWater(loc2.getBlock()))
                toIterate.remove();
            else if (loc2.getWorld().getBlockAt(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ()).getType().equals(Material.AIR)) {
                locatii.add(loc2.clone().add(0, 1, 0));
            }
        }
        Material sugar_cane=Main.plugin.getNms().getSugarCane();
        for (Location dePlantat : locatii) {
            dePlantat.getBlock().setType(sugar_cane);
        }
        Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX()+(float)0.5, (float)loc.getY(), (float)loc.getZ()+(float)0.5, 1, 1, 1, 1, 700, loc);
    }
}
