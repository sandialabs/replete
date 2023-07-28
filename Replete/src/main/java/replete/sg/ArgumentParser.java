
//////////////////////////
/// Sentence Generator ///
/// ------------------ ///
///    Derek Trumbo    ///
//////////////////////////

package replete.sg;

import java.util.ArrayList;

/**
 * @author Derek Trumbo
 */

// An object to pass all the argument information back and forth.
// Below are all the default values for the command line arguments.
class ArgumentGlob
{
   String errorMsg        = null;
   boolean showHelp       = false;
   boolean showVerbs      = false;
   boolean showVerbsSF    = false;
   boolean useInteractive = false;
   boolean noSave         = false;
   boolean webMode        = false;
   int numSentences       = 15;
   long randSeed          = -1L;
   String limitTypes      = "";
   String limitVerbs      = "";
}

class ArgumentParser
{
   static ArgumentGlob parse( String args[], ArrayList<Verb> verbs )
   {
      ArgumentGlob aGlob = new ArgumentGlob();
      boolean foundNum = false;

      for( String arg : args )
      {
         if( arg.equals( "-?" ) || arg.equals( "--help" ) ) {
            aGlob.showHelp = true;
        } else if( arg.equals( "-s" ) || arg.equals( "--show-verbs" ) ) {
            aGlob.showVerbs = true;
        } else if( arg.equals( "-a" ) || arg.equals( "--show-verbs-sf" ) ) {
            aGlob.showVerbsSF = true;
        } else if( arg.equals( "-i" ) || arg.equals( "--use-interactive" ) ) {
            aGlob.useInteractive = true;
        } else if( arg.equals( "-n" ) || arg.equals( "--no-save" ) ) {
            aGlob.noSave = true;
        } else if( arg.equals( "-w" ) || arg.equals( "--web-mode" ) ) {
            aGlob.webMode = true;
        } else if( arg.startsWith( "-r=" ) || arg.startsWith( "--random-seed=" ) )
         {
            int equal = arg.indexOf( "=" );
            String randSeedStr = arg.substring( equal + 1 );
            try
            {
               aGlob.randSeed = Long.parseLong( randSeedStr );
            }
            catch( Exception e )
            {
               Driver.err( "Could not parse <seed>." );
            }
         }

         else if( !arg.startsWith( "-" ) )
         {
            if( foundNum )
            {
               Driver.err( "Already have a number of sentences." );
               continue;
            }
            foundNum = true;
            try
            {
               aGlob.numSentences = Integer.parseInt( arg );
            }
            catch( Exception e )
            {
               Driver.err( "Could not parse <num-sentences>.  Defaulting to " + aGlob.numSentences + "." );
            }
         }

         else if( arg.startsWith( "-t=" ) || arg.startsWith( "--limit-types=" ) )
         {
            int equal = arg.indexOf( "=" );
            aGlob.limitTypes += "," + arg.substring( equal + 1 );
            if( aGlob.limitTypes.startsWith( "," ) ) {
                aGlob.limitTypes = aGlob.limitTypes.substring( 1 );
            }
            String[] ltypes = aGlob.limitTypes.split( "," );
            for( String ss : ltypes )
            {
               String ltype = ss.trim();
               boolean found = false;
               for( Driver.SentenceType st : Driver.SentenceType.values() ) {
                if( st.toString().equalsIgnoreCase( ltype ) )
                  {
                     found = true;
                     break;
                  }
            }
               if( !found )
               {
                  aGlob.errorMsg = "Unknown type '" + ltype + "'.";
                  return aGlob;
               }
            }
         }

         else if( arg.startsWith( "-v=" ) || arg.startsWith( "--limit-verbs=" ) )
         {
            int equal = arg.indexOf( "=" );
            aGlob.limitVerbs += "," + arg.substring( equal + 1 );
            if( aGlob.limitVerbs.startsWith( "," ) ) {
                aGlob.limitVerbs = aGlob.limitVerbs.substring( 1 );
            }
            String[] lverbs = aGlob.limitVerbs.split( "," );
            for( String ss : lverbs )
            {
               String lverb = ss.trim();
               boolean found = false;
               for( Verb v : verbs ) {
                if( v.infinitive.equalsIgnoreCase( lverb ) )
                  {
                     found = true;
                     break;
                  }
            }
               if( !found )
               {
                  aGlob.errorMsg = "Unknown verb '" + lverb + "'.";
                  return aGlob;
               }
            }
         }

         else if( arg.startsWith( "-o=" ) || arg.startsWith( "--other-options=" ) )
         {
            int equal = arg.indexOf( "=" );
            String otherOpts = arg.substring( equal + 1 );
            String[] oos = otherOpts.split( "," );
            for( String oo : oos )
            {
               String oo_name, oo_val;

               if( oo.endsWith( "+" ) || oo.endsWith( "-" ) )
               {
                  oo_name = oo.substring( 0, oo.length() - 1 );
                  oo_val = oo.substring( oo.length() - 1 );
               }
               else
               {
                  oo_name = oo;
                  oo_val = "+";
               }

               boolean found = false;
               for( String[] option : Utils.otherOptions )
               {
                  if( option[ 0 ].equalsIgnoreCase( oo_name ) )
                  {
                     found = true;
                     option[ 1 ] = oo_val;
                     break;
                  }
               }
               if( !found )
               {
                  aGlob.errorMsg = "Unknown other-option '" + oo_name + "'.";
                  return aGlob;
               }
            }
         }

         else
         {
            aGlob.errorMsg = "Unknown option '" + arg + "'.";
            return aGlob;
         }
      }

      if( aGlob.randSeed == -1L ) {
        aGlob.randSeed = System.nanoTime();
    }

      aGlob.randSeed &= 0xFFFF;

      if( aGlob.webMode )
      {
         aGlob.useInteractive = false;
         aGlob.noSave = true;
         if( aGlob.numSentences > 1000 ) {
            aGlob.numSentences = 1000;
        }
      }

      return aGlob;
   }
}

