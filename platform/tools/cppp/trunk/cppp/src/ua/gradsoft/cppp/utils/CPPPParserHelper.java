package ua.gradsoft.cppp.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;

import java.util.Map;
import ua.gradsoft.cppp.parse.CEXPRParser;
import ua.gradsoft.termware.DefaultFacts;
import ua.gradsoft.termware.IEnv;
import ua.gradsoft.termware.Substitution;
import ua.gradsoft.termware.Term;
import ua.gradsoft.termware.TermHelper;
import ua.gradsoft.termware.TermSystem;
import ua.gradsoft.termware.TermWare;
import ua.gradsoft.termware.TermWareException;
import ua.gradsoft.termware.XTerm;
import ua.gradsoft.termware.envs.SystemLogEnv;
import ua.gradsoft.termware.exceptions.AssertException;
import ua.gradsoft.termware.strategies.BTStrategy;

public class CPPPParserHelper extends DefaultFacts {

    public CPPPParserHelper() throws TermWareException {
        this(new SystemLogEnv());
    }

    public CPPPParserHelper(IEnv env) throws TermWareException {
        env_ = env;
        defs_ = new HashMap<String,Term>();

        cppEvalTermSystem_ = new TermSystem(new BTStrategy(),
                this, TermWare.getInstance());
        //cppEvalTermSystem_.setLoggingMode(true);
        TermWare.addGeneralTransformers(cppEvalTermSystem_);
        cppEvalTermSystem_.addNormalizer("defined", new DefinedTransformer(this));

        setLine(0);
        setFileName("unknown");
    }

    public void printTerm(Term t, PrintWriter out) throws TermWareException {
        if (t == null) {
            out.print("NULL");
        } else {
            Term left = TermHelper.getAttribute(t, "left");
            if (!left.isNil()) {
                out.print(left.getString());
            }
            if (t.isString()) {
                String s = t.getString();
                if (s == null) {
                    out.print("NULL");
                } else {
                    out.print(t.getString());
                }
            } else if (t.isComplexTerm()) {
                if (t.getName().equals("cons") || t.getName().equals("cons1")) {
                    printTerm(t.getSubtermAt(0), out);
                    if (mustBeDistance(t.getSubtermAt(0),t.getSubtermAt(1))) {
                        out.print(" ");
                    }
                    printTerm(t.getSubtermAt(1), out);
                } else if (t.getName().equals("W")) {
                    printTerm(t.getSubtermAt(0), out);
                } else if (t.getName().equals("I")) {
                    printTerm(t.getSubtermAt(0), out);
                } else if (t.getName().equals("N")) {
                    printTerm(t.getSubtermAt(0), out);
                } else if (t.getName().equals("EA")) {
                    printTerm(t.getSubtermAt(0), out);
                } else {
                    t.print(out);
                }
            } else {
                t.print(out);
            }
            Term right = TermHelper.getAttribute(t, "right");
            if (!right.isNil()) {
                out.print(right.getString());
            }
        }
    }

    public boolean checkUnparsedConstantExpression(Term t) throws TermWareException {
        if (t.isBoolean()) {
            return t.getBoolean();
        }
        String st = stringTerm(t);
        if (cexprParser_ == null) {
            cexprParser_ = new CEXPRParser(new StringReader(st));
        } else {
            cexprParser_.ReInit(new StringReader(st));
        }
        t = cexprParser_.lineWrapped();
        return checkConstantExpression(t);
    }

    public String stringTerm(Term t) throws TermWareException {
        ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(tmpOut);
        printTerm(t, writer);
        writer.flush();
        return tmpOut.toString();
    }

    public boolean checkConstantExpression(Term t) throws TermWareException {
        if (t.isBoolean()) {
            return t.getBoolean();
        } else if (t.isInt()) {
            return t.getInt() != 0;
        } else if (t.isDouble()) {
            return t.getDouble() != 0.0;
        } else if (t.isString()) {
            env_.getLog().println("warning: string in constant expression");
            return false;
        } else if (t.isComplexTerm()) {
            t = evalConstantExpression(t);
            if (t.isComplexTerm()) {
                ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
                t.print(new PrintStream(tmpOut));
                String st = tmpOut.toString();
                throw new AssertException("Can't eval constant expression:" + st);
            } else {
                return checkConstantExpression(t);
            }
        } else {
            ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
            t.print(new PrintStream(tmpOut));
            String st = tmpOut.toString();
            throw new AssertException("Can't eval constant expression:" + st);
        }
    }

