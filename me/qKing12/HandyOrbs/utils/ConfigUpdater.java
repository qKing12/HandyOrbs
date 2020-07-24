package me.qKing12.HandyOrbs.utils;

import me.qKing12.HandyOrbs.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Set;

public class ConfigUpdater {
        public ConfigUpdater(Main plugin){
            File inFile = new File(plugin.getDataFolder(), "config.yml");
            File outFile = new File(plugin.getDataFolder(), "$$$$$$$$.tmp");

            try {
                // input
                FileInputStream fis = new FileInputStream(inFile);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                // output
                FileOutputStream fos = new FileOutputStream(outFile);
                PrintWriter out = new PrintWriter(fos);

                Set<String> keys = plugin.getConfig().getKeys(true);

                String thisLine;
                while ((thisLine = in.readLine()) != null) {
                    if (thisLine.startsWith("version:"))
                        out.println("version: 2.0");
                    else {
                        if(thisLine.startsWith("  farmer:")){
                            out.println(thisLine);
                            if(!keys.contains("permanent-orbs.farmer.check-light-level")){
                                out.println("    #If set to true, orbs won't plant if there is not enough light");
                                out.println("    check-light-level: false");
                                out.println("    minimum-light-level: 9");
                            }
                        }
                        else if (thisLine.startsWith("#This setting enables a listener from 10 to 10 seconds")) {
                            in.readLine();
                            in.readLine();
                            in.readLine();
                            in.readLine();
                            in.readLine();
                            if (!keys.contains("orb-rotate-only")) {
                                out.println("#If you set this to true, the orb will stop bobbing and");
                                out.println("#only rotate");
                                out.println("orb-rotate-only: false");
                            }
                        } else if (thisLine.startsWith("  fishing:") && !keys.contains("permanent-orbs.farmer.beetroot")) {
                            out.println("    beetroot:");
                            out.println("      skull-skin: 'eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMThmMzZlYTIyOGM0ZmQ5YWZlZDVhZGQ2ZDA1MjZkZTcxYjdhYzA1NTllYWJmYzJmNjBkZTZjNGFhNzMzZjUifX19'");
                            out.println("      orb-name: '&aFarmer Orb &7| &cBeetroot'");
                            out.println("      orb-lore:");
                            out.println("        - '&7Place down this orb'");
                            out.println("        - '&7and it will start to'");
                            out.println("        - '&7plant &cbeetroots&7!'");
                            out.println("        - ''");
                            out.println("        - '&7Radius: 15'");
                            out.println(thisLine);
                        } else
                            out.println(thisLine);
                    }
                }
                out.flush();
                out.close();
                in.close();

                inFile.delete();
                outFile.renameTo(inFile);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
}
