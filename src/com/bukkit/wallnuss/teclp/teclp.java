/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclp.java
 * Description:  Main File of the teclp bukkit plugin.
 *   
 * @author Valentin CHuravy, v.churavy [at] gmail [dot] com, Copyright (C) 2011.
 * @version v1.0
 *   
 * @see The GNU Public License (GPLv3)
 */

/* 
 * This file is part of teclp.
 * teclp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * teclp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with teclp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bukkit.wallnuss.teclp;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.bukkit.wallnuss.teclp.teclpBlockListener;
import com.bukkit.wallnuss.teclp.teclpPlayerListener;


public class teclp extends JavaPlugin {
    public static final String PATH_TO_TEC_OUTPUT = "tec-output";
    public static final String DISTANCE_FOR_UPDATE ="distance-for-update";
    public static final String CONFIGURATION_FILE ="plugins/teclp/config.yml";
	private final teclpPlayerListener playerListener = new teclpPlayerListener(this);
    private final teclpBlockListener blockListener = new teclpBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    public teclp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        // TODO: Place any custom initialisation code here
        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

   

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Monitor, this );
        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
       
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}

