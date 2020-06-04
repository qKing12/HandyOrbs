package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.AnvilGUI.AnvilGUI;
import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.NBT.NBTItem;
import me.qKing12.HandyOrbs.PlayerData;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static me.qKing12.HandyOrbs.ConfigLoad.useLogger;

public class OrbMenu {
    private Main plugin;
    private ArmorStand am;
    private Inventory inventory;

    private final ListenUp listener = new ListenUp();

    public OrbMenu(Player p, ArmorStand am, Main plugin){
        this.plugin=plugin;
        this.am=am;
        int size=Main.orbMenuCfg.getInt("menu-size");
        inventory = Bukkit.createInventory(null, size, utils.chat(Main.orbMenuCfg.getString("menu-title")));

        String permission;

        permission=Main.orbMenuCfg.getString("edit-orb-title-permission");
        if(permission.equals("none") || p.hasPermission(permission)){
            inventory.setItem(Main.orbMenuCfg.getInt("edit-orb-title-slot"), ConfigLoad.editOrbTitleItem.clone());
        }

        permission=Main.orbMenuCfg.getString("reset-orb-title-permission");
        if(permission.equals("none") || p.hasPermission(permission)){
            inventory.setItem(Main.orbMenuCfg.getInt("reset-orb-title-slot"), ConfigLoad.resetOrbTitleItem.clone());
        }

        permission=Main.orbMenuCfg.getString("toggle-title-visibility.permission");
        if(permission.equals("none") || p.hasPermission(permission)){
            if(am.isCustomNameVisible())
                inventory.setItem(Main.orbMenuCfg.getInt("toggle-title-visibility.slot"), ConfigLoad.toggleTitleVisibilityShownItem.clone());
            else
                inventory.setItem(Main.orbMenuCfg.getInt("toggle-title-visibility.slot"), ConfigLoad.toggleTitleVisibilityHiddenItem.clone());
        }

        inventory.setItem(Main.orbMenuCfg.getInt("pickup-orb-slot"), ConfigLoad.pickupOrbItem.clone());

        permission=Main.orbMenuCfg.getString("make-server-orb-permission");
        if(permission.equals("none") || p.hasPermission(permission)){
            inventory.setItem(Main.orbMenuCfg.getInt("make-server-orb-slot"), ConfigLoad.makeServerOrbItem.clone());
        }

        permission=Main.orbMenuCfg.getString("hyper-activity-permission");
        if(permission.equals("none") || p.hasPermission(permission)){
            inventory.setItem(Main.orbMenuCfg.getInt("hyper-activity-slot"), ConfigLoad.hyperActivityItem.clone());
        }

        inventory.setItem(Main.orbMenuCfg.getInt("close-menu-item-slot"), ConfigLoad.closeMenuItem.clone());

        if(Main.orbMenuCfg.getBoolean("use-background-item")) {
            for (int i = 0; i < size; i++)
                if (inventory.getItem(i) == null)
                    inventory.setItem(i, ConfigLoad.backgroundItem.clone());
        }

        p.openInventory(inventory);

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private class ListenUp implements Listener {

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(e.getInventory().equals(inventory)){
                HandlerList.unregisterAll(listener);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(e.getSlot()<0)
                return;
            if(e.getClickedInventory().equals(inventory)) {
                e.setCancelled(true);
                if(am.isDead()){
                    e.getWhoClicked().closeInventory();
                    return;
                }
                if (e.getCurrentItem().equals(ConfigLoad.closeMenuItem)) {
                    e.getWhoClicked().closeInventory();
                }
                else if(e.getCurrentItem().equals(ConfigLoad.toggleTitleVisibilityShownItem)){
                    am.setCustomNameVisible(false);
                    e.getClickedInventory().setItem(e.getSlot(), ConfigLoad.toggleTitleVisibilityHiddenItem);
                }
                else if(e.getCurrentItem().equals(ConfigLoad.toggleTitleVisibilityHiddenItem)){
                    am.setCustomNameVisible(true);
                    e.getClickedInventory().setItem(e.getSlot(), ConfigLoad.toggleTitleVisibilityShownItem);
                }
                else if(e.getCurrentItem().equals(ConfigLoad.pickupOrbItem)){
                    int slot=e.getWhoClicked().getInventory().firstEmpty();
                    try {
                        ConfigLoad.getOrbByLocation(am).unload();
                    }catch(Exception x){
                        x.printStackTrace();
                    }
                    if(slot!=-1) {
                        e.getWhoClicked().getInventory().addItem(am.getHelmet());
                        am.remove();
                        e.getWhoClicked().closeInventory();
                    }
                    if(useLogger) {
                        Location loc = am.getLocation();
                        NBTItem crystalNBT = new NBTItem(am.getHelmet());
                        utils.injectToLog(e.getWhoClicked().getName() + " removed an orb! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                    }
                }
                else if(e.getCurrentItem().equals(ConfigLoad.resetOrbTitleItem)){
                    NBTItem nbt=new NBTItem(am.getHelmet());
                    String owner=nbt.getString("Owner");
                    String title;
                    if(owner.equalsIgnoreCase("Server"))
                        title=am.getHelmet().getItemMeta().getDisplayName();
                    else{
                        String player = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
                        title=utils.chat(plugin.getConfig().getString("orb-with-owner-display-name").replace("%owner%", player).replace("%owner-display-name%", ((Player)e.getWhoClicked()).getDisplayName()).replace("%orb-name%", am.getHelmet().getItemMeta().getDisplayName()));
                    }

                    am.setCustomName(title);
                    am.setCustomNameVisible(true);

                    e.getWhoClicked().closeInventory();
                    if(useLogger) {
                        Location loc = am.getLocation();
                        NBTItem crystalNBT = new NBTItem(am.getHelmet());
                        utils.injectToLog(e.getWhoClicked().getName() + " reseted an orb's name! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                    }
                }
                else if(e.getCurrentItem().equals(ConfigLoad.makeServerOrbItem)){
                    ItemStack skull = am.getHelmet();
                    NBTItem nbt = new NBTItem(skull);
                    String owner = nbt.getString("Owner");
                    if(!owner.equals("Server")) {
                        nbt.setString("Owner", "Server");
                        am.setCustomName(am.getHelmet().getItemMeta().getDisplayName());
                        am.setHelmet(nbt.getItem());
                        e.getWhoClicked().closeInventory();

                        String type=nbt.getString("HandyOrbsType");
                        switch (type) {
                            case "farmer":
                                String farmType = nbt.getString("HandyOrbsFarmType");
                                if (farmType.equals("wheat")) {
                                    if (PlayerData.farmingWheatOwnOrbs.containsKey("Server"))
                                        PlayerData.farmingWheatOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                    else {
                                        ArrayList<Location> temp = new ArrayList<>();
                                        temp.add(am.getLocation().getBlock().getLocation());
                                        PlayerData.farmingWheatOwnOrbs.put("Server", temp);
                                    }

                                    PlayerData.farmingWheatOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                    if (PlayerData.farmingWheatOwnOrbs.get(owner).isEmpty())
                                        PlayerData.farmingWheatOwnOrbs.remove(owner);
                                } else if (farmType.equals("carrots")) {
                                    if (PlayerData.farmingCarrotsOwnOrbs.containsKey("Server"))
                                        PlayerData.farmingCarrotsOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                    else {
                                        ArrayList<Location> temp = new ArrayList<>();
                                        temp.add(am.getLocation().getBlock().getLocation());
                                        PlayerData.farmingCarrotsOwnOrbs.put("Server", temp);
                                    }

                                    PlayerData.farmingCarrotsOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                    if (PlayerData.farmingCarrotsOwnOrbs.get(owner).isEmpty())
                                        PlayerData.farmingCarrotsOwnOrbs.remove(owner);
                                } else {
                                    if (PlayerData.farmingPotatoesOwnOrbs.containsKey("Server"))
                                        PlayerData.farmingPotatoesOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                    else {
                                        ArrayList<Location> temp = new ArrayList<>();
                                        temp.add(am.getLocation().getBlock().getLocation());
                                        PlayerData.farmingPotatoesOwnOrbs.put("Server", temp);
                                    }

                                    PlayerData.farmingPotatoesOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                    if (PlayerData.farmingPotatoesOwnOrbs.get(owner).isEmpty())
                                        PlayerData.farmingPotatoesOwnOrbs.remove(owner);
                                }
                                break;
                            case "fishing":
                                if (PlayerData.fishingOwnOrbs.containsKey("Server"))
                                    PlayerData.fishingOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                else {
                                    ArrayList<Location> temp = new ArrayList<>();
                                    temp.add(am.getLocation().getBlock().getLocation());
                                    PlayerData.fishingOwnOrbs.put("Server", temp);
                                }

                                PlayerData.fishingOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                if (PlayerData.fishingOwnOrbs.get(owner).isEmpty())
                                    PlayerData.fishingOwnOrbs.remove(owner);
                                break;
                            case "nether-wart":
                                if (PlayerData.netherWartOwnOrbs.containsKey("Server"))
                                    PlayerData.netherWartOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                else {
                                    ArrayList<Location> temp = new ArrayList<>();
                                    temp.add(am.getLocation().getBlock().getLocation());
                                    PlayerData.netherWartOwnOrbs.put("Server", temp);
                                }

                                PlayerData.netherWartOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                if (PlayerData.netherWartOwnOrbs.get(owner).isEmpty())
                                    PlayerData.netherWartOwnOrbs.remove(owner);
                                break;
                            case "sugar-cane":
                                if (PlayerData.sugarCaneOwnOrbs.containsKey("Server"))
                                    PlayerData.sugarCaneOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                else {
                                    ArrayList<Location> temp = new ArrayList<>();
                                    temp.add(am.getLocation().getBlock().getLocation());
                                    PlayerData.sugarCaneOwnOrbs.put("Server", temp);
                                }

                                PlayerData.sugarCaneOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                if (PlayerData.sugarCaneOwnOrbs.get(owner).isEmpty())
                                    PlayerData.sugarCaneOwnOrbs.remove(owner);
                                break;
                            case "flower":
                                if (PlayerData.flowerOwnOrbs.containsKey("Server"))
                                    PlayerData.flowerOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                else {
                                    ArrayList<Location> temp = new ArrayList<>();
                                    temp.add(am.getLocation().getBlock().getLocation());
                                    PlayerData.flowerOwnOrbs.put("Server", temp);
                                }

                                PlayerData.flowerOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                if (PlayerData.flowerOwnOrbs.get(owner).isEmpty())
                                    PlayerData.flowerOwnOrbs.remove(owner);
                                break;
                            case "rainbow":
                                if (PlayerData.rainbowOwnOrbs.containsKey("Server"))
                                    PlayerData.rainbowOwnOrbs.get("Server").add(am.getLocation().getBlock().getLocation());
                                else {
                                    ArrayList<Location> temp = new ArrayList<>();
                                    temp.add(am.getLocation().getBlock().getLocation());
                                    PlayerData.rainbowOwnOrbs.put("Server", temp);
                                }

                                PlayerData.rainbowOwnOrbs.get(owner).remove(am.getLocation().getBlock().getLocation());
                                if (PlayerData.rainbowOwnOrbs.get(owner).isEmpty())
                                    PlayerData.rainbowOwnOrbs.remove(owner);
                                break;
                        }
                        if(useLogger) {
                            Location loc = am.getLocation();
                            NBTItem crystalNBT = new NBTItem(am.getHelmet());
                            utils.injectToLog(e.getWhoClicked().getName() + " transformed an orb into a server orb! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                        }
                    }
                }
                else if(e.getCurrentItem().equals(ConfigLoad.editOrbTitleItem)){
                    new AnvilGUI(am, plugin, (Player) e.getWhoClicked(), ConfigLoad.paperAnvilItem.clone(), (player, reply) -> {
                       if(reply==null)
                           return null;
                       else{
                           am.setCustomName(reply);
                           if(useLogger) {
                               Location loc = am.getLocation();
                               NBTItem crystalNBT = new NBTItem(am.getHelmet());
                               utils.injectToLog(e.getWhoClicked().getName() + " changed the name of an orb! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                           }
                           return null;
                       }
                    });
                }
                else if(e.getCurrentItem().equals(ConfigLoad.hyperActivityItem)){
                    NBTItem nbt=new NBTItem(am.getHelmet());
                    String uniqueID = nbt.getString("UniqueID");
                    String type = nbt.getString("HandyOrbsType");
                    if(type.equals("farmer") || type.equals("nether-wart") || type.equals("sugar-cane") || type.equals("flower") || type.equals("tree-spawner")) {
                        if (PlayerData.cooldownHyperActivity.containsKey(uniqueID)) {
                            if (PlayerData.cooldownHyperActivity.get(uniqueID) > ZonedDateTime.now().toInstant().toEpochMilli()) {
                                if (!e.getWhoClicked().hasPermission(Main.orbMenuCfg.getString("hyper-activity-cooldown-bypass-permission"))) {
                                    e.getWhoClicked().sendMessage(utils.chat(Main.orbMenuCfg.getString("hyper-activity-cooldown-message")));
                                    e.getWhoClicked().closeInventory();
                                    return;
                                }
                            } else
                                PlayerData.cooldownHyperActivity.remove(uniqueID);
                        } else {
                            PlayerData.cooldownHyperActivity.put(uniqueID, ZonedDateTime.now().toInstant().toEpochMilli() + 1000 * Main.orbMenuCfg.getInt("hyper-activity-cooldown"));
                        }
                    }
                    Player p = (Player) e.getWhoClicked();
                    if(type.equals("farmer"))
                        Farming.hyperActivity(am, p);
                    else if(type.equals("nether-wart"))
                        NetherWart.hyperActivity(am, p);
                    else if(type.equals("sugar-cane"))
                        SugarCane.hyperActivity(am, p);
                    else if(type.equals("flower"))
                        Flower.hyperActivity(am, p);
                    else if(type.equals("tree-spawner"))
                        TreeManager.hyperActivity(am, p);
                    else{
                        p.sendMessage(utils.chat(Main.orbMenuCfg.getString("no-hyper-activity-message")));
                    }
                    p.closeInventory();
                    if(useLogger) {
                        Location loc = am.getLocation();
                        NBTItem crystalNBT = new NBTItem(am.getHelmet());
                        utils.injectToLog(e.getWhoClicked().getName() + " used hyper activity of an orb! (UniqueID=" + crystalNBT.getString("UniqueID") + ", Type=" + crystalNBT.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
                    }
                }
            }
        }

    }
}
