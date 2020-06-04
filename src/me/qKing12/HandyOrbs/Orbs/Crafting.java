package me.qKing12.HandyOrbs.Orbs;

import me.qKing12.HandyOrbs.ConfigLoad;
import me.qKing12.HandyOrbs.Main;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import static me.qKing12.HandyOrbs.ConfigLoad.*;
import static me.qKing12.HandyOrbs.utils.utils.itemsAreEqual;

public class Crafting implements Listener {
    private static Main plugin;

    public Crafting(Main plugin){
        this.plugin=plugin;
        if(!isReloading)
            createRecipes();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void createRecipes(){
        try {
            ShapedRecipe ToCarrot;
            ShapedRecipe ToPotato;
            ShapedRecipe ToWheat;
            if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
                ToCarrot = new ShapedRecipe(farmingCarrotsOrb);
                ToPotato = new ShapedRecipe(farmingPotatoesOrb);
                ToWheat = new ShapedRecipe(farmingWheatOrb);
            } else {
                ToCarrot = new ShapedRecipe(new NamespacedKey(plugin, "carrotOrb"), farmingCarrotsOrb);
                ToPotato = new ShapedRecipe(new NamespacedKey(plugin, "potatoOrb"), farmingPotatoesOrb);
                ToWheat = new ShapedRecipe(new NamespacedKey(plugin, "wheatOrb"), farmingWheatOrb);
            }

            ToCarrot.shape("***", "*&*", "***");
            ToPotato.shape("***", "*&*", "***");
            ToWheat.shape("***", "*&*", "***");
            if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
                ToCarrot.setIngredient('&', Material.getMaterial("SKULL_ITEM"), (byte) 3);
                ToCarrot.setIngredient('*', Material.getMaterial("CARROT_ITEM"));

                ToPotato.setIngredient('&', Material.getMaterial("SKULL_ITEM"), (byte) 3);
                ToPotato.setIngredient('*', Material.getMaterial("POTATO_ITEM"));

                ToWheat.setIngredient('&', Material.getMaterial("SKULL_ITEM"), (byte) 3);
                ToWheat.setIngredient('*', Material.WHEAT);
            } else {
                ToCarrot.setIngredient('&', Material.PLAYER_HEAD);
                ToCarrot.setIngredient('*', Material.CARROT);

                ToPotato.setIngredient('&', Material.PLAYER_HEAD);
                ToPotato.setIngredient('*', Material.POTATO);

                ToWheat.setIngredient('&', Material.PLAYER_HEAD);
                ToWheat.setIngredient('*', Material.WHEAT);
            }


            Bukkit.addRecipe(ToCarrot);
            Bukkit.addRecipe(ToPotato);
            Bukkit.addRecipe(ToWheat);
        }catch(Exception x){

        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e){
        CraftingInventory inv = e.getInventory();
        ItemStack result=e.getInventory().getResult();
        if(result==null)
            return;
        if(itemsAreEqual(result, farmingCarrotsOrb)){
            if(!itemsAreEqual(inv.getMatrix()[4], farmingPotatoesOrb) && !itemsAreEqual(inv.getMatrix()[4], farmingWheatOrb))
                e.getInventory().setResult(new ItemStack(Material.AIR));
            else
                e.getInventory().setResult(utils.makeUnique(result));
        }
        else if(itemsAreEqual(result, farmingPotatoesOrb)){
            if(!itemsAreEqual(inv.getMatrix()[4], farmingCarrotsOrb) && !itemsAreEqual(inv.getMatrix()[4], farmingWheatOrb))
                e.getInventory().setResult(new ItemStack(Material.AIR));
            else
                e.getInventory().setResult(utils.makeUnique(result));
        }
        else if(itemsAreEqual(result, farmingWheatOrb)){
            if(!itemsAreEqual(inv.getMatrix()[4], farmingPotatoesOrb) && !itemsAreEqual(inv.getMatrix()[4], farmingCarrotsOrb))
                e.getInventory().setResult(new ItemStack(Material.AIR));
            else
                e.getInventory().setResult(utils.makeUnique(result));
        }
    }
}
