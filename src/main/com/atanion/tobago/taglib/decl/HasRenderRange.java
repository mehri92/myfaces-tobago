package com.atanion.tobago.taglib.decl;

import com.atanion.util.annotation.TagAttribute;
import com.atanion.util.annotation.UIComponentTagAttribute;

/**
 * $Id$
 */
public interface HasRenderRange {
  /**
   *  Range of items to render.
   */
  @TagAttribute @UIComponentTagAttribute(type=String.class)
  public void setRenderRange(String range);
}
