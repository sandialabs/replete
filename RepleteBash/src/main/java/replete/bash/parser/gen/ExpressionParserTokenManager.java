/* Generated By:JJTree&JavaCC: Do not edit this line. ExpressionParserTokenManager.java */
package replete.bash.parser.gen;

/** Token Manager. */
public class ExpressionParserTokenManager implements ExpressionParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x8000000000L) != 0L) {
            return 21;
        }
         if ((active0 & 0x20000000L) != 0L)
         {
            jjmatchedKind = 18;
            return -1;
         }
         if ((active0 & 0x4000000000L) != 0L) {
            return 1;
        }
         if ((active0 & 0x3000L) != 0L)
         {
            jjmatchedKind = 14;
            return 44;
         }
         if ((active0 & 0xc3800000000L) != 0L) {
            return 15;
        }
         return -1;
      case 1:
         if ((active0 & 0x20000000L) != 0L)
         {
            if (jjmatchedPos == 0)
            {
               jjmatchedKind = 18;
               jjmatchedPos = 0;
            }
            return -1;
         }
         if ((active0 & 0x3000L) != 0L)
         {
            jjmatchedKind = 14;
            jjmatchedPos = 1;
            return 44;
         }
         return -1;
      case 2:
         if ((active0 & 0x3000L) != 0L)
         {
            jjmatchedKind = 14;
            jjmatchedPos = 2;
            return 44;
         }
         return -1;
      case 3:
         if ((active0 & 0x1000L) != 0L) {
            return 44;
        }
         if ((active0 & 0x2000L) != 0L)
         {
            jjmatchedKind = 14;
            jjmatchedPos = 3;
            return 44;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         jjmatchedKind = 41;
         return jjMoveStringLiteralDfa1_0(0x100000000L);
      case 37:
         return jjStopAtPos(0, 40);
      case 38:
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      case 40:
         return jjStopAtPos(0, 46);
      case 41:
         return jjStopAtPos(0, 47);
      case 42:
         return jjStartNfaWithStates_0(0, 37, 15);
      case 43:
         return jjStartNfaWithStates_0(0, 35, 15);
      case 44:
         return jjStopAtPos(0, 25);
      case 45:
         return jjStartNfaWithStates_0(0, 36, 15);
      case 46:
         return jjStartNfaWithStates_0(0, 38, 1);
      case 47:
         return jjStartNfaWithStates_0(0, 39, 21);
      case 59:
         return jjStopAtPos(0, 26);
      case 60:
         jjmatchedKind = 28;
         return jjMoveStringLiteralDfa1_0(0x40000000L);
      case 61:
         return jjMoveStringLiteralDfa1_0(0x20000000L);
      case 62:
         jjmatchedKind = 27;
         return jjMoveStringLiteralDfa1_0(0x80000000L);
      case 91:
         return jjStopAtPos(0, 44);
      case 93:
         return jjStopAtPos(0, 45);
      case 94:
         jjmatchedKind = 42;
         return jjMoveStringLiteralDfa1_0(0x80000000000L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x2000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x1000L);
      case 123:
         return jjStopAtPos(0, 48);
      case 124:
         return jjMoveStringLiteralDfa1_0(0x400000000L);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 38:
         if ((active0 & 0x200000000L) != 0L) {
            return jjStopAtPos(1, 33);
        }
         break;
      case 61:
         if ((active0 & 0x20000000L) != 0L) {
            return jjStopAtPos(1, 29);
        } else if ((active0 & 0x40000000L) != 0L) {
            return jjStopAtPos(1, 30);
        } else if ((active0 & 0x80000000L) != 0L) {
            return jjStopAtPos(1, 31);
        } else if ((active0 & 0x100000000L) != 0L) {
            return jjStopAtPos(1, 32);
        }
         break;
      case 94:
         if ((active0 & 0x80000000000L) != 0L) {
            return jjStopAtPos(1, 43);
        }
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000L);
      case 124:
         if ((active0 & 0x400000000L) != 0L) {
            return jjStopAtPos(1, 34);
        }
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L) {
    return jjStartNfa_0(0, old0);
}
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L) {
    return jjStartNfa_0(1, old0);
}
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x1000L) != 0L) {
            return jjStartNfaWithStates_0(3, 12, 44);
        }
         break;
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L) {
    return jjStartNfa_0(2, old0);
}
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x2000L) != 0L) {
            return jjStartNfaWithStates_0(4, 13, 44);
        }
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 44;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff) {
        ReInitRounds();
    }
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 7) {
                        kind = 7;
                    }
                     jjCheckNAddStates(0, 4);
                  }
                  else if (curChar == 47) {
                    jjCheckNAddStates(5, 7);
                } else if (curChar == 42) {
                    jjCheckNAdd(15);
                } else if (curChar == 45) {
                    jjCheckNAdd(15);
                } else if (curChar == 43) {
                    jjCheckNAdd(15);
                } else if (curChar == 61)
                  {
                     if (kind > 18) {
                        kind = 18;
                    }
                  }
                  else if (curChar == 36)
                  {
                     if (kind > 14) {
                        kind = 14;
                    }
                     jjCheckNAddStates(8, 10);
                  }
                  else if (curChar == 34) {
                    jjCheckNAddStates(11, 13);
                } else if (curChar == 46) {
                    jjCheckNAdd(1);
                }
                  break;
               case 44:
                  if ((0x3ff001000000000L & l) != 0L)
                  {
                     if (kind > 14) {
                        kind = 14;
                    }
                     jjCheckNAddStates(8, 10);
                  }
                  else if (curChar == 39)
                  {
                     if (kind > 14) {
                        kind = 14;
                    }
                     jjCheckNAdd(13);
                  }
                  else if (curChar == 46) {
                    jjstateSet[jjnewStateCnt++] = 10;
                }
                  break;
               case 21:
                  if (curChar == 61)
                  {
                     if (kind > 18) {
                        kind = 18;
                    }
                  }
                  else if (curChar == 42) {
                    jjCheckNAddTwoStates(27, 28);
                } else if (curChar == 47) {
                    jjCheckNAddStates(14, 16);
                }
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 9) {
                    kind = 9;
                }
                  jjCheckNAddTwoStates(1, 2);
                  break;
               case 3:
                  if ((0x280000000000L & l) != 0L) {
                    jjCheckNAdd(4);
                }
                  break;
               case 4:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 9) {
                    kind = 9;
                }
                  jjCheckNAdd(4);
                  break;
               case 5:
                  if (curChar == 34) {
                    jjCheckNAddStates(11, 13);
                }
                  break;
               case 6:
                  if ((0xfffffffbffffdbffL & l) != 0L) {
                    jjCheckNAddStates(11, 13);
                }
                  break;
               case 8:
                  if ((0x8400000000L & l) != 0L) {
                    jjCheckNAddStates(11, 13);
                }
                  break;
               case 9:
                  if (curChar == 34 && kind > 11) {
                    kind = 11;
                }
                  break;
               case 10:
                  if (curChar != 36) {
                    break;
                }
                  if (kind > 14) {
                    kind = 14;
                }
                  jjCheckNAddStates(8, 10);
                  break;
               case 11:
                  if ((0x3ff001000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 14) {
                    kind = 14;
                }
                  jjCheckNAddStates(8, 10);
                  break;
               case 12:
                  if (curChar == 46) {
                    jjstateSet[jjnewStateCnt++] = 10;
                }
                  break;
               case 13:
                  if (curChar != 39) {
                    break;
                }
                  if (kind > 14) {
                    kind = 14;
                }
                  jjCheckNAdd(13);
                  break;
               case 14:
                  if (curChar == 61 && kind > 18) {
                    kind = 18;
                }
                  break;
               case 15:
                  if (curChar == 61 && kind > 18) {
                    kind = 18;
                }
                  break;
               case 16:
                  if (curChar == 43) {
                    jjCheckNAdd(15);
                }
                  break;
               case 17:
                  if (curChar == 45) {
                    jjCheckNAdd(15);
                }
                  break;
               case 18:
                  if (curChar == 42) {
                    jjCheckNAdd(15);
                }
                  break;
               case 20:
                  if (curChar == 47) {
                    jjCheckNAddStates(5, 7);
                }
                  break;
               case 22:
                  if ((0xffffffffffffdbffL & l) != 0L) {
                    jjCheckNAddStates(14, 16);
                }
                  break;
               case 23:
                  if ((0x2400L & l) != 0L && kind > 5) {
                    kind = 5;
                }
                  break;
               case 24:
                  if (curChar == 10 && kind > 5) {
                    kind = 5;
                }
                  break;
               case 25:
                  if (curChar == 13) {
                    jjstateSet[jjnewStateCnt++] = 24;
                }
                  break;
               case 26:
                  if (curChar == 42) {
                    jjCheckNAddTwoStates(27, 28);
                }
                  break;
               case 27:
                  if ((0xfffffbffffffffffL & l) != 0L) {
                    jjCheckNAddTwoStates(27, 28);
                }
                  break;
               case 28:
                  if (curChar == 42) {
                    jjAddStates(17, 18);
                }
                  break;
               case 29:
                  if ((0xffff7fffffffffffL & l) != 0L) {
                    jjCheckNAddTwoStates(30, 28);
                }
                  break;
               case 30:
                  if ((0xfffffbffffffffffL & l) != 0L) {
                    jjCheckNAddTwoStates(30, 28);
                }
                  break;
               case 31:
                  if (curChar == 47 && kind > 6) {
                    kind = 6;
                }
                  break;
               case 32:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 7) {
                    kind = 7;
                }
                  jjCheckNAddStates(0, 4);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 7) {
                    kind = 7;
                }
                  jjCheckNAdd(33);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) != 0L) {
                    jjCheckNAddTwoStates(34, 35);
                }
                  break;
               case 35:
                  if (curChar != 46) {
                    break;
                }
                  if (kind > 9) {
                    kind = 9;
                }
                  jjCheckNAddTwoStates(36, 37);
                  break;
               case 36:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 9) {
                    kind = 9;
                }
                  jjCheckNAddTwoStates(36, 37);
                  break;
               case 38:
                  if ((0x280000000000L & l) != 0L) {
                    jjCheckNAdd(39);
                }
                  break;
               case 39:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 9) {
                    kind = 9;
                }
                  jjCheckNAdd(39);
                  break;
               case 40:
                  if ((0x3ff000000000000L & l) != 0L) {
                    jjCheckNAddTwoStates(40, 41);
                }
                  break;
               case 42:
                  if ((0x280000000000L & l) != 0L) {
                    jjCheckNAdd(43);
                }
                  break;
               case 43:
                  if ((0x3ff000000000000L & l) == 0L) {
                    break;
                }
                  if (kind > 9) {
                    kind = 9;
                }
                  jjCheckNAdd(43);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 14) {
                        kind = 14;
                    }
                     jjCheckNAddStates(8, 10);
                  }
                  else if (curChar == 94) {
                    jjstateSet[jjnewStateCnt++] = 15;
                }
                  break;
               case 44:
               case 11:
                  if ((0x7fffffe87fffffeL & l) == 0L) {
                    break;
                }
                  if (kind > 14) {
                    kind = 14;
                }
                  jjCheckNAddStates(8, 10);
                  break;
               case 2:
                  if ((0x2000000020L & l) != 0L) {
                    jjAddStates(19, 20);
                }
                  break;
               case 6:
                  if ((0xffffffffefffffffL & l) != 0L) {
                    jjCheckNAddStates(11, 13);
                }
                  break;
               case 7:
                  if (curChar == 92) {
                    jjstateSet[jjnewStateCnt++] = 8;
                }
                  break;
               case 8:
                  if ((0x14404410000000L & l) != 0L) {
                    jjCheckNAddStates(11, 13);
                }
                  break;
               case 10:
                  if ((0x7fffffe87fffffeL & l) == 0L) {
                    break;
                }
                  if (kind > 14) {
                    kind = 14;
                }
                  jjCheckNAddStates(8, 10);
                  break;
               case 19:
                  if (curChar == 94) {
                    jjstateSet[jjnewStateCnt++] = 15;
                }
                  break;
               case 22:
                  jjAddStates(14, 16);
                  break;
               case 27:
                  jjCheckNAddTwoStates(27, 28);
                  break;
               case 29:
               case 30:
                  jjCheckNAddTwoStates(30, 28);
                  break;
               case 37:
                  if ((0x2000000020L & l) != 0L) {
                    jjAddStates(21, 22);
                }
                  break;
               case 41:
                  if ((0x2000000020L & l) != 0L) {
                    jjAddStates(23, 24);
                }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = curChar >> 8;
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 6:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                    jjAddStates(11, 13);
                }
                  break;
               case 22:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                    jjAddStates(14, 16);
                }
                  break;
               case 27:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                    jjCheckNAddTwoStates(27, 28);
                }
                  break;
               case 29:
               case 30:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                    jjCheckNAddTwoStates(30, 28);
                }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 44 - (jjnewStateCnt = startsAt))) {
        return curPos;
    }
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_1(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_1(int pos, long active0)
{
   return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
}
private int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 125:
         return jjStopAtPos(0, 49);
      default :
         return jjMoveNfa_1(0, 0);
   }
}
private int jjMoveNfa_1(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff) {
        ReInitRounds();
    }
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ffaf0000000000L & l) == 0L) {
                    break;
                }
                  kind = 50;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffec7fffffeL & l) == 0L) {
                    break;
                }
                  kind = 50;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = curChar >> 8;
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt))) {
        return curPos;
    }
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   33, 34, 35, 40, 41, 21, 26, 15, 11, 12, 13, 6, 7, 9, 22, 23,
   25, 29, 31, 3, 4, 38, 39, 42, 43,
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default :
         if ((jjbitVec0[i1] & l1) != 0L) {
            return true;
        }
         return false;
   }
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null,
"\164\162\165\145", "\146\141\154\163\145", null, null, null, null, null, null, null, null, null,
null, null, "\54", "\73", "\76", "\74", "\75\75", "\74\75", "\76\75", "\41\75",
"\46\46", "\174\174", "\53", "\55", "\52", "\56", "\57", "\45", "\41", "\136",
"\136\136", "\133", "\135", "\50", "\51", "\173", "\175", null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
   "IN_UNITS",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0,
   -1,
};
static final long[] jjtoToken = {
   0x7fffffe047a81L,
};
static final long[] jjtoSkip = {
   0x7eL,
};
protected JavaCharStream input_stream;
private final int[] jjrounds = new int[44];
private final int[] jjstateSet = new int[88];
protected char curChar;
/** Constructor. */
public ExpressionParserTokenManager(JavaCharStream stream){
   if (JavaCharStream.staticFlag) {
    throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
}
   input_stream = stream;
}

/** Constructor. */
public ExpressionParserTokenManager(JavaCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(JavaCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 44; i-- > 0;) {
    jjrounds[i] = 0x80000000;
}
}

/** Reinitialise parser. */
public void ReInit(JavaCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 2 || lexState < 0) {
    throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
} else {
    curLexState = lexState;
}
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken()
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   switch(curLexState)
   {
     case 0:
       try { input_stream.backup(0);
          while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L) {
            curChar = input_stream.BeginToken();
        }
       }
       catch (java.io.IOException e1) { continue EOFLoop; }
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_0();
       break;
     case 1:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_1();
       break;
   }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos) {
            input_stream.backup(curPos - jjmatchedPos - 1);
        }
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
       if (jjnewLexState[jjmatchedKind] != -1) {
        curLexState = jjnewLexState[jjmatchedKind];
    }
           return matchedToken;
        }
        else
        {
         if (jjnewLexState[jjmatchedKind] != -1) {
            curLexState = jjnewLexState[jjmatchedKind];
        }
           continue EOFLoop;
        }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        } else {
            error_column++;
        }
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
