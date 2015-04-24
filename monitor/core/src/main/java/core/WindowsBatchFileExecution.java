package core;

import java.io.File;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class WindowsBatchFileExecution {

	public static void execute( String batchFile ){

		try{

			File file = new File( batchFile  ); //JOptionPane.showMessageDialog( null, file.getAbsolutePath() );

			Process p = Runtime.getRuntime().exec( "cmd /C start /wait " + file.getAbsolutePath() );

			p.waitFor();
		}

		catch( Exception ex ){

			ex.printStackTrace();


			System.exit( -1 );
		}
	}
}
