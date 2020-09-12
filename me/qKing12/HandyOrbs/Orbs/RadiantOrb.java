package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.NBT.NBTItem;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static me.qKing12.HandyOrbs.ConfigLoad.useLogger;

public class RadiantOrb implements Listener {
    private static Main plugin;

    public static HashMap<Player, Long> radiantCooldown = new HashMap<>();
    public static ArrayList<ArmorStand> radiantArmorStands = new ArrayList<>();

    public RadiantOrb(Main plugin){
        this.plugin=plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCrystalPlace(BlockPlaceEvent e){
        if(e.getItemInHand().hasItemMeta()) {
            if (utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.radiantOrb)) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(!e.getBlock().getType().equals(Material.AIR)){
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("not-enough-space-to-place")));
                        return;
                    }
                    if(!"none".equals(plugin.getConfig().getString("temporary-orbs.radiant-orb.permission"))){
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.radiant-orb.no-permission-message")));
                        return;
                    }
                    if(radiantCooldown.containsKey(e.getPlayer())){
                        Long time = radiantCooldown.get(e.getPlayer());
                        if(time==0L) {
                            e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.radiant-orb.already-placed-message")));
                            return;
                        }
                        else if(time>ZonedDateTime.now().toInstant().toEpochMilli()){
                            int dif = (int)(time-ZonedDateTime.now().toInstant().toEpochMilli());
                            dif/=1000;
                            e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.radiant-orb.cooldown-message")).replace("%cooldown%", String.valueOf(dif)));
                            return;
                        }
                        else
                            radiantCooldown.remove(e.getPlayer());
                    }
                    for(Entity ent : e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation(), 3, 256, 3)) {
                        if (ent.getType().equals(EntityType.ARMOR_STAND)) {
                            ArmorStand tempAM = (ArmorStand) ent;
                            if (tempAM.isSmall() && !tempAM.isVisible()) {
                                e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("other-orb-too-near")));
                                return;
                            }
                        }
                    }
                    NBTItem finalCrystal = new NBTItem(e.getItemInHand().clone());
                    finalCrystal.setString("Owner", e.getPlayer().getUniqueId().toString());
                    createOrb(e.getPlayer(), finalCrystal.getItem(), e.getBlock().getLocation().add(0.5, 0, 0.5));
                }, 1);

            }
        }
    }

    public static void createOrb(Player p, ItemStack skull, Location placedLocation){
        Location loc;
        if(placedLocation==null)
            loc = p.getLocation().add(0, 2.5, 0);
        else
            loc = placedLocation;
        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setHelmet(skull);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);

        ArmorStand armorStand2 = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, 0.22, 0), EntityType.ARMOR_STAND);
        armorStand2.setVisible(false);
        armorStand2.setSmall(true);
        armorStand2.setGravity(false);
        armorStand2.setBasePlate(false);

        String armorstandName = utils.chat(plugin.getConfig().getString("orb-with-owner-display-name").replace("%owner%", p.getName()).replace("%owner-display-name%", p.getDisplayName()).replace("%orb-name%", skull.getItemMeta().getDisplayName()));
        armorStand2.setCustomName(armorstandName);
        armorStand2.setCustomNameVisible(true);

        String armorStandCooldown = utils.chat(plugin.getConfig().getString("temporary-orbs.radiant-orb.orb-subname").replace("%cooldown%", "30"));
        armorStand.setCustomName(armorStandCooldown);
        armorStand.setCustomNameVisible(true);

        double toAdd=p.getMaxHealth();
        toAdd*=plugin.getConfig().getInt("temporary-orbs.radiant-orb.regen-percent-per-second");
        toAdd/=100;

        radiantCooldown.put(p, 0L);
        radiantArmorStands.add(armorStand);
        radiantArmorStands.add(armorStand2);

        secondUpdate(armorStand, p, 29, toAdd);

        if(ConfigLoad.rotateOnly)
            rotateOnly(armorStand, armorStand2, loc);
        else
            move(armorStand, armorStand2, loc);

        if(useLogger) {
            NBTItem finalCrystal = new NBTItem(skull);
            utils.injectToLog(p.getName() + " placed a Radiant Power orb! (UniqueID=" + finalCrystal.getString("UniqueID") + ", Type=" + finalCrystal.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
        }
    }

    public static void secondUpdate(ArmorStand am, Player p, int second, double toAdd){
        if(second==-1 || !p.isOnline()){
            am.remove();
            Long cooldownEnd = ZonedDateTime.now().toInstant().toEpochMilli();
            cooldownEnd+=plugin.getConfig().getInt("temporary-orbs.radiant-orb.cooldown-in-seconds")*1000;
            radiantCooldown.put(p, cooldownEnd);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!p.isDead()) {
                String armorStandCooldown = utils.chat(plugin.getConfig().getString("temporary-orbs.radiant-orb.orb-subname").replace("%cooldown%", String.valueOf(second)));
                am.setCustomName(armorStandCooldown);
                if (p.getWorld().equals(am.getWorld()) && p.getLocation().distance(am.getLocation()) <= plugin.getConfig().getInt("temporary-orbs.radiant-orb.action-radius")) {
                    EntityRegainHealthEvent event = new EntityRegainHealthEvent(p, toAdd, EntityRegainHealthEvent.RegainReason.CUSTOM);
                    Bukkit.getPluginManager().callEvent(event);
                    if(!event.isCancelled()) {
                        if (p.getHealth() != p.getMaxHealth() && p.getHealth() + toAdd < p.getMaxHealth())
                            p.setHealth(p.getHealth() + toAdd);
                        else if (p.getHealth() + toAdd < p.getMaxHealth())
                            p.setHealth(p.getMaxHealth());
                        plugin.getNms().sendParticle("VILLAGER_HAPPY", true, (float) p.getLocation().getX(), (float) p.getLocation().getY(), (float) p.getLocation().getZ(), (float) 0.3, 0, (float) 0.3, 0, 12, am.getLocation());
                    }
                }
            }
            secondUpdate(am, p, second-1, toAdd);
        },20);
    }

    private static void rotateOnly(ArmorStand armorStand, ArmorStand armorStand2, Location loc){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    armorStand.remove();
                    armorStand2.remove();
                    radiantArmorStands.remove(armorStand);
                    radiantArmorStands.remove(armorStand2);
                    cancel();
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc);
                plugin.getNms().sendParticle("VILLAGER_HAPPY", true, (float)loc.getX(), (float)loc.getY()+(float)1.3, (float)loc.getZ(), (float)0.3, 0, (float)0.3, 0, 1, loc);

            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private static void move(ArmorStand armorStand, ArmorStand armorStand2, Location loc){
        new BukkitRunnable() {
            private boolean goingUp=true;
            private final int maximumHeight=loc.getBlockY()+1;
            private final int minimumHeight=loc.getBlockY();
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    armorStand.remove();
                    armorStand2.remove();
                    radiantArmorStands.remove(armorStand);
                    radiantArmorStands.remove(armorStand2);
                    cancel();
                    return;
                }
                if(goingUp) {
                    if (armorStand.getLocation().getY() > maximumHeight) {
                        goingUp=false;
                    }
                    else {
                        loc.setYaw(loc.getYaw() + (float) 7);
                        armorStand.teleport(loc.add(0, 0.07, 0));
                        armorStand2.teleport(loc.clone().add(0, 0.22, 0));
                        plugin.getNms().sendParticle("VILLAGER_HAPPY", true, (float) loc.getX(), (float) loc.getY() + (float) 1.3, (float) loc.getZ(), (float) 0.3, 0, (float) 0.3, 0, 1, loc);
                    }
                }
                else{
                    if(armorStand.getLocation().getY()<minimumHeight){
                        goingUp=true;
                    }
                    else {
                        loc.setYaw(loc.getYaw() + (float) 7);
                        armorStand.teleport(loc.add(0, -0.07, 0));
                        armorStand2.teleport(loc.clone().add(0, 0.22, 0));
                    }
                }

            }
        }.runTaskTimer(plugin, 1, 1);
    }
}
