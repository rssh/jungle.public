package ua.gradsoft.phpjao;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import ua.gradsoft.javachecker.ConfigException;
import ua.gradsoft.javachecker.EntityNotFoundException;
import ua.gradsoft.javachecker.JavaCheckerFacade;
import ua.gradsoft.javachecker.models.JavaResolver;
import ua.gradsoft.javachecker.models.JavaTypeModel;
import ua.gradsoft.javachecker.models.TermUtils;
import ua.gradsoft.parsers.php5.PhpParserFactory;
import ua.gradsoft.printers.php5.PhpPrinterFactory;
import ua.gradsoft.termware.IEnv;
import ua.gradsoft.termware.IParser;
import ua.gradsoft.termware.IPrinter;
import ua.gradsoft.termware.Term;
import ua.gradsoft.termware.TermSystem;
import ua.gradsoft.termware.TermWare;
import ua.gradsoft.termware.TermWareException;
import ua.gradsoft.termware.envs.SystemEnv;

/**
 *Facade for phpjao
 * @author rssh
 */
public class Facade {

    public void configure(Configuration cfg) throws PhpJaoConfigException
    {
      cfg_=cfg;
      init();
    }

    private void init() throws PhpJaoConfigException
    {
      IEnv   env = new SystemEnv();
      TermWare.getInstance().setEnv(env);
      if (cfg_.getPHPJAOHome()!=null) {
          JavaCheckerFacade.setHome(cfg_.getPHPJAOHome());
      }
      JavaCheckerFacade.setHomeRequired(false);
      JavaCheckerFacade.setMandatoryCheckersLoading(false);
      try {
          JavaCheckerFacade.init();
      }catch(ConfigException ex){
          throw new PhpJaoConfigException(ex.getMessage(),ex);
      }
      for(String includeDir: cfg_.getIncludeDirs()) {
          JavaCheckerFacade.addInputDirectory(includeDir, false);
      }
      for(String includeJar: cfg_.getIncludeJars()) {
          JavaCheckerFacade.addIncludeJar(includeJar);
      }
      TermWare.getInstance().addParserFactory("PHP", new PhpParserFactory());
      TermWare.getInstance().addPrinterFactory("PHP", new PhpPrinterFactory());
      Thread.currentThread().setContextClassLoader(Facade.class.getClassLoader());
      /*
      try {
        TermWare.getInstance().setTermLoaderClassName(PhpJaoTermLoader.class.getName());
      }catch(ClassNotFoundException ex){
         throw new PhpJaoConfigException("Internal error: can't initialized facade",ex);
      }catch(InstantiationException ex){
         throw new PhpJaoConfigException("Internal error: can't initialized facade",ex);
      }catch(IllegalAccessException ex){
          throw new PhpJaoConfigException("Internal error: can't initialized facade",ex); 
      }
       */
      try {
        facts_=new PhpJaoFacts(this);
      }catch(TermWareException ex){
          throw new PhpJaoConfigException("Can't initialized facade",ex);
      }
      //TODO: add debgu flag
      //TermWare.getInstance().getSysSystem().setLoggingMode(true);
      //TermWare.getInstance().getSysSystem().setLoggedEntity("All");
      try {
        system_ = TermWare.getInstance().getOrCreateDomain("phpjao").resolveSystem("phpjao");
      }catch(TermWareException ex){
          throw new PhpJaoConfigException("Can't initialized facade",ex);
      }
      system_.setFacts(facts_);
    }


    public void process() throws PhpJaoProcessingException
    {
        PrintWriter output = null;
        try {
          output = new PrintWriter(new FileWriter(cfg_.getOutputFile()));
        }catch(IOException ex){
          throw new PhpJaoProcessingException("Can't write to file "+cfg_.getOutputFile()+":"+ex.getMessage(),ex);
        }
        try {
          IPrinter printer = TermWare.getInstance().getPrinterFactory("PHP").createPrinter(output, cfg_.getOutputFile(), system_, TermUtils.createNil());
          if (!cfg_.getWithoutRequireHeader()) {
            printRequire(printer);
          }
          for(String className: cfg_.getClassnames()) {
            JavaTypeModel tm = JavaResolver.resolveTypeModelByFullClassName(className);
            Term javaTerm = tm.getModelTerm();
            Term wrappedTerm = null;
            //if (tm.getAnnotation("javax.persistence.Entity")!=null) {
                wrappedTerm = TermUtils.createTerm("JavaEntity",javaTerm);
            //}else{
            //    wrappedTerm = TermUtils.createTerm("JavaService",javaTerm);
            //}
            Term phpTerm = system_.reduce(wrappedTerm);
            printer.writeTerm(phpTerm);
          }
          printer.flush();
        }catch(TermWareException ex){
            throw new PhpJaoProcessingException("exception during processing", ex);
        }catch(EntityNotFoundException ex){
            throw new PhpJaoProcessingException("entity not found: ex.getEntityName()",ex);
        }finally{
            output.close();
        }
    }

    private void printRequire(IPrinter printer) throws TermWareException
    {
      String requireString="<?php require_once('"+cfg_.getPhpHeader()+"');  ?>";
      IParser parser = TermWare.getInstance().getParserFactory("PHP").createParser(
                          new StringReader(requireString),
                          "buildin", TermUtils.createNil() , system_.getInstance());
      Term rt = parser.readTerm();
      printer.writeTerm(rt);
      printer.flush();
    }

    private TermSystem  system_;
    private PhpJaoFacts facts_;
    private Configuration cfg_;

}