    public Term evalConstantExpression(Term t) throws TermWareException {
        return cppEvalTermSystem_.reduce(t);
    }

    //
    //
    public String getFileName() {
        return filename_;
    }

    public void setFileName(String filename) {
        filename_ = filename;
    }

    public int getLine() {
        return line_;
    }

    public void setLine(int line) {
        line_ = line;
    }


    //IFacts
    public String getDomainName() {
        return "CPPP";
    }

    /**
     * evaluate term like 'defined(x)' and return true, if x is situated
     * in set of active macrodefinitions.
     */
    public boolean check(Term t) throws TermWareException {
        if (t.isBoolean()) {
            return t.getBoolean();
        } else if (t.isComplexTerm()) {
            if (t.getName().equals("defined") && t.getArity() == 1) {
                Term t1 = t.getSubtermAt(0);
                if (t1.isAtom()) {
                    return defs_.containsKey(t.getName());
                }
                if (t1.isString()) {
                    return defs_.containsKey(t.getString());
                }
            }
        }
        return false;
    }

    public boolean defined(String name) {
        return defs_.containsKey(name);
    }

    public void undef(String name) {
        defs_.remove(name);
    }

    public Term ask(Term t) throws TermWareException {
        // nothing: return t
        // TODO: do something
        //t=cppTermSystem_.apply(t);
        t = ncons(t);
        
        //t=cppTermSystem1_.apply(t);
        t = resolveMacroses(t, false);

        return t;
    // return cppEvalTermSystem_.apply(t);
    }

    /**
     * <code> t </code> must be term of form "define(x,y)" where x and y is tokens
     * or define_list(x,y,z)
     * where x must have form [ Name , "(" , x1 , "," x2 ", " .... ")" ]
     *                  or      Name
     * and y must have form [ t1 : .. tn ]
     **/
    public void set(Term t) throws TermWareException {
        if (t.isComplexTerm()) {
            if (t.getName().equals("define")) {
                if (t.getArity() != 2) {
                    throw new AssertException("arity(define) must be 2");
                }
                Term t1 = t.getSubtermAt(0);
                Term t2 = t.getSubtermAt(1);
                String name = t1.getName();

                BooleanHolder changed = new BooleanHolder(false);
                t2 = ncons(t2);
                t2 = resolveMacroses(t2, changed, true);
                t2 = escapeAtom(t2, name);

                //t2=cppTermSystem_.apply(t2);
                //t2=cppEvalTermSystem_.apply(t2);

                Term rule = TermWare.getInstance().getTermFactory().createTerm("rule", t1, t2);


                //cppEvalTermSystem_.addRule(rule);

                defs_.put(name, rule);
            } else if (t.getName().equals("define_list")) {
                if (t.getArity() != 3) {
                    throw new AssertException("arity(define_list) must be 3");
                }
                Term t1 = t.getSubtermAt(0);
                Term t2 = t.getSubtermAt(1);
                Term t3 = t.getSubtermAt(2);
                //t2=cppTermSystem_.apply(t2); 
                //t3=cppTermSystem_.apply(t3);
                //t3=cppEvalTermSystem_.apply(t3);
                boolean escape_t2 = false;
                boolean escape_t3 = false;
                if (t3.isAtom()) {
                    t3 = TermWare.getInstance().getTermFactory().createTerm("Id", t3);
                    escape_t3 = true;
                }
                if (t2.isAtom()) {
                    t2 = TermWare.getInstance().getTermFactory().createTerm("Id", t2);
                    escape_t2 = true;
                }
                int nVariables = identToX(t2, t3);
                if (escape_t3) {
                    t3 = t3.getSubtermAt(0);
                }
                if (escape_t2) {
                    t2 = t2.getSubtermAt(0);
                }

                BooleanHolder changed = new BooleanHolder(false);
                t3 = ncons(t3);
                t3 = resolveMacroses(t3, changed, true);
                t3 = escapeAtom(t3, t1.getName());

                Term definition = TermWare.getInstance().getTermFactory().createTerm(
                        "definition", t1, TermWare.getInstance().getTermFactory().createInt(nVariables), t3);
                //cppEvalTermSystem_.addRule(rule);
                defs_.put(t1.getName(), definition);
            } else {
                throw new AssertException("unknown set term");
            }
        }
    }

