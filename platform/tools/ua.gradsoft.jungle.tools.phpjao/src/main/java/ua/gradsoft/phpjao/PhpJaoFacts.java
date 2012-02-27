package ua.gradsoft.phpjao;

import ua.gradsoft.javachecker.EntityNotFoundException;
import ua.gradsoft.javachecker.models.JavaResolver;
import ua.gradsoft.javachecker.models.JavaTypeModel;
import ua.gradsoft.javachecker.models.JavaTypeModelHelper;
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
      return (name.startsWith("get") && 
              name.length() > 3 && 
              Character.isUpperCase(name.charAt(3))
             )
            ||
             (name.startsWith("is") && 
              name.length() > 2 && 
              Character.isUpperCase(name.charAt(2))
             )
            ;
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
      } else if (s.startsWith("is")) {
          String withoutIs = s.substring(2);
          if (s.length()>2) {
            String withoutIsX = withoutIs.substring(1);
            char ch = Character.toLowerCase(withoutIs.charAt(0));
            retval = ""+ch+withoutIsX;
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

    public Boolean isException(JavaTypeModel m) throws TermWareException, EntityNotFoundException
    {
        return JavaTypeModelHelper.subtypeOrSame(m, JavaResolver.resolveTypeModelByFullClassName("java.lang.Exception"));
    }

    private Facade facade_;

}
