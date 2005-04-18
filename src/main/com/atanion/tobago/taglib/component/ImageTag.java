/*
 * Created by IntelliJ IDEA.
 * User: weber
 * Date: Jun 3, 2002
 * Time: 3:10:17 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.atanion.tobago.taglib.component;

import com.atanion.tobago.component.ComponentUtil;
import com.atanion.tobago.taglib.decl.HasAlt;
import com.atanion.tobago.taglib.decl.HasBinding;
import com.atanion.tobago.taglib.decl.HasBorder;
import com.atanion.tobago.taglib.decl.HasDimension;
import com.atanion.tobago.taglib.decl.HasId;
import com.atanion.tobago.taglib.decl.HasTip;
import com.atanion.tobago.taglib.decl.HasValue;
import com.atanion.tobago.taglib.decl.IsRendered;
import com.atanion.util.annotation.Tag;

import javax.faces.component.UIComponent;
import javax.faces.component.UIGraphic;


@Tag(name="image")
public class ImageTag extends TobagoTag
    implements HasId, HasValue, HasAlt, HasBorder, HasDimension, HasTip,
               IsRendered, HasBinding
    {

  private String value;
  private String alt;
  private String border;
  private String tip;

  public String getComponentType() {
    return UIGraphic.COMPONENT_TYPE;
  }

  protected void setProperties(UIComponent component) {
    super.setProperties(component);
   ComponentUtil.setStringProperty(component, ATTR_ALT, alt, getIterationHelper());
   ComponentUtil.setStringProperty(component, ATTR_BORDER, border, getIterationHelper());
   ComponentUtil.setStringProperty(component, ATTR_VALUE, value, getIterationHelper());
   ComponentUtil.setStringProperty(component, ATTR_TIP, tip, getIterationHelper());
  }

  public void release() {
    super.release();
    this.alt = null;
    this.border = null;
    this.value = null;
    this.tip = null;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getAlt() {
    return alt;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }

  public String getBorder() {
    return border;
  }

  public void setBorder(String border) {
    this.border = border;
  }

  public String getTip() {
    return tip;
  }

  public void setTip(String tip) {
    this.tip = tip;
  }
}
