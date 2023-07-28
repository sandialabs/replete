
//////////////////////////
/// Sentence Generator ///
/// ------------------ ///
///    Derek Trumbo    ///
//////////////////////////

package replete.sg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * @author Derek Trumbo
 */

class Loader
{
   private static final int INPUT_FILE_FIELD_AMOUNT = 14;
   private static final String verbsFile = "english-verbs.txt";

    static ArrayList<Verb> loadEnglishVerbs() {
        File f = null;
        try {
            f = new File(Loader.class.getResource(verbsFile).toURI());
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
        BufferedReader input = null;
        ArrayList<Verb> verbs = new ArrayList<Verb>();

      if( !f.exists() )
      {
         Driver.err( "Could not locate english verbs file." );
         return null;
      }

      try
      {
         input = new BufferedReader( new FileReader( f ) );
         String line = null;

         while( ( line = input.readLine() ) != null )
         {
            line = line.trim();

            // Ignore blank lines and remove line comments.
            if( line.equals( "" ) || line.startsWith( "#" ) ) {
                continue;
            }
            if( line.indexOf( "#" ) != -1 ) {
                line = line.substring( 0, line.indexOf( "#" ) );
            }

            // Split the verb data into its parts.
            String[] filePieces = line.split( "," );
            if( filePieces.length > INPUT_FILE_FIELD_AMOUNT )
            {
               Driver.err( "Parse error.  Too many verb parts (verb=" + filePieces[ 0 ] + ").  Skipping verb." );
               continue;
            }

            // Load up the real verb array with as many parts as are in the file
            // (there are a lot of optional parts that may not be in the file).
            String[] realPieces = new String[ INPUT_FILE_FIELD_AMOUNT ];
            for( int p = 0; p < filePieces.length; p++ ) {
                realPieces[ p ] = filePieces[ p ].trim();
            }

            // Construct a new verb from the data.
            Verb newVerb = new Verb( realPieces );

            // Add the verb in alphabetical order.
            if( verbs.size() == 0 ) {
                verbs.add( newVerb );
            } else
            {
               int iv;
               for( iv = 0; iv < verbs.size(); iv++ ) {
                if( verbs.get( iv ).infinitive.compareTo( newVerb.infinitive ) > 0 ) {
                    break;
                }
            }
               verbs.add( iv, newVerb );
            }
         }

         if( verbs.size() == 0 )
         {
            Driver.err( "No verbs found in input file." );
            return null;
         } else {
            return verbs;
        }
      }
      catch( FileNotFoundException ex )
      {
         ex.printStackTrace();
         return null;
      }
      catch( IOException ex )
      {
         ex.printStackTrace();
         return null;
      }
      finally
      {
         try
         {
            if( input!= null ) {
                input.close();
            }
         }
         catch( IOException ex )
         {
            ex.printStackTrace();
            return null;
         }
      }
   }
}
