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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
