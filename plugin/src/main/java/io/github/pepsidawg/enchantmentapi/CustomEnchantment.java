package io.github.pepsidawg.enchantmentapi;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CustomEnchantment implements Listener{
    private String displayName;
    private String enchantmentName;
    private int id;
    private int startingLevel;
    private int maxLevel;
    private List<EnchantmentTarget> enchantmentTargets;
    private List<String> conflictsWith;

    public CustomEnchantment(String ench, String displayName, int id) {
        this.enchantmentName = ench;
        this.displayName = displayName;
        this.id = id;
        this.startingLevel = 1;
        this.maxLevel = 1;
        this.enchantmentTargets = new ArrayList<EnchantmentTarget>();
        this.conflictsWith = new ArrayList<String>();
    }

    public void setStartingLevel(int level) {
        this.startingLevel = level;
    }

    public void setMaxLevel(int level) {
        this.maxLevel = level;
    }

    public void addEnchantmentTarget(EnchantmentTarget target) {
        this.enchantmentTargets.add(target);
    }

    public void addConflictingEnchantment(String enchantment) {
        this.conflictsWith.add(enchantment.toLowerCase());
    }

    public void addConflictingEnchantments(Collection<String> enchantments) {
        this.conflictsWith.addAll(enchantments);
    }

    public int getStartingLevel() {
        return this.startingLevel;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public boolean canEnchantItem(ItemStack item) {
        boolean result = false;
        for(EnchantmentTarget target : this.enchantmentTargets) {
            result = result || target.includes(item);
        }
        return result;
    }

    public boolean conflicts(String enchantment) {
        return this.conflictsWith.contains(enchantment.toLowerCase());
    }

    public String getEnchantmentName() {
        return this.enchantmentName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getID() {
        return this.id;
    }
}
