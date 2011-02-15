package com.gmail.wallnuss.bukkit.teclp;

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
