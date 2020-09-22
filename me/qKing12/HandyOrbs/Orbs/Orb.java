package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.NBT.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Orb {
    private String type;
    private final Location loc;

    private ArmorStand armorStand;

    private BukkitTask activity;
    private BukkitTask movement;

    public Location getLocation(){
        return this.loc;
    }

    public void setActivity(BukkitTask runnable){
        if(this.activity!=null) {
            this.activity.cancel();
        }
        this.activity=runnable;
    }

    private void setupMovement(){
        if(!ConfigLoad.isLoadedChunk(this.loc)){
            //Main.plugin.getLogger().info("[DEBUG] An unloaded orb tried to register movement. "+this.loc.toString());
            return;
        }
        //Bukkit.getScheduler().runTask(Main.plugin, () -> {
            if(ConfigLoad.rotateOnly)
                rotateOnly();
            else {
                move();
            }
        //});
    }

    private void setupActivity(){
        if(!ConfigLoad.isLoadedChunk(this.loc)){
            //Main.plugin.getLogger().info("[DEBUG] An unloaded orb tried to register activity. "+this.loc.toString());
            return;
        }
        Bukkit.getScheduler().runTask(Main.plugin, () -> {
            if(type!=null) {
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
            }
        });
    }

    public Orb(Location loc, String type){
        ArmorStand am = ConfigLoad.getCrystal(loc);
        this.loc=loc;
        if(am!=null) {
            this.armorStand = am;
            if(type==null) {
                this.type = new NBTItem(am.getHelmet()).getString("HandyOrbsType");
            }
            else{
                this.type=type;
            }
            checkFreeze();
        }
    }

    public void load() {
        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
            ArmorStand am = ConfigLoad.getCrystal(this.loc);
            if (am == null) {
                Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
                    ArmorStand am2=ConfigLoad.getCrystal(this.loc);
                    if(am2!=null && ConfigLoad.isLoadedChunk(loc)) {
                        this.armorStand = am2;
                        if (this.type == null) {
                            this.type = new NBTItem(am2.getHelmet()).getString("HandyOrbsType");
                        }
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, () -> {
                            setupActivity();
                            setupMovement();
                        }, 2);
                    }
                }, 10);
            }
            else {
                this.armorStand = am;
                if (this.type == null) {
                    try {
                        this.type = new NBTItem(am.getHelmet()).getString("HandyOrbsType");
                    }catch(Exception x){
                        Main.plugin.getLogger().info("[DEBUG] Orb failed to load at location "+this.loc.toString());
                        return;
                    }
                }
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, () -> {
                    setupActivity();
                    setupMovement();
                }, 7);
            }
        },3 );
    }

    public ArmorStand getArmorStand(){
        return armorStand;
    }

    public boolean compareArmorStand(ArmorStand am){
        return am.getLocation().getBlock().getLocation().equals(this.loc.getBlock().getLocation());
    }

    public void checkFreeze(){
        if(armorStand==null){
            ArmorStand am = ConfigLoad.getCrystal(this.loc);
            if (am != null) {
                this.armorStand = am;
                if (this.type == null) {
                    try {
                        this.type = new NBTItem(am.getHelmet()).getString("HandyOrbsType");
                    }catch(Exception x){
                        Main.plugin.getLogger().info("[DEBUG] Orb failed to load at location "+this.loc.toString());
                        return;
                    }
                }
            }
            else{
                Main.plugin.getLogger().warning("An orb can't find it's armorstand so it won't load.");
                Main.plugin.getLogger().warning(this.loc.toString());
                return;
            }
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                //if(activity==null || !Bukkit.getScheduler().isCurrentlyRunning(activity.getTaskId()))
                    setupActivity();
                //if(movement==null || !Bukkit.getScheduler().isCurrentlyRunning(movement.getTaskId()))
                    setupMovement();
            }
        }.runTaskLaterAsynchronously(Main.plugin, 10);
    }

    public void unload(boolean chunkUnloaded){
        //Main.plugin.getLogger().warning("[DEBUG] An orb was unloaded. "+this.loc.toString());
        if(activity!=null) {
            this.activity.cancel();
            activity=null;
        }
        if(this.movement!=null) {
            movement.cancel();
        }
        if(!chunkUnloaded) {
            String toRemove = ConfigLoad.getChunkString(loc);
            ConfigLoad.orbsManager.get(toRemove).remove(this);
            if (ConfigLoad.orbsManager.get(toRemove).isEmpty())
                ConfigLoad.orbsManager.remove(toRemove);
        }
    }

    public void debug(Player p, ArmorStand am){
        if(armorStand==null)
            p.sendMessage("Orb ArmorStand is null");
        else {
            if (armorStand.equals(am))
                p.sendMessage("ArmorStand equals Orb ArmorStand");
            else {
                p.sendMessage("ArmorStand is not equal Orb ArmorStand ");
                if(armorStand.isDead())
                    p.sendMessage("Orb ArmorStand is Dead");
            }
        }
        if(type!=null)
            p.sendMessage("Orb type: "+type);

        if(movement!=null) {
            p.sendMessage("Orb has movement");

            if (Bukkit.getScheduler().isCurrentlyRunning(movement.getTaskId()))
                p.sendMessage("Orb movement is not canceled");
        }

        if(activity!=null)
            p.sendMessage("Orb has activity");
    }

    private void move() {
        Location location = armorStand.getLocation();
        if (movement != null) {
            movement.cancel();
        }

        if(armorStand.isDead()) {
            movement=null;
            return;
        }

        movement = new BukkitRunnable() {
            private final int minimumHeight=loc.getBlockY();
            private final int maximumHeight=minimumHeight+1;
            private boolean goingUp=false;

            @Override
            public void run() {
                if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    if(armorStand.hasBasePlate()){
                        if(activity!=null) {
                            activity.cancel();
                            activity=null;
                        }
                        movement.cancel();
                        return;
                    }

                    Bukkit.getScheduler().runTask(Main.plugin, () -> {
                        ArmorStand am = ConfigLoad.getCrystal(loc);
                        if(am==null) {
                            am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                            am.setHelmet(armorStand.getHelmet());
                            am.setCustomName(armorStand.getCustomName());
                            am.setCustomNameVisible(armorStand.isCustomNameVisible());
                            am.setVisible(false);
                            am.setSmall(true);
                            am.setRemoveWhenFarAway(false);
                            am.setArms(true);
                            am.setGravity(false);
                            am.setBasePlate(false);
                        }
                        armorStand=am;
                    });
                }
                if(goingUp) {
                    if (armorStand.getLocation().getY() >= maximumHeight) {
                        goingUp=false;
                    }
                    else {
                        location.setYaw(location.getYaw() + (float) 7);
                        Bukkit.getScheduler().runTask(Main.plugin, () -> armorStand.teleport(location.add(0, 0.07, 0)));
                        Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float) loc.getX(), (float) loc.getY() + (float) 1.3, (float) loc.getZ(), (float) 0.5, 0, (float) 0.5, 0, 1, location);
                    }
                }
                else{
                    if (armorStand.getLocation().getY() <= minimumHeight) {
                        goingUp=true;
                    }
                    else {
                        location.setYaw(location.getYaw() + (float) 7);
                        Bukkit.getScheduler().runTask(Main.plugin, () -> armorStand.teleport(location.add(0, -0.07, 0)));
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1, 1);
    }

    private void rotateOnly() {
        if (movement != null)
            movement.cancel();
        if(armorStand.isDead())
            return;

        movement = new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    if(armorStand.hasBasePlate()){
                        if(activity!=null) {
                            activity.cancel();
                            activity=null;
                        }
                        movement.cancel();
                        return;
                    }
                    Location loc = armorStand.getLocation().getBlock().getLocation();
                    Bukkit.getScheduler().runTask(Main.plugin, () -> {
                        ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                        am.setHelmet(armorStand.getHelmet());
                        am.setCustomName(armorStand.getCustomName());
                        am.setCustomNameVisible(armorStand.isCustomNameVisible());
                        am.setVisible(false);
                        am.setSmall(true);
                        am.setArms(true);
                        am.setRemoveWhenFarAway(false);
                        am.setGravity(false);
                        am.setBasePlate(false);
                        armorStand = am;
                    });
                }
                loc.setYaw(loc.getYaw() + (float) 7);
                Bukkit.getScheduler().runTask(Main.plugin, () -> armorStand.teleport(loc));
                Main.plugin.getNms().sendParticle("FIREWORKS_SPARK", true, (float) loc.getX(), (float) loc.getY() + (float) 1.3, (float) loc.getZ(), (float) 0.5, 0, (float) 0.5, 0, 1, loc);
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1, 1);
    }

}
