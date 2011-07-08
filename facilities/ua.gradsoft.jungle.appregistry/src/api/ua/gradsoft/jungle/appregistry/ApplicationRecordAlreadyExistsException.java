package ua.gradsoft.jungle.appregistry;

public class ApplicationRecordAlreadyExistsException extends Exception
{
 public ApplicationRecordAlreadyExistsException(String name)
 {
   super("application already registered ("+name+")");
 }
}
