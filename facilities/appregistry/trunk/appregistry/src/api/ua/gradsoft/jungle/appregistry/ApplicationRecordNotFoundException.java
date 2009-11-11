package ua.gradsoft.jungle.appregistry;

public class ApplicationRecordNotFoundException extends Exception
{
 public ApplicationRecordNotFoundException(String name)
 {
   super("application record not found for ("+name+")");
 }
}
