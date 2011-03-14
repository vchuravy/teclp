package org.vastness.bukkit.teclp.embedded;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONValue;

public class WorldDataServlet extends HttpServlet {
    
    private JavaPlugin plugin;
    public WorldDataServlet(JavaPlugin plugin){
        this.plugin = plugin;
    }
    
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       String world = request.getParameter("world");
       String json = createJson(world);
       
       response.setContentType("test/javascript");
       response.setStatus(HttpServletResponse.SC_OK);
       response.getWriter().print(json);
    }
    
    private String createJson(String worldName){
        World world;
        if (worldName == null){
            world = plugin.getServer().getWorlds().get(0);
            worldName = world.getName();
        }else{
            world = plugin.getServer().getWorld(worldName);
            if (world == null){
                world = plugin.getServer().getWorlds().get(0);
                worldName = world.getName();
            }
        }
        Map jsonObj = new LinkedHashMap();
        jsonObj.put("worlds", getWorlds() );
        jsonObj.put("name", worldName);
        jsonObj.put("time", new Long(world.getTime()));
        jsonObj.put("players", getPlayers(world));
        jsonObj.put("specialplaces", getSpecialPlaces());
        jsonObj.put("wgregions", getWgRegions());
        return JSONValue.toJSONString(jsonObj);
        
    }

    private Object getWgRegions() {
        // TODO Auto-generated method stub
        return null;
    }

    private Object getSpecialPlaces() {
        // TODO Auto-generated method stub
        return null;
    }

    private Map getPlayers(World world) {
        List<Player> players =world.getPlayers();
        Map data = new LinkedHashMap();
        for (Player player : players){
            data.put(player.getName(), getPlayer(player));
        }
        return data;
    }

    private Map getPlayer(Player player) {
        Map data = new LinkedHashMap();
        data.put("health", new Integer(player.getHealth()));
        data.put("air", new Integer(player.getRemainingAir()));
        data.put("money", new Double(0.0)); //TODO
        
        Location loc = player.getLocation();
        Map postionMap = new LinkedHashMap<String, Integer>();
        postionMap.put("x", new Integer(loc.getBlockX()));
        postionMap.put("y", new Integer(loc.getBlockY()));
        postionMap.put("z", new Integer(loc.getBlockZ()));
        
        data.put("postion", postionMap);
        
        Map inventory = new LinkedHashMap<String, Integer>();
        PlayerInventory pInv = player.getInventory();
        ItemStack[] stack = pInv.getContents();
        for(int i = 0; i < stack.length; i++){
            inventory.put(new Integer(stack[i].getTypeId()).toString(), new Integer(stack[i].getAmount()));
        }
        
        data.put("inventory", inventory);
        data.put("permissions", null);//TODO
        return data;
    }

    private Map getWorlds() {
        List<World> worldList = plugin.getServer().getWorlds();
        Map data = new LinkedHashMap<String, Integer>();
        for (World world : worldList ){
            data.put(world.getName(), new Integer(worldList.indexOf(world)));
        }
        return data;
    }

}