    /**
     * remove macros with name <code> t.name </code> from list of macroses.
     */
    public void remove(Term t) {
        if (t.isAtom()) {
            defs_.remove(t.getName());
        }
    }

    public Term resolveMacroses(Term line, boolean inDefine) throws TermWareException {
        BooleanHolder changed = new BooleanHolder(false);
        return resolveMacroses(line, changed, inDefine);
    }

    /**
     * resolve macroses.
     *  line - line in which we want to resolve macroses.
     *  chaned - setted to true, if line was changed (i.e. some substitution was succesfull)
     */
    public Term resolveMacroses(Term line, BooleanHolder changed, boolean inDefine) throws TermWareException {
        int n = 0;
        do {
            int n1 = 0;
            do {
                changed.set(false);
                line = resolveMacroses1(line, changed, inDefine);
                line = ncons(line);
                ++n;
            } while (changed.get() && n1 < 100);
            if (n1 == 100) {
                throw new AssertException("Too many nested macro evaluations");
            }
            if (!inDefine) {
                changed.set(false);
                line = checkNN(line, changed);
            }
            ++n;
        } while (changed.get() && n < 10);
        return line;
    }

    public Term resolveMacroses1(Term line, BooleanHolder changed, boolean inDefine) throws TermWareException {
        Term t = formMacroTerm(line, changed, inDefine);
        String tname = t.getName();
        if (t.getName().equals("l")) {
            return t.getSubtermAt(0);
        } else if (t.getName().equals("m")) {
            changed.set(true);
            return t.getSubtermAt(0);
        } else if (t.getName().equals("lm")) {
            changed.set(true);
            return ncons(t.getSubtermAt(0), t.getSubtermAt(1));
        } else if (t.getName().equals("mr")) {
            changed.set(true);
            Term r = resolveMacroses1(t.getSubtermAt(1), changed, inDefine);
            return ncons(t.getSubtermAt(0), r);
        } else if (t.getName().equals("lmr")) {
            changed.set(true);
            Term r = resolveMacroses1(t.getSubtermAt(2), changed, inDefine);
            return ncons(t.getSubtermAt(0), ncons(t.getSubtermAt(1), r));
        } else {
            throw new AssertException("internal error: bad macro term:" + t.getName());
        }
    }

    private Term escapeAtom(Term t, String name) throws TermWareException {
        if (t.isAtom()) {
            if (t.getName().equals(name)) {
                return TermWare.getInstance().getTermFactory().createTerm("EA", t);
            }
        } else if (t.isComplexTerm()) {
            for (int i = 0; i < t.getArity(); ++i) {
                t.setSubtermAt(i, escapeAtom(t.getSubtermAt(i), name));
            }
        }
        return t;
    }

    public void printDefs(PrintStream out)
    {
        out.println("{");
        for(Map.Entry<String,Term> e: defs_.entrySet()) {
            out.print("(");
            out.print(e.getKey());
            out.print(",");
            e.getValue().print(out);
            out.print(")");
            out.println();
        }
        out.println("}");
    }
    
    private int identToX(Term t1, Term t2) throws TermWareException {
        HashMap identifierList = new HashMap();
        createIdentToX(t1, identifierList);
        substIdentToX(t2, identifierList);
        return identifierList.size();
    }

    private void createIdentToX(Term t, HashMap s) throws TermWareException {
        if (!t.isComplexTerm()) {
            return;
        } else {
            for (int i = 0; i < t.getArity(); ++i) {
                Term tt = t.getSubtermAt(i);
                if (tt.isAtom()) {
                    Term x = TermWare.getInstance().getTermFactory().createX(s.size());
                    s.put(tt.getName(), x);
                    t.setSubtermAt(i, x);
                } else if (tt.isComplexTerm()) {
                    createIdentToX(tt, s);
                }
            }
        }
    }

