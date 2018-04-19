package io.github.pepsidawg.enchantmentapi;

import io.github.pepsidawg.api.NMS;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantmentAPI extends JavaPlugin {
    private NMS nmsHandler;
    private static EnchantmentAPI self;

    @Override
    public void onEnable() {
        String packageName = getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        self = this;

        try {
            String name = "io.github.pepsidawg.enchantmentapi.nms." + version + ".NMSHandler";
            Class clazz = Class.forName(name);

            if(NMS.class.isAssignableFrom(clazz)) {
                this.nmsHandler = (NMS) clazz.getConstructor().newInstance();
            }
        } catch (Exception e) {
            getLogger().severe("Could not find support for version " + version);
            getLogger().severe("Disabling Custom Enchantments API");
            e.printStackTrace();
            setEnabled(false);
            return;
        }

        getServer().getPluginManager().registerEvents(new AnvilHandler(), this);

        getLogger().info("Loaded support for " + version);
    }

    public static EnchantmentAPI getInstance() {
        return self;
    }

    protected NMS getNMSHandler() {
        return this.nmsHandler;
    }
}
