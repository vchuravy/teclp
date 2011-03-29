package org.vastness.bukkit.teclp.embedded;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;

public class InternalStaticServlet extends DefaultServlet {
    
    private String pathPrefix;
    
    public InternalStaticServlet (String pathPrefix){
        super();
        this.pathPrefix = pathPrefix;
    }
    public Resource getResource(String resource)
    {
       return super.getResource(pathPrefix+resource);
    }
}
