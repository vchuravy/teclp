package org.vastness.bukkit.teclp.tectonicus;

import java.io.File;

import org.bukkit.util.config.Configuration;


public class TectonicusConfig {

    private static TectonicusConfig instance = new TectonicusConfig();
    
    private String tileType = "gif"; // jpg, gif, png
    private boolean showSpawn = true;
    private int maxZoom = 8;
    private boolean signsInitiallyVisible = true;
    private boolean playersInitiallyVisible = true;
    private boolean placesInitiallyVisible = true;
    private boolean pyBukkitWebEnabled = false;
    private boolean chatEnabled = false;
    private boolean regionsInitiallyVisible = true;

    public static TectonicusConfig getInstance() {
        return instance;
    }
    
    public void loadConfig(String path){
        Configuration config = new Configuration(new File(path) );
        config.load();
        tileType = config.getString("ImageFormat", tileType);
        showSpawn = config.getBoolean("showSpawn", showSpawn);
        maxZoom = config.getInt("maxZoom", maxZoom);
        signsInitiallyVisible = config.getBoolean("signsVisible", signsInitiallyVisible);
        playersInitiallyVisible = config.getBoolean("playersVisible", playersInitiallyVisible);
        placesInitiallyVisible = config.getBoolean("placesVisible", placesInitiallyVisible);
        regionsInitiallyVisible = config.getBoolean("regionsVisible", regionsInitiallyVisible);
        pyBukkitWebEnabled = config.getBoolean("usePyBukkitWeb", pyBukkitWebEnabled);
        chatEnabled = config.getBoolean("activateChat", chatEnabled);
    }

    public boolean isPyBukkitWebEnabled() {
        return pyBukkitWebEnabled;
    }

    public void setPyBukkitWebEnabled(boolean pyBukkitWebEnabled) {
        this.pyBukkitWebEnabled = pyBukkitWebEnabled;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public String getTileType() {
        return tileType;
    }

    public boolean isShowSpawn() {
        return showSpawn;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public boolean isSignsInitiallyVisible() {
        return signsInitiallyVisible;
    }

    public boolean isPlayersInitiallyVisible() {
        return playersInitiallyVisible;
    }

    public boolean isPlacesInitiallyVisible() {
        return placesInitiallyVisible;
    }
    
    public boolean isRegionsInitiallyVisible() {
        return regionsInitiallyVisible;
    }
}
