package ua.gradsoft.cppp.utils;

import java.io.PrintStream;
import ua.gradsoft.cppp.parse.CPPPParser;
import ua.gradsoft.termware.Term;
import ua.gradsoft.termware.TermWareException;
import ua.gradsoft.termware.exceptions.AssertException;





public class IfHandleHelper 
{

 // MODE:
 static final int  enIF=1;
 static final int  enELIF=2;
 static final int  enELSE=3;
 static final int  enENDIF=4; // for completer, not used.
 static final int  enNONE=5;

static final class IfElement
{
 public int op;
 public boolean state;
 public int     level;

 IfElement(boolean theState, int theOp, int theLevel)
 {
  state=theState;
  op=theOp;
  level=theLevel;
 }
}

 /*
  *01  L: [S] (enabled, IF (true) X) -> L+1: [S][enabled, IF (true), L+1 ] (enabled, X)
  *02  L: [S][enabled, IF,true, L] (ELIF $x X)  -> L: [S][enabled,IF,true,L][disabled, ELSIF, $x, L] (disabled, X)
  *03  L: [S][enabled, IF,true, L] [disabled, ELIF, $x,L] (ELIF $y X) -> L: [S][enabled,IF,true,L][disabled, ELSIF, $x, L] (disabled, X)
  *04  L: [S][enabled, IF,true, L] [disabled, ELIF, $x,L] (ELSE X) -> L: [enabled, IF, true, L][disabled ELSE, L] (disabled, X)
  *05  L: [S][enabled, IF,true, L] [disabled, ELSE, L](ENDIF X)  -> L-1: [S] (enabled, X)
  *06  L: [s][enabled, IF,true, L] [disabled, ELIF, $x, L] ( IF X ) -> L+1: [S][enabled,IF,true,L][disabled,ELIF,$x,L][disabled,IF,$x,L+1]
  *07  L: [S][enabled, IF,true, L] [disabled, ELSE, L](ELSE X) -> ERROR "ENDIF after ELSE expected"
  *08  L: [S][enabled, IF,true, L] [disabled, ELIF, L](ENDIF X) -> L-1: [S] (X)
  *09  L: [S][enabled, IF,true, L] (ELSE X) -> L:[S][enabled,IF,true,L][disabled, ELSE, L](X)
  *10  L: [S][enabled, IF,true, L] (IF (true) X) -> *01
  *11  L: [S][enabled, IF,true, L] (ENDIF X) -> L-1:[S](X)
  *11  L: [S](enabled, IF (false) X) -> L+1: [S][enable,NONE][disable,IF,L+1](X)
  *12  L: [S][enable,NONE,L-1][disable,IF,L](IF ($x) X) -> [S][enable,NONE,L-1][disable,IF,L][disable,IF,L+1](disabled,X)
  *13  L: [S][enable,NONE,L-1][disable,IF,L](ELIF (true) X) ->[S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L](X)
  *14  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L](ELIF ($x) X) -> L:[S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L][disable,ELIF,L](disabled,X) 
  *15  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L][disable,ELIF,L](ELIF, ($x), X) -> [enable,NONE,L-1][disable,IF,L][enable,ELIF,L][disable,ELIF,L] X
  *15  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L][disable,ELIF,L](ELSE X) ->[S][enable,NONE,L-1][disable,IF,L][disable,ELSE,L](disable,X)
  *16  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L][disable,ELIF,L](IF X) -> *26
  *17  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L][disable,ELIF,L](ENDIF X) -> L-1:[S](enable,X)
  *18  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L](ELSE X)->L:[S][enable,NONE,L-1][disable,IF,L][disable,ELSE,L]X
  *19  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELIF,L](ENDIF)->L:[S](enable,X)
  *20  L: [S][enable,NONE,L-1][disable,IF,L](ELIF (false) X) -> [S][enable,NONE,L-1][disable,IF,L] (disable, X)
  *21  L: [S][enable,NONE,L-1][disable,IF,L](ELSE X) -> [S][enable,NONE,L-1][disable,IF,L][enable,ELSE,L](enable,X)
  *22  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELSE,L](IF X)->*01
  *23  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELSE,L](ELIF X) -> ERROR
  *23  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELSE,L](ELSE X) -> ERROR
  *24  L: [S][enable,NONE,L-1][disable,IF,L][enable,ELSE,L](ENDIF X) -> [S](enable,X)
  *25  L: [S][enable,NONE,L-1][disable,IF,L](ENDIF X) -> [S](enable,X)
  *26  L: [S][disabled,*,L](IF, ($x), X) -> L+1:[S][disabled,*,L][disabled,IF,L+1]
  *27  L: [S][disabled,*,L-1][disabled,IF,L](IF X)->*26
  *28  L: [S][disabled,*,L-1][disabled,IF,L](ELIF $x X)->[S][disabled,*,L-1][disabled,IF,L]
  *29  L: [S][disabled,*,L-1][disabled,IF,L](ELSE X) -> [S][disabled,*,L-1][disabled,IF,L][disabled,ELSE,L](disabled,X)
  *30  L: [S][disabled,*,L-1][disabled,IF,L][disabled,ELSE,L](IF X) -> *26
  *31  L: [S][disabled,*,L-1][disabled,IF,L][disabled,ELSE,L](ELIF X) -> "ERROR"
  *32  L: [S][disabled,*,L-1][disabled,IF,L][disabled,ELSE,L](ELSE X) -> "ERROR"
  *33  L: [S][disabled,*,L-1][disabled,IF,L][disabled,ELSE,L](ENDIF X) -> L-1:[S][disabled,*,L-1](disable,X)
  *34  L: [S][disabled,*,L-1][disabled,IF,L](ENDIF X) -> L-1:[S][disabled,*,L-1](disable,X)
  *
  **/

