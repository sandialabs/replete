
//////////////////////////
/// Sentence Generator ///
/// ------------------ ///
///    Derek Trumbo    ///
//////////////////////////

package replete.sg;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Derek Trumbo
 */

// Handles options and randomness.
class Utils
{
   private static Random R;

   static String[][] otherOptions = {
      { "USENAMES",   "", "Always/Never use names for 3rd person." },
      { "NEGATED",    "", "Always/Never negate the sentence." },
      { "USEIO",      "", "Always/Never use an indirect object when it's optional (IO_OPT verbs)." },
      { "USEDO",      "", "Always/Never use a direct object when it's optional (TR_INTR verbs)." },
      { "USEPRONOUN", "", "Always/Never translate direct objects to their pronoun equivalents." },
      { "USEADVERB",  "", "Always/Never use an adverb when they are optional." },
      { "USEAGAIN",   "", "Always/Never use add 'again' to the end of a sentence." },
      { "IOAFTER",    "", "Always/Never place the IO after the DO when io-after-only unspecified\n                    for given verb."}
   };

   static void setRandom( long randSeed )
   {
      R = new Random( randSeed );
   }

   static boolean percentChoose( int pct )
   {
      return R.nextInt( 100 ) < pct;
   }

   static boolean percentChoose( int pct, String otherOptionName )
   {
      for( String[] option : otherOptions )
      {
         if( option[ 0 ].equalsIgnoreCase( otherOptionName ) )
         {
            if( option[ 1 ].equals( "" ) ) {
                return percentChoose( pct );
            } else if( option[ 1 ].equals( "+" ) ) {
                return true;
            } else if( option[ 1 ].equals( "-" ) ) {
                return false;
            }
         }
      }
      Driver.err( "Unknown option in percentChoose(int,String)." );
      System.exit( 0 );
      return false;
   }

   // Flip a coin, or let an option override the result.
   static boolean yesOrNo()                         { return percentChoose( 50 ); }
   static boolean yesOrNo( String otherOptionName ) { return percentChoose( 50, otherOptionName ); }

   // Choose randomly from a collection of objects.
   static String randString( String[] strs )     { return strs[ R.nextInt( strs.length ) ].trim(); }
   static Enum randEnum( Enum[] values )         { return values[ R.nextInt( values.length ) ]; }
   static Verb randVerb( ArrayList<Verb> verbs ) { return verbs.get( R.nextInt( verbs.size() ) ); }
}

