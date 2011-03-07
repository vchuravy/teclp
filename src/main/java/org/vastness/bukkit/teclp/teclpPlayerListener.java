/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclpPlayerListener.java
 * Description:  Handle events for all player related events.
 *   
 * @author Valentin Churavy, v.churavy [at] gmail [dot] com, Copyright (C) 2011.
 * @version v1.2
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

package org.vastness.bukkit.teclp ;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.vastness.bukkit.teclp.teclp;

public class teclpPlayerListener extends PlayerListener{
	private final teclp plugin; 
	
	public teclpPlayerListener(teclp t) {
        plugin = t;
    }
    
    @Override
    public void onPlayerJoin(PlayerEvent event) {
    	super.onPlayerJoin(event);
    	Player player = event.getPlayer(); 
    	plugin.addPlayer(new TECLPPlayer(plugin, player.getName())); //Add player to list
    	plugin.update();
    }
    
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
    	super.onPlayerMove(event);
    	if(plugin == null){
    		System.out.println("Weird things are happening");
    	}
    	if(plugin.getPlayer(event.getPlayer().getName()).moved()){
    		plugin.update();
    	}
    }
    
    public void onPlayerTeleport(PlayerMoveEvent event) {
    	super.onPlayerTeleport(event);
    	plugin.update();
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	super.onPlayerRespawn(event);
    	plugin.update();
    }

    public void onPlayerQuit(PlayerEvent event) {
    	super.onPlayerQuit(event);
    	plugin.removePlayer(event.getPlayer().getName());
    	plugin.update();
    }
    
}

