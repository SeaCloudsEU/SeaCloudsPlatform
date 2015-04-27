package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class TxtFileWriter {

	static public String writeWithChangingLine( String text, String file ){//It takes as input a file whose lines are separated by "+" and replace them with "\n".

		text = text.replaceAll( "+", "\n" );


    	Writer output = null;

    	File f = new File( file );


        try{

        	f.createNewFile();

        	output = new BufferedWriter( new FileWriter( file ) );

            output.write( text );
        }

        catch ( IOException ex ){ System.err.println( "[ERROR] TxtFileWriter: " + file + " does not exist!" );  }

        finally{

        	try{ if ( output != null ) output.close(); }

            catch( IOException ex ){ ex.printStackTrace(); }
        }


        return text;
	}

	static public void write( String text, String file ){

    	Writer output = null;

    	File f = new File( file );


        try{

        	f.createNewFile();

        	output = new BufferedWriter( new FileWriter( file ) );

            output.write( text );
        }

        catch ( IOException ex ){ System.err.println( "[ERROR] TxtFileWriter: " + file + " does not exist!" );  }

        finally{

        	try{ if ( output != null ) output.close(); }

            catch( IOException ex ){ ex.printStackTrace(); }
        }
	}

	public static void copyFile( File sourceFile, File destFile ) throws IOException{

	    if( ! destFile.exists() ) destFile.createNewFile();

	    FileChannel source = null, destination = null;

	    try{

	    	source = new FileInputStream( sourceFile ).getChannel();

	        destination = new FileOutputStream( destFile ).getChannel();

	        destination.transferFrom( source, 0, source.size() );
	    }

	    finally{

	        if( source != null ) source.close();

	        if( destination != null ) destination.close();
	    }
	}

	public static void copyFolder( File source, File destination ){

	    if( source.isDirectory() ){

	        if( ! destination.exists() ) destination.mkdirs();

	        String files[] = source.list();

	        for( String file : files ){

	            File srcFile = new File( source, file );

	            File destFile = new File( destination, file );


	            copyFolder( srcFile, destFile );
	        }
	    }

	    else{

	        try{

	        	copyFile( source, destination );
	        }

	        catch( Exception ex ){

	           ex.printStackTrace();
	        }
	    }
	}


	//Main.
	public static void main( String[] args ){

		String LOCAL_FUSEKI = "lib/jena-fuseki-1.1.1";
		String SERVER_FUSEKI = "./jena-fuseki-1.1.1";


		copyFolder( new File( LOCAL_FUSEKI ), new File( SERVER_FUSEKI ) );
	}
}
