/**
 * Project: teclp: Live player updates for tectonicus. A Bukkit plugin.
 * File name: teclpBlockListener.java
 * Description:  Handle events for all Block related events.
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
package com.gmail.wallnuss.bukkit.teclp;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class teclpBlockListener extends BlockListener {
    private final teclp plugin;

    public teclpBlockListener(final teclp teclp) {
        this.plugin = teclp;
    }

    //put all Block related code here
}