    private void substIdentToX(Term t, HashMap s) throws TermWareException {
        if (!t.isComplexTerm()) {
            return;
        } else {
            for (int i = 0; i < t.getArity(); ++i) {
                Term tt = t.getSubtermAt(i);
                if (tt.isAtom()) {
                    Object o = s.get(tt.getName());
                    if (o == null) {
                        continue;
                    }
                    XTerm x = (XTerm) o;
                    t.setSubtermAt(i, x);
                } else if (tt.isComplexTerm()) {
                    substIdentToX(tt, s);
                }
            }
        }
    }

    //
    private final Term cons(Term t1, Term t2) throws TermWareException {
        return TermWare.getInstance().getTermFactory().createTerm("cons", t1, t2);
    }

    private final Term cons(String s1, Term t2) throws TermWareException {
        return cons(TermWare.getInstance().getTermFactory().createString(s1), t2);
    }

    private final Term cons(Term t1, String s2) throws TermWareException {
        return cons(t1, TermWare.getInstance().getTermFactory().createString(s2));
    }

    private final Term ncons(Term t) throws TermWareException {
        if (t.isComplexTerm() && t.getName().equals("cons")) {
         if (t.getArity()==2) {
            Term t1 = t.getSubtermAt(0);
            Term t2 = t.getSubtermAt(1);
            Term left = TermHelper.getAttribute(t, "left");
            if (!left.isNil()) {
                t1=addLeftTermAttribute(t1, left.getString());
            }
            Term right = TermHelper.getAttribute(t, "right");
            if (!right.isNil()) {
                t2=this.addRightTermAttribute(t2, right.getString());
            }
            Term retval = ncons(t1,t2);
            return retval;
         } else {
            throw new AssertException("arity of cons must be 2");
         }
        }
        return t;
    }

    /**
     * must be called immediatly after ncons
     * x ## y -> concat(x,y)
     * # x -> string(x)
     **/
    private final Term checkNN(Term x, BooleanHolder changed) throws TermWareException {
        if (!(x.isComplexTerm() && x.getName().equals("cons"))) {
            return x;
        }
        Term x1o = x.getSubtermAt(0);
        Term x1 = x1o;
        if (x1.isString() && x1.getName().equals("#")) {
            Term x2 = x.getSubtermAt(1);
            Term rest = null;
            if (x2.isComplexTerm()) {
                if (x2.getArity() == 1) {
                    x2 = x2.getSubtermAt(0);
                } else if (x2.getName().equals("cons")) {
                    rest = x2.getSubtermAt(1);
                    x2 = x2.getSubtermAt(0);
                    if (x2.getArity() == 1) {
                        x2 = x2.getSubtermAt(0);
                    }
                } else {
                    return x;
                }
            }
            if (x2.isX()) {
                // unbinded variable inside substitution
                return x;
            }
            String newS = "\"" + x2.getName() + "\"";
            changed.set(true);
            Term replacement = TermWare.getInstance().getTermFactory().createString(newS);
            if (rest == null) {
                return replacement;
            } else {
                return cons(replacement, rest);
            }
        }
        if (x1.getArity() == 1) {
            x1 = x1.getSubtermAt(0);
        }
        Term x2 = x.getSubtermAt(1);
        Term x3 = null;
        if (x2.isComplexTerm() && x2.getName().equals("cons")) {
            Term y = x2.getSubtermAt(0);
            if (!(y.isString() && y.getString().equals("##"))) {
                return cons(x1o, checkNN(x2, changed));
            }
            x3 = x2.getSubtermAt(1);
        }
        if (x3 == null) {
            return x;
        } // x ## <NEWLINE>
        // TODO: insert exception here.
        Term z;
        Term rest = null;
        if (x3.isComplexTerm() && x3.getName().equals("cons")) {
            z = x3.getSubtermAt(0);
            rest = x3.getSubtermAt(1);
        } else {
            if (x3.getArity() == 1) {
                z = x3.getSubtermAt(0);
            } else if (x3.isString() || x3.isChar() || x3.isNumber() || x.isAtom()) {
                z = x3;
            } else {
                // impossible;
                return x;
            }
        }
        if (z.getArity() == 1) {
            z = z.getSubtermAt(0);
        }
        Term ct = TermWare.getInstance().getTermFactory().createString(x1.getName() + z.getName());
        changed.set(true);
        if (rest == null) {
            return ct;
        } else {
            return cons(ct, checkNN(rest, changed));
        }
    }

