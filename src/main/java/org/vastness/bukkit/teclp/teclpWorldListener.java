/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclpWorldListener.java
 * Description:  Handle events for all world related events.
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

package org.vastness.bukkit.teclp;

import org.bukkit.World;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.event.world.WorldListener;

public class teclpWorldListener extends WorldListener {
	
	private final teclp plugin;
	
	public teclpWorldListener(teclp teclp) {
		plugin = teclp;
	}

	public void onWorldLoaded(WorldEvent event){
		super.onWorldLoaded(event);
    	World world = event.getWorld();
    	System.out.println("World "+world.getName()+"loaded");
    	plugin.addWorld(world);
    	plugin.update();
    }
}
