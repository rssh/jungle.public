package ua.gradsoft.t6;


public class MyException extends RuntimeException
{

  public MyException(String message, String additionalInfo)
  { super(message);
    this.additionalInfo = additionalInfo;
  }

  public String getAdditionalInfo()
  { return additionalInfo; }

  public void setAdditionalInfo(String additionalInfo)
  { this.additionalInfo = additionalInfo; }
 

  private String additionalInfo;

}