    private final Term ncons(Term t1, Term t2) throws TermWareException {
        Term retval;
        if (t1 == null) {
            retval=ncons(t2);
        }  else if (t1.getName().equals("W")) {
            t2=addLeftTermAttribute(t2, t1);
            retval=ncons(t2);
        } else if (t2 == null) {
            retval = ncons(t1);
        } else if (t2.getName().equals("W")) {
            t1=addRightTermAttribute(t1,t2);
            retval = ncons(t1);
        } else if (t1.isComplexTerm() && t1.getName().equals("cons")) {
            Term x = t1.getSubtermAt(0);
            Term y = t1.getSubtermAt(1);
            Term z = t2;
            if (x.getName().equals("W")) {
                retval = ncons(y,z);
                retval = addLeftTermAttribute(retval,x);            
            } else if (y.getName().equals("W")) {
                x = addRightTermAttribute(x, y);
                retval = ncons(x, z);              
            } else if (z.getName().equals("W")) {
                retval = ncons(x, y);
                retval=addRightTermAttribute(retval, z);             
            } else if (x.getName().equals("cons")) {
                retval = ncons(x, ncons(y, z));
            } else {
                retval = cons(x, ncons(y, z));
            }
        } else {
            retval = cons(ncons(t1), ncons(t2));
        }
        return retval;
    }

    private Term addLeftTermAttribute(Term t, Term w) throws TermWareException
    {
      String s = w.getSubtermAt(0).getString();
      Term wLeft = TermHelper.getAttribute(w, "left");
      if (!wLeft.isNil()) {
          s=wLeft.getString()+s;
      }
      Term wRight = TermHelper.getAttribute(w, "right");
      if (!wRight.isNil()) {
          s=s+wRight.getString();
      }
      return this.addLeftTermAttribute(t, s);
    }

    private Term addLeftTermAttribute(Term t, String s) throws TermWareException
    {
      Term left = TermHelper.getAttribute(t, "left");
      if (left.isNil()) {
          left=createString(s);
      }else{
          left=createString(s+left.getString());
      }
      Term retval = TermHelper.setAttribute(t, "left", left);
      return retval;
    }


    private Term addRightTermAttribute(Term t, Term w) throws TermWareException
    {
      String s = w.getSubtermAt(0).getString();
      Term wLeft = TermHelper.getAttribute(w, "left");
      if (!wLeft.isNil()) {
          s=wLeft.getString()+s;
      }
      Term wRight = TermHelper.getAttribute(w, "right");
      if (!wRight.isNil()) {
          s=s+wRight.getString();
      }
      return this.addRightTermAttribute(t, s);
    }

    private Term addRightTermAttribute(Term t, String s) throws TermWareException
    {
      Term right = TermHelper.getAttribute(t, "right");
      if (right.isNil()) {
          right=createString(s);
      }else{
          right=createString(right.getString()+s);
      }
      Term retval = TermHelper.setAttribute(t, "right", right);
      return retval;        
    }


