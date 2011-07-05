package ua.gradsoft.cppp.utils;

import java.io.*;
import java.util.*;
import java.text.*;

public class CommentsDB
{
                 
 public  CommentsDB()
 { files_=null; }

 public  synchronized void  add(String file, int line, int column, String comment)
 {
   if (files_==null) {
       files_=new HashMap();
   }
   FilesEntry fe = (FilesEntry)files_.get(file);
   if (fe==null) {
     fe=new FilesEntry();
     files_.put(file,fe);
   }
   fe.add(line,column, comment);
 }

 //public  void  removeFile(String file);

 //public  boolean  checkForCommentEnded

 static class CommentEntryInFiles
 {
  CommentEntryInFiles(int firstLine, int firstColumn, String comment)
   {
    firstLine_ = firstLine;
    firstColumn_ = firstColumn;
    lastLine_ = firstLine;
    lastColumn_= firstColumn;
    BreakIterator boundary=BreakIterator.getLineInstance();
    boundary.setText(comment);
    int start=boundary.first();
    int next=boundary.next();
    if (next==BreakIterator.DONE) {
     // one-line comment.
     comment_ = new Comment(comment);
     lastColumn_=firstColumn+next-start;
    }else{
     comment_ = new Comment(comment.substring(start,next));
     add(comment.substring(next));
    }
   }

  public final int getFirstLine()
  {
   return firstLine_;
  }

  public final int getFirstColumn()
  {
   return firstColumn_;
  }


  public final int getLastLine()
  {
   return firstLine_;
  }


  public final int getLastColumn()
  {
   return lastColumn_;
  }


  public final void add(String str)
  {
   BreakIterator boundary = BreakIterator.getLineInstance();
   boundary.setText(str);
   int start=boundary.first();
   for(int end=boundary.next(); end!=BreakIterator.DONE;
                                start=end,end=boundary.next()) {
     comment_.addLine(str.substring(start,end));
     lastColumn_=end-start;
     ++lastLine_;
   }
   --lastLine_;
  }

  public  String getLineRelativeAt(int index)
  {
   return comment_.getLineAt(index); 
  }

  public  String getLineAbsoluteAt(int index)
  {
   return comment_.getLineAt(firstLine_+index); 
  }

  public void print(String fname, PrintStream out)
  {
   comment_.print(fname,firstLine_, out);
  }

  private int firstLine_;
  private int firstColumn_;
  private int lastLine_;
  private int lastColumn_;
  private Comment comment_;
 }

 static class CommentEntryComparator implements Comparator
 {
  public int compare(Object x, Object y)
  {
   int z=((CommentEntryInFiles)y).getFirstLine()-((CommentEntryInFiles)x).getFirstLine();
   if (z!=0) return 0; 
   else return ((CommentEntryInFiles)y).getFirstColumn()-((CommentEntryInFiles)x).getFirstColumn();
  }                                                                                      
 } 

 static class FilesEntry 
 {

  FilesEntry()
  {
   entries_ = new TreeSet(new CommentEntryComparator());
  }

  void add(int line, int column, String comments)
  {
   CommentEntryInFiles newEntry=new CommentEntryInFiles(line,column,comments);
   SortedSet less=entries_.headSet(newEntry);
   if (less.isEmpty()) {
     entries_.add(newEntry);
   }else{
     CommentEntryInFiles prev = (CommentEntryInFiles)less.last();
     if (prev.getLastLine()==line-1) {
       prev.add(comments);
     }else{
       entries_.add(newEntry);
     }
   }
  }
  
  void print(String fname, PrintStream out)
  {
    Iterator it=entries_.iterator();
    while(it.hasNext()) {
      CommentEntryInFiles ce=(CommentEntryInFiles)(it.next());
      ce.print(fname,out);
    }
  }

  private TreeSet entries_;
    
 }

 public void print(PrintStream out)
 {
   Iterator esi=files_.entrySet().iterator();
   while(esi.hasNext()) {
     Map.Entry e = (Map.Entry)esi.next();
     ((FilesEntry)e.getValue()).print((String)e.getKey(),out);
   }
 }


 private HashMap files_;
}
