package ua.gradsoft.cppp.utils;

import java.io.*;
import java.util.*;

public class Comment
{

 public  Comment()
 {
  lines_ = new Vector();
 }

 public  Comment(String line)
 {
  lines_ = new Vector();
  lines_.add(line);
 }


 public  void    addLine(String line)
 {
  lines_.add(line);
 }

 public  void    clear()
 {
  lines_.clear();
 }

 public  String  getLineAt(int n)
 {
  return (String)lines_.get(n);
 }

 public  int     nLines()
 {
  return lines_.size();
 }

 public  void print(String fname, int firstLine, PrintStream out)
 {
  for(int i=0; i<nLines(); ++i) {
    out.print(fname + ":"+(firstLine+i)+":" + getLineAt(i));
  }
 }

 private Vector  lines_;
}
