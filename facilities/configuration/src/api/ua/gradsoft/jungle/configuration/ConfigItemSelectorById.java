package ua.gradsoft.jungle.configuration;

public class ConfigItemSelectorById implements ConfigItemSelector
{
  public ConfigItemSelectorById(BigDecimal id)
  {
    id_=id;
  }

  public BigDecimal getId()
  { return id_; }

  public void setId()
  { id_=id; }

  private BigDecimal id_;
}
