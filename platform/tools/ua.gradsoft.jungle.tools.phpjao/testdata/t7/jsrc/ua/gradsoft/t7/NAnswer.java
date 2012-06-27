package ua.gradsoft.t7;

import java.math.BigDecimal;

public class NAnswer
{

  private static final long serialVersionUID = 1L;

  public BigDecimal getId() {
      return id;
  }

  public void setId(BigDecimal id) {
      this.id = id;
  }


  public String getAnswerText() {
                return answerText;
  }

  public void setAnswerText(String answerText) {
        this.answerText = answerText;
  }

  public boolean isPublished() {
         return published;
  }

  public boolean getPublished(){
         return published;
  }

  public void setPublished(boolean published) {
         this.published = published;
  }

  public NAnswer withPublished(boolean published) {
         setPublished(published);
         return this;
  }


  private BigDecimal id;
  private String answerText;
  private boolean published;

}
