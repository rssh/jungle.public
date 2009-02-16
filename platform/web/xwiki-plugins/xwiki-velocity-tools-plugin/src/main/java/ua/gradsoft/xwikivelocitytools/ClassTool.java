
package ua.gradsoft.xwikivelocitytools;

/**
 *Provide interface, simular to velocity-2.0 ClassTool
 * (implemented small subset of velocity-2.0 ClassTool methods,
 *  which we really needed)
 * see <a href="http://velocity.apache.org/tools/devel/javadoc/org/apache/velocity/tools/generic/ClassTool.html">
 *  Velocity Class Tool documnetation.
 * </a>
 * @author rssh
 */
public class ClassTool {

    public static abstract class CallableSub<T extends CallableSub>
    {
        abstract public int getParameterCount();
        abstract public Class[]  getParameters();
        abstract public String   getSignature();
        //abstract public String   getUniqieName();
        abstract public boolean  isVarArgs();
    }
    
    public ClassTool()
    {
      class_ = Object.class;  
    }
    
    protected ClassTool(Class<?> theClass)
    {
        class_ = theClass;
    }
    
    
    public ClassTool inspect(Class<?> theClass)
    {
      return new ClassTool(theClass);
    }
    
    public ClassTool inspect(String className)
    {
       try { 
         Class<?> theClass = Class.forName(className);
         return inspect(theClass);
       }catch(ClassNotFoundException ex){
           return null;
       }       
    }
    
    public Class<?> getType()
    {
      return class_;  
    }
    
    public String getName()
    {
      return class_.getSimpleName();  
    }
    
    public String getFullName()
    {
      return class_.getName();  
    }
    
    public String getPackage()
    {
        return class_.getPackage().getName();
    }
    
    private  Class<?> class_;
}
