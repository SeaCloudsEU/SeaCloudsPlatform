package core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class TxtFileReader {

	public static String readPureContent( File file ){

    	String contents = "", strLine = null;

        try{

        	FileInputStream fstream = new FileInputStream( file );

            DataInputStream in = new DataInputStream( fstream );

            BufferedReader br = new BufferedReader( new InputStreamReader( in ) );


            while( ( strLine = br.readLine() ) != null ) contents += strLine;


            in.close();
        }

        catch( Exception ex ){

        	ex.printStackTrace();
        }


        return contents;
    }

	public static String readWithoutChangingLine( File file ){

    	String contents = "", strLine = null;

        try{

        	FileInputStream fstream = new FileInputStream( file );

            DataInputStream in = new DataInputStream( fstream );

            BufferedReader br = new BufferedReader( new InputStreamReader( in ) );


            while( ( strLine = br.readLine() ) != null ) contents += strLine + "+";


            in.close();
        }

        catch( Exception ex ){

        	ex.printStackTrace();
        }


        return contents;
    }

	public static String read( File file ){

    	String contents = "", strLine = null;

        try{

        	FileInputStream fstream = new FileInputStream( file );

            DataInputStream in = new DataInputStream( fstream );

            BufferedReader br = new BufferedReader( new InputStreamReader( in ) );


            while( ( strLine = br.readLine() ) != null ) contents += strLine + "\n";


            in.close();
        }

        catch( Exception ex ){

        	contents = null;
        }


        return contents;
    }
}
