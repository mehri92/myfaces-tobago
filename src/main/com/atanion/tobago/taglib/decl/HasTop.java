package com.atanion.tobago.taglib.decl;

import com.atanion.util.annotation.TagAttribute;
import com.atanion.util.annotation.UIComponentTagAttribute;

import java.util.List;
import java.util.Map;

/**
 * $Id$
 */
public interface HasTop {
  /**
   * 
   */
  @TagAttribute @UIComponentTagAttribute(type=Integer.class)
  public void setTop(String top);
}
