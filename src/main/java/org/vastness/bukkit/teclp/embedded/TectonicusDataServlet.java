package org.vastness.bukkit.teclp.embedded;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;
import org.vastness.bukkit.teclp.teclp;

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
    
    @SuppressWarnings({ "rawtypes" })
    private String createJson(String worldName){
        final Map jsonObj = new LinkedHashMap();
       // jsonObj.put("worlds", getWorlds() );
        return JSONValue.toJSONString(jsonObj);
        
    }
}
