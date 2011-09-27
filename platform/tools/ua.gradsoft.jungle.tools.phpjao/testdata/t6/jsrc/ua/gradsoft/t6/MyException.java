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
 
  public String getInfo2()
  { return info2; }

  public void  setInfo2(String info2)
  { this.info2 = info2; }

  private String additionalInfo;

  private String info2;

}
