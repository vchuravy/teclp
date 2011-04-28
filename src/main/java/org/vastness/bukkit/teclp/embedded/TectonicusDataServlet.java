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

public class TectonicusDataServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private final teclp plugin;
    public TectonicusDataServlet(final teclp plugin){
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String createJson(String worldName){
        final Map jsonObj = new LinkedHashMap();
       // jsonObj.put("worlds", getWorlds() );
        return JSONValue.toJSONString(jsonObj);
        
    }
}
