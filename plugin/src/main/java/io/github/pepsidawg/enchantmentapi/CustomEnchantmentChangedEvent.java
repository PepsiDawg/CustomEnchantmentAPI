package io.github.pepsidawg.enchantmentapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantmentChangedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    ItemStack targetItem;
    ItemStack sacrificeItem;
    ItemStack result;
    EnchantmentChangeReason reason;

    public CustomEnchantmentChangedEvent(ItemStack target, ItemStack sacrifice, ItemStack result, EnchantmentChangeReason reason) {
        this.targetItem = target;
        this.sacrificeItem = sacrifice;
        this.result = result;
        this.reason = reason;
    }

    public ItemStack getTargetItem() {
        return this.targetItem;
    }

    public ItemStack getSacrificeItem() {
        return this.sacrificeItem;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setResult(ItemStack item) {
        this.result = item;
    }

    public EnchantmentChangeReason getReason() {
        return this.reason;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum EnchantmentChangeReason {
        ANVIL,
        ENCHANTED,
        REMOVED,
    }
}
