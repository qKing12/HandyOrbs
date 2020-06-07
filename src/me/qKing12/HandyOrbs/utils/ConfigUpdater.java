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

                Set<String> keys = plugin.getConfig().getKeys(false);

                String thisLine;
                while ((thisLine = in.readLine()) != null) {
                    if (thisLine.startsWith("version:"))
                        out.println("version: 1.2");
                    else {
                        if (thisLine.startsWith("#This setting enables a listener from 10 to 10 seconds")) {
                            in.readLine(); in.readLine(); in.readLine(); in.readLine(); in.readLine();
                            if (!keys.contains("orb-rotate-only")) {
                                out.println("#If you set this to true, the orb will stop bobbing and");
                                out.println("#only rotate");
                                out.println("orb-rotate-only: false");
                            }
                        }
                        else
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