  public boolean handleIF(Term condition) throws TermWareException
  {
   if (enabled_) {
     boolean b=checkConstantExpressionInLine(condition);
     if (b) {
       ++ifLevel_;
       pushState(true,enIF,ifLevel_);
     }else{
       pushState(true,enNONE,ifLevel_);
       ++ifLevel_;
       pushState(false,enIF,ifLevel_);
       enabled_=false;       
     }
   }else{
     ++ifLevel_;
     pushState(false,enIF,ifLevel_); 
     enabled_=false;
   }
   return enabled_;
  }

  public boolean handleELIF(Term condition) throws TermWareException
  {
   IfElement e0=null;
   IfElement e1=null;
   IfElement e2=null;
   IfElement e3=null;
   if (stackLength() > 0) e0=peekState(0);
   if (stackLength() > 1) e1=peekState(1);
   if (stackLength() > 2) e2=peekState(2);
   if (stackLength() > 3) e3=peekState(3);
   if (e0==null) throw new AssertException("Unexpected ELIF, e0==null");
   if (e0.op==enIF && e0.state==true) {
     // 02
     pushState(false,enELIF,ifLevel_);
     enabled_=false;
     return enabled_;
   }
   if (e0.op==enELIF && e0.state==false) {
     if (e1==null) throw new AssertException("Unexpected ELIF");
     if (e1.op==enIF && e1.state==true) {
      // 03 - do nothing;
       return false;
     }
     if (e1.op==enELIF && e1.state==true) {
      // 015 - do nothing.
       return false;
     }  
   }
   if (e0.op==enIF && e0.state==false) {
     if (e1==null) e1=new IfElement(true,enNONE,0);
     if (e1.op==enNONE && e1.state==true) {
        //13||20
        boolean b=checkConstantExpressionInLine(condition);
        if (b) {
          // 13
          enabled_=true;
          pushState(true,enELIF,ifLevel_);
          return enabled_;
        }else{
          // 20
          return false;
        }
     }
     if (e1.state==false) {
        // 28
        return false;
     }
   }
   if (e0.op==enELIF && e0.state==true) {
     if (e1==null) throw new AssertException("unexpected ELIF");
     if (e1.op==enIF && e1.state==false) {
       pushState(false, enELIF, ifLevel_);
       return false;
     }
   }
   throw new AssertException("unexpected ELIF");
  }


