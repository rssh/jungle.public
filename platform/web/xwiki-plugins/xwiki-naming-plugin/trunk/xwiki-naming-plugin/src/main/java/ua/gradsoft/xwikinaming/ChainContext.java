/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.gradsoft.xwikinaming;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author rssh
 */
public class ChainContext implements Context
{
    
   ChainContext(Context first, Context second, 
                boolean writeFirst, boolean writeSecond,
                ChainInitialContextFactory initialContextFactory)
   {
     first_=first; second_=second;  
     writeFirst_=writeFirst; writeSecond_=writeSecond;
     initialContextFactory_=initialContextFactory;
   }
   
   public Object addToEnvironment(String name, Object value) throws NamingException
   {
     Object o1=null;
     Object o2=null;    
     o1 = first_.addToEnvironment(name, value);
     o2 = second_.addToEnvironment(name, value);  
     return o1 != null ? o1 : o2;
   }
   
   public Object removeFromEnvironment(String name) throws NamingException
   {
     Object o1=null;
     Object o2=null;    
     o1 = first_.removeFromEnvironment(name);
     o2 = second_.removeFromEnvironment(name);  
     return o1 != null ? o1 : o2;
   }
   
   
   private void bindInternal(Name nName, String sName, Object o) throws NamingException
   {
     boolean isBound=false;
     if (writeFirst_) {
       if (nName!=null) {  
         first_.bind(nName, o);
       } else {
         first_.bind(sName, o);  
       }
       isBound=true;
     }
     if (writeSecond_) {
       if (!writeFirst_) {
           // check that we have nothibg in first.
          Object of=null; 
          try {
            if (nName!=null) {  
              of = first_.lookup(nName);
            } else {
              of = first_.lookup(sName);  
            }
          }catch(NameNotFoundException ex){
              // all ok, do nothing.
          }
          if (of!=null) {
              throw new NameAlreadyBoundException();
          }
       }  
       if (nName!=null) {  
         second_.bind(nName, o);
       } else {
         second_.bind(sName, o);  
       }
       isBound=true;
     }
     if (!isBound) {
         throw new OperationNotSupportedException();
     }
   }
   
   public void bind(Name name, Object o) throws NamingException
   {
     bindInternal(name,null,o);  
   }
   

   public void bind(String name, Object o) throws NamingException
   {
     bindInternal(null,name,o);  
   }
   
   public void close() throws NamingException
   {
     first_.close();
     second_.close();
   }
   
   public Name composeName(Name name, Name prefix) throws NamingException
   {
     try {    
       return first_.composeName(name, prefix);
     }catch(OperationNotSupportedException ex){
         return second_.composeName(name, prefix);
     }
   }
   
   public String composeName(String name, String prefix) throws NamingException
   {
     try {  
       return first_.composeName(name, prefix);
     }catch(OperationNotSupportedException ex){
         return second_.composeName(name, prefix);       
     }
   }
   
   private Context createSubcontextInternal(Name nName, String sName) throws NamingException
   {
     Context ctx1=null;
     Context ctx2=null;
     if (writeFirst_) {
       if (nName!=null) {  
         ctx1=first_.createSubcontext(nName);
       } else {
         ctx1=first_.createSubcontext(sName);  
       }
     }
     if (writeSecond_) {
       if (nName!=null) {  
         ctx2=second_.createSubcontext(nName);
       } else {
         ctx2=second_.createSubcontext(sName);  
       }
     }
     if (ctx1!=null && ctx2!=null) {
         return new ChainContext(ctx1,ctx2,writeFirst_,writeSecond_, initialContextFactory_);
     }else if (ctx1!=null && ctx2==null) {
         return ctx1;
     }else if (ctx1==null && ctx2!=null) {
         return ctx2;
     }else{
         throw new OperationNotSupportedException();
     }
   }

   public Context createSubcontext(Name name) throws NamingException
   {
     return createSubcontextInternal(name,null);  
   }
   
   
   public Context createSubcontext(String name) throws NamingException
   {
     return createSubcontextInternal(null,name);  
   }
   
   private void destroySubcontextInternal(Name nName, String sName) throws NamingException
   {       
     boolean destroyed=false;  
     if (writeFirst_) {
       if (nName!=null)  {
         first_.destroySubcontext(nName);
       }else{
         first_.destroySubcontext(sName);   
       }
       destroyed=true;
     }  
     if (writeSecond_) {
       if (nName!=null) {  
         second_.destroySubcontext(nName);
       }else{
         second_.destroySubcontext(sName);  
       }
       destroyed=true;
     }
     if (!destroyed) {
         throw new OperationNotSupportedException();
     }
   }

   public void destroySubcontext(Name name) throws NamingException
   {       
     destroySubcontextInternal(name,null);  
   }
   
   
   public void destroySubcontext(String name) throws NamingException
   {       
     destroySubcontextInternal(null,name);  
   }
   
   public Hashtable getEnvironment() throws NamingException
   {
       Hashtable h1 = first_.getEnvironment();
       Hashtable h2 = second_.getEnvironment();
       Hashtable retval = new Hashtable();
       retval.putAll(h1);
       retval.putAll(h2);
       return retval;
   }
   

