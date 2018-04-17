package io.github.pepsidawg.enchantmentfun.enchantments;

import io.github.pepsidawg.plugin.CustomEnchantment;
import io.github.pepsidawg.plugin.EnchantmentManager;
import org.bukkit.enchantments.EnchantmentTarget;

public class TestEnchantment extends CustomEnchantment {
    private EnchantmentManager manager;

    public TestEnchantment() {
        super("test_enchant", "Test Enchantment", EnchantmentManager.getInstance().getNextID());
        setStartingLevel(1);
        setMaxLevel(5);
        addEnchantmentTarget(EnchantmentTarget.ALL);
        manager = EnchantmentManager.getInstance();
    }
}
