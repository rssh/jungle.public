package ua.gradsoft.jungle.configuration;

import java.math.BigDecimal;

public class ConfigItemSelectorById extends ConfigItemSelector
{
  public ConfigItemSelectorById(BigDecimal id)
  {
    id_=id;
  }

  public BigDecimal getId()
  { return id_; }

  public void setId(BigDecimal id)
  { id_=id; }

  private BigDecimal id_;
}
