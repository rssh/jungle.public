package ua.gradsoft.phpjao;

import ua.gradsoft.javachecker.models.JavaPlaceContext;
import ua.gradsoft.javachecker.models.TermUtils;
import ua.gradsoft.termware.DefaultFacts;
import ua.gradsoft.termware.Term;
import ua.gradsoft.termware.TermWareException;
import ua.gradsoft.termware.TransformationContext;
import ua.gradsoft.termware.exceptions.AssertException;

/**
 *
 * @author rssh
 */
public class PhpJaoFacts extends DefaultFacts
{

    public PhpJaoFacts(Facade facade) throws TermWareException
    {
      super();
      facade_=facade;
    }


    public boolean isGetterString(String name)
    {
      return name.startsWith("get");
    }

    public boolean getNameFromGetterString(TransformationContext ctx, String s, Term x) throws TermWareException
    {
      if (!x.isX()) {
          throw new AssertException("second paramter for getNameFromGetterString must be X");
      }
      String retval = s;
      if (s.startsWith("get")) {
          String withoutGet = s.substring(3);
          if (s.length()>3) {
            String withoutGetX = withoutGet.substring(1);
            char ch = Character.toLowerCase(withoutGet.charAt(0));
            retval = ""+ch+withoutGetX;
          }
      }
      ctx.getCurrentSubstitution().put(x, TermUtils.createString(retval));
      return true;
    }

    public boolean getClassModifiersFromInt(TransformationContext ctx, int n, Term x) throws TermWareException
    {
      if (!x.isX()) {
          throw new AssertException("second paramter for getNameFromGetterString must be X");
      }
      Term retval = TermUtils.createNil();
      if ((n & 0x0008)!=0) {
          Term at = TermUtils.createAtom("abstract");
          retval = TermUtils.createTerm("cons",at,retval);
      }else if ((n & 0x0020)!=0) {
          Term ft = TermUtils.createAtom("final");
          retval = TermUtils.createTerm("cons",ft,retval);
      }
      ctx.getCurrentSubstitution().put(x, retval);
      return true;
    }


    private Facade facade_;

}
