package data.access;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


public class LogReader {
	Queue<Log> queue = new LinkedList<Log>();

	
	public LogReader(String filePath)
	{
		BufferedReader reader = null;
    	try 
    	{
    		File file = new File(filePath);
    		reader = new BufferedReader(new FileReader(file));
    		String line;
    		Log log = null;
    		while ((line = reader.readLine()) != null)
    		{
    			log = generateLog(line);
    			queue.add(log);
    		}
    	}
    	
    	catch (IOException e) {System.out.println("Invalid file path to log");}
    	
    	finally 
    	{
    	    try {reader.close();} 
    	    catch (IOException e) {e.printStackTrace();}
    	}
	}
    	
    public Log getNextLog()
    {
    	return queue.poll();
    }
    
    public boolean hasNext()
    {
    	return queue.peek() != null;
    }

	private Log generateLog(String line) {
		String parts[] = line.split(" ");
		String name = parts[0];
		String from = parts[1];
		String to = parts[2];
		String policy = parts[3];
		int counter = Integer.parseInt(parts[4]);
		Log log = new Log(name,from,to,policy,counter);
		return log;
	}
}
