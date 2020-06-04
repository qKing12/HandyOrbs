package me.qKing12.HandyOrbs;

import me.qKing12.HandyOrbs.NBT.NBTItem;
import me.qKing12.HandyOrbs.Orbs.*;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class ConfigLoad implements Listener {

    private static Main plugin;

    public static ArrayList<Orb> orbs = new ArrayList<>();

    public static ArrayList<Location> fishingType = new ArrayList<>();
    public static HashMap<Location, ArrayList<Location>> farmingType = new HashMap<>();
    public static HashMap<Location, ArrayList<Location>> netherWartType = new HashMap<>();
    public static HashMap<Location, ArrayList<Location>> sugarCaneType = new HashMap<>();
    public static HashMap<Location, ArrayList<Location>> flowerType = new HashMap<>();

    public static HashMap<Location, ArrayList<ArrayList<BlockState>>> treeManagerType = new HashMap<>();

    public static Inventory adminInventory;

    public static ItemStack skullItem;
    public static ItemStack farmingWheatOrb;
    public static ItemStack farmingCarrotsOrb;
    public static ItemStack farmingPotatoesOrb;
    public static ItemStack fishingOrb;
    public static ItemStack netherWartOrb;
    public static ItemStack sugarCaneOrb;
    public static ItemStack flowerOrb;
    public static ItemStack rainbowOrb;

    public static ItemStack radiantOrb;
    public static ItemStack savingGraceOrb;

    public static ItemStack treeSpawnerOrb;

    public static ItemStack backgroundItem;
    public static ItemStack editOrbTitleItem;
    public static ItemStack resetOrbTitleItem;
    public static ItemStack toggleTitleVisibilityHiddenItem;
    public static ItemStack toggleTitleVisibilityShownItem;
    public static ItemStack pickupOrbItem;
    public static ItemStack makeServerOrbItem;
    public static ItemStack hyperActivityItem;
    public static ItemStack closeMenuItem;
    public static ItemStack goBackItem;
    public static ItemStack paperAnvilItem;

    public static ItemStack nextPageItem;
    public static ItemStack previousPageItem;

    public static boolean isReloading=false;
    public static boolean useLogger=true;
    public static boolean orbListener=true;

    public static Orb getOrbByLocation(ArmorStand am){
        for(Orb orb : orbs)
            if(orb.compareArmorStand(am))
                return orb;
        return null;
    }

    public int hasReachedLimit(String type, Player p){
        String player = p.getUniqueId().toString();
        int nr;
        if(type.equals("wheat"))
            nr = PlayerData.getTotalFarmingWheatOrbs(player);
        else if(type.equals("carrots"))
            nr = PlayerData.getTotalFarmingCarrotsOrbs(player);
        else if(type.equals("potatoes"))
            nr = PlayerData.getTotalFarmingPotatoesOrbs(player);
        else if(type.equals("nether-wart"))
            nr = PlayerData.getTotalNetherWartOrbs(player);
        else if(type.equals("sugar-cane"))
            nr = PlayerData.getTotalSugarCaneOrbs(player);
        else if(type.equals("fishing"))
            nr = PlayerData.getTotalFishingOrbs(player);
        else if(type.equals("flower"))
            nr = PlayerData.getTotalFlowerOrbs(player);
        else
            nr = PlayerData.getTotalRainbowOrbs(player);
        if(nr==7){
            p.sendMessage(utils.chat(plugin.getConfig().getString("orb-limit-reached-message")));
            if(p.hasPermission(plugin.getConfig().getString("admin-orbs-use-permission"))){
                p.sendMessage(utils.chat(plugin.getConfig().getString("extra-admin-limit-reach-message")));
                return 2;
            }
            else
                return 0;
        }
        else{
            if(p.hasPermission(plugin.getConfig().getString("admin-orbs-use-permission")))
                return 1;
            else if(p.hasPermission("handyorbs.limit."+type+"."+nr)){
                p.sendMessage(utils.chat(plugin.getConfig().getString("orb-limit-reached-message")));
                return 0;
            }
            else
                return 1;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getSlot()<0 || e.getWhoClicked().getInventory().firstEmpty()==-1)
            return;
        String title=e.getWhoClicked().getOpenInventory().getTitle();
        if(e.getInventory().equals(adminInventory)){
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if(e.getSlot()==49)
                e.getWhoClicked().closeInventory();
            else if(e.getSlot()==19)
                p.getInventory().addItem(utils.makeUnique(farmingWheatOrb));
            else if(e.getSlot()==28)
                p.getInventory().addItem(utils.makeUnique(farmingCarrotsOrb));
            else if(e.getSlot()==37)
                p.getInventory().addItem(utils.makeUnique(farmingPotatoesOrb));
            else if(e.getSlot()==20)
                p.getInventory().addItem(utils.makeUnique(netherWartOrb));
            else if(e.getSlot()==29)
                p.getInventory().addItem(utils.makeUnique(sugarCaneOrb));
            else if(e.getSlot()==38)
                p.getInventory().addItem(utils.makeUnique(fishingOrb));
            else if(e.getSlot()==21)
                p.getInventory().addItem(utils.makeUnique(flowerOrb));
            else if(e.getSlot()==30)
                p.getInventory().addItem(utils.makeUnique(rainbowOrb));
            else if(e.getSlot()==23)
                p.getInventory().addItem(utils.makeUnique(radiantOrb));
            else if(e.getSlot()==32)
                p.getInventory().addItem(utils.makeUnique(savingGraceOrb));
            else if(e.getSlot()==25)
                p.getInventory().addItem(utils.makeUnique(treeSpawnerOrb));
        }
        else if(title.startsWith(utils.chat(Main.orbManageCfg.getString("own-orbs-menu-title")))){
            e.setCancelled(true);
            if(e.getClickedInventory().getSize()==54){
                if(e.getSlot()==49){
                    e.getWhoClicked().closeInventory();
                    return;
                }
                NBTItem nbt=new NBTItem(e.getCurrentItem());
                if(nbt.getString("Orbs").length()>2){
                    boolean canTeleport=false;
                    if(e.getWhoClicked().hasPermission(plugin.getConfig().getString("teleport-other-orb-permission")))
                        canTeleport=true;
                    else if(Bukkit.getPlayer(UUID.fromString(nbt.getString("Orbs"))).equals((Player) e.getWhoClicked()) && e.getWhoClicked().hasPermission("teleport-own-orb-permission"))
                        canTeleport=true;
                    PlayerData.ownOrbsLocation((Player) e.getWhoClicked(), nbt.getString("Orbs"), nbt.getString("Type"), canTeleport, 0);
                }
            }
            else{
                if(e.getSlot()==22){
                    PlayerData.ownOrbsMenu((Player)e.getWhoClicked(), new NBTItem(e.getInventory().getItem(0)).getString("pData"));
                    return;
                }
                if(e.getCurrentItem()==null || !e.getCurrentItem().hasItemMeta() || e.getSlot()<10 || e.getSlot()>16) {
                    if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta()){
                        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(nextPageItem.getItemMeta().getDisplayName())) {
                            NBTItem nbt=new NBTItem(e.getClickedInventory().getItem(0));
                            boolean canTeleport=false;
                            if(e.getWhoClicked().hasPermission(plugin.getConfig().getString("teleport-other-orb-permission")))
                                canTeleport=true;
                            else if(Bukkit.getPlayer(UUID.fromString(nbt.getString("pData"))).equals((Player) e.getWhoClicked()) && e.getWhoClicked().hasPermission("teleport-own-orb-permission"))
                                canTeleport=true;
                            PlayerData.ownOrbsLocation((Player) e.getWhoClicked(), nbt.getString("pData"), nbt.getString("Type"), canTeleport, e.getCurrentItem().getAmount());
                        }
                        else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(previousPageItem.getItemMeta().getDisplayName())){
                            NBTItem nbt=new NBTItem(e.getClickedInventory().getItem(0));
                            boolean canTeleport=false;
                            if(e.getWhoClicked().hasPermission(plugin.getConfig().getString("teleport-other-orb-permission")))
                                canTeleport=true;
                            else if(Bukkit.getPlayer(UUID.fromString(nbt.getString("pData"))).equals((Player) e.getWhoClicked()) && e.getWhoClicked().hasPermission("teleport-own-orb-permission"))
                                canTeleport=true;
                            PlayerData.ownOrbsLocation((Player) e.getWhoClicked(), nbt.getString("pData"), nbt.getString("Type"), canTeleport, e.getCurrentItem().getAmount()-1);
                        }
                    }
                    return;
                }
                String locS = new NBTItem(e.getCurrentItem()).getString("Location");
                if(locS.length()>2){
                    try {
                        Player p = (Player) e.getWhoClicked();
                        Location loc = utils.locationFromBase64(locS);
                        p.teleport(loc);
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(SavingGrace.placedDown.containsKey(e.getPlayer().getName())) {
            e.getPlayer().getInventory().addItem(utils.makeUnique(savingGraceOrb));
            SavingGrace.placedDown.remove(e.getPlayer().getName());
        }
        else if(Main.ungivenSavingGrace.contains(e.getPlayer().getName())){
            e.getPlayer().getInventory().addItem(utils.makeUnique(savingGraceOrb));
            Main.ungivenSavingGrace.remove(e.getPlayer().getName());
        }
        /*if(Bukkit.getServer().getOnlinePlayers().size()==1) {
            Bukkit.getScheduler().cancelTasks(plugin);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for(Location loc : ConfigLoad.crystals){
                    ArmorStand am=ConfigLoad.getCrystal(loc);
                    if(am!=null) {
                        am.setArms(true);
                        if(orbListener)
                            checkOffOrb(am);
                    }
                }
                checkPlayers();
            }, 20);
        }*/
        if(Bukkit.getServer().getOnlinePlayers().size()==1)
            checkPlayers();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCrystalPlace(BlockPlaceEvent e){
        if(e.isCancelled())
            return;
        if(e.getItemInHand().getType().equals(ConfigLoad.skullItem.getType()) && e.getItemInHand().hasItemMeta()) {
            if (utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.farmingPotatoesOrb) || utils.itemsAreEqual(ConfigLoad.farmingWheatOrb, e.getItemInHand()) || utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.farmingCarrotsOrb) || utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.fishingOrb) || utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.netherWartOrb) || utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.sugarCaneOrb) || utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.rainbowOrb) || utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.flowerOrb)) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(!e.getBlock().getType().equals(Material.AIR)){
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("not-enough-space-to-place")));
                        return;
                    }
                    if(!"none".equals(plugin.getConfig().getString("permanent-orbs."+new NBTItem(e.getItemInHand()).getString("HandyOrbsType")+".permission")) && !e.getPlayer().hasPermission(plugin.getConfig().getString("permanent-orbs."+new NBTItem(e.getItemInHand()).getString("HandyOrbsType")+".permission"))){
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("permanent-orbs."+new NBTItem(e.getItemInHand()).getString("HandyOrbsType")+".no-permission-message")));
                        return;
                    }
                    for(Entity ent : e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation(), 3, 3, 3)) {
                        if (ent.getType().equals(EntityType.ARMOR_STAND)) {
                            ArmorStand tempAM = (ArmorStand) ent;
                            if (tempAM.isSmall() && !tempAM.isVisible()) {
                                e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("other-orb-too-near")));
                                return;
                            }
                        }
                    }
                    NBTItem finalCrystal = new NBTItem(e.getItemInHand().clone());
                    int arePermission;
                    if(finalCrystal.getString("HandyOrbsType").equals("farmer"))
                        arePermission = hasReachedLimit(finalCrystal.getString("HandyOrbsFarmType"), e.getPlayer());
                    else
                        arePermission = hasReachedLimit(finalCrystal.getString("HandyOrbsType"), e.getPlayer());
                    if(arePermission==0)
                        return;
                    if(arePermission==2) {
                        finalCrystal.setString("Owner", "Server");
                    }
                    else
                        finalCrystal.setString("Owner", e.getPlayer().getUniqueId().toString());
                    createOrb(e.getPlayer(), finalCrystal.getItem(), e.getBlock().getLocation().add(0.5, 0, 0.5));
                    e.getPlayer().getInventory().removeItem(e.getItemInHand());
                    if(useLogger)
                        utils.injectToLog(e.getPlayer().getName()+" placed an orb! (UniqueID="+finalCrystal.getString("UniqueID")+", Type="+finalCrystal.getString("HandyOrbsType")+", Location: "+e.getBlock().getX()+" "+e.getBlock().getY()+" "+e.getBlock().getZ()+")");
                }, 1);

            }
        }
    }

    @EventHandler
    public void onCrystalClick(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)){
            ItemStack crystal = ((ArmorStand) e.getRightClicked()).getHelmet().clone();
            if(crystal.getType().equals(Material.AIR))
                return;
            NBTItem crystalNBT = new NBTItem(crystal);
            String type = crystalNBT.getString("HandyOrbsType");
            if(type.equals("radiant-orb") || type.equals("saving-grace-orb") || type.equals("tree-spawner")) {
                e.setCancelled(true);
                return;
            }
            if(crystalNBT.getString("UniqueID").length()>5) {
                e.setCancelled(true);
                if(!crystalNBT.getString("Owner").equals(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().hasPermission(plugin.getConfig().getString("bypass-claim-permission"))){
                    e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("can-not-claim-message")));
                    return;
                }
                if (e.getPlayer().isSneaking()) {
                    Orb orb = getOrbByLocation((ArmorStand) e.getRightClicked());
                    try {
                        orb.unload();
                    }catch(Exception x){
                        x.printStackTrace();
                    }
                    e.getRightClicked().remove();
                    e.getPlayer().getInventory().addItem(crystal);
                    if(useLogger) {
                        Location loc = e.getRightClicked().getLocation();
                        utils.injectToLog(e.getPlayer().getName() + " removed an orb! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                    }
                }
                else{
                    new OrbMenu(e.getPlayer(), (ArmorStand) e.getRightClicked(), plugin);
                }
            }
        }
    }

    public static ArmorStand getCrystal(Location loc){
        for(Entity ent : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
            if (ent.getType().equals(EntityType.ARMOR_STAND)) {
                ArmorStand am = (ArmorStand) ent;
                if(am.isSmall() && !am.isVisible() && am.hasArms())
                    return (ArmorStand) ent;
            }
        }
        return null;
    }

    public static void checkPlayers(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Bukkit.getServer().getOnlinePlayers().size()==0){
                    Bukkit.getScheduler().cancelTasks(plugin);
                    for(Orb orb : ConfigLoad.orbs)
                        orb.disable();
                    return;
                }
                for(Orb orb : ConfigLoad.orbs){
                    orb.update();
                }
            }
        }.runTaskTimer(plugin, 60, 60);
    }

    //3
    /*public static void goUp(ArmorStand armorStand){
        Location loc=armorStand.getLocation();
        loc.setY(Math.floor(loc.getY()));
        double height=loc.getY()+0.95;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    Location loc=armorStand.getLocation().getBlock().getLocation();
                    NBTItem crystalNBT = new NBTItem(armorStand.getHelmet());
                    String type=crystalNBT.getString("HandyOrbsType");
                    cancel();
                    if(crystals.contains(loc)){
                        ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                        am.setHelmet(armorStand.getHelmet());
                        am.setCustomName(armorStand.getCustomName());
                        am.setCustomNameVisible(armorStand.isCustomNameVisible());
                        am.setVisible(false);
                        am.setSmall(true);
                        am.setGravity(false);
                        am.setBasePlate(false);
                        enableOrb(am);
                        return;
                    }
                    else {
                        armorStand.remove();
                        if (type.equals("farmer")) {
                            String farmType = crystalNBT.getString("HandyOrbsFarmType");
                            if (farmType.equals("wheat")) {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.farmingWheatOwnOrbs.get(p).remove(loc);
                                if (PlayerData.farmingWheatOwnOrbs.get(p).isEmpty())
                                    PlayerData.farmingWheatOwnOrbs.remove(p);
                            } else if (farmType.equals("carrots")) {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.farmingCarrotsOwnOrbs.get(p).remove(loc);
                                if (PlayerData.farmingCarrotsOwnOrbs.get(p).isEmpty())
                                    PlayerData.farmingCarrotsOwnOrbs.remove(p);
                            } else {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.farmingPotatoesOwnOrbs.get(p).remove(loc);
                                if (PlayerData.farmingPotatoesOwnOrbs.get(p).isEmpty())
                                    PlayerData.farmingPotatoesOwnOrbs.remove(p);
                            }
                            ConfigLoad.farmingType.remove(loc);
                        } else if (type.equals("fishing")) {
                            String p = crystalNBT.getString("Owner");
                            ConfigLoad.fishingType.remove(loc);
                            PlayerData.fishingOwnOrbs.get(crystalNBT.getString("Owner")).remove(loc);
                            if (PlayerData.fishingOwnOrbs.get(p).isEmpty())
                                PlayerData.fishingOwnOrbs.remove(p);
                        } else if (type.equals("nether-wart")) {
                            ConfigLoad.netherWartType.remove(loc);
                            String p = crystalNBT.getString("Owner");
                            PlayerData.netherWartOwnOrbs.get(p).remove(loc);
                            if (PlayerData.netherWartOwnOrbs.get(p).isEmpty())
                                PlayerData.netherWartOwnOrbs.remove(p);
                        } else if (type.equals("sugar-cane")) {
                            ConfigLoad.sugarCaneType.remove(loc);
                            String p = crystalNBT.getString("Owner");
                            PlayerData.sugarCaneOwnOrbs.get(p).remove(loc);
                            if (PlayerData.sugarCaneOwnOrbs.get(p).isEmpty())
                                PlayerData.sugarCaneOwnOrbs.remove(p);
                        } else if (type.equals("flower")) {
                            ConfigLoad.flowerType.remove(loc);
                            String p = crystalNBT.getString("Owner");
                            PlayerData.flowerOwnOrbs.get(p).remove(loc);
                            if (PlayerData.flowerOwnOrbs.get(p).isEmpty())
                                PlayerData.flowerOwnOrbs.remove(p);
                        } else if (type.equals("rainbow")) {
                            String p = crystalNBT.getString("Owner");
                            PlayerData.rainbowOwnOrbs.get(p).remove(loc);
                            if (PlayerData.rainbowOwnOrbs.get(p).isEmpty())
                                PlayerData.rainbowOwnOrbs.remove(p);
                        }
                        return;
                    }
                }
                else if(armorStand.hasArms()){
                    cancel();
                    if(orbListener)
                        checkOffOrb(armorStand);
                    armorStand.setRemainingAir(3);
                    return;
                }
                if(armorStand.getLocation().getY()>=height){
                    cancel();
                    goDown(armorStand);
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc.add(0, 0.07, 0));
                plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX(), (float)loc.getY()+(float)1.3, (float)loc.getZ(), (float)0.5, 0, (float)0.5, 0, 1, loc);

            }
        }.runTaskTimer(plugin, 1, 1);
    }

    //2
    public static void goDown(ArmorStand armorStand){
        Location loc=armorStand.getLocation();
        double height=loc.getBlockY()+0.02;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    Location loc=armorStand.getLocation().getBlock().getLocation();
                    NBTItem crystalNBT = new NBTItem(armorStand.getHelmet());
                    String type=crystalNBT.getString("HandyOrbsType");
                    cancel();
                    if(crystals.contains(loc)){
                        ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                        am.setHelmet(armorStand.getHelmet());
                        am.setCustomName(armorStand.getCustomName());
                        am.setCustomNameVisible(armorStand.isCustomNameVisible());
                        am.setVisible(false);
                        am.setSmall(true);
                        am.setGravity(false);
                        am.setBasePlate(false);
                        enableOrb(am);
                        return;
                    }
                    else {
                        armorStand.remove();
                        if (type.equals("farmer")) {
                            String farmType = crystalNBT.getString("HandyOrbsFarmType");
                            if (farmType.equals("wheat")) {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.farmingWheatOwnOrbs.get(p).remove(loc);
                                if (PlayerData.farmingWheatOwnOrbs.get(p).isEmpty())
                                    PlayerData.farmingWheatOwnOrbs.remove(p);
                            } else if (farmType.equals("carrots")) {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.farmingCarrotsOwnOrbs.get(p).remove(loc);
                                if (PlayerData.farmingCarrotsOwnOrbs.get(p).isEmpty())
                                    PlayerData.farmingCarrotsOwnOrbs.remove(p);
                            } else {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.farmingPotatoesOwnOrbs.get(p).remove(loc);
                                if (PlayerData.farmingPotatoesOwnOrbs.get(p).isEmpty())
                                    PlayerData.farmingPotatoesOwnOrbs.remove(p);
                            }
                            ConfigLoad.farmingType.remove(loc);
                        } else if (type.equals("fishing")) {
                            String p = crystalNBT.getString("Owner");
                            ConfigLoad.fishingType.remove(loc);
                            PlayerData.fishingOwnOrbs.get(crystalNBT.getString("Owner")).remove(loc);
                            if (PlayerData.fishingOwnOrbs.get(p).isEmpty())
                                PlayerData.fishingOwnOrbs.remove(p);
                        } else if (type.equals("nether-wart")) {
                            ConfigLoad.netherWartType.remove(loc);
                            String p = crystalNBT.getString("Owner");
                            PlayerData.netherWartOwnOrbs.get(p).remove(loc);
                            if (PlayerData.netherWartOwnOrbs.get(p).isEmpty())
                                PlayerData.netherWartOwnOrbs.remove(p);
                        } else if (type.equals("sugar-cane")) {
                            ConfigLoad.sugarCaneType.remove(loc);
                            String p = crystalNBT.getString("Owner");
                            PlayerData.sugarCaneOwnOrbs.get(p).remove(loc);
                            if (PlayerData.sugarCaneOwnOrbs.get(p).isEmpty())
                                PlayerData.sugarCaneOwnOrbs.remove(p);
                        } else if (type.equals("flower")) {
                            ConfigLoad.flowerType.remove(loc);
                            String p = crystalNBT.getString("Owner");
                            PlayerData.flowerOwnOrbs.get(p).remove(loc);
                            if (PlayerData.flowerOwnOrbs.get(p).isEmpty())
                                PlayerData.flowerOwnOrbs.remove(p);
                        } else if (type.equals("rainbow")) {
                            String p = crystalNBT.getString("Owner");
                            PlayerData.rainbowOwnOrbs.get(p).remove(loc);
                            if (PlayerData.rainbowOwnOrbs.get(p).isEmpty())
                                PlayerData.rainbowOwnOrbs.remove(p);
                        }
                        return;
                    }
                }
                else if(armorStand.hasArms()){
                    cancel();
                    if(orbListener)
                        checkOffOrb(armorStand);
                    armorStand.setRemainingAir(2);
                    return;
                }
                if(armorStand.getLocation().getY()<=height){
                    cancel();
                    goUp(armorStand);
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc.add(0, -0.07, 0));
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    public static void checkOffOrb(ArmorStand armorStand){
        Location loc=armorStand.getLocation().getBlock().getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!crystals.contains(loc)){
                    NBTItem crystalNBT = new NBTItem(armorStand.getHelmet());
                    String type=crystalNBT.getString("HandyOrbsType");
                    try {
                        switch (type) {
                            case "farmer":
                                String farmType = crystalNBT.getString("HandyOrbsFarmType");
                                if (farmType.equals("wheat")) {
                                    String p = crystalNBT.getString("Owner");
                                    PlayerData.farmingWheatOwnOrbs.get(p).remove(loc);
                                    if (PlayerData.farmingWheatOwnOrbs.get(p).isEmpty())
                                        PlayerData.farmingWheatOwnOrbs.remove(p);
                                } else if (farmType.equals("carrots")) {
                                    String p = crystalNBT.getString("Owner");
                                    PlayerData.farmingCarrotsOwnOrbs.get(p).remove(loc);
                                    if (PlayerData.farmingCarrotsOwnOrbs.get(p).isEmpty())
                                        PlayerData.farmingCarrotsOwnOrbs.remove(p);
                                } else {
                                    String p = crystalNBT.getString("Owner");
                                    PlayerData.farmingPotatoesOwnOrbs.get(p).remove(loc);
                                    if (PlayerData.farmingPotatoesOwnOrbs.get(p).isEmpty())
                                        PlayerData.farmingPotatoesOwnOrbs.remove(p);
                                }
                                ConfigLoad.farmingType.remove(loc);
                                break;
                            case "fishing": {
                                String p = crystalNBT.getString("Owner");
                                ConfigLoad.fishingType.remove(loc);
                                PlayerData.fishingOwnOrbs.get(crystalNBT.getString("Owner")).remove(loc);
                                if (PlayerData.fishingOwnOrbs.get(p).isEmpty())
                                    PlayerData.fishingOwnOrbs.remove(p);
                                break;
                            }
                            case "nether-wart": {
                                ConfigLoad.netherWartType.remove(loc);
                                String p = crystalNBT.getString("Owner");
                                PlayerData.netherWartOwnOrbs.get(p).remove(loc);
                                if (PlayerData.netherWartOwnOrbs.get(p).isEmpty())
                                    PlayerData.netherWartOwnOrbs.remove(p);
                                break;
                            }
                            case "sugar-cane": {
                                ConfigLoad.sugarCaneType.remove(loc);
                                String p = crystalNBT.getString("Owner");
                                PlayerData.sugarCaneOwnOrbs.get(p).remove(loc);
                                if (PlayerData.sugarCaneOwnOrbs.get(p).isEmpty())
                                    PlayerData.sugarCaneOwnOrbs.remove(p);
                                break;
                            }
                            case "flower": {
                                ConfigLoad.flowerType.remove(loc);
                                String p = crystalNBT.getString("Owner");
                                PlayerData.flowerOwnOrbs.get(p).remove(loc);
                                if (PlayerData.flowerOwnOrbs.get(p).isEmpty())
                                    PlayerData.flowerOwnOrbs.remove(p);
                                break;
                            }
                            case "rainbow": {
                                String p = crystalNBT.getString("Owner");
                                PlayerData.rainbowOwnOrbs.get(p).remove(loc);
                                if (PlayerData.rainbowOwnOrbs.get(p).isEmpty())
                                    PlayerData.rainbowOwnOrbs.remove(p);
                                break;
                            }
                        }
                    }catch(Exception x){

                    }
                    cancel();
                }
                else {
                    if (!armorStand.getLocation().getChunk().isLoaded() || !armorStand.hasArms()) {
                        cancel();
                        return;
                    }
                    if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                        ArmorStand am = (ArmorStand) armorStand.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                        am.setHelmet(armorStand.getHelmet());
                        am.setCustomName(armorStand.getCustomName());
                        am.setCustomNameVisible(armorStand.isCustomNameVisible());
                        am.setVisible(false);
                        am.setSmall(true);
                        am.setArms(true);
                        am.setGravity(false);
                        am.setBasePlate(false);
                        cancel();
                        checkOffOrb(am);
                    }
                }
            }
        }.runTaskTimer(plugin, 200, 200);
    }

    public static void enableOrb(ArmorStand am){
        am.setArms(false);
        if(am.getRemainingAir()==2) {
            goDown(am);
        }
        else{
            goUp(am);
        }
        am.setRemainingAir(1);
        switch(new NBTItem(am.getHelmet()).getString("HandyOrbsType")){
            case "nether-wart":
                NetherWart.wartManager(am);
                break;
            case "sugar-cane":
                SugarCane.sugarManager(am);
                break;
            case "rainbow":
                Rainbow.rainbowManager(am);
                break;
            case "flower":
                Flower.flowerManager(am);
                break;
            case "tree-spawner":
                TreeManager.treeSpawnerManager(am);
                break;
        }
    }*/

    @SuppressWarnings("deprecation")
    public static void createOrb(Player p, ItemStack skull, Location placedLocation){
        Location loc = placedLocation;
        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setHelmet(skull);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setArms(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setRemainingAir(1);

        NBTItem nbt = new NBTItem(skull);
        String type=nbt.getString("HandyOrbsType");
        String owner=nbt.getString("Owner");
        String armorstandName;
        if(type.equals("tree-spawner"))
            armorstandName=utils.chat(plugin.getConfig().getString("admin-orbs.tree-spawner.orb-name"));
        else if(owner.equals("Server")){
            armorstandName=skull.getItemMeta().getDisplayName();
        }
        else
            armorstandName = utils.chat(plugin.getConfig().getString("orb-with-owner-display-name").replace("%owner%", p.getName()).replace("%owner-display-name%", p.getDisplayName()).replace("%orb-name%", skull.getItemMeta().getDisplayName()));
        armorStand.setCustomName(armorstandName);
        armorStand.setCustomNameVisible(true);

        if(type.equals("farmer")) {

            String farmingType = new NBTItem(skull).getString("HandyOrbsFarmType");
            if (farmingType.equals("wheat")) {
                if (PlayerData.farmingWheatOwnOrbs.containsKey(owner))
                    PlayerData.farmingWheatOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
                else {
                    ArrayList<Location> temp = new ArrayList<>();
                    temp.add(armorStand.getLocation().getBlock().getLocation());
                    PlayerData.farmingWheatOwnOrbs.put(owner, temp);
                }
            } else if (farmingType.equals("carrots")) {
                if (PlayerData.farmingCarrotsOwnOrbs.containsKey(owner))
                    PlayerData.farmingCarrotsOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
                else {
                    ArrayList<Location> temp = new ArrayList<>();
                    temp.add(armorStand.getLocation().getBlock().getLocation());
                    PlayerData.farmingCarrotsOwnOrbs.put(owner, temp);
                }
            } else {
                if (PlayerData.farmingPotatoesOwnOrbs.containsKey(owner))
                    PlayerData.farmingPotatoesOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
                else {
                    ArrayList<Location> temp = new ArrayList<>();
                    temp.add(armorStand.getLocation().getBlock().getLocation());
                    PlayerData.farmingPotatoesOwnOrbs.put(owner, temp);
                }
            }
        }
        else if(type.equals("fishing")) {
            ConfigLoad.fishingType.add(armorStand.getLocation().getBlock().getLocation());

            if (PlayerData.fishingOwnOrbs.containsKey(owner))
                PlayerData.fishingOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
            else {
                ArrayList<Location> temp = new ArrayList<>();
                temp.add(armorStand.getLocation().getBlock().getLocation());
                PlayerData.fishingOwnOrbs.put(owner, temp);
            }

        }
        else if(type.equals("nether-wart")) {

            if (PlayerData.netherWartOwnOrbs.containsKey(owner))
                PlayerData.netherWartOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
            else {
                ArrayList<Location> temp = new ArrayList<>();
                temp.add(armorStand.getLocation().getBlock().getLocation());
                PlayerData.netherWartOwnOrbs.put(owner, temp);
            }

        }
        else if(type.equals("sugar-cane")) {

            if (PlayerData.sugarCaneOwnOrbs.containsKey(owner))
                PlayerData.sugarCaneOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
            else {
                ArrayList<Location> temp = new ArrayList<>();
                temp.add(armorStand.getLocation().getBlock().getLocation());
                PlayerData.sugarCaneOwnOrbs.put(owner, temp);
            }

        }
        else if(type.equals("flower")) {

            if (PlayerData.flowerOwnOrbs.containsKey(owner))
                PlayerData.flowerOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
            else {
                ArrayList<Location> temp = new ArrayList<>();
                temp.add(armorStand.getLocation().getBlock().getLocation());
                PlayerData.flowerOwnOrbs.put(owner, temp);
            }

        }
        else if(type.equals("rainbow")) {

            if (PlayerData.rainbowOwnOrbs.containsKey(owner))
                PlayerData.rainbowOwnOrbs.get(owner).add(armorStand.getLocation().getBlock().getLocation());
            else {
                ArrayList<Location> temp = new ArrayList<>();
                temp.add(armorStand.getLocation().getBlock().getLocation());
                PlayerData.rainbowOwnOrbs.put(owner, temp);
            }
        }
        else if(type.equals("tree-spawner")){
            ArrayList<ArrayList<BlockState>> trees = new ArrayList<>();
            World world = loc.getWorld();
            int radius=plugin.getConfig().getInt("admin-orbs.tree-spawner.action-radius");
            for (int y = -radius; y < radius; y++) {
                for (int x = -radius; x < radius; x++) {
                    for (int z = -radius; z < radius; z++) {
                        Block block = world.getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
                        if (block.getType().toString().contains("LOG") || block.getType().toString().contains("WOOD")) {
                            ArrayList<BlockState> trees2=new ArrayList<>();
                            plugin.getNms().treeSaver(trees2, block.getLocation());
                            if(!trees2.isEmpty())
                                trees.add(trees2);
                        }
                    }
                }
            }
            ConfigLoad.treeManagerType.put(armorStand.getLocation().getBlock().getLocation(), trees);
        }

        orbs.add(new Orb(loc, type));
    }

    public static void loadMenuItems(){
        String ID;
        Short data;

        ItemMeta meta;
        ArrayList<String> lore;

        File orb_menu_file = new File(plugin.getDataFolder(), "orb_menu.yml");
        FileConfiguration orbMenuCfg = YamlConfiguration.loadConfiguration(orb_menu_file);

        if(Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            String[] item = orbMenuCfg.getString("background-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                backgroundItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                backgroundItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = backgroundItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("background-item-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("background-item-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            backgroundItem.setItemMeta(meta);

            item = orbMenuCfg.getString("edit-orb-title-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                editOrbTitleItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                editOrbTitleItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = editOrbTitleItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("edit-orb-title-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("edit-orb-title-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            editOrbTitleItem.setItemMeta(meta);

            item = orbMenuCfg.getString("reset-orb-title-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                resetOrbTitleItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                resetOrbTitleItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = resetOrbTitleItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("reset-orb-title-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("reset-orb-title-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            resetOrbTitleItem.setItemMeta(meta);

            item = orbMenuCfg.getString("toggle-title-visibility.title-hidden-item.item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                toggleTitleVisibilityHiddenItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                toggleTitleVisibilityHiddenItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = toggleTitleVisibilityHiddenItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("toggle-title-visibility.title-hidden-item.name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("toggle-title-visibility.title-hidden-item.lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            toggleTitleVisibilityHiddenItem.setItemMeta(meta);

            item = orbMenuCfg.getString("toggle-title-visibility.title-shown-item.item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                toggleTitleVisibilityShownItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                toggleTitleVisibilityShownItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = toggleTitleVisibilityShownItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("toggle-title-visibility.title-shown-item.name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("toggle-title-visibility.title-shown-item.lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            toggleTitleVisibilityShownItem.setItemMeta(meta);

            item = orbMenuCfg.getString("pickup-orb-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                pickupOrbItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                pickupOrbItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = pickupOrbItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("pickup-orb-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("pickup-orb-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            pickupOrbItem.setItemMeta(meta);

            item = orbMenuCfg.getString("make-server-orb-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                makeServerOrbItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                makeServerOrbItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = makeServerOrbItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("make-server-orb-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("make-server-orb-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            makeServerOrbItem.setItemMeta(meta);

            item = orbMenuCfg.getString("hyper-activity-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                hyperActivityItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                hyperActivityItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = hyperActivityItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("hyper-activity-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("hyper-activity-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            hyperActivityItem.setItemMeta(meta);

            item = orbMenuCfg.getString("close-menu-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                closeMenuItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                closeMenuItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = closeMenuItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("close-menu-item-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("close-menu-item-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            closeMenuItem.setItemMeta(meta);

            item = Main.orbManageCfg.getString("go-back-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                goBackItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                goBackItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = goBackItem.getItemMeta();
            meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("go-back-item-name")));
            lore = new ArrayList<>();
            for (String line : Main.orbManageCfg.getStringList("go-back-item-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            goBackItem.setItemMeta(meta);

            paperAnvilItem = new ItemStack(Material.PAPER, 1);
            meta = paperAnvilItem.getItemMeta();
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("edit-paper-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            meta.setDisplayName(" ");
            paperAnvilItem.setItemMeta(meta);

            nextPageItem = new ItemStack(Material.ARROW, 1);
            meta = nextPageItem.getItemMeta();
            meta.setDisplayName(utils.chat("&aNext Page"));
            lore = new ArrayList<>();
            lore.add(utils.chat("&fClick to go to"));
            lore.add(utils.chat("&fthe next page!"));
            meta.setLore(lore);
            nextPageItem.setItemMeta(meta);

            previousPageItem = new ItemStack(Material.ARROW, 1);
            meta = previousPageItem.getItemMeta();
            meta.setDisplayName(utils.chat("&aPrevious Page"));
            lore = new ArrayList<>();
            lore.add(utils.chat("&fClick to go to"));
            lore.add(utils.chat("&fthe previous page!"));
            meta.setLore(lore);
            previousPageItem.setItemMeta(meta);
        }
        else{
            String[] item = orbMenuCfg.getString("background-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                backgroundItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                backgroundItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = backgroundItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("background-item-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("background-item-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            backgroundItem.setItemMeta(meta);

            item = orbMenuCfg.getString("edit-orb-title-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                editOrbTitleItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                editOrbTitleItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = editOrbTitleItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("edit-orb-title-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("edit-orb-title-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            editOrbTitleItem.setItemMeta(meta);

            item = orbMenuCfg.getString("reset-orb-title-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                resetOrbTitleItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                resetOrbTitleItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = resetOrbTitleItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("reset-orb-title-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("reset-orb-title-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            resetOrbTitleItem.setItemMeta(meta);

            item = orbMenuCfg.getString("toggle-title-visibility.title-hidden-item.item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                toggleTitleVisibilityHiddenItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                toggleTitleVisibilityHiddenItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = toggleTitleVisibilityHiddenItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("toggle-title-visibility.title-hidden-item.name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("toggle-title-visibility.title-hidden-item.lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            toggleTitleVisibilityHiddenItem.setItemMeta(meta);

            item = orbMenuCfg.getString("toggle-title-visibility.title-shown-item.item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                toggleTitleVisibilityShownItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                toggleTitleVisibilityShownItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = toggleTitleVisibilityShownItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("toggle-title-visibility.title-shown-item.name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("toggle-title-visibility.title-shown-item.lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            toggleTitleVisibilityShownItem.setItemMeta(meta);

            item = orbMenuCfg.getString("pickup-orb-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                pickupOrbItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                pickupOrbItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = pickupOrbItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("pickup-orb-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("pickup-orb-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            pickupOrbItem.setItemMeta(meta);

            item = orbMenuCfg.getString("make-server-orb-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                makeServerOrbItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                makeServerOrbItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = makeServerOrbItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("make-server-orb-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("make-server-orb-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            makeServerOrbItem.setItemMeta(meta);

            item = orbMenuCfg.getString("hyper-activity-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                hyperActivityItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                hyperActivityItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = hyperActivityItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("hyper-activity-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("hyper-activity-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            hyperActivityItem.setItemMeta(meta);

            item = orbMenuCfg.getString("close-menu-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                closeMenuItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                closeMenuItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = closeMenuItem.getItemMeta();
            meta.setDisplayName(utils.chat(orbMenuCfg.getString("close-menu-item-name")));
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("close-menu-item-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            closeMenuItem.setItemMeta(meta);

            item = Main.orbManageCfg.getString("go-back-item").split(":");
            if (item[0].equals("skull") || item[0].equals("head")) {
                goBackItem = utils.setSkullOwner(skullItem.clone(), item[1]);
            } else {
                ID = "LEGACY_"+utils.getIdF(Integer.parseInt(item[0]));
                if (item.length == 1) {
                    data = (short) 0;
                } else {
                    data = Short.parseShort(item[1]);
                }
                goBackItem = new ItemStack(Material.getMaterial(ID), 1, data);
            }

            meta = goBackItem.getItemMeta();
            meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("go-back-item-name")));
            lore = new ArrayList<>();
            for (String line : Main.orbManageCfg.getStringList("go-back-item-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            goBackItem.setItemMeta(meta);

            paperAnvilItem = new ItemStack(Material.PAPER, 1);
            meta = paperAnvilItem.getItemMeta();
            lore = new ArrayList<>();
            for (String line : orbMenuCfg.getStringList("edit-paper-lore"))
                lore.add(utils.chat(line));
            meta.setLore(lore);
            meta.setDisplayName(" ");
            paperAnvilItem.setItemMeta(meta);

            nextPageItem = new ItemStack(Material.ARROW, 1);
            meta = nextPageItem.getItemMeta();
            meta.setDisplayName(utils.chat("&aNext Page"));
            lore = new ArrayList<>();
            lore.add(utils.chat("&fClick to go to"));
            lore.add(utils.chat("&fthe next page!"));
            meta.setLore(lore);
            nextPageItem.setItemMeta(meta);

            previousPageItem = new ItemStack(Material.ARROW, 1);
            meta = previousPageItem.getItemMeta();
            meta.setDisplayName(utils.chat("&aPrevious Page"));
            lore = new ArrayList<>();
            lore.add(utils.chat("&fClick to go to"));
            lore.add(utils.chat("&fthe previous page!"));
            meta.setLore(lore);
            previousPageItem.setItemMeta(meta);
        }
    }

    public void loadAdminMenuItems(){
        adminInventory=Bukkit.createInventory(null, 54, utils.chat("&8Get Orbs"));

        ItemStack item;
        ItemMeta meta;
        ArrayList<String> lore;

        item=new ItemStack(Material.BOOK, 1);
        meta=item.getItemMeta();
        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("get-orbs-book-1.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("get-orbs-book-1.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(10, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("get-orbs-book-2.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("get-orbs-book-2.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(11, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("get-orbs-book-3.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("get-orbs-book-3.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(12, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("get-orbs-book-4.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("get-orbs-book-4.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(14, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("get-orbs-book-5.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("get-orbs-book-5.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(16, item.clone());

        ArrayList<String> loreToAdd =  new ArrayList<>();
        for(String line: Main.orbManageCfg.getStringList("get-orbs-lore-addition"))
            loreToAdd.add(utils.chat(line));

        item=farmingWheatOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(19, item);

        item=farmingCarrotsOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(28, item);

        item=farmingPotatoesOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(37, item);

        item=netherWartOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(20, item);

        item=sugarCaneOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(29, item);

        item=fishingOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(38, item);

        item=flowerOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(21, item);

        item=rainbowOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(30, item);

        item=radiantOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(23, item);

        item=savingGraceOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(32, item);

        item=treeSpawnerOrb.clone();
        meta=item.getItemMeta();
        lore=(ArrayList<String>)meta.getLore();
        lore.add(" ");
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
        adminInventory.setItem(25, item);

        adminInventory.setItem(49, closeMenuItem.clone());

        if(Main.orbManageCfg.getBoolean("use-background-item")) {
            for (int i = 0; i < 54; i++)
                if (adminInventory.getItem(i) == null)
                    adminInventory.setItem(i, ConfigLoad.backgroundItem.clone());
        }
    }

    public ConfigLoad(Main plugin) {
        this.plugin = plugin;

        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            skullItem = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
        }
        else {
            skullItem = new ItemStack(Material.PLAYER_HEAD, 1);
        }

        loadMenuItems();

        ItemMeta meta;
        ArrayList<String> lore = new ArrayList<>();
        NBTItem addNbt;

        farmingWheatOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.farmer.wheat.skull-skin"));
        meta=farmingWheatOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.farmer.wheat.orb-name")));
        for(String line : plugin.getConfig().getStringList("permanent-orbs.farmer.wheat.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        farmingWheatOrb.setItemMeta(meta);
        addNbt = new NBTItem(farmingWheatOrb);
        addNbt.setString("HandyOrbsType", "farmer");
        addNbt.setString("HandyOrbsFarmType", "wheat");
        farmingWheatOrb = addNbt.getItem();

        farmingCarrotsOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.farmer.carrots.skull-skin"));
        meta=farmingCarrotsOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.farmer.carrots.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.farmer.carrots.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        farmingCarrotsOrb.setItemMeta(meta);
        addNbt = new NBTItem(farmingCarrotsOrb);
        addNbt.setString("HandyOrbsType", "farmer");
        addNbt.setString("HandyOrbsFarmType", "carrots");
        farmingCarrotsOrb = addNbt.getItem();

        farmingPotatoesOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.farmer.potatoes.skull-skin"));
        meta=farmingPotatoesOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.farmer.potatoes.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.farmer.potatoes.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        farmingPotatoesOrb.setItemMeta(meta);
        addNbt = new NBTItem(farmingPotatoesOrb);
        addNbt.setString("HandyOrbsType", "farmer");
        addNbt.setString("HandyOrbsFarmType", "potatoes");
        farmingPotatoesOrb = addNbt.getItem();

        fishingOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.fishing.skull-skin"));
        meta=fishingOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.fishing.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.fishing.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        fishingOrb.setItemMeta(meta);
        addNbt = new NBTItem(fishingOrb);
        addNbt.setString("HandyOrbsType", "fishing");
        fishingOrb = addNbt.getItem();

        netherWartOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.nether-wart.skull-skin"));
        meta=netherWartOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.nether-wart.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.nether-wart.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        netherWartOrb.setItemMeta(meta);
        addNbt = new NBTItem(netherWartOrb);
        addNbt.setString("HandyOrbsType", "nether-wart");
        netherWartOrb = addNbt.getItem();

        sugarCaneOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.sugar-cane.skull-skin"));
        meta=sugarCaneOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.sugar-cane.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.sugar-cane.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        sugarCaneOrb.setItemMeta(meta);
        addNbt = new NBTItem(sugarCaneOrb);
        addNbt.setString("HandyOrbsType", "sugar-cane");
        sugarCaneOrb = addNbt.getItem();

        flowerOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.flower.skull-skin"));
        meta=flowerOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.flower.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.flower.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        flowerOrb.setItemMeta(meta);
        addNbt = new NBTItem(flowerOrb);
        addNbt.setString("HandyOrbsType", "flower");
        flowerOrb = addNbt.getItem();

        rainbowOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("permanent-orbs.rainbow.skull-skin"));
        meta=rainbowOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("permanent-orbs.rainbow.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("permanent-orbs.rainbow.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        rainbowOrb.setItemMeta(meta);
        addNbt = new NBTItem(rainbowOrb);
        addNbt.setString("HandyOrbsType", "rainbow");
        rainbowOrb = addNbt.getItem();

        radiantOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("temporary-orbs.radiant-orb.skull-skin"));
        meta=radiantOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("temporary-orbs.radiant-orb.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("temporary-orbs.radiant-orb.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        radiantOrb.setItemMeta(meta);
        addNbt = new NBTItem(radiantOrb);
        addNbt.setString("HandyOrbsType", "radiant-orb");
        radiantOrb = addNbt.getItem();

        savingGraceOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("temporary-orbs.saving-grace-orb.skull-skin"));
        meta=savingGraceOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("temporary-orbs.saving-grace-orb.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        savingGraceOrb.setItemMeta(meta);
        addNbt = new NBTItem(savingGraceOrb);
        addNbt.setString("HandyOrbsType", "saving-grace-orb");
        savingGraceOrb = addNbt.getItem();

        treeSpawnerOrb = utils.setSkullOwner(skullItem, plugin.getConfig().getString("admin-orbs.tree-spawner.skull-skin"));
        meta=treeSpawnerOrb.getItemMeta();
        meta.setDisplayName(utils.chat(plugin.getConfig().getString("admin-orbs.tree-spawner.orb-name")));
        lore=new ArrayList<>();
        for(String line : plugin.getConfig().getStringList("admin-orbs.tree-spawner.orb-lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        treeSpawnerOrb.setItemMeta(meta);
        addNbt = new NBTItem(treeSpawnerOrb);
        addNbt.setString("HandyOrbsType", "tree-spawner");
        treeSpawnerOrb = addNbt.getItem();

        orbListener=plugin.getConfig().getBoolean("orb-removal-listener");
        useLogger=plugin.getConfig().getBoolean("use-logger");

        new Farming(plugin);
        new Fishing(plugin);
        new NetherWart(plugin);
        new Flower(plugin);
        new Rainbow();

        new RadiantOrb(plugin);
        new SavingGrace(plugin);

        new TreeManager(plugin);

        loadAdminMenuItems();

        if(plugin.getConfig().getBoolean("use-crafting-recipes"))
            new Crafting(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
