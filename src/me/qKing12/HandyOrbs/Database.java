package me.qKing12.HandyOrbs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.qKing12.HandyOrbs.Orbs.Orb;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Location;
import org.bukkit.block.BlockState;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    public static void saveToFile(Main plugin) throws IOException {

        ArrayList<String> crystalsBase64Copy = new ArrayList<>();
        ArrayList<String> fishingTypeCopy = new ArrayList<>();
        HashMap<String, ArrayList<String>> farmingTypeClone = new HashMap<>();
        HashMap<String, ArrayList<String>> wartTypeClone = new HashMap<>();
        HashMap<String, ArrayList<String>> flowerTypeClone = new HashMap<>();
        HashMap<String, ArrayList<ArrayList<String>>> treeSpawnerClone = new HashMap<>();

        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();

        Gson gson = builder.create();

        for(Orb orb : ConfigLoad.orbs)
            crystalsBase64Copy.add(utils.locationToBase64(orb.getLocation()));

        for(Location key : ConfigLoad.fishingType)
            fishingTypeCopy.add(utils.locationToBase64(key));

        for(Location loc : ConfigLoad.farmingType.keySet()) {
            ArrayList<String> locations = new ArrayList<>();
            for(Location loc2 : ConfigLoad.farmingType.get(loc))
                locations.add(utils.locationToBase64(loc2));
            farmingTypeClone.put(utils.locationToBase64(loc), locations);
        }

        for(Location loc : ConfigLoad.netherWartType.keySet()) {
            ArrayList<String> locations = new ArrayList<>();
            for(Location loc2 : ConfigLoad.netherWartType.get(loc))
                locations.add(utils.locationToBase64(loc2));
            wartTypeClone.put(utils.locationToBase64(loc), locations);
        }

        for(Location bigLoc : ConfigLoad.flowerType.keySet()) {
            ArrayList<String> locations = new ArrayList<>();
            for(Location loc : ConfigLoad.flowerType.get(bigLoc))
                locations.add(utils.locationToBase64(loc));
            flowerTypeClone.put(utils.locationToBase64(bigLoc), locations);
        }


        for(Location loc : ConfigLoad.treeManagerType.keySet()){
            ArrayList<ArrayList<String>> trees = new ArrayList<>();
            for(ArrayList<BlockState> tree : ConfigLoad.treeManagerType.get(loc)){
                ArrayList<String> treeTransition = new ArrayList<>();
                for(BlockState bs : tree){
                    treeTransition.add(utils.blockStateToBase64(bs));
                }
                trees.add(treeTransition);
            }
            treeSpawnerClone.put(utils.locationToBase64(loc), trees);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.getDataFolder()+"/database/data.txt"));
        writer.write(gson.toJson(crystalsBase64Copy)+"\n"+gson.toJson(farmingTypeClone)+"\n"+gson.toJson(fishingTypeCopy)+"\n"+gson.toJson(wartTypeClone)+"\n"+gson.toJson(Main.ungivenSavingGrace)+"\n"+gson.toJson(flowerTypeClone)+"\n"+gson.toJson(treeSpawnerClone));

        writer.close();

        writer = new BufferedWriter(new FileWriter(plugin.getDataFolder()+"/database/playerData.txt"));

        HashMap<String, ArrayList<String>> playerDataCopy = new HashMap<>();
        for(String key : PlayerData.farmingWheatOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.farmingWheatOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.farmingCarrotsOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.farmingCarrotsOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.farmingPotatoesOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.farmingPotatoesOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.netherWartOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.netherWartOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.sugarCaneOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.sugarCaneOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.fishingOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.fishingOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.flowerOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.flowerOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy)+"\n");

        playerDataCopy.clear();
        for(String key : PlayerData.rainbowOwnOrbs.keySet()){
            ArrayList<String> undeDuc = new ArrayList<>();
            for(Location loc : PlayerData.rainbowOwnOrbs.get(key))
                undeDuc.add(utils.locationToBase64(loc));
            playerDataCopy.put(key, undeDuc);
        }

        writer.write(gson.toJson(playerDataCopy));

        writer.close();
    }

    public static void loadFromFile(Main plugin) throws IOException {
        File directory = new File(plugin.getDataFolder(), "database");
        if(!directory.exists())
            directory.mkdir();

        File database = new File(plugin.getDataFolder(), "database/data.txt");
        if(!database.exists()) {
            database.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.getDataFolder()+"/database/data.txt"));
            writer.write("[]\n{}\n[]\n{}\n[]\n{}\n{}");
            writer.close();
        }
        else {
            GsonBuilder builder = new GsonBuilder();

            builder.serializeNulls();

            Gson gson = builder.create();

            BufferedReader reader = new BufferedReader(new FileReader(plugin.getDataFolder() + "/database/data.txt"));

            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> transitCrystal = gson.fromJson(reader.readLine(), type);
            for(String loc : transitCrystal) {
                ConfigLoad.orbs.add(new Orb(utils.locationFromBase64(loc), null));
            }
            type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            HashMap<String, ArrayList<String>>  transit = gson.fromJson(reader.readLine(), type);
            for(String key : transit.keySet()) {
                ArrayList<Location> transitLoc = new ArrayList<>();
                for(String loc : transit.get(key))
                    transitLoc.add(utils.locationFromBase64(loc));
                Location loc=utils.locationFromBase64(key);
                ConfigLoad.farmingType.put(loc, transitLoc);
            }

            type = new TypeToken<ArrayList<String>>() {}.getType();
            transitCrystal = gson.fromJson(reader.readLine(), type);
            for(String loc : transitCrystal){
                Location loc2=utils.locationFromBase64(loc);
                ConfigLoad.fishingType.add(loc2);
            }

            type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            transit = gson.fromJson(reader.readLine(), type);
            for(String key : transit.keySet()) {
                ArrayList<Location> transitLoc = new ArrayList<>();
                for(String loc : transit.get(key))
                    transitLoc.add(utils.locationFromBase64(loc));
                Location loc = utils.locationFromBase64(key);
                ConfigLoad.netherWartType.put(loc, transitLoc);
            }

            type = new TypeToken<ArrayList<String>>() {}.getType();
            Main.ungivenSavingGrace = gson.fromJson(reader.readLine(), type);

            type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            transit = gson.fromJson(reader.readLine(), type);
            for(String key : transit.keySet()) {
                ArrayList<Location> transitLoc = new ArrayList<>();
                for(String loc : transit.get(key))
                    transitLoc.add(utils.locationFromBase64(loc));
                Location loc = utils.locationFromBase64(key);
                ConfigLoad.flowerType.put(loc, transitLoc);
            }

            HashMap<String, ArrayList<ArrayList<String>>> adminTransit = new HashMap<>();
            type = new TypeToken<HashMap<String, ArrayList<ArrayList<String>>>>() {}.getType();
            adminTransit=gson.fromJson(reader.readLine(), type);
            for(String key : adminTransit.keySet()){
                ArrayList<ArrayList<BlockState>> finalTrees = new ArrayList<>();
                ArrayList<ArrayList<String>> trees = adminTransit.get(key);
                for(ArrayList<String> tree : trees){
                    ArrayList<BlockState> finalTree = new ArrayList<>();
                    for(String bs : tree)
                        finalTree.add(utils.blockStateFromBase64(bs));
                    finalTrees.add(finalTree);
                }
                Location loc = utils.locationFromBase64(key);
                ConfigLoad.treeManagerType.put(loc, finalTrees);
            }
            reader.close();
        }

        database = new File(plugin.getDataFolder(), "database/playerData.txt");
        if(!database.exists()) {
            database.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.getDataFolder()+"/database/playerData.txt"));
            writer.write("{}\n{}\n{}\n{}\n{}\n{}\n{}\n{}");
            writer.close();
        }
        else{
            GsonBuilder builder = new GsonBuilder();

            builder.serializeNulls();

            Gson gson = builder.create();

            BufferedReader reader = new BufferedReader(new FileReader(plugin.getDataFolder() + "/database/playerData.txt"));

            Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            HashMap<String, ArrayList<String>> transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.farmingWheatOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.farmingCarrotsOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.farmingPotatoesOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.netherWartOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.sugarCaneOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.fishingOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.flowerOwnOrbs.put(key, locatii);
            }

            transitPlayerData = gson.fromJson(reader.readLine(), type);
            for(String key : transitPlayerData.keySet()){
                ArrayList<Location> locatii = new ArrayList<>();
                for(String loc : transitPlayerData.get(key))
                    locatii.add(utils.locationFromBase64(loc));
                PlayerData.rainbowOwnOrbs.put(key, locatii);
            }

            reader.close();
        }

    }

}