   public NameParser getNameParser(Name name) throws NamingException
   {
     return initialContextFactory_.getChainNameParser(name, first_, second_);  
   }
   
   
   public NameParser getNameParser(String name) throws NamingException
   {
     return initialContextFactory_.getChainNameParser(name, first_, second_);  
   }
   
   private NamingEnumeration compoundList(NamingEnumeration n1, NamingEnumeration n2) throws NamingException
   {
     if (n1!=null && n2!=null) {
         return new CompoundNamingEnumeration(n1,n2);
     }else if (n1!=null) {
         return n1;
     }else if (n2!=null) {
         return n2;
     }else{
         throw new NameNotFoundException();
     }       
   }
   
   private NamingEnumeration listInternal(Name nName, String sName, boolean bindings) throws NamingException
   {
     NamingEnumeration n1=null;
     NamingEnumeration n2=null;
     try {
       if (nName!=null) {
         if (bindings) {  
           n1=first_.listBindings(nName);  
         } else {
           n1=first_.list(nName);  
         }
       } else {
         if (bindings) {  
           n1=first_.listBindings(sName);  
         } else {
           n1=first_.list(sName);  
         }           
       }          
     }catch(NameNotFoundException ex){
         ;
     }catch(InvalidNameException ex){
         ;
     }
     try {
       if (nName!=null) {  
         if (bindings) {
           n2=second_.listBindings(nName);  
         } else { 
           n2=second_.list(nName);
         }
       } else {
         if (bindings) {
           n2=second_.listBindings(sName);  
         } else { 
           n2=second_.list(sName);
         }           
       }
     }catch(NameNotFoundException ex){
         ;
     }catch(InvalidNameException ex){
         ;
     }
     return compoundList(n1,n2);
   }

   public NamingEnumeration list(Name name) throws NamingException
   {
     return listInternal(name,null,false);  
   }

   
   public NamingEnumeration list(String name) throws NamingException
   {
     return listInternal(null,name,false);  
   }

   public NamingEnumeration listBindings(Name name) throws NamingException
   {
     return listInternal(name,null,true);         
   }

   public NamingEnumeration listBindings(String name) throws NamingException
   {
     return listInternal(null,name,true);                
   }
   
   private Object lookupInternal(Name nName, String sName, boolean link) throws NamingException
   {       
      Object o = lookupWithNull(first_,nName, sName, link);
      if (o==null) {
          o = lookupWithNull(second_,nName, sName, link);          
      }else if (o instanceof Context) {
          o = compoundContext((Context)o,lookupWithNull(second_,nName,sName,link));
      }else{
         // LOG.debug("found in first, o="+o.toString());
      }
      if (o==null) {
          LOG.debug("name not found:"+sName);
          NameNotFoundException ex = new NameNotFoundException();
          if (nName!=null) {
            ex.setRemainingName(nName);
          }else{
            try {  
             nName=this.getNameParser(sName).parse(sName);  
             ex.setRemainingName(nName);
            }catch(NamingException ex1){
                LOG.debug("can't parse name "+sName, ex1);
            }
            ex.appendRemainingComponent(sName);
          }
          throw ex;
      }
      return o;
   }

   
     
   private static Object lookupWithNull(Context ctx, Name nName, String sName, boolean link) throws NamingException 
   {
      Object o=null; 
      try {
        if (link) {
          if (nName!=null) {  
            o=ctx.lookupLink(nName);
          } else {
            o=ctx.lookupLink(sName);  
          }
        }else{  
          if (nName!=null) {  
            o = ctx.lookup(nName);
          } else {
            o=ctx.lookup(sName);    
          }
        }
      }catch(NameNotFoundException ex){
          LOG.debug("NameNotFound",ex);          
      }catch(InvalidNameException ex){
          LOG.debug("InvalidNameException",ex);           
      }catch(NamingException ex){
          // we need this, becouse spring jndi throw generic namign exception
          //(with 'scheme not recognized) where it must throw NameNotFoundException
          LOG.debug("NamingException",ex);          
      }       
      return o;
   }

   
   
   private Object compoundContext(Context c1, Object o2)
   {
       if (o2==null) {
            return c1;
       }else if (o2 instanceof Context) {
            return new ChainContext(c1,(Context)o2,writeFirst_,writeSecond_,initialContextFactory_);
       }else{
           // o2 is object wheb o1 is context => o1 is first.
           return c1;
       }
   
   }
   
   public Object lookup(Name name) throws NamingException          
   {
      return lookupInternal(name,null,false); 
   }
   

   public Object lookup(String name) throws NamingException           
   {
      return lookupInternal(null,name,false); 
   }
   
   public Object lookupLink(Name name) throws NamingException
   {
       return lookupInternal(name,null,true);
   }   
   
   public Object lookupLink(String name) throws NamingException
   {
       return lookupInternal(null,name,true);
   }      
   
   
   public static class CompoundNamingEnumeration implements NamingEnumeration
   {
     
