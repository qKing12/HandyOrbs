package me.qKing12.HandyOrbs;

import me.qKing12.HandyOrbs.NMS.*;
import me.qKing12.HandyOrbs.Orbs.Orb;
import me.qKing12.HandyOrbs.Orbs.RadiantOrb;
import me.qKing12.HandyOrbs.Orbs.SavingGrace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;

public class Main extends JavaPlugin {

    public static ArrayList<String> ungivenSavingGrace = new ArrayList<>();
    public static FileConfiguration orbMenuCfg;
    public static FileConfiguration orbManageCfg;
    public static double radius;
    public static Main plugin;

    private NMS nms;

    private boolean setupNMS(){
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        if(version.equals("v1_8_R3"))
            nms=new NMS_v1_8_R3();
        else if(version.equals("v1_9_R1"))
            nms=new NMS_v1_9_R1();
        else if(version.equals("v1_9_R2"))
            nms=new NMS_v1_9_R2();
        else if(version.equals("v1_10_R1"))
            nms=new NMS_v1_10_R1();
        else if(version.equals("v1_11_R1"))
            nms=new NMS_v1_11_R1();
        else if(version.equals("v1_12_R1"))
            nms=new NMS_v1_12_R1();
        else if(version.equals("v1_13_R2"))
            nms=new NMS_v1_13_R2();
        else if(version.equals("v1_14_R1"))
            nms=new NMS_v1_14_R1();
        else if(version.equals("v1_15_R1"))
            nms=new NMS_v1_15_R1();
        return nms!=null;
    }

    public NMS getNms(){
        return nms;
    }

    @Override
    public void onEnable(){
        this.plugin=this;
        if(!setupNMS()){
            getLogger().info("Your server version is incompatible!");
            getLogger().info("If you use 1.8 to 1.8.7 update to 1.8.8");
            getLogger().info("If you use 1.13 update to 1.13.1-1.13.2");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        saveDefaultConfig();
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        File orb_menu = new File(this.getDataFolder(), "orb_menu.yml");
        if (!orb_menu.exists()) {
            try {
                orb_menu.createNewFile();
                InputStream input = this.getClass().getResourceAsStream("/orb_menu.yml");
                OutputStream output = new FileOutputStream(orb_menu);
                int realLength;
                byte[] buffer = new byte[1024];

                while (input != null && (realLength = input.read(buffer)) > 0) {
                    output.write(buffer, 0, realLength);
                }
                output.flush();
                output.close();
                this.getLogger().info("Loading orb_menu.yml config...");
            } catch (IOException e) {
                this.getLogger().info("Could not create the orb_menu.yml config.");
            }
        }

        File orb_manage = new File(this.getDataFolder(), "orb_list_menus.yml");
        if (!orb_manage.exists()) {
            try {
                orb_manage.createNewFile();
                InputStream input = this.getClass().getResourceAsStream("/orb_list_menus.yml");
                OutputStream output = new FileOutputStream(orb_manage);
                int realLength;
                byte[] buffer = new byte[1024];

                while (input != null && (realLength = input.read(buffer)) > 0) {
                    output.write(buffer, 0, realLength);
                }
                output.flush();
                output.close();
                this.getLogger().info("Loading orb_list_menus.yml config...");
            } catch (IOException e) {
                this.getLogger().info("Could not create the orb_list_menus.yml config.");
            }
        }

        orbMenuCfg=YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "orb_menu.yml"));
        orbManageCfg=YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "orb_list_menus.yml"));
        new ConfigLoad(this);
        radius=(double)plugin.getConfig().getInt("deactivate-radius");
        if(!ConfigLoad.isReloading) {
            try {
                Database.loadFromFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new Commands(this);
        if(Bukkit.getServer().getOnlinePlayers().size()>0)
            ConfigLoad.checkPlayers();
    }

    @Override
    public void onDisable(){
        ungivenSavingGrace.clear();
        ungivenSavingGrace.addAll(SavingGrace.placedDown.keySet());
        try {
            Database.saveToFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Orb orb : ConfigLoad.orbs)
            orb.disable();
        for(ArmorStand am : RadiantOrb.radiantArmorStands)
            am.remove();
        for(ArmorStand am : SavingGrace.savingArmorStands)
            am.remove();
    }
}
