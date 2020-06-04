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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static me.qKing12.HandyOrbs.ConfigLoad.useLogger;

public class SavingGrace implements Listener {
    private static Main plugin;

    public static HashMap<String, Long> savingCooldown = new HashMap<>();
    public static HashMap<String, ArmorStand> placedDown = new HashMap<>();
    public static ArrayList<ArmorStand> savingArmorStands = new ArrayList<>();

    public SavingGrace(Main plugin){
        this.plugin=plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if(e.getEntity().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            if(p.getHealth()<=e.getFinalDamage())
                if(placedDown.containsKey(p.getName())){
                    ArmorStand am=placedDown.get(p.getName());
                    if(p.getWorld().equals(am.getWorld()) && p.getLocation().distance(am.getLocation())<=plugin.getConfig().getInt("temporary-orbs.saving-grace-orb.action-radius")) {
                        am.remove();
                        e.setCancelled(true);
                        plugin.getNms().sendParticle("FLAME", true, (float)am.getLocation().getX(), (float) am.getLocation().getY()+(float)1.3, (float)am.getLocation().getZ(), (float)1, (float)1, (float)1, 1, 150, am.getLocation());
                        placedDown.remove(p.getName());
                        p.setHealth(p.getMaxHealth());
                        p.sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.saved-message")));
                    }
                }
        }
    }

    @EventHandler
    public void onCrystalPlace(BlockPlaceEvent e){
        if(e.getItemInHand().hasItemMeta()) {
            if (utils.itemsAreEqual(e.getItemInHand(), ConfigLoad.savingGraceOrb)) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(!e.getBlock().getType().equals(Material.AIR)){
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("not-enough-space-to-place")));
                        return;
                    }
                    if(!"none".equals(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.permission"))){
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.no-permission-message")));
                        return;
                    }
                    if(placedDown.containsKey(e.getPlayer().getName())) {
                        e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.already-placed-message")));
                        return;
                    }
                    if(savingCooldown.containsKey(e.getPlayer().getName())) {
                        Long time = savingCooldown.get(e.getPlayer().getName());
                        if (time > ZonedDateTime.now().toInstant().toEpochMilli()) {
                            int dif = (int) (time - ZonedDateTime.now().toInstant().toEpochMilli());
                            dif /= 1000;
                            e.getPlayer().sendMessage(utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.cooldown-message")).replace("%cooldown%", String.valueOf(dif)));
                            return;
                        } else
                            savingCooldown.remove(e.getPlayer().getName());
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
                    finalCrystal.setString("Owner", e.getPlayer().getUniqueId().toString());
                    e.getPlayer().getInventory().remove(e.getItemInHand());
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

        ArmorStand armorStand2 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand2.setVisible(false);
        armorStand2.setSmall(true);
        armorStand2.setGravity(false);
        armorStand2.setBasePlate(false);

        String armorstandName = utils.chat(plugin.getConfig().getString("orb-with-owner-display-name").replace("%owner%", p.getName()).replace("%owner-display-name%", p.getDisplayName()).replace("%orb-name%", skull.getItemMeta().getDisplayName()));
        armorStand2.setCustomName(armorstandName);
        armorStand2.setCustomNameVisible(true);

        String armorStandCooldown = utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.orb-subname").replace("%cooldown%", "30"));
        armorStand.setCustomName(armorStandCooldown);
        armorStand.setCustomNameVisible(true);

        placedDown.put(p.getName(), armorStand);
        savingArmorStands.add(armorStand);
        savingArmorStands.add(armorStand2);

        secondUpdate(armorStand, p, 29);

        goUp(armorStand, armorStand2, loc);

        if(useLogger) {
            NBTItem finalCrystal = new NBTItem(skull);
            utils.injectToLog(p.getName() + " placed a Saving Grace orb! (UniqueID=" + finalCrystal.getString("UniqueID") + ", Type=" + finalCrystal.getString("HandyOrbsType") + ", Location: " + loc.getBlock().getX() + " " + loc.getBlock().getY() + " " + loc.getBlock().getZ() + ")");
        }
    }

    public static void secondUpdate(ArmorStand am, Player p, int second){
        if(am.isDead() || !p.isOnline()) {
            am.remove();
            Long cooldownEnd = ZonedDateTime.now().toInstant().toEpochMilli();
            cooldownEnd+=plugin.getConfig().getInt("temporary-orbs.saving-grace-orb.cooldown-in-seconds")*1000;
            savingCooldown.put(p.getName(), cooldownEnd);
            return;
        }
        if(second==-1){
            p.getInventory().addItem(am.getHelmet());
            am.remove();
            Long cooldownEnd = ZonedDateTime.now().toInstant().toEpochMilli();
            cooldownEnd+=plugin.getConfig().getInt("temporary-orbs.saving-grace-orb.cooldown-in-seconds")*1000;
            savingCooldown.put(p.getName(), cooldownEnd);
            placedDown.remove(p.getName());
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            String armorStandCooldown = utils.chat(plugin.getConfig().getString("temporary-orbs.saving-grace-orb.orb-subname").replace("%cooldown%", String.valueOf(second)));
            am.setCustomName(armorStandCooldown);
            secondUpdate(am, p, second-1);
        },20);
    }

    public static void goUp(ArmorStand armorStand, ArmorStand armorStand2, Location loc){
        double height=loc.getY()+1;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    armorStand.remove();
                    armorStand2.remove();
                    savingArmorStands.remove(armorStand);
                    savingArmorStands.remove(armorStand2);
                    cancel();
                    return;
                }
                if(armorStand.getLocation().getY()>height){
                    cancel();
                    goDown(armorStand, armorStand2, loc);
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc.add(0, 0.07, 0));
                armorStand2.teleport(loc.clone().add(0, 0.22, 0));
                plugin.getNms().sendParticle("FLAME", true, (float)loc.getX(), (float)loc.getY()+(float)1.3, (float)loc.getZ(), (float)0.3, 0, (float)0.3, 0, 1, loc);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    //2
    public static void goDown(ArmorStand armorStand, ArmorStand armorStand2, Location loc){
        double height=loc.getY()-1;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    armorStand.remove();
                    armorStand2.remove();
                    savingArmorStands.remove(armorStand);
                    savingArmorStands.remove(armorStand2);
                    cancel();
                    return;
                }
                if(armorStand.getLocation().getY()<height){
                    cancel();
                    goUp(armorStand, armorStand2, loc);
                    return;
                }
                loc.setYaw(loc.getYaw()+(float)7);
                armorStand.teleport(loc.add(0, -0.07, 0));
                armorStand2.teleport(loc.clone().add(0, 0.22, 0));
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}
