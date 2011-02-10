/*
 * Copyright 2011 OrangyTang
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author OrangyTang
 * @see Apache License v2.0 (ASF2.0)
 */
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