  public boolean handleELSE() throws TermWareException
  {
   IfElement e0=null;
   IfElement e1=null;
   IfElement e2=null;
   if (stackLength() > 0) e0=peekState(0);
   if (stackLength() > 1) e1=peekState(1);
   if (stackLength() > 2) e2=peekState(2);
   if (e0==null) throw new AssertException("Unexpected ELSE");
   if (e0.op==enELIF && e0.state==false) {
     if (e1==null) throw new AssertException("Unexpected ELSE");
     if (e1.op==enIF && e1.state==true) {
       // 04
       e0.op=enELSE;
       e0.state=false;
       enabled_=false;
     }else if (e1.op==enELIF && e1.state==true) {
       // 15
       popState();
       popState();
       pushState(false,enELSE,ifLevel_);
       enabled_=false;
     }else{
       throw new AssertException("Unexpected #else");
     }
   }else if (e0.op==enELSE) throw new AssertException("Unexpected ELSE");
   else if (e0.op==enIF && e0.state==true) {
     pushState(false,enELSE,ifLevel_);
     enabled_=false;
   }else if (e0.op==enIF && e0.state==false) {
     if (e1==null || e1.state==true) {
       enabled_=true;
       pushState(true,enELSE,ifLevel_);
     }else{
       pushState(false,enELSE,ifLevel_);
     }
   }else if (e0.op==enELIF && e0.state==true) {
     if (e1==null) throw new AssertException("Unexpected ELSE");
     // (e1.op==enIF && e1.state==false) kk    
     e0.op=enELSE;
     e0.state=false;
     enabled_=false;
   }else{ 
     throw new AssertException("Unexpected #else");
   }
   return enabled_;
  }

  public boolean  handleENDIF() throws TermWareException
  {
   IfElement last=null;
   while(stackLength()>0) {
     IfElement e=peekState(0);
     if (e.level==ifLevel_) {
       last=popState();
     }else{
       break;
     }             
   }
   --ifLevel_;
   if (last==null) throw new AssertException("Unexpected #endif");
   if (last.op!=enIF) {
     throw new AssertException("#endif without corresponding #if");
   }                           
   if (stackLength()==0) {
     enabled_ = true;
   }else{
     IfElement prev=peekState(0);
     if (prev.op==enNONE) {
        popState();
        if (stackLength()==0) {
           enabled_=true;          
        }else{
           enabled_ = peekState(0).state;
        }        
     }else{
        enabled_ = peekState(0).state;
     }
   }
   return enabled_;
  }

  public IfHandleHelper(CPPPParser parser)
  {
   ifLevel_=0;
   stateStack_=new IfElementStack();
   enabled_=true;  
   parser_=parser;
  }                

  private final boolean checkConstantExpressionInLine(Term t) throws TermWareException
  {
   return parser_.checkConstantExpressionInLine(t);
  }

  private int ifLevel_;
  private IfElementStack stateStack_;
  private boolean enabled_;
  private CPPPParser parser_;
                 
  private final IfElement peekState(int n) throws TermWareException
  {
   return stateStack_.peek(n);
  }

  private final IfElement popState() throws TermWareException
  {
   return stateStack_.pop();
  }

  private final void pushState(boolean state,int op,int level)
  {
   stateStack_.push(state,op,level);
  }

  private final int  stackLength()
  {
   return stateStack_.length();
  }

  private final void printStack(PrintStream out)
  {
   stateStack_.print(out);
  }

static final class IfElementStack
{
 private IfElement[] v_;
 private int         len_;
 private int         size_;

 IfElementStack()
 {
   v_=new IfElement[10];
   len_=0;
   size_=10;
 }

 IfElement  peek(int n) throws TermWareException
 {
   if (n>=len_) throw new AssertException("CPPP Stack underflow");
   return v_[len_-n-1];
 }

 IfElement  pop() throws TermWareException
 {
   if (len_==0) throw new AssertException("CPPP Stack underflow");
   return v_[--len_];
 }

 void  push(boolean state, int op, int level)
 {
   if (len_==size_) {
     int newSize = size_*2;
     IfElement[] newV = new IfElement[newSize];
     System.arraycopy(v_,0,newV,0,len_);
     v_=newV;
     size_=newSize;
   }
   IfElement e=v_[len_];
   if (e==null){
     e=new IfElement(false,0,0);
     v_[len_]=e;
   }
   e.state=state;
   e.op=op;
   e.level=level;
   ++len_;
 }

 final int    length()
 { return len_; }

 final void   print(PrintStream out)
 {
   for(int i=0; i<len_; ++i) {
     out.print("["+v_[i].state+","+opNames_[v_[i].op]+","+v_[i].level+"]");
   }
   out.println();                  
 }

 static String[] opNames_;

 static {                         
   opNames_ = new String[6];
   opNames_[0]="XXX";
   opNames_[1]="IF";
   opNames_[2]="ELIF";
   opNames_[3]="ELSE";
   opNames_[4]="ENDIF";
   opNames_[5]="NONE";
 }

}

};
