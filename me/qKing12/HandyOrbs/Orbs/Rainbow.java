package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class Rainbow {
    static ArrayList<Color> colors = new ArrayList<>();
    static ArrayList<FireworkEffect.Type> types = new ArrayList<>();

    public Rainbow(){
        colors.add(Color.WHITE);
        colors.add(Color.PURPLE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.AQUA);
        colors.add(Color.BLUE);
        colors.add(Color.FUCHSIA);
        colors.add(Color.GRAY);
        colors.add(Color.LIME);
        colors.add(Color.MAROON);
        colors.add(Color.YELLOW);
        colors.add(Color.SILVER);
        colors.add(Color.TEAL);
        colors.add(Color.ORANGE);
        colors.add(Color.OLIVE);
        colors.add(Color.NAVY);
        colors.add(Color.BLACK);

        types.add(FireworkEffect.Type.BURST);
        types.add(FireworkEffect.Type.BALL);
        types.add(FireworkEffect.Type.BALL_LARGE);
        types.add(FireworkEffect.Type.CREEPER);
        types.add(FireworkEffect.Type.STAR);
    }

    private static Location findAir(Location loc, int radius){
        Random rand = new Random();
        int xVal = rand.nextInt(radius*2)-radius;
        int yVal = rand.nextInt(radius*2)-radius;
        int zVal = rand.nextInt(radius*2)-radius;
        Block block = loc.getWorld().getBlockAt(loc.getBlockX()+xVal, loc.getBlockY()+yVal, loc.getBlockZ()+zVal);
        if (block.getType().equals(Material.AIR))
            return block.getLocation();
        return findAir(loc, radius);
    }

    public static void spawnFirework(Location loc){
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        //Our random generator
        Random r = new Random();


        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(colors.get(r.nextInt(17))).withFade(colors.get(r.nextInt(17))).with(types.get(r.nextInt(5))).trail(r.nextBoolean()).build();

        fwm.addEffect(effect);

        int rp = r.nextInt(3);
        fwm.setPower(rp);
        fw.setFireworkMeta(fwm);
    }

    public static void rainbowManager(ArmorStand armorStand, Orb runnable){
        final int radius=Main.plugin.getConfig().getInt("permanent-orbs.rainbow.action-radius");
        runnable.setActivity(new BukkitRunnable() {
            @Override
            public void run() {
                /*if (armorStand.isDead() || armorStand.getHelmet().getType().equals(Material.AIR)) {
                    //ConfigLoad.crystals.remove(armorStand);
                    //armorStand.remove();
                    cancel();
                    return;
                }*/

                spawnFirework(findAir(armorStand.getLocation(), radius));
                spawnFirework(findAir(armorStand.getLocation(), radius));
                spawnFirework(findAir(armorStand.getLocation(), radius));
            }
        }.runTaskTimer(Main.plugin, 20*Main.plugin.getConfig().getInt("permanent-orbs.rainbow.ability-cooldown"), 20*Main.plugin.getConfig().getInt("permanent-orbs.rainbow.ability-cooldown")));
    }
}
