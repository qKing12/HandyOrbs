package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.Main;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.lang.reflect.Method;
import java.util.Random;

import static me.qKing12.HandyOrbs.ConfigLoad.fishingType;

public class Fishing implements Listener {
    private static Main plugin;

    public Fishing(Main plugin){
        this.plugin=plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e){
        if(e.getState().equals(PlayerFishEvent.State.FISHING)) {
            for (Location loc : fishingType) {
                if(e.getPlayer().getWorld().equals(loc.getWorld())) {
                    if (e.getPlayer().getLocation().distance(loc) <= 15) {
                        try {
                            Random rand = new Random();
                            int secunde = rand.nextInt(20) + 5;
                            secunde *= 20;
                            secunde *= 100 - plugin.getConfig().getInt("permanent-orbs.fishing.boost-percent");
                            secunde /= 100;
                            Method fishGetMethod = PlayerFishEvent.class.getMethod("getHook");
                            Object fishObject = fishGetMethod.invoke(e);
                            plugin.getNms().setBiteTime((FishHook) fishObject, secunde + 1);
                        } catch (Exception e2) {

                        }
                        break;
                    }
                }
            }
        }
    }
}
