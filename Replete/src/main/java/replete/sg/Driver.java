
//////////////////////////
/// Sentence Generator ///
/// ------------------ ///
///    Derek Trumbo    ///
//////////////////////////

package replete.sg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Derek Trumbo
 */

/*
   Verbal Phrases (and whether they have been implemented in the SG)

   IMPL  PHRASE
   ----  ------
    X    querer + <inf> = to want to <inf>
    X    necesitar + <inf> = to need to <inf>
    X    poder + <inf> = to be able to <inf>
    X    deber + <inf> = "to should" <inf> = to be obligated to <inf>
    X    ir a + <inf> = to be going to <inf>
    X    volver a + <inf> = <subj> <fin> again (he sings again = vuelve a cantar)
    X    dejar de + <inf> = to stop <pres.p> (I stop breathing = dejo de respirar)
    X    dejar a alguien + <inf> = to allow s.o. to <inf>/to let s.o. <inf>
    X    tener que + <inf> = to have to <inf>
    X    pensar <inf> = to intend to <inf> / to plan on <pres.p>
    X    intentar <inf> = to try to <inf> ("lo intento hacer, pero no puedo")
    X    gustarle a alguien <inf> = to like to <inf> (me gusta correr)
    X    encantarle a alguien <inf> = to love to <inf>
         interesarle a alguien <inf> = to interest someone to <inf>
         agradarle a alguien <inf> = to please someone to <inf>
    X    empezar a <inf> = to begin to <inf> / to begin <pres.p>
         acabar de <inf> = to just <inf> (I just wrote a letter)
    X    continuar <pres.p> = to continue to <inf> / to continue <pres.p> /
            to keep <pres.p> (continua hablando = he continues talking)
    X    seguir <pres.p> = [same as "continuar"]
    X    haber (pres) <past.p> = to have <past.p>
    X    haber (fut) <past.p> = to will have <past.p>

#saber + <inf>
#tener ganas de
#preferir  + <inf>
#es necesario <inf>

*/

public class Driver
{

   /////////////
   /// ENUMS ///
   /////////////

   public enum Person
   {
      FIRST,
      SECOND,
      THIRD
   }
   public enum Number
   {
      SINGULAR,
      PLURAL
   }
   public enum Respect
   {
      FAMILIAR,
      FORMAL
   }
   public enum Gender
   {
      HE,
      SHE
   }
   public enum Transitivity
   {
      TRANS,
      INTRA,
      TR_INTR
   }
   public enum IndirectObject
   {
      IO_NO,
      IO_YES,
      IO_OPT
   }
   public enum SentenceType
   {
      PRESENT,       DO,
      COMMAND,       WANT_TO,
      TRY_TO,        NEED_TO,
      GOING_TO,      PROGRESS,
      QUESTION_PRES, PRES_SUBJ,
      LIKE_TO,       LOVE_TO,
      HAVE_TO,       SHOULD_TO,
      WILL,          WOULD,
      PERHAPS,       CAN,
      ALLOW,         STOP,
      INTEND_TO,     BEGIN_TO,
      CONTINUE_TO,   PRES_PERF,
      FUT_PERF,      PRETERITE
   }

   /////////////////
   /// CONSTANTS ///
   /////////////////

   private static final String sentenceFile = "sentences.txt";

   private static final String SP = " ";

   private static final String YOU_SFA = "sing/fam";
   private static final String YOU_SFO = "sing/form";
   private static final String YOU_PFA = "plu/fam";
   private static final String YOU_PFO = "plu/form";

   private static final String[][] subjPronouns =   { { "I", "You", "He" }, { "We", "Y'all", "They" } };
   private static final String[][] objPronouns =    { { "me", "you", "him" }, { "us", "you", "them" } };
   private static final String[][] reflexPronouns = { { "myself", "yourself", "himself" }, { "ourselves", "yourselves", "themselves" } };
   private static final String[][] possessivePronouns = { { "my", "your", "his" }, { "our", "your", "their" } };

   // Juan, Marcos, Antonio, Roberto, Erico, Jorge, Samuel, Jose, Guillermo, Daniel, David, Luis, Carlos
   // Maria, Patricia, Carolina, Ana, Cristina, Emilia, Gabriela, Helena, Marta, Rosana, Vivian
   private static final String[][] names = { { "John", "Mark", "Tony", "Robert", "Eric", "George",
                                               "Sam", "Joseph", "William", "Daniel", "David", "Louis", "Carl" },
                                             { "Mary", "Patricia", "Carol", "Anna", "Christine", "Emily",
                                               "Gabrielle", "Helen", "Martha", "Rose", "Vivian" } };

   //////////////
   /// FIELDS ///
   //////////////

