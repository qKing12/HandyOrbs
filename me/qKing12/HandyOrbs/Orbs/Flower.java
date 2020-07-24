package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Flower implements Listener {
    private static ArrayList<String> flowers = new ArrayList<>();
    private static boolean higherVersion=true;

    public Flower(Main plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        flowers=new ArrayList<>();
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            higherVersion=false;
            flowers.add("YELLOW_FLOWER");
            flowers.add("RED_ROSE");
            flowers.add("RED_ROSE 1");
            flowers.add("RED_ROSE 2");
            flowers.add("RED_ROSE 3");
            flowers.add("RED_ROSE 4");
            flowers.add("RED_ROSE 5");
            flowers.add("RED_ROSE 6");
            flowers.add("RED_ROSE 7");
            flowers.add("RED_ROSE 8");
            flowers.add("DOUBLE_PLANT");
            flowers.add("DOUBLE_PLANT 1");
            flowers.add("DOUBLE_PLANT 4");
            flowers.add("DOUBLE_PLANT 5");
        }
        else{
            flowers.add("OXEYE_DAISY");
            flowers.add("DANDELION");
            flowers.add("POPPY");
            flowers.add("BLUE_ORCHID");
            flowers.add("ALLIUM");
            flowers.add("AZURE_BLUET");
            flowers.add("RED_TULIP");
            flowers.add("ORANGE_TULIP");
            flowers.add("WHITE_TULIP");
            flowers.add("PINK_TULIP");
            flowers.add("SUNFLOWER");
            flowers.add("ROSE_BUSH");
            flowers.add("LILAC");
            flowers.add("PEONY");
        }
    }

    public static void flowerManager(ArmorStand armorStand, Orb runnable){
        final Location finalLoc = armorStand.getLocation().getBlock().getLocation();
        if(!ConfigLoad.flowerType.containsKey(finalLoc)){
            ArrayList<Location> soluri = new ArrayList<>();
            World world = finalLoc.getWorld();
            int radius = Main.plugin.getConfig().getInt("permanent-orbs.flower.action-radius");
            Material grass=Main.plugin.getNms().getGrass();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = world.getBlockAt(finalLoc.getBlockX() + x, finalLoc.getBlockY() + y, finalLoc.getBlockZ() + z);
                        if (block.getType().equals(grass)) {
                            soluri.add(block.getLocation());
                        }
                    }
                }
            }
            ConfigLoad.flowerType.put(armorStand.getLocation().getBlock().getLocation(), soluri);
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
                    if (!ConfigLoad.flowerType.get(finalLoc).isEmpty()) {
                        Iterator<Location> toIterate = ConfigLoad.flowerType.get(finalLoc).iterator();
                        while (toIterate.hasNext()) {
                            Location loc = toIterate.next();
                            if (!loc.getBlock().getType().equals(Main.plugin.getNms().getGrass()))
                                toIterate.remove();
                            else if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getType().equals(Material.AIR))
                                locatii.add(loc);
                        }
                        int marime = locatii.size();
                        Location randomLoc;
                        Location loc = finalLoc.clone();
                        loc.setY(loc.getY() + 1.3);
                        if (marime == 1) {
                            randomLoc = locatii.get(0).clone();
                            randomLoc.setY(randomLoc.getY() + 1.3);
                            particlePath(loc, randomLoc, 0);
                        } else if (marime > 1) {
                            Random rand = new Random();
                            randomLoc = locatii.get(rand.nextInt(marime)).clone();
                            randomLoc.setY(randomLoc.getY() + 1);
                            particlePath(loc, randomLoc, 0);
                        }
                    }
                }catch(Exception x){
                    Main.plugin.getLogger().info("[DEBUG] Orb activity forcefully removed by an internal error. FlowerType");
                    cancel();
                    runnable.setActivity(null);
                }
            }
        }.runTaskTimer(Main.plugin, 20*Main.plugin.getConfig().getInt("permanent-orbs.flower.ability-cooldown"), 20*Main.plugin.getConfig().getInt("permanent-orbs.flower.ability-cooldown")));
    }

    @EventHandler
    public void onGrassBlockPut(BlockPlaceEvent e){
        if(e.getBlockPlaced().getType().equals(Main.plugin.getNms().getGrass())) {
            for(Location loc : ConfigLoad.flowerType.keySet()) {
                if(e.getBlockPlaced().getWorld().equals(loc.getWorld())) {
                    ArmorStand am = ConfigLoad.getCrystal(loc);
                    if (e.getBlockPlaced().getLocation().distance(loc) <= Main.plugin.getConfig().getInt("permanent-orbs.flower.action-radius")) {
                        ConfigLoad.flowerType.get(am).add(e.getBlockPlaced().getLocation());
                    }
                }
            }
        }
    }

    public static void particlePath(Location start, Location end, float procent){
        if(Math.floor(procent)==1) {
            Random r = new Random();
            if(!higherVersion) {
                String[] material = flowers.get(r.nextInt(flowers.size())).split(" ");
                end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Material.getMaterial(material[0]));
                if (material[0].equals("DOUBLE_PLANT") || material[0].equals("LEGACY_DOUBLE_PLANT")) {
                    end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY() + 1, end.getBlockZ()).setType(Material.getMaterial(material[0]));
                    Main.plugin.getNms().changeBlockData(end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY() + 1, end.getBlockZ()), (byte) 10, false);
                }
                if (material.length == 2) {
                    Main.plugin.getNms().changeBlockData(end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()), Byte.valueOf(material[1]), false);
                }
            }
            else{
                int index=r.nextInt(flowers.size());
                String material = flowers.get(index);
                if(index>9) {
                    Block flowerBlockLower = end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ());
                    Block flowerBlockHigher = flowerBlockLower.getRelative(BlockFace.UP);
                    flowerBlockLower.setType(Material.getMaterial(material), false);
                    flowerBlockHigher.setType(Material.getMaterial(material), false);

                    Main.plugin.getNms().changeBlockData(flowerBlockLower, (byte)0, false);
                    Main.plugin.getNms().changeBlockData(flowerBlockHigher, (byte)1, false);
                }
                else{
                    end.getWorld().getBlockAt(end.getBlockX(), end.getBlockY(), end.getBlockZ()).setType(Material.getMaterial(material));
                }
            }
            return;
        }
        float locX = (float)start.getBlockX()+(float)(end.getBlockX()-start.getBlockX())*procent;
        float locY = (float)start.getY()+(float)(end.getY()-start.getY())*procent;
        float locZ = (float)start.getBlockZ()+(float)(end.getBlockZ()-start.getBlockZ())*procent;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () ->{
            Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, locX+(float)0.5, locY, locZ+(float)0.5, 0, 0, 0, 0, 1, start);
            particlePath(start, end, procent+(float)0.05);
        }, 1);
    }

    public static void hyperActivity(ArmorStand am, Player p){
        Location loc = am.getLocation();
        ArrayList<Location> locatii = new ArrayList<>();
        Iterator<Location> toIterate = ConfigLoad.flowerType.get(am.getLocation().getBlock().getLocation()).iterator();
        while (toIterate.hasNext()) {
            Location loc2 = toIterate.next();
            if (!loc2.getBlock().getType().equals(Main.plugin.getNms().getGrass()))
                toIterate.remove();
            else if (loc2.getWorld().getBlockAt(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ()).getType().equals(Material.AIR)) {
                locatii.add(loc2.clone().add(0, 1, 0));
            }
        }
        try {
            if(higherVersion) {
                for (Location dePlantat : locatii) {
                    Random r = new Random();
                    int index=r.nextInt(flowers.size());
                    String material = flowers.get(index);
                    if(index>9) {
                        Block flowerBlockLower = dePlantat.getBlock();
                        Block flowerBlockHigher = flowerBlockLower.getRelative(BlockFace.UP);
                        flowerBlockLower.setType(Material.getMaterial(material), false);
                        flowerBlockHigher.setType(Material.getMaterial(material), false);

                        Main.plugin.getNms().changeBlockData(flowerBlockLower, (byte)0, false);
                        Main.plugin.getNms().changeBlockData(flowerBlockHigher, (byte)1, false);
                    }
                    else{
                        dePlantat.getBlock().setType(Material.getMaterial(material));
                    }
                }
            }
            else{
                for (Location dePlantat : locatii) {
                    Random r = new Random();
                    String[] material = flowers.get(r.nextInt(flowers.size())).split(" ");
                    dePlantat.getBlock().setType(Material.getMaterial(material[0]));
                    if (material[0].equals("DOUBLE_PLANT") || material[0].equals("LEGACY_DOUBLE_PLANT")) {
                        Location temp = dePlantat.clone();
                        temp.add(0, 1, 0);
                        temp.getBlock().setType(Material.getMaterial(material[0]));
                        Main.plugin.getNms().changeBlockData(temp.getBlock(), (byte) 10, false);
                    }
                    if (material.length == 2) {
                        Main.plugin.getNms().changeBlockData(dePlantat.getBlock(), Byte.valueOf(material[1]), false);
                    }
                }
            }
        }catch(Exception x){
            x.printStackTrace();
        }
        Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX()+(float)0.5, (float)loc.getY(), (float)loc.getZ()+(float)0.5, 1, 1, 1, 1, 700, loc);
    }
}
