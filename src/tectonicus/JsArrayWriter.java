package tectonicus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/*
 * @author: OrangyTang
 * All rights to the original developer.
 * To not reuse and redistribute with out the permission of the author.
 */

public class JsArrayWriter
{
	private OutputStream out;
	private PrintWriter writer;
	
	private boolean hasWritenEntry;
	
	public JsArrayWriter(File file, String arrayName) throws FileNotFoundException, IOException
	{
		if (file.exists())
			file.delete();
		
		out = new FileOutputStream(file);
		writer = new PrintWriter(out);
		
		writer.println("var "+arrayName+"=[");
	}
	
	public void write(Map<String, String> vars)
	{
		if (hasWritenEntry)
			writer.println(",");
		writer.println("\t{");
		
		boolean hasWrittenLine = false;
		
		for (String name : vars.keySet())
		{
			String value = vars.get(name);
			
			if (hasWrittenLine)
				writer.println(",");
			
			writer.print("\t\t");
			writer.print(name);
			writer.print(": ");
			writer.print(value);
			
			
			// ..
			
			hasWrittenLine = true;
		}
		
		writer.println();
		
		writer.print("\t}");
		hasWritenEntry = true;
	}
	
	public void close()
	{
		writer.println();
		writer.println("];");
		
		try
		{
			if (writer != null)
				writer.close();
		
			if (out != null)
				out.close();
		}
		catch (IOException e) {}
	}
}