   private static ArrayList<Verb> verbs;
   private static PrintWriter pw;
   private static ArgumentGlob aGlob;

   //////////////////////
   /// HELPER METHODS ///
   //////////////////////

   static void out_( String s )
   {
      if( pw != null ) {
        pw.print( s );
    }
      System.out.print( s );
   }
   static void out( String s )
   {
      if( pw != null ) {
        pw.println( s );
    }
      System.out.println( s );
   }

   static void err( String s )  { System.err.println( "ERROR: "   + s ); }
   static void warn( String s ) { System.err.println( "WARNING: " + s ); }

   private static String capitalizeFirst( String s )
   {
      return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 );
   }

   private static boolean is1stSing( Person p, Number n ) { return p.equals( Person.FIRST  ) && n.equals( Number.SINGULAR ); }
   private static boolean is1stPlu( Person p, Number n )  { return p.equals( Person.FIRST  ) && n.equals( Number.PLURAL   ); }
   private static boolean is2ndSing( Person p, Number n ) { return p.equals( Person.SECOND ) && n.equals( Number.SINGULAR ); }
   private static boolean is2ndPlu( Person p, Number n )  { return p.equals( Person.SECOND ) && n.equals( Number.PLURAL   ); }
   private static boolean is3rdSing( Person p, Number n ) { return p.equals( Person.THIRD  ) && n.equals( Number.SINGULAR ); }
   private static boolean is3rdPlu( Person p, Number n )  { return p.equals( Person.THIRD  ) && n.equals( Number.PLURAL   ); }

   /////////////
   /// USAGE ///
   /////////////

   private static void showUsage()
   {
      out( "Usage: java sg.Driver [<options>] [<num-sentences>]" );
      out( "* Example: java sg.Driver -x -t=WILL,WOULD -v=construct,go,run -o=NEGATE+ 30" );
      out( "* If not specified <num-sentences> defaults to 15." );
      out( "* The options available are:" );
      out( "  -?, --help" );
      out( "       Show this usage and exit." );
      out( "  -r=<seed>, --random-seed=<seed>" );
      out( "       Set the seed for the random number generator.  If unspecified, the current" );
      out( "       time is used.  When used to repeat a set of sentences, make sure the other" );
      out( "       options you had supplied are the same as well." );
      out( "  -s, --show-verbs" );
      out( "       Show loaded verbs and exit." );
      out( "  -a, --show-verbs-sf" );
      out( "       Show the short form of verbs (only valid with -s)." );
      out( "  -i, --use-interactive" );
      out( "       Wait for user to hit enter between sentences." );
      out( "  -n, --no-save" );
      out( "       Do not save generated sentences to './sentences.txt'" );
      out( "  -w, --web-mode" );
      out( "       Do not save generated sentences to './sentences.txt', 1000 sentences max, and" );
      out( "       interactive mode not available (all for obvious reasons)." );
      out( "  -v=<verbs>, --limit-verbs=<verbs>" );
      out( "       Limit chosen verbs to those in <verbs>, a comma-delimited list.  Use" );
      out( "       --show-verbs to show which verbs you can use." );
      out( "       Examples:  -v=go,run or for multi-word verbs use quotes: \"-v=look for,go\"" );
      out( "  -t=<types>, --limit-types=<types>" );
      out( "       Limit chosen sentence types to those in <types>, a comma-delimited list." );
      out( "       Example: -t=PRESENT,WILL,PROGRESS,CAN" );
      out( "       Valid sentence types:" );
      int col = 0;
      int prevlen = 0;
      for( SentenceType st : SentenceType.values() )
      {
         if( col == 0 )
         {
            out_( "          " + st );
            prevlen = st.toString().length();
         }
         else
         {
            for( int x = 0; x < 20 - prevlen; x++ ) {
                out_( " " );
            }
            out( "" + st );
         }
         col = 1 - col;
      }
      if( col == 1 ) {
        out( "" );
    }
      out( "  -o=<options>, --other-options=<options>" );
      out( "       Force certain yesOrNo() calls to a specific value always.  For example you" );
      out( "       can force the program to always negate the sentence or always use names." );
      out( "       <options> has the form: OPT[+|-],OPT[+|-],..." );
      out( "       If you leave off the + or -, + is assumed." );
      out( "       Examples: -o=USENAMES,NEGATED-  (sentences always use names, never negated)" );
      out( "       Valid options types:" );
      for( String[] option : Utils.otherOptions ) {
        out( "          " + option[ 0 ] + " - " + option[ 2 ] );
    }
   }

   ////////////
   /// MAIN ///
   ////////////

   public static void main( String args[] )
   {

      /////////////////////
      /// READ IN VERBS ///
      /////////////////////

      verbs = Loader.loadEnglishVerbs();

      if( verbs == null ) {
        return;
    }

      ///////////////////////
      /// PARSE ARGUMENTS ///
      ///////////////////////

      aGlob = ArgumentParser.parse( args, verbs );

      // There was an error in the parse.
      if( aGlob.errorMsg != null )
      {
         err( aGlob.errorMsg );
         showUsage();
         return;
      }

      // The -? help was given.
      else if( aGlob.showHelp )
      {
         showUsage();
         return;
      }

      // The -s flag was given.
      else if( aGlob.showVerbs )
      {
         out( "Loaded English verbs" );
         out( "Total: " + verbs.size() );
         out( "--------------------" );
         out( ">>FORMAT=" + Verb.fields() + "<<" );
         for( Verb v : verbs ) {
            out( v.toString( aGlob.showVerbsSF ) );
        }
         out( ">>FORMAT=" + Verb.fields() + "<<" );
         return;
      }

      Utils.setRandom( aGlob.randSeed );

      ///////////////////
      /// RUN PROGRAM ///
      ///////////////////

      try
      {
         if( aGlob.noSave ) {
            pw = null;
        } else {
            File f = null;
            f = new File(Driver.class.getResource(sentenceFile).toURI());
            f = new File(f.getAbsolutePath().replaceAll("/target/classes/", "/src/"));
            pw = new PrintWriter( new BufferedWriter( new FileWriter( f, true ) ) );
        }

         out( "Sentence Generator" );
         out( "==================" );
         out( "Date: " + new Date().toString() );
         out( "# Sentences: " + aGlob.numSentences );
         out_( "Arguments: " );
         if( args.length == 0 ) {
            out_( "<none>" );
        } else {
            for( String arg : args ) {
                out_( arg + " " );
            }
        }
         out( "" );
         out( "Random Seed: " + aGlob.randSeed );
         out( "" );

         // Implement a "safety net" feature as a quick work-around for the
         // --limit-types & <forbidden-types> problem.
         int safetyNet = 0;

         for( int s = 0; s < aGlob.numSentences; s++ )
         {
            String S = makeSentence();

            // It's possible a sentence could not be constructed with the verb chosen,
            // so discard the verb and try again.
            if( S.equals( "<ERROR>" ) )
            {
               if( safetyNet++ > 5000 )
               {
                  out( ( s + 1 ) + ".  <ERROR>: safetyNet stopped this dangerous loop.  Verb doesn't take available types." );
                  break;
               }
               s--;
               continue;
            }

            out( ( s + 1 ) + ".  " + S );

            if( aGlob.useInteractive )
            {
               out( "" );
               System.in.read();
            }
         }
      }
      catch( Exception e )
      {
         err( e.getMessage() );
         e.printStackTrace();
      }

      out( "" );

      if( pw != null ) {
        pw.close();
    }
   }

   private static String makeSentence()
   {

      //////////////////////
      /// CALCULATE VERB ///
      //////////////////////

      Verb verb = null;
      if( aGlob.limitVerbs.equals( "" ) ) {
        verb = Utils.randVerb( verbs );
    } else
      {
         String[] lVerbs = aGlob.limitVerbs.split( "," );
         boolean notFound = true;
         while( notFound )
         {
            String lVerb = Utils.randString( lVerbs );
            for( Verb v : verbs ) {
                if( v.infinitive.equalsIgnoreCase( lVerb ) )
                   {
                      verb = v;
                      notFound = false;
                      break;
                   }
            }
         }
      }

      ///////////////////////////////
      /// CALCULATE SENTENCE TYPE ///
      ///////////////////////////////

      SentenceType sType = null;
      final int MAX_ATTEMPTS = 100;
      int attempt = 0;
      boolean tryAgain;

      do
      {

         // Don't let the loop go forever because with the --limit-types option combined
         // with the <forbidden-types> option on a given verb, you could feasibly get into a
         // situation where no good type can be chosen.
         if( attempt++ == MAX_ATTEMPTS ) {
            return "<ERROR>";
        }

         if( aGlob.limitTypes.equals( "" ) ) {
            sType = ( SentenceType ) Utils.randEnum( SentenceType.values() );
        } else
         {
            String[] lTypes = aGlob.limitTypes.split( "," );
            String lType = Utils.randString( lTypes );
            sType = SentenceType.valueOf( lType.toUpperCase() );    // Has already been validated in ArgumentParser.
         }

         // Decide whether the random type chosen above (either from the whole set
         // or from those types specified on the command line) is valid according to
         // the <forbidden-types> option for the given verb.
         tryAgain =
            verb.forbiddenTypes != null &&
            (
               verb.forbiddenTypes.startsWith( "-" ) && verb.forbiddenTypes.indexOf( sType.toString() ) != -1
                 ||
               verb.forbiddenTypes.startsWith( "+" ) && verb.forbiddenTypes.indexOf( sType.toString() ) == -1
            );

      }
      while( tryAgain );

      ////////////////////////////
      /// CALCULATE # & PERSON ///
      ////////////////////////////

      Person subjPerson;
      Number subjNumber;
      boolean good;

      do
      {
         subjPerson = ( Person ) Utils.randEnum( Person.values() );
         subjNumber = ( Number ) Utils.randEnum( Number.values() );

         if( sType.equals( SentenceType.COMMAND ) ) {
            good = subjPerson.equals( Person.SECOND ) || is1stPlu( subjPerson, subjNumber );
        } else {
            good = true;
        }
      }
      while( !good );

      /////////////////////////////
      /// CALCULATE OTHER STUFF ///
      /////////////////////////////

      Respect subjRespect = ( Respect ) Utils.randEnum( Respect.values() );
      Gender  subjGender  = ( Gender ) Utils.randEnum( Gender.values() );

      boolean useNames = Utils.yesOrNo( "USENAMES" );
      boolean negated = Utils.yesOrNo( "NEGATED" );

      // The BEGIN_TO sentence type doesn't really read well when negated.
      if( sType.equals( SentenceType.BEGIN_TO ) ) {
        negated = false;
    }

      //////////////////////////////////////////
      /// CALCULATE SUBJECT PRONOUN AND TYPE ///
      //////////////////////////////////////////

      String subjPronoun;

      if( is3rdSing( subjPerson, subjNumber ) ) {
        subjPronoun = subjGender.equals( Gender.HE ) ? "He" : "She";
    } else {
        subjPronoun = subjPronouns[ subjNumber.ordinal() ][ subjPerson.ordinal() ];
    }

      String secondPersType = "";

      if( is2ndSing( subjPerson, subjNumber ) ) {
        secondPersType = "[S: " + ( subjRespect.equals( Respect.FAMILIAR ) ? YOU_SFA: YOU_SFO ) + "]";
    } else if( is2ndPlu( subjPerson, subjNumber ) ) {
        secondPersType = "[S: " + ( subjRespect.equals( Respect.FAMILIAR ) ? YOU_PFA : YOU_PFO ) + "]";
    }

      /////////////////////////
      /// CALCULATE SUBJECT ///
      /////////////////////////

      String subject;

      if( !useNames ) {
        subject = subjPronoun;
    } else
      {
         String name = Utils.randString( names[ subjGender.ordinal() ] );

         if( is3rdSing( subjPerson, subjNumber ) ) {
            subject = name;
        } else if( is3rdPlu( subjPerson, subjNumber ) ) {
            subject = name + SP + "and" + SP + chooseAnotherName( name );
        } else if( is1stPlu( subjPerson, subjNumber ) ) {
            subject = name + SP + "and I";
        } else
         {
            subject = subjPronoun;
            useNames = false;
         }
      }

      ///////////////////////////////
      /// ALTERNATE SUBJECT VERBS ///
      ///////////////////////////////

      // If this is an 'alternate subjects' verb, override the values chosen above.
      if( verb.altSubjs != null )
      {
         String subj = Utils.randString( verb.altSubjs );
         char num = subj.charAt( 0 );
         subject = subj.substring( 1 );

         subjPerson = Person.THIRD;                                       // Always third person.
         subjNumber = ( num == '.' ? Number.SINGULAR : Number.PLURAL );   // Choose number.
         subjGender = Gender.HE;                                          // Irrelevant.
         subjRespect = Respect.FAMILIAR;                                  // Irrelevant.
         useNames = Utils.yesOrNo( "USENAMES" );                          // Recalculate.
         secondPersType = "";
      }

      //////////////////////////
      /// CALCULATE SENTENCE ///
      //////////////////////////

      String sentence;
      String principalPart;
      String temp;
      ObjectGlob allowGlob = null;

      switch( sType )
      {
         case PRESENT:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( principalPart, subjPerson, subjNumber, negated );
            break;

         case DO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "do", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case COMMAND:
            principalPart = verb.infinitive;

            if( is1stPlu( subjPerson, subjNumber ) ) {
                sentence = "Let's" + ( negated ? SP + "not" : "" ) + SP + principalPart;
            } else
            {
               if( !negated ) {
                sentence = capitalizeFirst( principalPart );
            } else {
                sentence = "Don't" + SP + principalPart;
            }
            }

            break;

         case WANT_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "want", subjPerson, subjNumber, negated ) + SP + "to" + SP + principalPart;
            break;

         case NEED_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "need", subjPerson, subjNumber, negated ) + SP + "to" + SP + principalPart;
            break;

         case TRY_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "try", subjPerson, subjNumber, negated ) + SP + "to" + SP + principalPart;
            break;

         case LIKE_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "like", subjPerson, subjNumber, negated ) + SP + "to" + SP + principalPart;
            break;

         case LOVE_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "love", subjPerson, subjNumber, negated ) + SP + "to" + SP + principalPart;
            break;

         case GOING_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "be", subjPerson, subjNumber, negated ) + SP + "going to" + SP + principalPart;
            break;

         case PROGRESS:
            principalPart = verb.pres_part;
            sentence = subject + SP + conjPresVerb( "be", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case QUESTION_PRES:
            principalPart = verb.infinitive;
            if( !useNames && !subject.equals( "I" ) ) {
                subject = subject.toLowerCase();
            }
            if( principalPart.equals( "be" ) ) {
                sentence = conjPresVerb( "be", subjPerson, subjNumber, false ) + SP + subject + ( negated ? SP + "not" : "" );
            } else {
                sentence = conjPresVerb( "do", subjPerson, subjNumber, negated ) + SP + subject + SP + principalPart;
            }
            sentence = capitalizeFirst( sentence );
            break;

         case PRES_SUBJ:
            principalPart = verb.infinitive;
            if( !useNames && !subject.equals( "I" ) ) {
                subject = subject.toLowerCase();
            }
            temp = Utils.yesOrNo() ? "important" : "possible";
            sentence = "It's" + ( negated ? SP + "not" : "" ) + SP + temp + SP + "that" + SP + subject + SP + principalPart;
            break;

         case HAVE_TO:
            principalPart = verb.infinitive;
            boolean useAlt = Utils.yesOrNo();
            String altText;
            String regText;
            if( !negated )
            {
               regText = conjPresVerb( "have", subjPerson, subjNumber, negated ) + SP + "to";
               altText = "must";
            }
            else
            {
               regText = conjPresVerb( "do", subjPerson, subjNumber, negated ) + SP + "have to";
               altText = conjPresVerb( "be", subjPerson, subjNumber, negated ) + SP + "obligated to";
            }
            sentence = subject + SP + ( useAlt ? altText : regText ) + SP + principalPart;
            break;

         case SHOULD_TO:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "should", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case WILL:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "will", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case WOULD:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "would", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case PERHAPS:
            principalPart = verb.infinitive;
            if( !useNames && !subject.equals( "I" ) ) {
                subject = subject.toLowerCase();
            }
            temp = Utils.yesOrNo() ? "Perhaps" : "Maybe";
            sentence = temp + SP + subject + SP + conjPresVerb( principalPart, subjPerson, subjNumber, negated );
            break;

         case CAN:
            principalPart = verb.infinitive;
            sentence = subject + SP + conjPresVerb( "can", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case ALLOW:
            principalPart = verb.infinitive;
            String[] aWords = { "let", "allow", "permit" };
            String aWord = Utils.randString( aWords );
            allowGlob = makePersonObject( "DO", subjPerson, subjNumber, subjRespect, subjGender, sType, true );
            String objDO = allowGlob.image;
            String[] doParts = objDO.split( "," );
            secondPersType += doParts[1].trim();
            objDO = doParts[0];
            sentence = subject + SP + conjPresVerb( aWord, subjPerson, subjNumber, negated );
            sentence += SP + objDO;
            if( !aWord.equals( "let" ) ) {
                sentence += SP + "to";
            }
            sentence += SP + principalPart;
            break;

         case STOP:
            principalPart = verb.pres_part;
            sentence = subject + SP + conjPresVerb( "stop", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case INTEND_TO:
            // "I intend on jumping"      // "I plan on jumping"
            // "I intend to jump"         // "I plan to jump"
            boolean useOn = Utils.yesOrNo();
            temp = Utils.yesOrNo() ? "intend" : "plan";
            if( useOn ) {
                sentence = subject + SP + conjPresVerb( temp, subjPerson, subjNumber, negated ) + SP + "on" + SP + verb.pres_part;
            } else {
                sentence = subject + SP + conjPresVerb( temp, subjPerson, subjNumber, negated ) + SP + "to" + SP + verb.infinitive;
            }
            break;

         case BEGIN_TO:
            if( Utils.yesOrNo() ) {
                sentence = subject + SP + conjPresVerb( "begin", subjPerson, subjNumber, negated ) + SP + verb.pres_part;
            } else {
                sentence = subject + SP + conjPresVerb( "begin", subjPerson, subjNumber, negated ) + SP + "to" + SP + verb.infinitive;
            }
            break;

         case CONTINUE_TO:
            if( Utils.yesOrNo() && !negated ) {
                sentence = subject + SP + conjPresVerb( "keep", subjPerson, subjNumber, negated ) + SP + verb.pres_part;
            } else
            {
               if( Utils.yesOrNo() ) {
                sentence = subject + SP + conjPresVerb( "continue", subjPerson, subjNumber, negated ) + SP + verb.pres_part;
            } else {
                sentence = subject + SP + conjPresVerb( "continue", subjPerson, subjNumber, negated ) + SP + "to" + SP + verb.infinitive;
            }
            }
            break;

         case PRES_PERF:
            principalPart = verb.past_part;
            sentence = subject + SP + conjPresVerb( "have", subjPerson, subjNumber, negated ) + SP + principalPart;
            break;

         case FUT_PERF:
            principalPart = verb.past_part;
            sentence = subject + SP + conjPresVerb( "will", subjPerson, subjNumber, negated ) + SP + "have" + SP + principalPart;
            break;

         case PRETERITE:
            principalPart = verb.past_tense;
            sentence = subject + SP + principalPart;
            break;

         default:
            sentence = "<ERROR>";
      }

      /////////////////////////////////////
      /// CALCULATE ADVERBS/DIR OBJECTS ///
      /////////////////////////////////////

      // Decide whether or not to add IO.
      boolean addIO = verb.ioType.equals( IndirectObject.IO_YES ) ||
         verb.ioType.equals( IndirectObject.IO_OPT ) && Utils.yesOrNo( "USEIO" );

      // Decide whether or not to add DO.
      boolean addDO = verb.directObjs != null &&
         ( verb.transitivity.equals( Transitivity.TRANS ) ||
            verb.transitivity.equals( Transitivity.TR_INTR ) && Utils.yesOrNo( "USEDO" ) );

      // Decide where the IO will go.
      boolean ioAfter = addDO && Utils.yesOrNo( "IOAFTER" );
      if( verb.ioAfterOnly ) {
        ioAfter = true;
    }

      // Avoid sentences like "John reads me."
      if( verb.infinitive.equals( "read" ) && !addDO && addIO ) {
        ioAfter = true;
    }

      // Add IO before the DO.
      if( addIO && !ioAfter )
      {
         ObjectGlob ioGlob = makePersonObject( "IO", subjPerson, subjNumber, subjRespect, subjGender, sType, verb.ioReflexAllowed );
         String io = ioGlob.image;
         String[] ioParts = io.split( "," );
         secondPersType += ioParts[ 1 ].trim();
         sentence += SP + ioParts[ 0 ];
      }

      // Add DO.
      if( addDO )
      {

         // Choose and parse out different parts of the direct object.
         // Form:   (*|.|:)<obj>[;<prn>]
         // Where <obj> can contain two special characters, & and ^.
         String obj = Utils.randString( verb.directObjs );
         char num = obj.charAt( 0 );                      // (*|.|:)
         obj = obj.substring( 1 );                        // <obj>[;<prn>]
         int semi = obj.indexOf( ";" );                   // Index of ';'
         String prn = "";
         if( semi != -1 )
         {
            String newObj = obj.substring( 0, semi );     // <obj>
            prn = obj.substring( semi + 1 );              // <prn>
            obj = newObj;
         }

         // Perform & substitution.
         obj = doSubsPossessivePronoun( obj, subjPerson, subjNumber, subjGender );

         // Perform ^ substitution.
         if( sType.equals( SentenceType.ALLOW ) ) {
            obj = doSubsReflexivePronoun( obj, allowGlob.objPerson, allowGlob.objNumber, allowGlob.objGender );
        } else {
            obj = doSubsReflexivePronoun( obj, subjPerson, subjNumber, subjGender );
        }

         // Replace DO with its pronoun form.
         if( Utils.yesOrNo( "USEPRONOUN" ) ) {
            obj = doReplPronoun( obj, prn, num, semi );
        }

         sentence += SP + obj;
      }

      // Add IO after the DO.
      if( addIO && ioAfter )
      {
         ObjectGlob ioGlob = makePersonObject( "IO", subjPerson, subjNumber, subjRespect, subjGender, sType, verb.ioReflexAllowed );
         String io = ioGlob.image;
         String[] ioParts = io.split( "," );
         secondPersType += ioParts[ 1 ].trim();
         sentence += SP + verb.ioPrep + SP + ioParts[ 0 ];
      }

      // Add adverbs.
      if( verb.adverbs != null && ( verb.adverbReqd || Utils.yesOrNo( "USEADVERB" ) ) ) {
        sentence += SP + Utils.randString( verb.adverbs );
    }

      // Add 'again'.
      if( Utils.percentChoose( 7, "USEAGAIN" ) ) {
        sentence += SP + "again";
    }

      /////////////////////////////
      /// CALCULATE PUNCTUATION ///
      /////////////////////////////

      if( sType.equals( SentenceType.COMMAND ) ) {
        sentence += "!";
    } else if( sType.equals( SentenceType.QUESTION_PRES ) ) {
        sentence += "?";
    } else {
        sentence += ".";
    }

      // Add 2nd person type.
      sentence += "  " + secondPersType;

      return sentence;
   }

   private static String conjPresVerb( String verb, Person person, Number number, boolean negated )
   {
      if( verb.equalsIgnoreCase( "be" ) )
      {
         String neg = negated ? SP + "not" : "";

         if( is3rdSing( person, number ) ) {
            return "is" + neg;
        } else if( is1stSing( person, number ) ) {
            return "am" + neg;
        } else {
            return "are" + neg;
        }
      }
      else if( verb.equalsIgnoreCase( "do" ) )
      {
         String neg = negated ? "n't" : "";

         if( is3rdSing( person, number ) ) {
            return "does" + neg;
        } else {
            return "do" + neg;
        }
      }
      else if( verb.equalsIgnoreCase( "have" ) )
      {
         String neg = negated ? "n't" : "";

         if( is3rdSing( person, number ) ) {
            return "has" + neg;
        } else {
            return "have" + neg;
        }
      }
      else if( verb.equalsIgnoreCase( "can" ) )
      {
         String neg = negated ? "'t" : "";

         return "can" + neg;
      }
      else if( verb.equalsIgnoreCase( "would" ) )
      {
         String neg = negated ? "n't" : "";

         return "would" + neg;
      }
      else if( verb.equalsIgnoreCase( "should" ) )
      {
         String neg = negated ? "n't" : "";

         return "should" + neg;
      }
      else if( verb.equalsIgnoreCase( "will" ) )
      {

         String ret = negated ? "won't" : "will";

         return ret;
      }

      // All other verbs.
      else
      {

         // I don't swim.
         if( negated )
         {
            if( is3rdSing( person, number ) ) {
                return "doesn't" + SP + verb;
            } else {
                return "don't" + SP + verb;
            }
         }

         // He marches.
         else
         {

            // Put the 's' onto the end of the verb.
            if( is3rdSing( person, number ) )
            {
               if( verb.indexOf( SP ) != -1 ) {
                return conj3rdSingPresent( verb.substring( 0, verb.indexOf( SP ) ) ) + verb.substring( verb.indexOf( SP ) );
            } else {
                return conj3rdSingPresent( verb );
            }
            } else {
                return verb;
            }
         }
      }
   }

   // Conjugate the 3rd person singular present tense form.  When you add the 's'
   // onto the end of the verb, you have to check some different variations.
   private static String conj3rdSingPresent( String s )
   {
      if( s.endsWith( "y" ) )
      {
         if( "aeiou".indexOf( s.charAt( s.length() - 2 ) ) != -1 ) {
            return s + "s";
        } else {
            return s.substring( 0, s.length() - 1 ) + "ies";
        }
      }
      else if( s.endsWith( "ch" ) ) {
        return s + "es";
    } else if( s.endsWith( "s" ) ) {
        return s + "es";
    } else {
        return s + "s";
    }
   }

   private static ObjectGlob makePersonObject( String id, Person subjPerson, Number subjNumber,
                                               Respect subjRespect, Gender subjGender, SentenceType sType,
                                               boolean objReflexAllowed )
   {
      Person  objPerson;
      Number  objNumber;
      Respect objRespect = subjRespect;
      Gender  objGender  = ( Gender ) Utils.randEnum( Gender.values() );
      boolean useNames   = Utils.yesOrNo( "USENAMES" );

      //////////////////////////////////
      /// CALCULATE PRONOUN AND TYPE ///
      //////////////////////////////////

      do
      {
         do
         {
            objPerson = ( Person ) Utils.randEnum( Person.values() );
            objNumber = ( Number ) Utils.randEnum( Number.values() );
         }
         while( ( is1stPlu( subjPerson, subjNumber ) && is1stSing( objPerson, objNumber ) ) ||
                ( id.equals( "IO" ) && is1stSing( subjPerson, subjNumber ) && is1stPlu( objPerson, objNumber ) ) );

         if( subjPerson.equals( Person.SECOND ) && objPerson.equals( Person.SECOND ) ) {
            objNumber = subjNumber;
        }

         if( is1stPlu( subjPerson, subjNumber ) && sType.equals( SentenceType.COMMAND ) ) {
            objPerson = Person.THIRD;
        }
      }
      while( !objReflexAllowed && objPerson.equals( subjPerson ) && objNumber.equals( subjNumber ) );

      String objPronoun;
      String secondPersType = " ";

      if( is1stPlu( objPerson, objNumber ) && is1stPlu( subjPerson, subjNumber ) ) {
        useNames = false;
    }

      if( is3rdSing( objPerson, objNumber ) ) {
        objPronoun = objGender.equals( Gender.HE ) ? "him" : "her";
    } else {
        objPronoun = objPronouns[ objNumber.ordinal() ][ objPerson.ordinal() ];
    }

      if( is2ndSing( objPerson, objNumber ) ) {
        secondPersType = "[" + id + ": " + ( objRespect.equals( Respect.FAMILIAR ) ? YOU_SFA : YOU_SFO ) + "]";
    } else if( is2ndPlu( objPerson, objNumber ) ) {
        secondPersType = "[" + id + ": " + ( objRespect.equals( Respect.FAMILIAR ) ? YOU_PFA : YOU_PFO ) + "]";
    }

      String obj;

      if( !useNames )
      {
         obj = objPronoun;
         if( objPerson.equals( subjPerson ) && objNumber.equals( subjNumber ) ) {
            if( is3rdSing( objPerson, objNumber ) && subjGender.equals( Gender.SHE ) ) {
                obj = "herself";
            } else {
                obj = reflexPronouns[ objNumber.ordinal() ][ objPerson.ordinal() ];
            }
        }
      }
      else
      {
         String name = Utils.randString( names[ objGender.ordinal() ] );

         if( is3rdSing( objPerson, objNumber ) ) {
            obj = name;
        } else if( is3rdPlu( objPerson, objNumber ) ) {
            obj = name + SP + "and" + SP + chooseAnotherName( name );
        } else if( is1stPlu( objPerson, objNumber ) ) {
            obj = name + SP + "and me";
        } else
         {
            obj = objPronoun;
            if( objPerson.equals( subjPerson ) && objNumber.equals( subjNumber ) ) {
                if( is3rdSing( objPerson, objNumber ) && subjGender.equals( Gender.SHE ) ) {
                    obj = "herself";
                } else {
                    obj = reflexPronouns[ objNumber.ordinal() ][ objPerson.ordinal() ];
                }
            }
         }
      }

      ObjectGlob glob = new ObjectGlob();
      glob.objPerson  = objPerson;
      glob.objNumber  = objNumber;
      glob.objRespect = objRespect;
      glob.objGender  = objGender;
      glob.useNames   = useNames;
      glob.image      = obj + "," + secondPersType;

      return glob;
   }

   private static String chooseAnotherName( String name1 )
   {
      Gender gender2;
      String name2;

      do
      {
         gender2 = ( Gender ) Utils.randEnum( Gender.values() );
         name2 = Utils.randString( names[ gender2.ordinal() ] );
      }
      while( name1.equals( name2 ) );

      return name2;
   }

   private static String doSubsPossessivePronoun( String obj, Person subjPerson, Number subjNumber, Gender subjGender )
   {
      int amp = obj.indexOf( "&" );

      if( amp == -1 ) {
        return obj;
    }

      String pp;
      if( is3rdSing( subjPerson, subjNumber ) && subjGender.equals( Gender.SHE ) ) {
        pp = "her";
    } else {
        pp = possessivePronouns[ subjNumber.ordinal() ][ subjPerson.ordinal() ];
    }

      return obj.substring( 0, amp ) + pp + obj.substring( amp + 1 );
   }

   private static String doSubsReflexivePronoun( String obj, Person subjPerson, Number subjNumber, Gender subjGender )
   {
      int dol = obj.indexOf( "^" );

      if( dol == -1 ) {
        return obj;
    }

      String rp;
      if( is3rdSing( subjPerson, subjNumber ) && subjGender.equals( Gender.SHE ) ) {
        rp = "herself";
    } else {
        rp = reflexPronouns[ subjNumber.ordinal() ][ subjPerson.ordinal() ];
    }

      return obj.substring( 0, dol ) + rp + obj.substring( dol + 1 );
   }

   private static String doReplPronoun( String obj, String prn, char num, int semi )
   {
      String antecedant = SP + "(" + obj + ")";

      if( semi != -1 ) {
        obj = prn + antecedant;
    } else if( num == '.' )
      {
         if( !obj.equals( "it" ) ) {
            obj = "it" + antecedant;
        }
      }
      else if( num == ':' )
      {
         if( !obj.equals( "them" ) ) {
            obj = "them" + antecedant;
        }
      }
      else if( num == '*' )
      {
         // nothing
      } else {
        warn( "Invalid noun character." );
    }

      return obj;
   }

   private static class ObjectGlob
   {
      String image;
      Person objPerson;
      Number objNumber;
      Respect objRespect;
      Gender objGender;
      boolean useNames;
   }
}

