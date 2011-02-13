/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclpPlayerListener.java
 * Description:  Handle events for all Player related events.
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

package com.bukkit.wallnuss.teclp ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import tectonicus.JsArrayWriter;

import com.bukkit.wallnuss.teclp.teclp;


public class teclpPlayerListener extends PlayerListener {
    private final teclp plugin;
    private double distance;
    Configuration config;
    private HashMap<String, TECLPPlayer> players = new HashMap <String, TECLPPlayer>(); //PlayerList
    private HashMap<Long, JsArrayWriter[]> worlds = new HashMap<Long, JsArrayWriter[]>(); //TODO
    private boolean debug = true;//TODO
    
    public teclpPlayerListener(teclp t) {
        plugin = t;/*
        config = new Configuration(new File(teclp.CONFIGURATION_FILE) );
        config.load();
        
        config.setProperty("worlds.world2", "[C:\\dev\\bukkit\\plugins\\]");
        Map<String,ConfigurationNode> world_nodes = config.getNodes("worlds");
        if(world_nodes==null){
        	System.out.println("YAML error");
        }else{
        	System.out.println("Size of map:" +world_nodes.size());
        	System.out.println("Object of map:" +world_nodes.toString());
        	System.out.println("Values of map:" +world_nodes.values());
        	System.out.println(world_nodes.get("world"));
        }
        int i = 3;
        *//*
        for (Map.Entry<String, ConfigurationNode> entry : world_nodes.entrySet()){
        	System.out.println(i);
        	i++;
        	List<String> file_list = entry.getValue().getStringList(entry.getKey(), null);
        	System.out.println("List init success");
        	World world = plugin.getServer().getWorld(entry.getKey());
        	if(debug){
            	System.out.println("world name:"+entry.getKey()+"");
            }
        	if (world != null){
        		JsArrayWriter[] jsWriter_array = new JsArrayWriter[file_list.size()];
        		for(String file_name : file_list){
        			if(debug){
        	        	System.out.println("\t output:"+file_name);
        	        }
        			try {
						jsWriter_array[file_list.indexOf(file_name)] = new JsArrayWriter(new File(file_name+"players.js"), "playerData");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		
        		worlds.put(world.getId(), jsWriter_array);
        	}else{
        		System.out.println("World: "+entry.getKey()+"does not exist");
        	}
        }*/
        //distance =  config.getDouble(teclp.DISTANCE_FOR_UPDATE, 10);
        if(debug){
        	System.out.println("init done");
        }
    }
    
    @Override
    public void onPlayerJoin(PlayerEvent event) {
    	super.onPlayerJoin(event);
    	Player p = event.getPlayer(); 
    	players.put(p.getName(), new TECLPPlayer(p, distance, p.getWorld().getId())); //Add player to list
    	
    	update();
    }
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
    	super.onPlayerMove(event);
    	if(players.get(event.getPlayer().getName()).moved()){
    		update();
    	}
    }
    
    public void onPlayerTeleport(PlayerMoveEvent event) {
    	super.onPlayerTeleport(event);
    	update();
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	super.onPlayerRespawn(event);
    	update();
    }
    
    /*
     * Updates the player.js file.
     */
    private void update(){
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
				HashMap<String, String> args = new HashMap<String, String>();
				args = e.getValue().getData();
				long world = e.getValue().getWorld();
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

    public void onPlayerQuit(PlayerEvent event) {
    	super.onPlayerQuit(event);
    	players.remove(event.getPlayer().getName());
    	update();
    }
    
}

