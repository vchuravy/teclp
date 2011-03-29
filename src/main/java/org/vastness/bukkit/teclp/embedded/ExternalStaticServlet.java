package org.vastness.bukkit.teclp.embedded;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.URLResource;

public class ExternalStaticServlet extends DefaultServlet {
    String path;
    
    public ExternalStaticServlet(String path) {
        this.path = path;
    }

    public Resource getResource(String pathInContext)
         {
           try {
            return new FileResource(this.getClass().getClassLoader().getResource(path+pathInContext));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
         }
 
}
