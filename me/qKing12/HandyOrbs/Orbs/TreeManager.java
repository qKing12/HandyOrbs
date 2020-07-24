package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.NBT.NBTItem;
import me.qKing12.HandyOrbs.PlayerData;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static me.qKing12.HandyOrbs.ConfigLoad.useLogger;

public class TreeManager implements Listener {

    public TreeManager(Main plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCrystalPlace(BlockPlaceEvent e){
        if(e.getItemInHand().hasItemMeta()) {
            if (utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.treeSpawnerOrb)) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
                    if(!e.getBlock().getType().equals(Material.AIR)){
                        e.getPlayer().sendMessage(utils.chat(Main.plugin.getConfig().getString("not-enough-space-to-place")));
                        return;
                    }
                    if(!e.getPlayer().hasPermission(Main.plugin.getConfig().getString("admin-orbs-use-permission"))){
                        e.getPlayer().sendMessage(utils.chat(Main.plugin.getConfig().getString("admin-orb-deny")));
                        return;
                    }
                    for(Entity ent : e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation(), 3, 3, 3)) {
                        if (ent.getType().equals(EntityType.ARMOR_STAND)) {
                            ArmorStand tempAM = (ArmorStand) ent;
                            if (tempAM.isSmall() && !tempAM.isVisible()) {
                                e.getPlayer().sendMessage(utils.chat(Main.plugin.getConfig().getString("other-orb-too-near")));
                                return;
                            }
                        }
                    }
                    NBTItem finalCrystal = new NBTItem(e.getItemInHand().clone());
                    finalCrystal.setString("Owner", "Server");
                    ConfigLoad.createOrb(e.getPlayer(), finalCrystal.getItem(), e.getBlock().getLocation().add(0.5, 0, 0.5));
                    e.getPlayer().getInventory().removeItem(e.getItemInHand());
                    if(useLogger)
                        utils.injectToLog(e.getPlayer().getName()+" placed an orb! (UniqueID="+finalCrystal.getString("UniqueID")+", Type="+finalCrystal.getString("HandyOrbsType")+", Location: "+e.getBlock().getX()+" "+e.getBlock().getY()+" "+e.getBlock().getZ()+")");
                }, 1);

            }
        }
    }

    @EventHandler
    public void onCrystalClick(PlayerInteractAtEntityEvent e){
        if (!Bukkit.getVersion().contains("1.8") && !e.getHand().equals(EquipmentSlot.HAND))
            return;
        if(e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)){
            ItemStack crystal = ((ArmorStand) e.getRightClicked()).getHelmet().clone();
            if(crystal.getType().equals(Material.AIR))
                return;
            NBTItem crystalNBT = new NBTItem(crystal);
            String type = crystalNBT.getString("HandyOrbsType");
            if(!type.equals("tree-spawner"))
                return;
            if(crystalNBT.getString("UniqueID").length()>5) {
                e.setCancelled(true);
                if(!e.getPlayer().hasPermission(Main.plugin.getConfig().getString("admin-orbs-use-permission"))){
                    e.getPlayer().sendMessage(utils.chat(Main.plugin.getConfig().getString("admin-orb-deny")));
                    return;
                }
                if (e.getPlayer().isSneaking()) {
                    if(useLogger) {
                        Location loc = e.getRightClicked().getLocation();
                        utils.injectToLog(e.getPlayer().getName() + " removed an orb! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                    }
                    e.getPlayer().getInventory().addItem(crystal);
                    try {
                        PlayerData.removeFromPlayerData(e.getRightClicked().getLocation().getBlock().getLocation(), crystalNBT);
                        ConfigLoad.getOrbByLocation((ArmorStand) e.getRightClicked()).unload(false);
                    }catch(Exception x){
                        ((ArmorStand) e.getRightClicked()).setBasePlate(true);
                        e.getRightClicked().remove();
                        Main.plugin.getLogger().warning("An orb is active but not in the database, forcefully removing it on player click: "+e.getPlayer().getName());
                        return;
                    }
                    e.getRightClicked().remove();
                    ConfigLoad.treeManagerType.remove(e.getRightClicked().getLocation().getBlock().getLocation());
                }
                else{
                    new OrbMenu(e.getPlayer(), (ArmorStand) e.getRightClicked(), Main.plugin);
                }
            }
        }
    }

    public static void treeSpawnerManager(ArmorStand armorStand, Orb runnable){
        ArrayList<ArrayList<BlockState>> trees = ConfigLoad.treeManagerType.get(armorStand.getLocation().getBlock().getLocation());
        if(trees==null)
            return;
        final int[] i = {0};
        int iF=trees.size();
        if(iF==0)
            return;
        runnable.setActivity(new BukkitRunnable() {
            @Override
            public void run() {
                /*if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    //ConfigLoad.crystals.remove(armorStand);
                    cancel();
                    return;
                }*/
                try {
                    i[0]++;
                    if (i[0] == iF) {
                        i[0] = 0;
                    }
                    ArrayList<BlockState> b = trees.get(i[0]);
                    Location loc = armorStand.getLocation().clone();
                    loc.setY(loc.getY() + 1.3);

                    particlePath(loc, b.get(0).getLocation(), 0, b);
                }catch(Exception x){
                    Main.plugin.getLogger().info("[DEBUG] Orb activity forcefully removed by an internal error. TreeType");
                    cancel();
                    runnable.setActivity(null);
                }
            }
        }.runTaskTimer(Main.plugin, 20*Main.plugin.getConfig().getInt("admin-orbs.tree-spawner.ability-cooldown"), 20*Main.plugin.getConfig().getInt("admin-orbs.tree-spawner.ability-cooldown")));
    }

    public static void particlePath(Location start, Location end, float procent, ArrayList<BlockState> tree){
        if(Math.floor(procent)==1) {
            String ver = Bukkit.getVersion();
            if (ver.contains("1.13") || ver.contains("1.14") || ver.contains("1.15") || ver.contains("1.16")) {
                for (BlockState block : tree) {
                    block.getBlock().setType(block.getType());
                    block.getBlock().setBlockData(block.getBlockData());
                }
            }
            else {
                try {
                    Method block2 = Block.class.getMethod("setData", byte.class);
                    for (BlockState block : tree) {
                        block.getBlock().setType(block.getType());
                        block2.invoke(block.getBlock(), (byte) block.getRawData());
                    }
                }catch(Exception x){
                    x.printStackTrace();
                }
            }
            return;
        }
        float locX = (float)start.getBlockX()+(float)(end.getBlockX()-start.getBlockX())*procent;
        float locY = (float)start.getY()+(float)(end.getY()-start.getY())*procent;
        float locZ = (float)start.getBlockZ()+(float)(end.getBlockZ()-start.getBlockZ())*procent;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () ->{
            Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, locX+(float)0.5, locY, locZ+(float)0.5, 0, 0, 0, 0, 1, start);
            particlePath(start, end, procent+(float)0.05, tree);
        }, 1);
    }

    public static void hyperActivity(ArmorStand am, Player p){
        Location loc = am.getLocation();
        String ver = Bukkit.getVersion();
        for(ArrayList<BlockState> tree : ConfigLoad.treeManagerType.get(am.getLocation().getBlock().getLocation())) {
            if (ver.contains("1.13") || ver.contains("1.14") || ver.contains("1.15") || ver.contains("1.16")) {
                for (BlockState block : tree) {
                    block.getBlock().setType(block.getType());
                    block.getBlock().setBlockData(block.getBlockData());
                }
            } else {
                try {
                    Method block2 = Block.class.getMethod("setData", byte.class);
                    for (BlockState block : tree) {
                        block.getBlock().setType(block.getType());
                        block2.invoke(block.getBlock(), (byte) block.getRawData());
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
        Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX()+(float)0.5, (float)loc.getY(), (float)loc.getZ()+(float)0.5, 1, 1, 1, 1, 700, loc);
    }
}
