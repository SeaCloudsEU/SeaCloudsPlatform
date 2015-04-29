/**
 * Copyright 2014 SeaClouds
 * Contact: Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;

public class TxtFileWriter {

	static public String writeWithChangingLine( String text, String file ){

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
}
