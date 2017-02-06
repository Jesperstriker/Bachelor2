package data.generation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Tools {
	//Exceptions should probably be handled differently
	public static void WriteAndCreateFile(Path file, List<String> lines) throws IOException
	{
		Files.write(file, lines, Charset.forName("UTF-8"));
	}
}
