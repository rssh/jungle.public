/* Generated By:JavaCC: Do not edit this line. CEXPRParserTokenManager.java */
package ua.gradsoft.cppp.parse;
import java.io.*;
import java.util.*;
import ua.gradsoft.termware.*;
import ua.gradsoft.cppp.utils.*;

public class CEXPRParserTokenManager implements CEXPRParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 13;
            return -1;
         }
         if ((active0 & 0x8L) != 0L)
         {
            jjmatchedKind = 12;
            return 19;
         }
         return -1;
      case 1:
         if ((active0 & 0x8L) != 0L)
         {
            jjmatchedKind = 12;
            jjmatchedPos = 1;
            return 19;
         }
         if ((active0 & 0x100000L) != 0L)
         {
            if (jjmatchedPos == 0)
            {
               jjmatchedKind = 13;
               jjmatchedPos = 0;
            }
            return -1;
         }
         return -1;
      case 2:
         if ((active0 & 0x8L) != 0L)
         {
            jjmatchedKind = 12;
            jjmatchedPos = 2;
            return 19;
         }
         return -1;
      case 3:
         if ((active0 & 0x8L) != 0L)
         {
            jjmatchedKind = 12;
            jjmatchedPos = 3;
            return 19;
         }
         return -1;
      case 4:
         if ((active0 & 0x8L) != 0L)
         {
            jjmatchedKind = 12;
            jjmatchedPos = 4;
            return 19;
         }
         return -1;
      case 5:
         if ((active0 & 0x8L) != 0L)
         {
            jjmatchedKind = 12;
            jjmatchedPos = 5;
            return 19;
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
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         jjmatchedKind = 32;
         return jjMoveStringLiteralDfa1_0(0x200000L);
      case 35:
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      case 37:
         return jjStopAtPos(0, 30);
      case 38:
         jjmatchedKind = 19;
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 40:
         return jjStopAtPos(0, 1);
      case 41:
         return jjStopAtPos(0, 2);
      case 42:
         return jjStopAtPos(0, 28);
      case 43:
         return jjStopAtPos(0, 26);
      case 44:
         return jjStopAtPos(0, 4);
      case 45:
         return jjStopAtPos(0, 27);
      case 47:
         return jjStopAtPos(0, 29);
      case 60:
         jjmatchedKind = 14;
         return jjMoveStringLiteralDfa1_0(0x1400000L);
      case 61:
         return jjMoveStringLiteralDfa1_0(0x100000L);
      case 62:
         jjmatchedKind = 15;
         return jjMoveStringLiteralDfa1_0(0x2800000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x8L);
      case 124:
         jjmatchedKind = 18;
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 126:
         return jjStopAtPos(0, 31);
      default :
         return jjMoveNfa_0(1, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(1, 33);
         break;
      case 38:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         break;
      case 60:
         if ((active0 & 0x1000000L) != 0L)
            return jjStopAtPos(1, 24);
         break;
      case 61:
         if ((active0 & 0x100000L) != 0L)
            return jjStopAtPos(1, 20);
         else if ((active0 & 0x200000L) != 0L)
            return jjStopAtPos(1, 21);
         else if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(1, 22);
         else if ((active0 & 0x800000L) != 0L)
            return jjStopAtPos(1, 23);
         break;
      case 62:
         if ((active0 & 0x2000000L) != 0L)
            return jjStopAtPos(1, 25);
         break;
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x8L);
      case 124:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(1, 16);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x8L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x8L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x8L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0x8L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 100:
         if ((active0 & 0x8L) != 0L)
            return jjStartNfaWithStates_0(6, 3, 19);
         break;
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 60;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 5);
                  else if ((0xac00401000000000L & l) != 0L)
                  {
                     if (kind > 13)
                        kind = 13;
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 6)
                        kind = 6;
                  }
                  else if (curChar == 34)
                     jjCheckNAddStates(6, 8);
                  else if (curChar == 39)
                     jjAddStates(9, 10);
                  else if (curChar == 32)
                  {
                     if (kind > 5)
                        kind = 5;
                     jjCheckNAdd(0);
                  }
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 8)
                        kind = 8;
                     jjCheckNAddTwoStates(10, 11);
                  }
                  else if (curChar == 48)
                     jjAddStates(11, 12);
                  else if (curChar == 46)
                     jjCheckNAdd(13);
                  else if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 4;
                  else if (curChar == 10)
                     jjstateSet[jjnewStateCnt++] = 2;
                  if (curChar == 48)
                  {
                     if (kind > 7)
                        kind = 7;
                     jjCheckNAddTwoStates(7, 8);
                  }
                  break;
               case 0:
                  if (curChar != 32)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAdd(0);
                  break;
               case 2:
                  if (curChar == 13 && kind > 6)
                     kind = 6;
                  break;
               case 3:
                  if (curChar == 10)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 4:
                  if (curChar == 10 && kind > 6)
                     kind = 6;
                  break;
               case 5:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 6:
                  if (curChar != 48)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(7, 8);
                  break;
               case 7:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(7, 8);
                  break;
               case 9:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 12:
                  if (curChar == 46)
                     jjCheckNAdd(13);
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(13, 15);
                  break;
               case 15:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(16);
                  break;
               case 16:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddTwoStates(16, 17);
                  break;
               case 19:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 20:
                  if ((0xac00401000000000L & l) != 0L && kind > 13)
                     kind = 13;
                  break;
               case 21:
                  if (curChar == 39)
                     jjAddStates(9, 10);
                  break;
               case 22:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(23);
                  break;
               case 23:
                  if (curChar == 39 && kind > 34)
                     kind = 34;
                  break;
               case 25:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAdd(23);
                  break;
               case 26:
                  if (curChar == 48)
                     jjCheckNAddTwoStates(27, 23);
                  break;
               case 27:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(27, 23);
                  break;
               case 28:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(29, 23);
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(29, 23);
                  break;
               case 30:
                  if (curChar == 48)
                     jjAddStates(16, 17);
                  break;
               case 32:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(32, 23);
                  break;
               case 34:
                  if (curChar == 34)
                     jjCheckNAddStates(6, 8);
                  break;
               case 35:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 37:
                  if ((0x8400000000L & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 38:
                  if (curChar == 34 && kind > 35)
                     kind = 35;
                  break;
               case 39:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(18, 21);
                  break;
               case 40:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 41:
                  if ((0xf000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 42;
                  break;
               case 42:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(40);
                  break;
               case 43:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 5);
                  break;
               case 44:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(44, 45);
                  break;
               case 45:
                  if (curChar != 46)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(22, 24);
                  break;
               case 46:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(22, 24);
                  break;
               case 48:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(49);
                  break;
               case 49:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddTwoStates(49, 17);
                  break;
               case 50:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 52:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(53);
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddTwoStates(53, 17);
                  break;
               case 54:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(54, 17);
                  break;
               case 55:
                  if (curChar == 48)
                     jjAddStates(11, 12);
                  break;
               case 57:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjAddStates(25, 26);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 12)
                        kind = 12;
                     jjCheckNAdd(19);
                  }
                  else if ((0x2800000178000001L & l) != 0L)
                  {
                     if (kind > 13)
                        kind = 13;
                  }
                  break;
               case 8:
                  if ((0x20100000201000L & l) != 0L && kind > 7)
                     kind = 7;
                  break;
               case 11:
                  if ((0x20100000201000L & l) != 0L && kind > 8)
                     kind = 8;
                  break;
               case 14:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(27, 28);
                  break;
               case 17:
                  if ((0x5000000050L & l) != 0L && kind > 10)
                     kind = 10;
                  break;
               case 18:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(19);
                  break;
               case 19:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(19);
                  break;
               case 20:
                  if ((0x2800000178000001L & l) != 0L && kind > 13)
                     kind = 13;
                  break;
               case 22:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(23);
                  break;
               case 24:
                  if (curChar == 92)
                     jjAddStates(29, 32);
                  break;
               case 25:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(23);
                  break;
               case 31:
                  if (curChar == 120)
                     jjCheckNAdd(32);
                  break;
               case 32:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddTwoStates(32, 23);
                  break;
               case 33:
                  if (curChar == 88)
                     jjCheckNAdd(32);
                  break;
               case 35:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 36:
                  if (curChar == 92)
                     jjAddStates(33, 35);
                  break;
               case 37:
                  if ((0x14404410000000L & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 47:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(36, 37);
                  break;
               case 51:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(38, 39);
                  break;
               case 56:
                  if (curChar == 120)
                     jjCheckNAdd(57);
                  break;
               case 57:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAddTwoStates(57, 58);
                  break;
               case 58:
                  if ((0x20100000201000L & l) != 0L && kind > 9)
                     kind = 9;
                  break;
               case 59:
                  if (curChar == 88)
                     jjCheckNAdd(57);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 22:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 35:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(6, 8);
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
      if ((i = jjnewStateCnt) == (startsAt = 60 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   44, 45, 50, 51, 54, 17, 35, 36, 38, 22, 24, 56, 59, 13, 14, 17, 
   31, 33, 35, 36, 40, 38, 46, 47, 17, 57, 58, 15, 16, 25, 26, 28, 
   30, 37, 39, 41, 48, 49, 52, 53, 
};
public static final String[] jjstrLiteralImages = {
"", "\50", "\51", "\144\145\146\151\156\145\144", "\54", null, null, null, 
null, null, null, null, null, null, "\74", "\76", "\174\174", "\46\46", "\174", 
"\46", "\75\75", "\41\75", "\74\75", "\76\75", "\74\74", "\76\76", "\53", "\55", 
"\52", "\57", "\45", "\176", "\41", "\43\43", null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0xffffff7dfL, 
};
static final long[] jjtoSkip = {
   0x20L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[60];
private final int[] jjstateSet = new int[120];
protected char curChar;
public CEXPRParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public CEXPRParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 60; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
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

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
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
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
