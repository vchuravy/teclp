/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclpPlayerListener.java
 * Description:  Handle events for all Player related events.
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

package com.bukkit.wallnuss.teclp ;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.config.Configuration;

import tectonicus.JsArrayWriter;

import com.bukkit.wallnuss.teclp.teclp;


public class teclpPlayerListener extends PlayerListener {
    private final teclp plugin;
    private double distance;
    Configuration config;
    private HashMap<String, TECLPPlayer> players = new HashMap <String, TECLPPlayer>(); //PlayerList
    
    public teclpPlayerListener(teclp teclp) {
        plugin = teclp;
        config = new Configuration(new File(teclp.CONFIGURATION_FILE) );
        config.load();
        distance =  config.getDouble(teclp.DISTANCE_FOR_UPDATE, 10);
        System.out.println("TECLP: output: "+config.getString(teclp.PATH_TO_TEC_OUTPUT)+" update each "+ config.getDouble(teclp.DISTANCE_FOR_UPDATE, 30)+" blocks");
        
    }
    
    @Override
    public void onPlayerJoin(PlayerEvent event) {
    	super.onPlayerJoin(event);
    	Player p = event.getPlayer(); 
    	players.put(p.getName(), new TECLPPlayer(p, distance)); //Add player to list
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
    	JsArrayWriter jsWriter = null;
    	try
		{
			jsWriter = new JsArrayWriter(new File(config.getString(teclp.PATH_TO_TEC_OUTPUT)+"players.js"), "playerData");
			
			for(Map.Entry<String, TECLPPlayer> e : players.entrySet()){
				HashMap<String, String> args = new HashMap<String, String>();
				args = e.getValue().getData();
				jsWriter.write(args);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jsWriter != null)
				jsWriter.close();
		}
    	
		
	}

    public void onPlayerQuit(PlayerEvent event) {
    	super.onPlayerQuit(event);
    	players.remove(event.getPlayer().getName());
    	update();
    }
    
}