    private Term formMacroTerm(Term t, BooleanHolder changed, boolean inDefine) throws TermWareException {
        // find first macro entity.
        if (t.isString() || t.isChar() || t.isNumber() || t.isAtom()) {
            if (t.isAtom()) {
                if (defs_.containsKey(t.getName())) {
                    Term x = parseMacroWithRest(t, changed);
                    if (x.isComplexTerm()) {
                        if (x.getArity() == 1) {
                            return TermWare.getInstance().getTermFactory().createTerm("m", x.getSubtermAt(0));
                        } else {
                            return TermWare.getInstance().getTermFactory().createTerm("mr", x.getSubtermAt(0), x.getSubtermAt(1));
                        }
                    } else {
                        return TermWare.getInstance().getTermFactory().createTerm("m", x);
                    }
                } else if (!inDefine) {
                    t = checkFileOrLine(t, changed);
                }
            }
            return TermWare.getInstance().getTermFactory().createTerm("l", t);
        }
        Term cursor = t;
        Term left = null;
        Term right = null;
        Term middle = null;
        while (cursor.isComplexTerm() && cursor.getName().equals("cons")) {
            Term frs = cursor.getSubtermAt(0);
            Term next = cursor.getSubtermAt(1);
            Term snd = null;
            if (next.getName().equals("cons")) {
                snd = next.getSubtermAt(0);
            }
            if (frs.isAtom()) {
                String s = frs.getName();
                if (!defs_.containsKey(s)) {
                    if (!inDefine) {
                        frs = checkFileOrLine(frs, changed);
                    }
                    if (left == null) {
                        left = frs;
                    } else {
                        left = ncons(left, frs);
                    }
                    cursor = next;
                    continue;
                } else {
                    Term x = parseMacroWithRest(cursor, changed);
                    if (x.isString() || x.isChar() || x.isNumber() || x.isAtom()) {
                        middle = x;
                    } else {
                        middle = x.getSubtermAt(0);
                    }
                    if (x.getArity() == 2) {
                        right = x.getSubtermAt(1);
                    }
                    if (left == null) {
                        if (right == null) {
                            return TermWare.getInstance().getTermFactory().createTerm("m", middle);
                        } else {
                            return TermWare.getInstance().getTermFactory().createTerm("mr", middle, right);
                        }
                    } else if (right == null) {
                        if (middle == null) {
                            return TermWare.getInstance().getTermFactory().createTerm("l", left);
                        } else {
                            return TermWare.getInstance().getTermFactory().createTerm("lm", left, middle);
                        }
                    } else {
                        return TermWare.getInstance().getTermFactory().createTerm("lmr", left, middle, right);
                    }
                }
            } else {
                if (left == null) {
                    left = frs;
                } else {
                    left = ncons(left, frs);
                }
                cursor = next;
                continue;
            }
        }
        if (cursor.isAtom()) {
            Object o = defs_.get(cursor.getName());
            if (o != null) {
                cursor = parseMacroWithRest(cursor, changed);
                if (cursor.isComplexTerm()) {
                    middle = cursor.getSubtermAt(0);
                    if (cursor.getArity() == 2) {
                        right = cursor.getSubtermAt(1);
                    }
                } else {
                    middle = cursor;
                }
                if (left == null && middle == null && right == null) {
                    // impossible
                    throw new AssertException("impossible parsing: no parts at all");
                } else if (left == null && middle == null && right != null) {
                    throw new AssertException("impossible parsing: only right part");
                } else if (left == null && middle != null && right == null) {
                    return TermWare.getInstance().getTermFactory().createTerm("m", middle);
                } else if (left == null && middle != null && right != null) {
                    return TermWare.getInstance().getTermFactory().createTerm("mr", middle, right);
                } else if (left != null && middle == null && right == null) {
                    return TermWare.getInstance().getTermFactory().createTerm("l", left);
                } else if (left != null && middle == null && right != null) {
                    // impossible
                    return TermWare.getInstance().getTermFactory().createTerm("lr", left, right);
                } else if (left != null && middle != null && right == null) {
                    return TermWare.getInstance().getTermFactory().createTerm("lm", left, middle);
                } else {
                    return TermWare.getInstance().getTermFactory().createTerm("lmr", left, middle, right);
                }
            } else if (!inDefine) {
                cursor = checkFileOrLine(cursor, changed);
            }
        }
        if (left == null) {
            left = cursor;
        } else {
            left = ncons(left, cursor);
        }
        return TermWare.getInstance().getTermFactory().createTerm("l", left);
    }

    private Term checkFileOrLine(Term x, BooleanHolder changed) throws TermWareException {
        String s = x.getName();
        if (s.equals("__FILE__")) {
            changed.set(true);
            x = TermWare.getInstance().getTermFactory().createString("\"" + getFileName() + "\"");
        } else if (s.equals("__LINE__")) {
            changed.set(true);
            x = TermWare.getInstance().getTermFactory().createString("" + getLine());
            x = TermWare.getInstance().getTermFactory().createTerm("N", x);
        }
        return x;
    }