      public CompoundNamingEnumeration(NamingEnumeration frs, NamingEnumeration snd)
      {
        frs_=frs;
        snd_=snd;
        inFrs_=true;
      }
      
      public boolean hasMore() throws NamingException
      {
        if (inFrs_) {
            if (frs_.hasMore()) {
                return true;
            }else{
                inFrs_=false;
                return snd_.hasMore();
            }
        }else{
            return snd_.hasMore();
        }  
      }
      
      public Object next() throws NamingException
      {
        if (inFrs_) {
            if (frs_.hasMore()) {
                return frs_.next();
            }else{
                inFrs_=false;
                return snd_.next();
            }
        }else{
            return snd_.next();
        }  
      }
      
      public void close() throws NamingException
      {
        frs_.close();  
        snd_.close();
      }
      
      public boolean hasMoreElements() 
      { 
        try {  
          return hasMore(); 
        }catch(NamingException ex){
            return false;
        }
      }
      
      public Object nextElement()
      {
       try {   
         return next(); 
       }catch(NamingException ex){
           return null;
       }
      }
      
      boolean inFrs_;        
      NamingEnumeration frs_;
      NamingEnumeration snd_;
   }
   
   
   /**
    * throws OperationNotSupportedException    
    * @return
    */
   public String getNameInNamespace() throws NamingException
   {
     throw new OperationNotSupportedException();  
   }
   
   private void rebindInternal(Name nName, String sName, Object obj) throws NamingException
   {
     boolean rebinded = false;  
     boolean notFound = false;
     if (writeFirst_) {
       try {  
         if (nName!=null) {
             first_.rebind(nName,obj);
         }else{
             first_.rebind(sName,obj);
         }
         rebinded=true;
       }catch(NameNotFoundException ex){
           notFound=true;
       }       
     }
     if (writeSecond_) {
         try{
             if (nName!=null) {
                 second_.rebind(nName, obj);
             }else{
                 second_.rebind(sName, obj);
             }
             rebinded=true;
         }catch(NameNotFoundException ex){        
             notFound=true;
         }
     }
     if (!rebinded) {
        if (notFound) {
            throw new NameNotFoundException();
        }else{
            throw new OperationNotSupportedException();
        }
     }
   }
   
   public void rebind(Name name, Object obj) throws NamingException
   {
       rebindInternal(name,null,obj);
   }
   
   public void rebind(String name, Object obj) throws NamingException
   {
       rebindInternal(null,name,obj);
   }
   
   private void renameInternal(Name n1, Name n2, String s1, String s2) throws NamingException
   {
       boolean notFound=false;
       boolean inFirstNotFound=false;
       boolean inSecondNotFound=false;
       boolean done=false;      
       if (writeFirst_) {
          try {
            if (n1!=null) {  
              first_.rename(n1, n2);
            }else{
              first_.rename(s1, s2);  
            }
            done=true;
          }catch(NameNotFoundException ex){
              notFound=true;
              inFirstNotFound=true;
          }
       }
       if (writeSecond_) {
           try {
               if (n1!=n2) {
                   second_.rename(n1, n2);
               }else{
                   second_.rename(s1, s2);
               }
           }catch(NameNotFoundException ex){
               notFound=true;
           }
       }
       if (notFound){
           throw new NameNotFoundException();
       }
   }
   
   public void rename(Name n1, Name n2) throws NamingException
   {
       renameInternal(n1,n2,null,null);
   }
   
   public void rename(String s1, String s2) throws NamingException
   {
       renameInternal(null,null,s1,s2);
   }
   
   private void unbindInternal(Name nName, String sName) throws NamingException
   {
      boolean unbinded=false; 
      boolean notfound=false;
      if (writeFirst_) {
          try {
             if (nName!=null) {
                 first_.unbind(nName);
             }else{
                 first_.unbind(sName);
             } 
             unbinded=true;
          }catch(NameNotFoundException ex){
              notfound=true;
          }
      }
      if (writeSecond_) {
          if (!writeFirst_) {
             Object o=null;
             try {
               if (nName!=null) {
                 o = first_.lookup(nName);
               }else{
                 o = first_.lookup(sName);  
               }
             }catch(NameNotFoundException ex){
                 //all ok
             }
             if (o!=null) {
                 // so, it is impossible to unbomd.
                 throw new CannotProceedException();
             }
          }
          if (nName!=null) {
            second_.unbind(nName);
          }else{
            second_.unbind(sName);  
          }
          unbinded=true;
      }
      if (!unbinded) {
          throw new OperationNotSupportedException();
      }
   }
   
   public void unbind(Name name) throws NamingException
   {
     unbindInternal(name,null);  
   }

   public void unbind(String name) throws NamingException
   {
     unbindInternal(null, name);  
   }
   
   
   private Context first_;
   private boolean writeFirst_;
   private Context second_;
   private boolean writeSecond_;
   
   private ChainInitialContextFactory initialContextFactory_;
 
   private static Log LOG = LogFactory.getLog(ChainContext.class);
   
}
