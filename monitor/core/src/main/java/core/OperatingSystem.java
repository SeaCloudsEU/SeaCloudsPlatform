package core;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class OperatingSystem {

	private static String getOsName(){

		return System.getProperty( "os.name" );
	}

	public static boolean isWindows(){

		return getOsName().startsWith( "Windows" );
	}

	public static boolean isUnix(){

		 return getOsName().startsWith( "Unix" );
	}
}
