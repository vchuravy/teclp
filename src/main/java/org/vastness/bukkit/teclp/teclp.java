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
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.vastness.bukkit.teclp.embedded.WorldDataServlet;
import org.vastness.bukkit.teclp.tectonicus.TectonicusConfig;

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
	public static final String CONFIGURATION_FILE ="plugins/teclp/config.yml";
	public static String LOG_HEADER="TECLP";
	
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	private boolean debug;
	private Configuration config; //Configuration engine
	private boolean useJSONOutput = false;
	
        private int port;
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
        public void onEnable() {
            PluginDescriptionFile pdfFile = this.getDescription();
            LOG_HEADER+=" "+pdfFile.getVersion()+" : ";
            loadConfig();

            TectonicusConfig tecConfig = TectonicusConfig.getInstance();
            tecConfig.loadConfig("plugins/teclp/tectonicus.yml");

            org.eclipse.jetty.server.Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new WorldDataServlet(this)), "/getData.js");

            try {
                server.start();
                server.join();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            // Register our events
            PluginManager pm = getServer().getPluginManager();

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

	private void loadConfig(){
		config = new Configuration(new File(teclp.CONFIGURATION_FILE) );
		config.load();
		//Load debug flag
		debug=config.getBoolean("debug", false);	    
		
		if(debug){
			System.out.println(LOG_HEADER+"debug activated");
		}
		port = config.getInt("port", 5555);
	}
}