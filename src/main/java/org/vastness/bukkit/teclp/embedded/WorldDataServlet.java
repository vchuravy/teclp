package org.vastness.bukkit.teclp.embedded;

import java.io.File;
import java.io.FileReader;
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
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.vastness.bukkit.teclp.teclp;
import org.vastness.bukkit.teclp.tectonicus.TectonicusConfig;

public class WorldDataServlet extends HttpServlet {
    
    private teclp plugin;
    public WorldDataServlet(teclp plugin){
        this.plugin = plugin;
    }
    
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       String world = request.getParameter("world");
       String json = createJson(world);
       
       response.setContentType("text/javascript");
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
        jsonObj.put("spawn", getSpawn(world));
        jsonObj.put("tectonicus", getTectonicusConf());
        jsonObj.put("time", new Long(world.getTime()));
        jsonObj.put("players", getPlayers(world));
        jsonObj.put("specialplaces", getSpecialPlaces());
        jsonObj.put("wgregions", getWgRegions(worldName));
        return JSONValue.toJSONString(jsonObj);
        
    }

    private Map getTectonicusConf() {
        Map data = new LinkedHashMap();
        TectonicusConfig tecConfig = TectonicusConfig.getInstance();
        data.put("tiletype", tecConfig.getTileType());
        data.put("showspawn", tecConfig.isShowSpawn());
        data.put("maxzoom", new Integer(tecConfig.getMaxZoom()));
        data.put("signsinitiallyvisible", new Boolean(tecConfig.isSignsInitiallyVisible()));
        data.put("playersinitiallyvisible", new Boolean(tecConfig.isPlayersInitiallyVisible()));
        data.put("placesinitiallyvisible", new Boolean(tecConfig.isPlacesInitiallyVisible()));
        data.put("pybukkitwebenabled", new Boolean(tecConfig.isPyBukkitWebEnabled()));
        data.put("chatenabled", new Boolean(tecConfig.isChatEnabled()));
        data.put("regionssinitiallyvisible", new Boolean(tecConfig.isRegionsInitiallyVisible()));
        return data;
    }

    private Map getSpawn(World world) {
        Map data = new LinkedHashMap();
        Location loc = world.getSpawnLocation();
        data.put("x", new Integer(loc.getBlockX()));
        data.put("y", new Integer(loc.getBlockY()));
        data.put("z", new Integer(loc.getBlockZ()));
        return data;
    }

    private Map getWgRegions(String worldName) {
        JSONParser json = new JSONParser();
        Map data;
        try{
            data = (Map) json.parse(new FileReader(new File("plugins/WorldGuard/"+worldName+".regions.json")));
        } catch (Exception e){
            data = new LinkedHashMap();
            //TODO
        }
        return data;
    }

    private Map getSpecialPlaces() {
        Map data = new LinkedHashMap();
        return data;
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
        Map positionMap = new LinkedHashMap<String, Integer>();
        positionMap.put("x", new Integer(loc.getBlockX()));
        positionMap.put("y", new Integer(loc.getBlockY()));
        positionMap.put("z", new Integer(loc.getBlockZ()));
        
        data.put("position", positionMap);
        
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