    //
    //  current=cons                     or current=<MacroName>
    //           <MacroName>
    //           cons(
    //                 "(" - must be
    //                 cons( .....
    private Term parseMacroWithRest(Term t, BooleanHolder changed) throws TermWareException {
        if (!t.isComplexTerm()) {
            if (t.isAtom()) {
                Object o = defs_.get(t.getName());
                if (o == null) {
                    throw new AssertException("Invalid call of parseMacro");
                }
                Term def = (Term) o;
                if (def == null) {
                    throw new AssertException("Invalid definition in table");
                }
                if (def.getName().equals("rule")) {
                    return def.getSubtermAt(1);
                } else if (def.getName().equals("definition")) {
                    throw new AssertException("call of macro " + t.getName() + " without arguments");
                }
            }
            throw new AssertException("Invalid call of parseMacroWithRest");
        }
        Object o = defs_.get(t.getSubtermAt(0).getName());
        if (o == null) {
            throw new AssertException("Invalid call of parseMacro");
        }
        Term def = (Term) o;
        if (def == null) {
            throw new AssertException("Invalid definition in table");
        }
        if (def.getName().equals("rule")) {
            t.setSubtermAt(0, def.getSubtermAt(1));
            changed.set(true);
            return t;
        } else if (def.getName().equals("definition")) {

            int n = getDefinitionArity(def);
            String macroName = def.getSubtermAt(0).getName();
            Term[] substTerms = new Term[n];
            t = t.getSubtermAt(1);
            if (!t.getName().equals("cons")) {
                throw new AssertException("reading of pattern <MacroName> \"(\" failed: end of line");
            }
            if (!t.getSubtermAt(0).isString()) {
                throw new AssertException("<MacroName> \"(\" expected for macro:" + macroName);
            } else if (!t.getSubtermAt(0).getString().equals("(")) {
                throw new AssertException("<MacroName> \"(\" expected for macro:" + macroName);
            } else {
                t = t.getSubtermAt(1);
            }
            int i = 0;
            boolean lastIsRB = false;
            while (t.getName().equals("cons") && i < n) {
                t = parseMacroArgument(t, substTerms, i, 0);
                if (t.getName().equals("cons")) {
                    if (t.getSubtermAt(0).isString()) {
                        String s = t.getSubtermAt(0).getString();
                        if (!s.equals("(") && !s.equals(",") && !s.equals(")")) {
                            throw new AssertException("macro arguments must be delimited by \",\", i see:" + s);
                        } else if (s.equals(")") && i != n - 1) {
                            throw new AssertException("too few arguments for macro:" + macroName);
                        }
                    }
                    t = t.getSubtermAt(1);
                } else if (t.isString()) {
                    if (!t.getString().equals(")")) {
                        throw new AssertException("macro substitution does not ended with ')'");
                    } else {
                        if (i != n - 1) {
                            throw new AssertException("macro arguments must be delimited by \",\" ");
                        }
                        lastIsRB = true;
                    }
                }
                ++i;
            }
            Term out = getDefinitionOutput(def);
            out = substituteMacroOutput(out, substTerms);
            changed.set(true);
            //System.err.print("parseMacroWithRest, before_rest=");
            //t.print(System.err);
            //System.err.println();
            if (lastIsRB) {
                return TermWare.getInstance().getTermFactory().createTerm("one", out);
            } else {
                return TermWare.getInstance().getTermFactory().createTerm("pair", out, t);
            }
        } else {
            throw new AssertException("invalid macro definition in table. ");
        }
    }

    //         cons                      |end state:       current=cons
    //           "(" | ","               |                          ")" | ","
    //current=    cons                   |                          .....
    //              m1,                  |              or 
    //              cons( .....          |                 current=")"
    private Term parseMacroArgument(Term t, Term[] args, int argIndex, int nLevel) throws TermWareException {
        Term current = t;
        Term argument = null;
        Term prev_current = null;
        while (current.isComplexTerm() && current.getName().equals("cons")) {
            Term next = current.getSubtermAt(1);
            Term token = current.getSubtermAt(0);
            if (token.isString()) {
                String s = token.getString();
                if (s.equals("(")) {
                    ++nLevel;
                } else if (s.equals(",")) {
                    if (nLevel == 0) {
                        if (argument == null) {
                            throw new AssertException("empty macro argument");
                        }
                        args[argIndex] = argument;
                        return current;
                    }
                } else if (s.equals(")")) {
                    if (nLevel == 0) {
                        if (argument == null) {
                            throw new AssertException("empty last macro argument");
                        }
                        args[argIndex] = argument;
                        return current;
                    } else {
                        --nLevel;
                    }
                }
            } /* isString */
            if (argument == null) {
                argument = token;
            } else {
                argument = ncons(argument, token);
            }
            prev_current = current;
            current = next;
            continue;
        }
        if (current.isString()) {
            if (!current.getString().equals(")")) {
                throw new AssertException(" ')' expected at and of macro call for macro " + t.getName());
            }
            if (argument == null) {
                throw new AssertException(" last macro parameter of " + t.getName() + " is empty ");
            }
            args[argIndex] = argument;
        } else {
            throw new AssertException(" ')' expected at and of macro call for " + t.getName());
        }

        return current;
    }

