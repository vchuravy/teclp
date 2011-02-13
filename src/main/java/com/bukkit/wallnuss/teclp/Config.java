/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: Config.java
 * Description:  Replacment for the standard Bukkit config wich is broken... .
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

package com.bukkit.wallnuss.teclp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;


/*
 * Constrouctor load the Config file and supplies the rest of the programm with their needed informations.
 */
public class Config {
	private double distance;
	private HashMap<String, String[]> worlds;
	
	public Config (String configPath) throws FileNotFoundException{
		Yaml yaml = new Yaml();
		FileReader io;
		io = new FileReader(new File(configPath));
		Object data = yaml.load(io);
		System.out.println(data.toString());
		
	}
	
}
