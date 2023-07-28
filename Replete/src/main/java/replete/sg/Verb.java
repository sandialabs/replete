
//////////////////////////
/// Sentence Generator ///
/// ------------------ ///
///    Derek Trumbo    ///
//////////////////////////

package replete.sg;

/**
 * @author Derek Trumbo
 */

class Verb
{
   String infinitive                = null;
   String pres_part                 = null;
   String past_part                 = null;
   String past_tense                = null;
   Driver.Transitivity transitivity = Driver.Transitivity.INTRA;
   Driver.IndirectObject ioType     = Driver.IndirectObject.IO_NO;
   String[] adverbs                 = null;
   String[] directObjs              = null;
   String[] altSubjs                = null;
   String ioPrep                    = "to";
   boolean ioAfterOnly              = false;
   boolean ioReflexAllowed          = true;
   boolean adverbReqd               = false;
   String forbiddenTypes            = null;

   static String fields()
   {
      return "[inf, pres.p, past.p, past.t, trans, ioType, adv, do, asubj, ioPrep, ioAft, ioReflx, advReqd, ft]";
   }

   /* NOT USED
   Verb( String i, String p, String p2, String p3, Driver.Transitivity t, Driver.IndirectObject iot, String[] a, String[] o,
      String[] as, String iop, boolean iao, boolean ira, boolean advrq, String ft )
   {
      infinitive      = i;
      pres_part       = p;
      past_part       = p2;
      past_tense      = p3;
      transitivity    = t;
      ioType          = iot;
      adverbs         = a;
      directObjs      = o;
      altSubjs        = as;
      ioPrep          = iop;
      ioAfterOnly     = iao;
      ioReflexAllowed = ira;
      adverbReqd      = advrq;
      forbiddenTypes  = ft;
   }
   */

   Verb( String[] realPieces )
   {
      for( int p = 0; p < realPieces.length; p++ )
      {
         if( realPieces[ p ] == null ) {
            realPieces[ p ] = new String( "" );
        }

         String piece = realPieces[ p ];

         switch( p )
         {
            case 0: infinitive = piece; break;
            case 1: pres_part  = piece; break;
            case 2: past_part  = piece; break;
            case 3: past_tense = piece; break;

            case 4:
               if( piece.equals( "TRANS" ) ) {
                transitivity = Driver.Transitivity.TRANS;
            } else if( piece.equals( "TR_INTR" ) ) {
                transitivity = Driver.Transitivity.TR_INTR;
            }
               // else do nothing (remains INTRA)
               break;

            case 5:
               if( piece.equals( "IO_YES" ) ) {
                ioType = Driver.IndirectObject.IO_YES;
            } else if( piece.equals( "IO_OPT" ) ) {
                ioType = Driver.IndirectObject.IO_OPT;
            }
               // else do nothing (remains IO_NO)
               break;

            case 6:
               adverbs = parseListPiece( piece );
               adverbReqd = listRequired;
               break;

            case 7:
               directObjs = parseListPiece( piece );
               break;

            case 8:
               altSubjs = parseListPiece( piece );
               break;

            case 9:
               if( !piece.equals( "" ) ) {
                ioPrep = piece;
            }
               // else do nothing (remains "to")
               break;

            case 10:
               if( piece.equals( "true" ) ) {
                ioAfterOnly = true;
            }
               // else do nothing (remains false)
               break;

            case 11:
               if( piece.equals( "false" ) ) {
                ioReflexAllowed = false;
            }
               // else do nothing (remains true)
               break;

            case 12:
               if( piece.equals( "true" ) ) {
                adverbReqd = true;
            }
               // else do nothing (remains false)
               break;

            case 13:
               if( !piece.equals( "" ) && !piece.equals( "null" ) ) {
                forbiddenTypes = piece;
            }
               // else do nothing (remains null)
               break;
         }
      }
   }

   private static boolean listRequired;
   private static String[] parseListPiece( String piece )
   {
      String[] array = null;

      listRequired = false;

      if( !piece.equals( "" ) && !piece.equals( "null" ) )
      {
         if( piece.startsWith( "L(" ) ) {
            piece = piece.substring( 2 );
        }

         if( piece.startsWith( "R(" ) )
         {
            listRequired = true;
            piece = piece.substring( 2 );
         }

         if( piece.endsWith( ")" ) ) {
            piece = piece.substring( 0, piece.length() - 1 );
        }

         if( !piece.equals( "" ) )
         {
            array = piece.split( "/" );

            for( int i = 0; i < array.length; i++ ) {
                array[ i ] = array[ i ].trim();
            }
         }
      }

      return array;
   }

   private String listImage( String[] ss )
   {
      String image;

      if( ss == null ) {
        image = null;
    } else
      {
         image = "L(";
         for( String s : ss ) {
            image += s + "/";
        }
         image = image.substring( 0, image.length() - 1 );
         image += ")";
      }

      return image;
   }

   @Override
public String toString()
   {
      return toString( false );
   }

   public String toString( boolean shortForm )
   {
      String s_adv = listImage( adverbs );
      String s_obj = listImage( directObjs );
      String s_sbj = listImage( altSubjs );

      if( shortForm )
      {
         String sf = infinitive + ", " +
                     pres_part + ", " +
                     past_part + ", " +
                     past_tense + ", " +
                     transitivity.toString() + ", " +
                     ioType.toString() + ", " +
                     ( s_adv == null ? "" : s_adv ) + ", " +
                     ( s_obj == null ? "" : s_obj ) + ", " +
                     ( s_sbj == null ? "" : s_sbj ) + ", " +
                     ( ioPrep == "to" ? "" : ioPrep ) + ", " +
                     ( !ioAfterOnly ? "" : "" + ioAfterOnly ) + ", " +
                     ( ioReflexAllowed ? "" : "" + ioReflexAllowed ) + ", " +
                     ( !adverbReqd ? "" : "" + adverbReqd ) + ", " +
                     ( forbiddenTypes == null ? "" : "" + forbiddenTypes );

         while( sf.endsWith( ", " ) ) {
            sf = sf.substring( 0, sf.length() - 2 );
        }

         return sf;
      } else {
        return infinitive + ", " +
                pres_part + ", " +
                past_part + ", " +
                past_tense + ", " +
                transitivity.toString() + ", " +
                ioType.toString() + ", " +
                s_adv + ", " +
                s_obj + ", " +
                s_sbj + ", " +
                ioPrep + ", " +
                ioAfterOnly + ", " +
                ioReflexAllowed + ", " +
                adverbReqd + ", " +
                forbiddenTypes;
    }
   }
}

