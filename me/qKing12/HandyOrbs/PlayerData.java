package me.qKing12.HandyOrbs;

import me.qKing12.HandyOrbs.NBT.NBTItem;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static me.qKing12.HandyOrbs.ConfigLoad.farmingBeetrootOrb;

public class PlayerData {
    public static HashMap<String, ArrayList<Location>> farmingWheatOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> farmingCarrotsOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> farmingPotatoesOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> farmingBeetrootOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> fishingOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> netherWartOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> sugarCaneOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> flowerOwnOrbs = new HashMap<>();
    public static HashMap<String, ArrayList<Location>> rainbowOwnOrbs = new HashMap<>();

    public static HashMap<String, Long> cooldownHyperActivity = new HashMap<>();

    public static void checkRemoval(String type, String owner){
        switch (type) {
            case "wheat": {
                if(PlayerData.farmingWheatOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.farmingWheatOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "carrots": {
                if(PlayerData.farmingCarrotsOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.farmingCarrotsOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "beetroot": {
                if(PlayerData.farmingBeetrootOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.farmingBeetrootOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "potatoes": {
                if(PlayerData.farmingPotatoesOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.farmingPotatoesOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "fishing": {
                if(PlayerData.fishingOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.fishingOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "nether-wart": {
                if(PlayerData.netherWartOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.netherWartOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "sugar-cane": {
                if(PlayerData.sugarCaneOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.sugarCaneOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "flower": {
                if(PlayerData.flowerOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.flowerOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
            case "rainbow": {
                if(PlayerData.rainbowOwnOrbs.containsKey(owner)) {
                    Iterator<Location> locs = PlayerData.rainbowOwnOrbs.get(owner).iterator();
                    while (locs.hasNext()) {
                        Location loc = locs.next();
                        if (ConfigLoad.isLoadedChunk(loc)) {
                            ArmorStand crystal = ConfigLoad.getCrystal(loc);
                            if (crystal == null)
                                locs.remove();
                        }
                    }
                }
                break;
            }
        }
    }

    public static void removeFromPlayerData(Location loc, NBTItem crystalNBT){
        try {
            String type = crystalNBT.getString("HandyOrbsType");
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
                } else if (farmType.equals("beetroot")) {
                    String p = crystalNBT.getString("Owner");
                    PlayerData.farmingBeetrootOwnOrbs.get(p).remove(loc);
                    if (PlayerData.farmingBeetrootOwnOrbs.get(p).isEmpty())
                        PlayerData.farmingBeetrootOwnOrbs.remove(p);
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
        }catch(Exception x){
            Main.plugin.getLogger().warning("Couldn't remove orb from a player's data, location: "+loc.toString());
        }
    }

    public static int totalOrbs(String p){
        return getTotalFarmingBeetrootOrbs(p)+getTotalFarmingCarrotsOrbs(p)+getTotalFarmingPotatoesOrbs(p)+getTotalFarmingWheatOrbs(p)+getTotalFishingOrbs(p)+getTotalNetherWartOrbs(p)+getTotalSugarCaneOrbs(p)+getTotalFlowerOrbs(p)+getTotalRainbowOrbs(p);
    }

    public static int getTotalFarmingWheatOrbs(String p){
        if(farmingWheatOwnOrbs.containsKey(p))
            return farmingWheatOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalFarmingCarrotsOrbs(String p){
        if(farmingCarrotsOwnOrbs.containsKey(p))
            return farmingCarrotsOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalFarmingPotatoesOrbs(String p){
        if(farmingPotatoesOwnOrbs.containsKey(p))
            return farmingPotatoesOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalFarmingBeetrootOrbs(String p){
        if(farmingBeetrootOwnOrbs.containsKey(p))
            return farmingBeetrootOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalFishingOrbs(String p){
        if(fishingOwnOrbs.containsKey(p))
            return fishingOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalNetherWartOrbs(String p){
        if(netherWartOwnOrbs.containsKey(p))
            return netherWartOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalSugarCaneOrbs(String p){
        if(sugarCaneOwnOrbs.containsKey(p))
            return sugarCaneOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalFlowerOrbs(String p){
        if(flowerOwnOrbs.containsKey(p))
            return flowerOwnOrbs.get(p).size();
        return 0;
    }

    public static int getTotalRainbowOrbs(String p){
        if(rainbowOwnOrbs.containsKey(p))
            return rainbowOwnOrbs.get(p).size();
        return 0;
    }

    public static void ownOrbsLocation(Player p, String pData, String type, boolean canTeleport, int page) {
        Inventory ownOrbs;
        if(pData.equals("Server"))
            ownOrbs = Bukkit.createInventory(null, 27, utils.chat(Main.orbManageCfg.getString("own-orbs-menu-title"))+" Server");
        else
            ownOrbs = Bukkit.createInventory(null, 27, utils.chat(Main.orbManageCfg.getString("own-orbs-menu-title"))+" "+Bukkit.getOfflinePlayer(UUID.fromString(pData)).getName());


        for (int i = 1; i < 10; i++)
            ownOrbs.setItem(i, ConfigLoad.backgroundItem.clone());
        for (int i = 17; i < 27; i++)
            ownOrbs.setItem(i, ConfigLoad.backgroundItem.clone());

        ownOrbs.setItem(22, ConfigLoad.goBackItem.clone());

        NBTItem nbtSave = new NBTItem(ConfigLoad.backgroundItem.clone());
        nbtSave.setString("pData", pData);
        nbtSave.setString("Type", type);
        ownOrbs.setItem(0, nbtSave.getItem());

        int currentSlot = 10;
        ArrayList<Location> orbs;
        ItemStack toDisplay;
        ItemMeta meta;
        ArrayList<String> lore;
        switch (type) {
            case "wheat":
                orbs = farmingWheatOwnOrbs.get(pData);
                toDisplay = ConfigLoad.farmingWheatOrb.clone();
                break;
            case "carrots":
                orbs = farmingCarrotsOwnOrbs.get(pData);
                toDisplay = ConfigLoad.farmingCarrotsOrb.clone();
                break;
            case "beetroot":
                orbs = farmingBeetrootOwnOrbs.get(pData);
                toDisplay = farmingBeetrootOrb.clone();
                break;
            case "potatoes":
                orbs = farmingPotatoesOwnOrbs.get(pData);
                toDisplay = ConfigLoad.farmingPotatoesOrb.clone();
                break;
            case "nether-wart":
                orbs = netherWartOwnOrbs.get(pData);
                toDisplay = ConfigLoad.netherWartOrb.clone();
                break;
            case "sugar-cane":
                orbs = sugarCaneOwnOrbs.get(pData);
                toDisplay = ConfigLoad.sugarCaneOrb.clone();
                break;
            case "fishing":
                orbs = fishingOwnOrbs.get(pData);
                toDisplay = ConfigLoad.fishingOrb.clone();
                break;
            case "flower":
                orbs = flowerOwnOrbs.get(pData);
                toDisplay = ConfigLoad.flowerOrb.clone();
                break;
            default:
                orbs = rainbowOwnOrbs.get(pData);
                toDisplay = ConfigLoad.rainbowOrb.clone();
                break;
        }
        if(pData.equals("Server")) {
            if (orbs.size() > 7 * (page + 1)) {
                ownOrbs.setItem(17, ConfigLoad.nextPageItem);
                ownOrbs.getItem(17).setAmount(page);
            }
            if (page != 0) {
                ownOrbs.setItem(9, ConfigLoad.previousPageItem);
                ownOrbs.getItem(9).setAmount(page);
            }
            int max = orbs.size();
            ArrayList<Location> temp = new ArrayList<>();
            for (int i = 7 * page; i < 7 * (page + 1); i++) {
                if (i == max)
                    break;
                temp.add(orbs.get(i));
            }
            orbs = temp;
        }
        for (Location loc : orbs) {
            ItemStack toDisplayTemp = toDisplay.clone();
            meta = toDisplayTemp.getItemMeta();
            meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("orb-view-name")).replace("%orb-name%", meta.getDisplayName()).replace("%number%", String.valueOf(currentSlot - 9+page*7)));
            lore = new ArrayList<>();
            for (String line : Main.orbManageCfg.getStringList("orb-view-lore"))
                lore.add(utils.chat(line).replace("%orb-x%", String.valueOf(loc.getBlockX())).replace("%orb-y%", String.valueOf(loc.getBlockY())).replace("%orb-z%", String.valueOf(loc.getBlockZ())));
            if (canTeleport) {
                lore.add(" ");
                for (String line : Main.orbManageCfg.getStringList("orb-teleport-addition"))
                    lore.add(utils.chat(line));
                meta.setLore(lore);
                toDisplayTemp.setItemMeta(meta);
                NBTItem nbt = new NBTItem(toDisplayTemp);
                nbt.setString("Location", utils.locationToBase64(loc));
                ownOrbs.setItem(currentSlot, nbt.getItem());
            } else {
                meta.setLore(lore);
                toDisplayTemp.setItemMeta(meta);
                ownOrbs.setItem(currentSlot, toDisplayTemp);
            }
            currentSlot++;
        }

        p.openInventory(ownOrbs);
    }

    public static void ownOrbsMenu(Player p, String pData){
        Inventory ownOrbs;
        if(pData.equals("Server"))
            ownOrbs = Bukkit.createInventory(null, 54, utils.chat(Main.orbManageCfg.getString("own-orbs-menu-title"))+" Server");
        else
            ownOrbs = Bukkit.createInventory(null, 54, utils.chat(Main.orbManageCfg.getString("own-orbs-menu-title"))+" "+Bukkit.getOfflinePlayer(UUID.fromString(pData)).getName());

        int total=totalOrbs(pData);
        int totalFarmingWheat=getTotalFarmingWheatOrbs(pData);
        int totalFarmingCarrots=getTotalFarmingCarrotsOrbs(pData);
        int totalFarmingPotatoes=getTotalFarmingPotatoesOrbs(pData);
        int totalFishing=getTotalFishingOrbs(pData);
        int totalNetherWart=getTotalNetherWartOrbs(pData);
        int totalSugarCane=getTotalSugarCaneOrbs(pData);
        int totalFlower=getTotalFlowerOrbs(pData);
        int totalRainbow=getTotalRainbowOrbs(pData);
        int totalFarmingBeetroot=getTotalFarmingBeetrootOrbs(pData);


        ItemStack item;
        ItemMeta meta;
        ArrayList<String> lore;

        item=new ItemStack(Material.BOOK, 1);
        meta=item.getItemMeta();
        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("own-orbs-book-1.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("own-orbs-book-1.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        ownOrbs.setItem(10, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("own-orbs-book-2.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("own-orbs-book-2.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        ownOrbs.setItem(12, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("own-orbs-book-3.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("own-orbs-book-3.lore"))
            lore.add(utils.chat(line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        ownOrbs.setItem(14, item.clone());

        meta.setDisplayName(utils.chat(Main.orbManageCfg.getString("own-orbs-book-4.name")));
        lore=new ArrayList<>();
        for(String line : Main.orbManageCfg.getStringList("own-orbs-book-4.lore")) {
            line=utils.chat(line)
                    .replace("%total-orbs%", String.valueOf(total))
                    .replace("%farming-wheat-name%", ConfigLoad.farmingWheatOrb.getItemMeta().getDisplayName()).replace("%farming-wheat-total%", String.valueOf(totalFarmingWheat))
                    .replace("%farming-carrots-name%", ConfigLoad.farmingCarrotsOrb.getItemMeta().getDisplayName()).replace("%farming-carrots-total%", String.valueOf(totalFarmingCarrots))
                    .replace("%farming-potatoes-name%", ConfigLoad.farmingPotatoesOrb.getItemMeta().getDisplayName()).replace("%farming-potatoes-total%", String.valueOf(totalFarmingPotatoes))
                    .replace("%fishing-name%", ConfigLoad.fishingOrb.getItemMeta().getDisplayName()).replace("%fishing-total%", String.valueOf(totalFishing))
                    .replace("%nether-wart-name%", ConfigLoad.netherWartOrb.getItemMeta().getDisplayName()).replace("%nether-wart-total%", String.valueOf(totalNetherWart))
                    .replace("%sugar-cane-name%", ConfigLoad.sugarCaneOrb.getItemMeta().getDisplayName()).replace("%sugar-cane-total%", String.valueOf(totalSugarCane))
                    .replace("%flower-name%", ConfigLoad.flowerOrb.getItemMeta().getDisplayName()).replace("%flower-total%", String.valueOf(totalFlower))
                    .replace("%rainbow-name%", ConfigLoad.rainbowOrb.getItemMeta().getDisplayName()).replace("%rainbow-total%", String.valueOf(totalRainbow));
            if (farmingBeetrootOrb != null)
                line=line.replace("%farming-beetroot-name%", farmingBeetrootOrb.getItemMeta().getDisplayName()).replace("%farming-beetroot-total%", String.valueOf(totalFarmingBeetroot));
            lore.add(line);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        ownOrbs.setItem(16, item.clone());

        NBTItem nbt;

        item=ConfigLoad.farmingWheatOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalFarmingWheat)));
        if(totalFarmingWheat!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "wheat");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalFarmingWheat>1)
            item.setAmount(totalFarmingWheat);
        ownOrbs.setItem(19, item);

        item=ConfigLoad.farmingCarrotsOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalFarmingCarrots)));
        if(totalFarmingCarrots!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "carrots");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalFarmingCarrots>1)
            item.setAmount(totalFarmingCarrots);
        ownOrbs.setItem(28, item);

        item=ConfigLoad.farmingPotatoesOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalFarmingPotatoes)));
        if(totalFarmingPotatoes!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "potatoes");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalFarmingPotatoes>1)
            item.setAmount(totalFarmingPotatoes);
        ownOrbs.setItem(37, item);

        if(farmingBeetrootOrb!=null){
            item= farmingBeetrootOrb.clone();
            lore=(ArrayList<String>)item.getItemMeta().getLore();
            lore.add(" ");
            for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
                lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalFarmingBeetroot)));
            if(totalFarmingBeetroot!=0){
                for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                    lore.add(utils.chat(line));
                nbt=new NBTItem(item);
                nbt.setString("Orbs", pData);
                nbt.setString("Type", "beetroot");
                item=nbt.getItem();
            }
            meta=item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
            if(totalFarmingBeetroot>1)
                item.setAmount(totalFarmingBeetroot);
            ownOrbs.setItem(46, item);
        }

        item=ConfigLoad.netherWartOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalNetherWart)));
        if(totalNetherWart!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "nether-wart");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalNetherWart>1)
            item.setAmount(totalNetherWart);
        ownOrbs.setItem(21, item);

        item=ConfigLoad.sugarCaneOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalSugarCane)));
        if(totalSugarCane!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "sugar-cane");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalSugarCane>1)
            item.setAmount(totalSugarCane);
        ownOrbs.setItem(30, item);

        item=ConfigLoad.fishingOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalFishing)));
        if(totalFishing!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "fishing");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalFishing>1)
            item.setAmount(totalFishing);
        ownOrbs.setItem(39, item);

        item=ConfigLoad.flowerOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalFlower)));
        if(totalFlower!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "flower");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalFlower>1)
            item.setAmount(totalFlower);
        ownOrbs.setItem(23, item);

        item=ConfigLoad.rainbowOrb.clone();
        lore=(ArrayList<String>)item.getItemMeta().getLore();
        lore.add(" ");
        for(String line : Main.orbManageCfg.getStringList("own-orbs-lore-addition"))
            lore.add(utils.chat(line).replace("%orbs%", String.valueOf(totalRainbow)));
        if(totalRainbow!=0){
            for(String line : Main.orbManageCfg.getStringList("click-to-view-lore-addition"))
                lore.add(utils.chat(line));
            nbt=new NBTItem(item);
            nbt.setString("Orbs", pData);
            nbt.setString("Type", "rainbow");
            item=nbt.getItem();
        }
        meta=item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        if(totalRainbow>1)
            item.setAmount(totalRainbow);
        ownOrbs.setItem(32, item);

        ownOrbs.setItem(49, ConfigLoad.closeMenuItem);

        if(Main.orbManageCfg.getBoolean("own-use-background-item")){
            for (int i = 0; i < 54; i++)
                if (ownOrbs.getItem(i) == null)
                    ownOrbs.setItem(i, ConfigLoad.backgroundItem.clone());
        }

        p.openInventory(ownOrbs);
    }
}
