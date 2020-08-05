package dev.fadest.cubelets;

import dev.fadest.cubelets.listener.CubeletListener;
import dev.fadest.cubelets.manager.CubeletManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Cubelets extends JavaPlugin {

    private CubeletManager cubeletManager;

    @Override
    public void onEnable() {
        this.cubeletManager = new CubeletManager(this);
        this.getServer().getPluginManager().registerEvents(new CubeletListener(this), this);
    }

    public CubeletManager getCubeletManager() {
        return cubeletManager;
    }
}
