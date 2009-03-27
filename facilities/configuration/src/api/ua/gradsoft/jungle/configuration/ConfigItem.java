package ua.gradsoft.jungle.configuration;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity for configuration item with name, value and description.
 * @author rssh
 */
@Entity
@Table(name="jungle_configitems",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"name", "appname"})
       }
)
public class ConfigItem implements Serializable
{

 public ConfigItem()   
 {}

 @Id
 @Column(name="id", precision=ID_PRECISION, scale=ID_SCALE)
 public BigDecimal getId()
 { return id_; }
 
 public void setId(BigDecimal id)
 { id_=id; }

 @Column(name="appname", length=APPNAME_LENGTH)
 public String getAppName()
  { return appName_; }

 public void   setAppName(String appName)
  { appName_ = appName; }

 @Column(name="name", length=NAME_LENGTH)
 public String getName()
  { return name_; }

 public void  setName(String name)
  { name_=name; }

 @Enumerated(EnumType.ORDINAL)
 @Column(name="typecode")
 public ConfigItemType getType()
  { return type_; }


 public void setType(ConfigItemType type)
  { type_=type; }

 @Column(name="description", length=DESCRIPTION_LENGTH, nullable=false)
 public String getDescription()
 { return description_; }
 
 public void setDescription(String description)
 {
   description_=description;    
 }

 @Column(name="max_len")
 public int getMaxLen()
 {
   return maxLen_;
 }

 public void setMaxLen(int maxLen)
 {
    if (maxLen > MAX_MAXLEN) {
        throw new IllegalArgumentException("maxLen candidate value too big");
    }
    maxLen_=maxLen;
 }

 @Column(name="regexpr", length=REGEXPR_LENGTH)
 public String getRegexpr()
 { return regexpr_; }

 public void setRegexpr(String regexpr)
 {
   regexpr_=regexpr;
 }


 @Column
 public boolean getEditable()
 {
    return isEditable_;
 }

 
 public void setEditable(boolean isEditable)
 {
   isEditable_=isEditable;
 }

 @Column(name="value")
 public String getValue()
 { return value_; }
 
 public void setValue(String value)
 { value_=value; }

 public static final int APPNAME_LENGTH=64;
 public static final int NAME_LENGTH=64;
 public static final int DESCRIPTION_LENGTH=255;
 public static final int VALUE_LENGTH=255;
 public static final int REGEXPR_LENGTH=255;

 public static final int MAX_MAXLEN = VALUE_LENGTH;

 public static final int ID_PRECISION = 40;
 public static final int ID_SCALE = 0;

 private BigDecimal id_;
 private String name_; 
 private ConfigItemType type_;
 private String description_;
 private String appName_;
 private int    maxLen_;
 private String regexpr_;
 private boolean isEditable_;
 private String value_;
}
