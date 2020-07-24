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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NetherWart implements Listener {

    public NetherWart(Main plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void wartManager(ArmorStand armorStand, Orb runnable){
        final Location finalLoc = armorStand.getLocation().getBlock().getLocation();
        if(!ConfigLoad.netherWartType.containsKey(finalLoc)){
            ArrayList<Location> soluri = new ArrayList<>();
            World world = finalLoc.getWorld();
            int radius = Main.plugin.getConfig().getInt("permanent-orbs.nether-wart.action-radius");
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = world.getBlockAt(finalLoc.getBlockX() + x, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z);
                        if (block.getType().equals(Material.SOUL_SAND)) {
                            soluri.add(block.getLocation());
                        }
                    }
                }
            }
            ConfigLoad.netherWartType.put(armorStand.getLocation().getBlock().getLocation(), soluri);
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
                    if (!ConfigLoad.netherWartType.get(finalLoc).isEmpty()) {
                        Iterator<Location> toIterate = ConfigLoad.netherWartType.get(finalLoc).iterator();
                        while (toIterate.hasNext()) {
                            Location loc = toIterate.next();
                            if (!loc.getBlock().getType().equals(Material.SOUL_SAND))
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
                            particlePath2(loc, randomLoc, 0);
                        } else if (marime > 1) {
                            Random rand = new Random();
                            randomLoc = locatii.get(rand.nextInt(marime)).clone();
                            randomLoc.setY(randomLoc.getY() + 1);
                            particlePath2(loc, randomLoc, 0);
                        }
                    }
                }catch(Exception x){
                    Main.plugin.getLogger().info("[DEBUG] Orb activity forcefully removed by an internal error. NetherType");
                    cancel();
                    runnable.setActivity(null);
                }
            }
        }.runTaskTimer(Main.plugin, 20*Main.plugin.getConfig().getInt("permanent-orbs.nether-wart.ability-cooldown"), 20*Main.plugin.getConfig().getInt("permanent-orbs.nether-wart.ability-cooldown")));
    }

    @EventHandler
    public void onSoulSandPut(BlockPlaceEvent e){
        if(e.getBlockPlaced().getType().equals(Material.SOUL_SAND)) {
            for(Location loc : ConfigLoad.netherWartType.keySet()) {
                if(e.getBlockPlaced().getWorld().equals(loc.getWorld())) {
                    if (e.getBlockPlaced().getLocation().distance(loc) <= Main.plugin.getConfig().getInt("permanent-orbs.nether-wart.action-radius")) {
                        ConfigLoad.netherWartType.get(loc).add(e.getBlockPlaced().getLocation());
                    }
                }
            }
        }
    }

    public static void particlePath2(Location start, Location end, float procent){
        if(Math.floor(procent)==1) {
            end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Main.plugin.getNms().getNetherWarts());
            Main.plugin.getNms().changeBlockData(end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()), (byte) 3);
            return;
        }
        float locX = (float)start.getBlockX()+(float)(end.getBlockX()-start.getBlockX())*procent;
        float locY = (float)start.getY()+(float)(end.getY()-start.getY())*procent;
        float locZ = (float)start.getBlockZ()+(float)(end.getBlockZ()-start.getBlockZ())*procent;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () ->{
                Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, locX+(float)0.5, locY, locZ+(float)0.5, 0, 0, 0, 0, 1, start);
            particlePath2(start, end, procent+(float)0.05);
        }, 1);
    }

    public static void hyperActivity(ArmorStand am, Player p){
        Location loc = am.getLocation();
        ArrayList<Location> locatii = new ArrayList<>();
        Iterator<Location> toIterate = ConfigLoad.netherWartType.get(am.getLocation().getBlock().getLocation()).iterator();
        while (toIterate.hasNext()) {
            Location loc2 = toIterate.next();
            if (!loc2.getBlock().getType().equals(Material.SOUL_SAND))
                toIterate.remove();
            else if (loc2.getWorld().getBlockAt(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ()).getType().equals(Material.AIR)) {
                locatii.add(loc2.clone().add(0, 1, 0));
            }
        }
        try {
            Material nether_wart=Main.plugin.getNms().getNetherWarts();
            for (Location dePlantat : locatii) {
                dePlantat.getBlock().setType(nether_wart);
                Main.plugin.getNms().changeBlockData(dePlantat.getBlock(), (byte) 3);
            }
        }catch(Exception x){
            x.printStackTrace();
        }
        Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX()+(float)0.5, (float)loc.getY(), (float)loc.getZ()+(float)0.5, 1, 1, 1, 1, 700, loc);
    }
}
