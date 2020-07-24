package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.NBT.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Farming implements Listener {

    public Farming(Main plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void farmingManager(ArmorStand armorStand, Orb runnable){
        final Location finalLoc = armorStand.getLocation().getBlock().getLocation();
        if(!ConfigLoad.farmingType.containsKey(finalLoc)){
            ArrayList<Location> soluri = new ArrayList<>();
            World world = finalLoc.getWorld();
            int radius = Main.plugin.getConfig().getInt("permanent-orbs.farmer.action-radius");
            Material soil=Main.plugin.getNms().getSoil();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = world.getBlockAt(finalLoc.getBlockX() + x, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z);
                        if (block.getType().equals(soil)) {
                            soluri.add(block.getLocation());
                        }
                    }
                }
            }
            ConfigLoad.farmingType.put(armorStand.getLocation().getBlock().getLocation(), soluri);
        }
        runnable.setActivity(new BukkitRunnable() {
            @Override
            public void run() {
                /*if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    //ConfigLoad.crystals.remove(armorStand);
                    //armorStand.remove();
                    cancel();
                    return;
                }
                else if(armorStand.hasArms()){
                    cancel();
                    return;
                }*/
                try {
                    ArrayList<Location> locatii = new ArrayList<>();
                    if (!ConfigLoad.farmingType.get(finalLoc).isEmpty()) {
                        Iterator<Location> toIterate = ConfigLoad.farmingType.get(finalLoc).iterator();
                        while (toIterate.hasNext()) {
                            Location loc = toIterate.next();
                            if (!loc.getBlock().getType().equals(Main.plugin.getNms().getSoil()))
                                toIterate.remove();
                            else if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getType().equals(Material.AIR))
                                locatii.add(loc);
                        }
                        int marime = locatii.size();
                        Location randomLoc;
                        Location loc = armorStand.getLocation().clone();
                        loc.setY(loc.getY() + 1.3);
                        boolean instantGrowth = false;
                        if (new NBTItem(armorStand.getHelmet()).getString("Owner").equals("Server"))
                            instantGrowth = true;
                        if (marime == 1) {
                            randomLoc = locatii.get(0).clone();
                            randomLoc.setY(randomLoc.getY() + 1.3);
                            particlePath(loc, randomLoc, 0, new NBTItem(armorStand.getHelmet()).getString("HandyOrbsFarmType"), instantGrowth);
                        } else if (marime > 1) {
                            Random rand = new Random();
                            randomLoc = locatii.get(rand.nextInt(marime)).clone();
                            randomLoc.setY(randomLoc.getY() + 1);
                            particlePath(loc, randomLoc, 0, new NBTItem(armorStand.getHelmet()).getString("HandyOrbsFarmType"), instantGrowth);
                        }
                    }
                }catch(Exception x){
                    Main.plugin.getLogger().info("[DEBUG] Orb activity forcefully removed by an internal error. FarmingType");
                    cancel();
                    runnable.setActivity(null);
                }
            }
        }.runTaskTimer(Main.plugin, 20*Main.plugin.getConfig().getInt("permanent-orbs.farmer.ability-cooldown"), 20*Main.plugin.getConfig().getInt("permanent-orbs.farmer.ability-cooldown")));
    }

    @EventHandler
    public void onHoe(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material m = e.getClickedBlock().getType();
            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
                if(e.getClickedBlock().getType().equals(Main.plugin.getNms().getSoil()) && !m.equals(Main.plugin.getNms().getSoil())) {
                    for(Location loc : ConfigLoad.farmingType.keySet())
                        if(e.getClickedBlock().getWorld().equals(loc.getWorld())) {
                            if (e.getClickedBlock().getLocation().distance(loc) <= Main.plugin.getConfig().getInt("permanent-orbs.farmer.action-radius")) {
                                ConfigLoad.farmingType.get(loc).add(e.getClickedBlock().getLocation());
                            }
                        }
                }
            }, 2);
        }
    }

    public static void particlePath(Location start, Location end, float procent, String farm, boolean instantGrowth){
        if(Math.floor(procent)==1) {
            if(ConfigLoad.needsLight && end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY()+1, end.getBlockZ()).getLightLevel()<ConfigLoad.minimumLight){
                Main.plugin.getNms().sendParticle("VILLAGER_ANGRY", true, end.getBlockX()+(float)0.5, end.getBlockY(), end.getBlockZ()+(float)0.5, 0, 0, 0, 0, 3, end);
                return;
            }

            switch(farm) {
                case "wheat":
                    end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Main.plugin.getNms().getCrops());
                    break;
                case "carrots":
                    end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Main.plugin.getNms().getCarrots());
                    break;
                case "beetroot":
                    end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Main.plugin.getNms().getBeetroot());
                    if(instantGrowth){
                        Main.plugin.getNms().changeBlockData(end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()), (byte)3);
                    }
                    return;
                case "potatoes":
                    end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Main.plugin.getNms().getPotatoes());
                    break;
            }
            if(instantGrowth){
                Main.plugin.getNms().changeBlockData(end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()), (byte)7);
            }
            return;
        }
        float locX = (float)start.getBlockX()+(float)(end.getBlockX()-start.getBlockX())*procent;
        float locY = (float)start.getY()+(float)(end.getY()-start.getY())*procent;
        float locZ = (float)start.getBlockZ()+(float)(end.getBlockZ()-start.getBlockZ())*procent;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () ->{
            Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, locX+(float)0.5, locY, locZ+(float)0.5, 0, 0, 0, 0, 1, start);
            particlePath(start, end, procent+(float)0.05, farm, instantGrowth);
        }, 1);
    }

    public static void hyperActivity(ArmorStand am, Player p){
        Location loc = am.getLocation();
        Material plantat;
        String type = new NBTItem(am.getHelmet()).getString("HandyOrbsFarmType");
        switch(type) {
            case "wheat":
                plantat=Main.plugin.getNms().getCrops();
                break;
            case "carrots":
                plantat=Main.plugin.getNms().getCarrots();
                break;
            case "beetroot":
                plantat=Main.plugin.getNms().getBeetroot();
                break;
            default:
                plantat=Main.plugin.getNms().getPotatoes();
                break;
        }
        ArrayList<Location> locatii = new ArrayList<>();
        Iterator<Location> toIterate = ConfigLoad.farmingType.get(am.getLocation().getBlock().getLocation()).iterator();
        while (toIterate.hasNext()) {
            Location loc2 = toIterate.next();
            if (!loc2.getBlock().getType().equals(Main.plugin.getNms().getSoil()))
                toIterate.remove();
            else if (loc2.getWorld().getBlockAt(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ()).getType().equals(Material.AIR) && (!ConfigLoad.needsLight || loc2.getWorld().getBlockAt(loc2.getBlockX(), loc2.getBlockY() + 2, loc2.getBlockZ()).getLightLevel()>=ConfigLoad.minimumLight)) {
                locatii.add(loc2.clone().add(0, 1, 0));
            }
        }
        for(Location dePlantat : locatii)
            dePlantat.getBlock().setType(plantat);
        if(new NBTItem(am.getHelmet()).getString("Owner").equals("Server")){
            try {
                if(type.equals("beetroot")){
                    for (Location deCrescut : locatii)
                        Main.plugin.getNms().changeBlockData(deCrescut.getBlock(), (byte) 3);
                }
                else {
                    for (Location deCrescut : locatii)
                        Main.plugin.getNms().changeBlockData(deCrescut.getBlock(), (byte) 7);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX()+(float)0.5, (float)loc.getY(), (float)loc.getZ()+(float)0.5, 1, 1, 1, 1, 700, loc);
    }
}