    private Term substituteMacroOutput(Term t, Term[] args) throws TermWareException {
        if (t.isX()) {
            if (t.minFv() > args.length) {
                throw new AssertException("invalid macro substitution");
            }
            return args[t.minFv()];
        } else if (t.isComplexTerm()) {
            Term[] newBody = new Term[t.getArity()];
            for (int i = 0; i < t.getArity(); ++i) {
                newBody[i] = substituteMacroOutput(t.getSubtermAt(i), args);
            }
            return TermWare.getInstance().getTermFactory().createComplexTerm(t.getName(), newBody);
        } else {
            return t;
        }
    }
    //
    private IEnv env_;
    private TermSystem cppEvalTermSystem_ = null;

    private Term getDefinitionOutput(Term definition) throws TermWareException {
        return definition.getSubtermAt(2);
    }

    private final int getDefinitionArity(Term definition) throws TermWareException {
        return definition.getSubtermAt(1).getInt();
    }

    public IEnv getEnv() {
        return env_;
    }

    public void setEnv(IEnv env) {
        env_ = env;
    }

    public boolean isDebugMode() {
        return debugMode_;
    }

    public void setDebugMode(boolean mode) {
        debugMode_ = mode;
    }

    public Substitution getCurrentSubstitution() {
        return null;
    }

    public Substitution swapCurrentSubstitution(Substitution s) {
        return null;
    }

    public void setDebugEntity(String s) {
    }

    public void unsetDebugEntity(String s) {
    }

    public boolean getCurrentStopFlag() {
        return false;
    }

    /**
     * do nothing
     */
    public boolean setCurrentStopFlag(boolean stopFlag) {
        // do nothing
        return true;
    }
    
    /**
     * return true if tokens t1 and t2 can't be concatenated in parsed
     * state without inserting whitespace between them.
     * (i. e. when to insert timnespace)
     * 
     * @param t1
     * @param t2
     * @return true if t1 and t2 must be separated by whitespace during print
     */
    private boolean mustBeDistance(Term t1, Term t2)
    {
      if (t1.isAtom() && t2.isAtom()) {
          return true;
      }
      if (t1.isString()) {
          String st1 = t1.getString();
          if (st1.startsWith("\"") && st1.endsWith("\"")) {
              return true;
          }else if (st1.startsWith("'") && st1.endsWith("'")) {
              return true;
          }
      }else if(t2.isString()) {
          String st2 = t2.getString();
          if (st2.startsWith("\"") && st2.endsWith("\"")) {
              return true;
          }else if (st2.startsWith("'") && st2.endsWith("'")) {
              return true;
          }
      }else if (t2.isComplexTerm() && t2.getName().equals("cons")) {
          return mustBeDistance(t1,t2.getSubtermAt(0));
      }else if (t1.isComplexTerm() && t1.getName().equals("cons")) {
          if (t1.getSubtermAt(1).isNil()) {
              return mustBeDistance(t1.getSubtermAt(0),t2);
          }else{
              return mustBeDistance(t1.getSubtermAt(1),t2);
          }
      }
      return false;
    }
    
    private Term createString(String s) 
    {
      return TermWare.getInstance().getTermFactory().createString(s);  
    }

    //
    // we store in defs pairs <Name>, definition
    // where definition is rule(in,out)
    // or definition(name,arity,out);
    private HashMap<String,Term> defs_;
    private String filename_;
    private int line_;
    private CEXPRParser cexprParser_ = null;
    private boolean debugMode_ = false;
}

;
