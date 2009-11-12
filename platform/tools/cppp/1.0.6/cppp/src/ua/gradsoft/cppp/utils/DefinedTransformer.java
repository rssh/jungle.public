package ua.gradsoft.cppp.utils;

import ua.gradsoft.termware.AbstractBuildinTransformer;
import ua.gradsoft.termware.Term;
import ua.gradsoft.termware.TermSystem;
import ua.gradsoft.termware.TermWare;
import ua.gradsoft.termware.TermWareException;
import ua.gradsoft.termware.TransformationContext;
import ua.gradsoft.termware.exceptions.AssertException;


/*
 * (C) Ruslan Shevchenko <Ruslan@Shevchenko.Kiev.UA> 2002,2003
 * $Id: DefinedTransformer.java,v 1.3 2004/03/26 12:01:44 rssh Exp $
 */


                           
/**
 * Tramsformer for build-in CPPP facts database.
 *  define(x) -> macro definition of X, if X was defined by #define term.
 **/
public class DefinedTransformer extends AbstractBuildinTransformer
{

 public DefinedTransformer(CPPPParserHelper helper)
 {
   helper_=helper;
 }

 public  Term  transform(Term t, TermSystem sys, TransformationContext ctx) throws TermWareException
 {
  if (!t.getName().equals("defined")) return t;
  if (t.getArity()!=1) {
     throw new AssertException("defined must have arity 1");
  }
  Term frs = t.getSubtermAt(0);
  if (helper_.defined(frs.getName())) {
     ctx.setChanged(true);
     return TermWare.getInstance().getTermFactory().createBoolean(true);
  }else {
     ctx.setChanged(true);
     return TermWare.getInstance().getTermFactory().createBoolean(false);
  }
 }


 public String getDescription() {
     return "defined(x) - if x is previously defined in cppp, transform one to it definition";
 }
 
 public String getName() {
     return "defined";
 }
 
 
 private CPPPParserHelper helper_;

}
 
