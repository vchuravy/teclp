/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: TECLPPlayer.java
 * Description:  Stores the players data during runtime.
 *   
 * @author Valentin Churavy, v.churavy [at] gmail [dot] com, Copyright (C) 2011.
 * @version v1.22
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

package org.vastness.bukkit.teclp;

import java.util.HashMap;
import org.bukkit.entity.Player;

public class TECLPPlayer {

	private String name;
	private double worldX;
	private double worldY;
	private double worldZ;
	private int air;
	private int health;
	private teclp plugin;
	private double distance;
	private long world;
	
	public TECLPPlayer(teclp teclp, String playerName) {
		super();
		plugin=teclp;
		distance=plugin.getDistance();
		name = playerName;
	}
	
	public void update(){
		Player player =plugin.getServer().getPlayer(name);
		if (player != null){
			this.worldX = player.getLocation().getX();
			this.worldY = player.getLocation().getY();
			this.worldZ = player.getLocation().getZ();
			this.air = player.getRemainingAir();
			this.health = player.getHealth();
			this.world = player.getWorld().getId();
		}
	}

	public boolean moved (){
		Player player =plugin.getServer().getPlayer(name);
		if (player != null){
			if(diff_significant(worldX, player.getLocation().getX()) || diff_significant(worldY, player.getLocation().getY())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	private boolean diff_significant(double x, double y){
		double z = Math.abs(x-y);
		if (z >= distance){ 
			return true;
		}else {
			return false;
		}

	}

	public HashMap<String, String> getData() {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("name", "\"" + name + "\"");
		args.put("worldX", ""+ worldX );
		args.put("worldY", ""+ worldY);
		args.put("worldZ", ""+ worldZ);
		args.put("health", ""+ health);
		args.put("air", ""+ air);
		return args;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String playerName){
		this.name = playerName;
	}
	public Long getWorld(){
		return this.world;
	}
	
}
