/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclp.java
 * Description:  Main File of the teclp bukkit plugin.
 *   
 * @author Valentin Churavy, v.churavy [at] gmail [dot] com, Copyright (C) 2011.
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

package com.gmail.wallnuss.bukkit.teclp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import tectonicus.JsArrayWriter;

public class teclp extends JavaPlugin {
	public static final String PATH_TO_TEC_OUTPUT = "tec-output";
	public static final String DISTANCE_FOR_UPDATE ="distance_for_update";
	public static final String CONFIGURATION_FILE ="plugins/teclp/config.yml";
	private final teclpPlayerListener playerListener = new teclpPlayerListener(this);
	// private final teclpBlockListener blockListener = new teclpBlockListener(this);
	private final teclpWorldListener worldListener = new teclpWorldListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	boolean debug;
	private double distance;
	private Configuration config;
	private HashMap<String, TECLPPlayer> players = new HashMap <String, TECLPPlayer>(); //PlayerList
	private HashMap<Long, JsArrayWriter[]> worlds = new HashMap<Long, JsArrayWriter[]>(); //TODO

	public teclp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		loadConfig();
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
		pm.registerEvent(Type.WORLD_LOADED, worldListener, Priority.Monitor, this);

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

	public void update(){
		for (JsArrayWriter[] jsWriter_array :  worlds.values()){
			for(JsArrayWriter jsWriter : jsWriter_array){
				try {
					jsWriter.open();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		for(Map.Entry<String, TECLPPlayer> e : players.entrySet()){
			TECLPPlayer player = e.getValue();
			player.update();
			HashMap<String, String> args = new HashMap<String, String>();
			args = player.getData();
			long world = player.getWorld();
			if(worlds.containsKey(world)){
				for (JsArrayWriter jsWriter : worlds.get(world)){
					jsWriter.write(args);
				}
			}
		}
		for (JsArrayWriter[] jsWriter_array :  worlds.values()){
			for(JsArrayWriter jsWriter : jsWriter_array){
				jsWriter.close();
			}
		}

	}

	private void loadConfig(){
		config = new Configuration(new File(teclp.CONFIGURATION_FILE) );
		config.load();
		List<String> world_nodes = config.getKeys("worlds");

		if(world_nodes==null){
			System.out.println("YAML error");
		}else{
			for (String world_name : world_nodes){
				World world = this.getServer().getWorld(world_name);

				if(debug){
					System.out.println("world name:"+world_name+"");
				}
				if (world != null){
					List<String> paths =config.getStringList("worlds"+"."+world_name, null);
					JsArrayWriter[] jsWriter_array = new JsArrayWriter[paths.size()];
					if(!paths.isEmpty()){
						for (String path : paths){
							if(debug){
								System.out.println("\t output:"+path);
							}
							try {
								jsWriter_array[paths.indexOf(path)] = new JsArrayWriter(new File(path+"players.js"), "playerData");
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						worlds.put(world.getId(), jsWriter_array);
					}
				}else{
					System.out.println("World: "+world_name+"does not exist");
				}
			}
		}
		System.out.println("Size of map:" +world_nodes.size());
		System.out.println("Object of map:" +world_nodes.toString());
		distance =  config.getDouble(teclp.DISTANCE_FOR_UPDATE, 10);
	}



	public void addPlayer(String name, TECLPPlayer teclpPlayer) {
		players.put(name, teclpPlayer);

	}



	public TECLPPlayer getPlayer(String name) {
		return players.get(name);
	}



	public double getDistance() {
		return distance;
	}



	public void removePlayer(String name) {
		players.remove(name);

	}



	public void addWorld(World world) {
		List<String> paths =config.getStringList("worlds"+"."+world.getName(), null);
		JsArrayWriter[] jsWriter_array = new JsArrayWriter[paths.size()];
		if(!paths.isEmpty()){
			for (String path : paths){
				if(debug){
    	        	System.out.println("\t output:"+path);
    	        }
    			try {
					jsWriter_array[paths.indexOf(path)] = new JsArrayWriter(new File(path+"players.js"), "playerData");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}

    		worlds.put(world.getId(), jsWriter_array);
    		System.out.println("output for world "+world.getName()+"activated files:"+paths.toString());
		}
		
	}
}