package io.github.pepsidawg.plugin;

import io.github.pepsidawg.api.EnchantmentDetails;
import io.github.pepsidawg.api.NMS;
import io.github.pepsidawg.plugin.CustomEnchantmentChangedEvent.EnchantmentChangeReason;
import javafx.util.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentManager {
    private static EnchantmentManager self;
    private Map<String, CustomEnchantment> registeredEnchantments;
    private int currentID;
    private NMS nmsHandler;


    private EnchantmentManager() {
        registeredEnchantments = new HashMap<String, CustomEnchantment>();
        currentID = 100;
        nmsHandler = EnchantmentAPI.getInstance().getNMSHandler();
    }

    public static EnchantmentManager getInstance() {
        if(self == null) {
            self = new EnchantmentManager();
        }
        return self;
    }

    public void registerEnchantment(CustomEnchantment enchantment) throws Exception {
        if(registeredEnchantments.containsKey(enchantment.getEnchantmentName().toLowerCase())) {
            throw new Exception("Enchantment \"" + enchantment.getDisplayName() + "\" has already been registered!");
        }
        registeredEnchantments.put(enchantment.getEnchantmentName().toLowerCase(), enchantment);
        Bukkit.getPluginManager().registerEvents(enchantment, EnchantmentAPI.getInstance());
        Bukkit.getLogger().info("Registered enchantment \"" + enchantment.getDisplayName() + "\"");
    }

    public ItemStack enchantItem(ItemStack item, CustomEnchantment enchantment, int level, boolean unsafe) throws Exception{
        Map<String, Integer> enchants = nmsHandler.getEnchants(item);

        if(!unsafe) {
            if(!enchantment.canEnchantItem(item)) {
                throw new Exception("Item can not be enchanted with " + enchantment.getDisplayName());
            }

            if(level < enchantment.getStartingLevel() || level > enchantment.getMaxLevel()) {
                throw new Exception("Item enchantment level out of accepted level range! Expected " + enchantment.getStartingLevel() + "->" + enchantment.getMaxLevel());
            }
            for(Map.Entry<CustomEnchantment, Integer> entry : getCustomEnchantments(item).entrySet()) {
                if(enchantment.conflicts(entry.getKey().getEnchantmentName())) {
                    throw new Exception(enchantment.getDisplayName() + " conflicts with " + entry.getKey().getDisplayName() + "! Could not enchant!");
                }
            }

            for(Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                if(enchantment.conflicts(entry.getKey().getName())) {
                    throw new Exception(enchantment.getDisplayName() + " conflicts with " + entry.getKey().getName() + "! Could not enchant!");
                }
            }
        }

        enchants.put(enchantment.getEnchantmentName(), level);
        ItemStack result = nmsHandler.setEnchants(item, enchants);

        result = showEnchants(result);
        CustomEnchantmentChangedEvent event = new CustomEnchantmentChangedEvent(item, null, result, EnchantmentChangeReason.ENCHANTED);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return event.getResult();
    }

    public Pair<ItemStack, Integer> combine(ItemStack target, ItemStack sacrifice, ItemStack result) {
        Map<String, Integer> targetCustomEnchants = this.nmsHandler.getEnchants(target);
        Map<String, Integer> sacrificeCustomEnchants = this.nmsHandler.getEnchants(sacrifice);
        int enchCost = 0;

        //Check for combines
        for(String name : targetCustomEnchants.keySet()) {
            CustomEnchantment enchantment = getEnchantByName(name);
            if(sacrificeCustomEnchants.containsKey(name)) {
                int newLevel = getCombineLevel(targetCustomEnchants.get(name), sacrificeCustomEnchants.get(name), enchantment.getMaxLevel());
                targetCustomEnchants.put(name, newLevel);
                sacrificeCustomEnchants.remove(name);
                enchCost += 1;
            }
        }

        //Check remaining sacrifice enchants for conflicts
        for(String sacName : sacrificeCustomEnchants.keySet()) {
            CustomEnchantment sacEnch = getEnchantByName(sacName);
            boolean conflicts = false;
            for(String tarName : targetCustomEnchants.keySet()) {
                CustomEnchantment tarEnch = getEnchantByName(tarName);
                conflicts = conflicts || tarEnch.conflicts(sacEnch.getEnchantmentName());
            }
            if(!conflicts) {
                targetCustomEnchants.put(sacName, sacrificeCustomEnchants.get(sacName));
                enchCost += 1;
            }
        }

        if(result != null && !result.getType().equals(Material.AIR)) {
            //Check enchants on result for conflicts
            for (String tarName : targetCustomEnchants.keySet()) {
                CustomEnchantment tarEnch = getEnchantByName(tarName);

                for (Enchantment enchantment : result.getEnchantments().keySet()) {
                    if (tarEnch.conflicts(enchantment.getName())) {
                        result.removeEnchantment(enchantment);
                    }
                }
            }
            result = this.nmsHandler.setEnchants(result, targetCustomEnchants);
            return new Pair<ItemStack, Integer>(showEnchants(result), enchCost*2);
        }

        target = this.nmsHandler.setEnchants(target, targetCustomEnchants);
        return new Pair<ItemStack, Integer>(showEnchants(target), enchCost*2);
    }

    private int getCombineLevel(int a, int b, int max) {
        int result = 0;
        if(a == b) {
            result = a+1;
        } else if(a > b || b > a) {
            result = (a > b) ? a : b;
        }
        return (result > max) ? max : result;
    }

    public ItemStack removeEnchant(ItemStack item, CustomEnchantment enchantment) {
        Map<String, Integer> enchants = nmsHandler.getEnchants(item);
        enchants.remove(enchantment.getEnchantmentName());
        ItemStack result = nmsHandler.setEnchants(item, enchants);

        result = showEnchants(result);

        CustomEnchantmentChangedEvent event = new CustomEnchantmentChangedEvent(item, null, result, EnchantmentChangeReason.REMOVED);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return event.getResult();
    }

    public boolean isCustomEnchantment(String enchantmentName) {
        return this.registeredEnchantments.containsKey(enchantmentName);
    }

    public boolean hasCustomEnchantment(ItemStack item) {
        return nmsHandler.getEnchants(item).size() > 0;
    }

    public boolean hasCustomEnchantment(ItemStack item, String enchantmentName) {
        return nmsHandler.getEnchants(item).containsKey(enchantmentName);
    }

    public EnchantmentDetails getEnchantmentDetails(ItemStack item, String enchantmentName) {
        return this.nmsHandler.get(item, enchantmentName);
    }

    public Map<CustomEnchantment, Integer> getCustomEnchantments(ItemStack item) {
        Map<String, Integer> enchants = nmsHandler.getEnchants(item);
        Map<CustomEnchantment, Integer> result = new HashMap<CustomEnchantment, Integer>();

        for(Map.Entry<String, Integer> entry : enchants.entrySet()) {
            result.put(getEnchantByName(entry.getKey()), entry.getValue());
        }

        return result;
    }

    public CustomEnchantment getEnchantByName(String enchantmentName) {
        return this.registeredEnchantments.get(enchantmentName.toLowerCase());
    }

    public Map<String, CustomEnchantment> getRegisteredEnchantments() {
        return this.registeredEnchantments;
    }

    private ItemStack showEnchants(ItemStack item) {
        Map<CustomEnchantment, Integer> enchants = getCustomEnchantments(item);
        ItemStack result =  item.clone();
        ItemMeta meta = result.getItemMeta();
        List<String> lore = new ArrayList<String>();

        for(Map.Entry<CustomEnchantment, Integer> entry : enchants.entrySet()) {
            lore.add(ChatColor.GRAY + entry.getKey().getDisplayName() + " " + convertToRoman(entry.getValue()));
        }

        meta.setLore(lore);
        result.setItemMeta(meta);
        return result;
    }

    public int getNextID() {
        return ++currentID;
    }

    public static String convertToRoman(int number) {
        String result = "";
        int romanValues[] = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String romanConversion[] = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        for(int index = 0; index < romanValues.length; index++) {
            while(number % romanValues[index] < number) {
                result += romanConversion[index];
                number -= romanValues[index];
            }
        }
        return result;
    }
}