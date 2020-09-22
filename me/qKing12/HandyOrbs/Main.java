package me.qKing12.HandyOrbs;

import me.qKing12.HandyOrbs.NBT.utils.MinecraftVersion;
import me.qKing12.HandyOrbs.NBT.utils.nmsmappings.ClassWrapper;
import me.qKing12.HandyOrbs.NMS.*;
import me.qKing12.HandyOrbs.Orbs.Orb;
import me.qKing12.HandyOrbs.Orbs.RadiantOrb;
import me.qKing12.HandyOrbs.Orbs.SavingGrace;
import me.qKing12.HandyOrbs.utils.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends JavaPlugin {

    public static ArrayList<String> ungivenSavingGrace = new ArrayList<>();
    public static FileConfiguration orbMenuCfg;
    public static FileConfiguration orbManageCfg;
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
        else if(version.equals("v1_16_R1"))
            nms=new NMS_v1_16_R1();
        else if(version.equals("v1_16_R2"))
            nms=new NMS_v1_16_R2();
        return nms!=null;
    }

    public NMS getNms(){
        return nms;
    }

    private void loadingNBT() {
        this.getLogger().info("Loading NBT API Util...");
        MinecraftVersion.getVersion();
        this.getLogger().info("Gson:");
        MinecraftVersion.hasGsonSupport();
        boolean classUnlinked = false;
        ClassWrapper[] var2 = ClassWrapper.values();
        int var3 = var2.length;

        int var4;
        for (var4 = 0; var4 < var3; ++var4) {
            ClassWrapper c = var2[var4];
            if (c.isEnabled() && c.getClazz() == null) {
                if (!classUnlinked) {
                    this.getLogger().info("Classes:");
                }

                this.getLogger().warning(c.name() + " did not find it's class!");
                classUnlinked = true;
            }
        }
    }

    public class firstJoinInitialize implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event){
            try {
                Database.loadFromFile(plugin);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HandlerList.unregisterAll(this);
        }
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
        loadingNBT();

        saveDefaultConfig();

        if(Main.plugin.getConfig().getDouble("version")<2.0){
            new ConfigUpdater(this);
            saveDefaultConfig();
        }

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
        if(!ConfigLoad.isReloading) {
            Bukkit.getPluginManager().registerEvents(new firstJoinInitialize(), this);
        }
        else if(Bukkit.getServer().getOnlinePlayers().size()!=0){
            for(CopyOnWriteArrayList<Orb> orbs : ConfigLoad.orbsManager.values()) {
                if(ConfigLoad.isLoadedChunk(orbs.get(0).getLocation())) {
                    for (Orb orb : orbs) {
                        orb.checkFreeze();
                    }
                }
            }
        }
        new Commands(this);
    }

    @Override
    public void onDisable(){
        Bukkit.getScheduler().cancelTasks(this);
        ungivenSavingGrace.clear();
        ungivenSavingGrace.addAll(SavingGrace.placedDown.keySet());
        try {
            Database.saveToFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(ArmorStand am : RadiantOrb.radiantArmorStands)
            am.remove();
        for(ArmorStand am : SavingGrace.savingArmorStands)
            am.remove();
    }
}
