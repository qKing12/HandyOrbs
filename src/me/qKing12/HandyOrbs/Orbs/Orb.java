package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.NBT.NBTItem;
import me.qKing12.HandyOrbs.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Orb {
    private String type;
    private Location loc;
    private boolean isDisabled;

    private boolean goingDown;

    private ArmorStand armorStand;

    private BukkitTask activity;

    public Location getLocation(){
        return this.loc;
    }

    public void setActivity(BukkitTask runnable){
        if(this.activity!=null)
            this.activity.cancel();
        this.activity=runnable;
    }

    public Orb(Location loc, String type){
        ArmorStand am = ConfigLoad.getCrystal(loc);
        this.loc=loc;
        if(am==null) {
            this.isDisabled = true;
        }
        else {
            this.isDisabled = false;
            this.armorStand = am;
            if(type==null) {
                this.type = new NBTItem(am.getHelmet()).getString("HandyOrbsType");
                Bukkit.getScheduler().runTaskLater(Main.plugin, () ->{
                    switch (this.type) {
                        case "farmer":
                            Farming.farmingManager(am, this);
                            break;
                        case "nether-wart":
                            NetherWart.wartManager(am, this);
                            break;
                        case "sugar-cane":
                            SugarCane.sugarManager(am, this);
                            break;
                        case "rainbow":
                            Rainbow.rainbowManager(am, this);
                            break;
                        case "flower":
                            Flower.flowerManager(am, this);
                            break;
                        case "tree-spawner":
                            TreeManager.treeSpawnerManager(am, this);
                            break;
                    }
                }, 10);
            }
            else{
                this.type=type;
                switch (this.type) {
                    case "farmer":
                        Farming.farmingManager(am, this);
                        break;
                    case "nether-wart":
                        NetherWart.wartManager(am, this);
                        break;
                    case "sugar-cane":
                        SugarCane.sugarManager(am, this);
                        break;
                    case "rainbow":
                        Rainbow.rainbowManager(am, this);
                        break;
                    case "flower":
                        Flower.flowerManager(am, this);
                        break;
                    case "tree-spawner":
                        TreeManager.treeSpawnerManager(am, this);
                        break;
                }
            }
            if(ConfigLoad.rotateOnly)
                rotateOnly();
            else
                goUp();
        }
    }

    public void update(){
        if(isDisabled){
            if(this.armorStand!=null && this.armorStand.isDead()) {
                Bukkit.getScheduler().runTask(Main.plugin, () -> {
                    ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                    am.setHelmet(armorStand.getHelmet());
                    am.setCustomName(armorStand.getCustomName());
                    am.setCustomNameVisible(armorStand.isCustomNameVisible());
                    am.setVisible(false);
                    am.setSmall(true);
                    am.setGravity(false);
                    am.setArms(true);
                    am.setBasePlate(false);
                    this.armorStand = am;
                });
            }
        }

        boolean found=false;
        for(Entity testP : loc.getWorld().getNearbyEntities(loc, Main.radius, 255, Main.radius)){
            if(testP.getType().equals(EntityType.PLAYER)){
                found=true;
                if(isDisabled)
                    enable();
                break;
            }
        }
        if(!found) {
            if (!isDisabled) {
                disable();
            }
        }
    }

    public void enable() {
        this.isDisabled = false;
        ArmorStand am = ConfigLoad.getCrystal(this.loc);
        if (am == null) {
            this.unload();
            return;
        }
        this.armorStand = am;
        if (this.type == null) {
            this.type = new NBTItem(am.getHelmet()).getString("HandyOrbsType");
        }
        switch (type) {
            case "farmer":
                Farming.farmingManager(this.armorStand, this);
                break;
            case "nether-wart":
                NetherWart.wartManager(this.armorStand, this);
                break;
            case "sugar-cane":
                SugarCane.sugarManager(this.armorStand, this);
                break;
            case "rainbow":
                Rainbow.rainbowManager(this.armorStand, this);
                break;
            case "flower":
                Flower.flowerManager(this.armorStand, this);
                break;
            case "tree-spawner":
                TreeManager.treeSpawnerManager(this.armorStand, this);
                break;
        }
        if(ConfigLoad.rotateOnly)
            rotateOnly();
        else {
            if (this.goingDown)
                goDown();
            else
                goUp();
        }
    }

    public boolean compareArmorStand(ArmorStand am){
        return am.equals(this.armorStand);
    }

    public void unload(){
        this.isDisabled=true;
        if(activity!=null) {
            this.activity.cancel();
            activity=null;
        }
        try {
            ConfigLoad.orbs.remove(this);
        }catch(Exception x){

        }
        String toRemove = this.loc.getChunk().toString();
        ConfigLoad.orbsManager.get(toRemove).remove(this);
        if(ConfigLoad.orbsManager.get(toRemove).isEmpty())
            ConfigLoad.orbsManager.remove(toRemove);
    }

    public void disable(){
        if(activity!=null) {
            this.activity.cancel();
            activity=null;
        }
        this.isDisabled=true;
    }

    private void goUp(){
        this.goingDown=false;
        Location loc=armorStand.getLocation();
        double height=this.loc.getBlockY()+0.95;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    Location loc=armorStand.getLocation().getBlock().getLocation();
                    NBTItem crystalNBT = new NBTItem(armorStand.getHelmet());
                    String type=crystalNBT.getString("HandyOrbsType");
                    if(!isDisabled){
                        Bukkit.getScheduler().runTask(Main.plugin, () -> {
                            ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                            am.setHelmet(armorStand.getHelmet());
                            am.setCustomName(armorStand.getCustomName());
                            am.setCustomNameVisible(armorStand.isCustomNameVisible());
                            am.setVisible(false);
                            am.setSmall(true);
                            am.setArms(true);
                            am.setGravity(false);
                            am.setBasePlate(false);
                            armorStand=am;
                        });
                        //enable();
                        //return;
                    }
                    else {
                        cancel();
                        if(activity!=null){
                            activity.cancel();
                            activity=null;
                        }
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
                else if(isDisabled){
                    cancel();
                    if(activity!=null){
                        activity.cancel();
                        activity=null;
                    }
                    return;
                }
                if(armorStand.getLocation().getY()>=height){
                    cancel();
                    goDown();
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc.add(0, 0.07, 0));
                Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX(), (float)loc.getY()+(float)1.3, (float)loc.getZ(), (float)0.5, 0, (float)0.5, 0, 1, loc);

            }
        }.runTaskTimerAsynchronously(Main.plugin, 1, 1);
    }

    //2
    private void goDown(){
        this.goingDown=true;
        Location loc=armorStand.getLocation();
        double height=this.loc.getBlockY()+0.02;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    Location loc=armorStand.getLocation().getBlock().getLocation();
                    NBTItem crystalNBT = new NBTItem(armorStand.getHelmet());
                    String type=crystalNBT.getString("HandyOrbsType");
                    if(!isDisabled){
                        Bukkit.getScheduler().runTask(Main.plugin, () -> {
                            ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                            am.setHelmet(armorStand.getHelmet());
                            am.setCustomName(armorStand.getCustomName());
                            am.setCustomNameVisible(armorStand.isCustomNameVisible());
                            am.setVisible(false);
                            am.setSmall(true);
                            am.setArms(true);
                            am.setGravity(false);
                            am.setBasePlate(false);
                            armorStand=am;
                        });
                        //enable();
                        //return;
                    }
                    else {
                        cancel();
                        if(activity!=null){
                            activity.cancel();
                            activity=null;
                        }
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
                else if(isDisabled){
                    cancel();
                    if(activity!=null){
                        activity.cancel();
                        activity=null;
                    }
                    return;
                }
                if(armorStand.getLocation().getY()<=height){
                    cancel();
                    goUp();
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc.add(0, -0.07, 0));
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1, 1);
    }

    private void rotateOnly(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    Location loc=armorStand.getLocation().getBlock().getLocation();
                    NBTItem crystalNBT = new NBTItem(armorStand.getHelmet());
                    String type=crystalNBT.getString("HandyOrbsType");
                    if(!isDisabled){
                        Bukkit.getScheduler().runTask(Main.plugin, () -> {
                            ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                            am.setHelmet(armorStand.getHelmet());
                            am.setCustomName(armorStand.getCustomName());
                            am.setCustomNameVisible(armorStand.isCustomNameVisible());
                            am.setVisible(false);
                            am.setSmall(true);
                            am.setArms(true);
                            am.setGravity(false);
                            am.setBasePlate(false);
                            armorStand=am;
                        });
                        //enable();
                        //return;
                    }
                    else {
                        cancel();
                        if(activity!=null){
                            activity.cancel();
                            activity=null;
                        }
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
                else if(isDisabled){
                    cancel();
                    if(activity!=null){
                        activity.cancel();
                        activity=null;
                    }
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc);
                Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float)loc.getX(), (float)loc.getY()+(float)1.3, (float)loc.getZ(), (float)0.5, 0, (float)0.5, 0, 1, loc);
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1, 1);
    }

}
