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
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.vastness.bukkit.teclp.teclp;
import org.vastness.bukkit.teclp.tectonicus.TectonicusConfig;

public class WorldDataServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final teclp plugin;
    public WorldDataServlet(final teclp plugin){
        this.plugin = plugin;
    }
    
    
    protected void doGet (final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
       final String world = request.getParameter("world");
       final String json = createJson(world);
       
       response.setContentType("text/javascript");
       response.setStatus(HttpServletResponse.SC_OK);
       response.getWriter().print(json);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String createJson(String worldName){
        World world = null;
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
        final Map jsonObj = new LinkedHashMap();
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


    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getTectonicusConf() {
        final Map data = new LinkedHashMap();
        final TectonicusConfig tecConfig = TectonicusConfig.getInstance();
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


    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getSpawn(final World world) {
        final Map data = new LinkedHashMap();
        final Location loc = world.getSpawnLocation();
        data.put("x", new Integer(loc.getBlockX()));
        data.put("y", new Integer(loc.getBlockY()));
        data.put("z", new Integer(loc.getBlockZ()));
        return data;
    }

    @SuppressWarnings("rawtypes")
    private Map getWgRegions(final String worldName) {
        final JSONParser json = new JSONParser();
        Map data;
        try{
            data = (Map) json.parse(new FileReader(new File("plugins/WorldGuard/"+worldName+".regions.json")));
        } catch (final Exception e){
            data = new LinkedHashMap();
            //TODO
        }
        return data;
    }

    @SuppressWarnings({ "rawtypes" })
    private Map getSpecialPlaces() {
        final Map data = new LinkedHashMap();
        return data;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map getPlayers(World world) {
        List<Player> players =world.getPlayers();
        Map data = new LinkedHashMap();
        for (Player player : players){
            data.put(player.getName(), getPlayer(player));
        }
        return data;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map getPlayer(Player player) {
        Map data = new LinkedHashMap();
        data.put("health", new Integer(player.getHealth()));
        data.put("air", new Integer(player.getRemainingAir()));
        data.put("money", new Double(0.0)); //TODO
        
        final Location loc = player.getLocation();
        final Map positionMap = new LinkedHashMap<String, Integer>();
        positionMap.put("x", new Integer(loc.getBlockX()));
        positionMap.put("y", new Integer(loc.getBlockY()));
        positionMap.put("z", new Integer(loc.getBlockZ()));
        
        data.put("position", positionMap);
        
        final Map inventory = new LinkedHashMap<String, Integer>();
        final PlayerInventory pInv = player.getInventory();
        final ItemStack[] stack = pInv.getContents();
        for(int i = 0; i < stack.length; i++){
            inventory.put(new Integer(stack[i].getTypeId()).toString(), new Integer(stack[i].getAmount()));
        }
        
        data.put("inventory", inventory);
        data.put("permissions", null);//TODO
        return data;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map getWorlds() {
        final List<World> worldList = plugin.getServer().getWorlds();
        final Map data = new LinkedHashMap<String, Integer>();
        for (final World world : worldList ){
            data.put(world.getName(), new Integer(worldList.indexOf(world)));
        }
        return data;
    }

}
