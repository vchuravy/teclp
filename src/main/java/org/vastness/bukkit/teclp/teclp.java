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
package org.vastness.bukkit.teclp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import tectonicus.JsArrayWriter;

/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclp.java
 * Description:  Main File of the teclp bukkit plugin.
 *   
 * @author Valentin Churavy, v.churavy [at] gmail [dot] com, Copyright (C) 2011.
 * @version v1.22
 *   
 * @see The GNU Public License (GPLv3)
 */
public class teclp extends JavaPlugin {
	public static final String DISTANCE_FOR_UPDATE ="distance_for_update";
	public static final String CONFIGURATION_FILE ="plugins/teclp/config.yml";
	public static String LOG_HEADER="TECLP";
	
	private final teclpPlayerListener playerListener = new teclpPlayerListener(this);
	// private final teclpBlockListener blockListener = new teclpBlockListener(this);
	private final teclpWorldListener worldListener = new teclpWorldListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	private boolean debug;
	private double distance;
	private Configuration config; //Configuration engine
	
	private ArrayList<TECLPPlayer> players = new ArrayList <TECLPPlayer>(); //PlayerList
	private HashMap<Long, JsArrayWriter[]> worlds = new HashMap<Long, JsArrayWriter[]>(); // Stores a list of output paths (wrapped in JsArrayWriter) keys used are world ids.
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of any events
		PluginDescriptionFile pdfFile = this.getDescription();
		LOG_HEADER+=" "+pdfFile.getVersion()+" : ";
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Monitor, this );
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.WORLD_LOADED, worldListener, Priority.Monitor, this);

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		loadConfig();
		//Adding online player if any important for reload.
		for(Player player : this.getServer().getOnlinePlayers()){
			this.addPlayer(new TECLPPlayer(this, player.getName()));
		}
		
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	public void onDisable() {
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		//System.out.println("Goodbye world!");
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

	/*
	 * Updates all specified outputs.
	 */
	public void update(){
		//Initialize all JsArrayWriters
		for (JsArrayWriter[] jsWriter_array :  worlds.values()){
		    try {
			for(JsArrayWriter jsWriter : jsWriter_array){
				try {
					jsWriter.open();
				} catch (FileNotFoundException event) {
					System.err.println(LOG_HEADER+"File not found");
					event.printStackTrace();
				} catch (Exception event) {
					// TODO Auto-generated catch block
					event.printStackTrace();
				}
			}
		    }catch(NullPointerException event){
		        System.err.println("One or more paths inf your config is wrong, please fix that.");
		    }
		}
		//For each registred player.
		for( TECLPPlayer player : players){
			player.update(); //updates the data
			HashMap<String, String> args = new HashMap<String, String>();
			args = player.getData(); //get the data
			Long world = player.getWorld(); //get world
			if(worlds.containsKey(world)){ //Is world set ?
				if(debug){
					System.out.println(LOG_HEADER+"Writing player "+player.getName()+ " currently on World "+ world);
				}
				for (JsArrayWriter jsWriter : worlds.get(world)){
					jsWriter.write(args);
				}
			}else{
				System.out.println("World:"+world+" not found");
			}
		}
		//Close writers
		for (JsArrayWriter[] jsWriter_array :  worlds.values()){
			for(JsArrayWriter jsWriter : jsWriter_array){
				jsWriter.close();
			}
		}

	}

	/*
	 * Load config and init vairiables
	 */
	private void loadConfig(){
		config = new Configuration(new File(teclp.CONFIGURATION_FILE) );
		config.load();
		//Load debug flag
		debug=config.getBoolean("debug", false);
		if(debug){
			System.out.println(LOG_HEADER+"debug activated");
		}
		//Load worlds
		List<String> world_nodes = config.getKeys("worlds"); //get all world names

		if(world_nodes==null){
			System.out.println(LOG_HEADER+"YAML error");
		}else{
			for (String world_name : world_nodes){
				//Get the bukkit instance of this world.
				World world = this.getServer().getWorld(world_name);
				if(debug){
					System.out.println(LOG_HEADER+"world name: "+world_name+"");
				}
				if (world != null){
					this.addWorld(world);
				}else{
					System.out.println(LOG_HEADER+"World: "+world_name+"does not exist or is not yet loaded.");
				}
			}
		}
		//Load distance
		distance =  config.getDouble(teclp.DISTANCE_FOR_UPDATE, 10);
	}


	/*
	 * Add a player to the players list
	 */
	public void addPlayer(TECLPPlayer teclpPlayer) {
		players.add(teclpPlayer);
		if(debug){
			System.out.println(LOG_HEADER+"Player: "+teclpPlayer.getName()+" added.");
		}
	}


	/*
	 * get a player from the players list.
	 */
	public TECLPPlayer getPlayer(String name) {
		for (TECLPPlayer player : players){
			if(player.getName().equals(name)){
				return player;
			}
		}
		return null;
	}



	public double getDistance() {
		return distance;
	}



	public void removePlayer(String name) {
		players.remove(name);
		if(debug){
			System.out.println(LOG_HEADER+"Player: "+name+" removed");
		}

	}



	/*
	 * add a world to the output map
	 */
	public void addWorld(World world) {
		//Get the output paths specified for this world.
		List<String> paths =config.getStringList("worlds"+"."+world.getName(), null);
		JsArrayWriter[] jsWriter_array = new JsArrayWriter[paths.size()]; //Initialize the JsArrayWriters for each path.
		if(!paths.isEmpty()){
			for (String path : paths){
				if(debug){
					System.out.println(LOG_HEADER+"\t output: "+path);
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
    		System.out.println(LOG_HEADER+"Output for world "+world.getName()+" activated. Files: "+paths.toString());
		}
		
		
		
	}
}