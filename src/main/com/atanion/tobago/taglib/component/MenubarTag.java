package com.atanion.tobago.taglib.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;

/*
  * Copyright (c) 2002 Atanion GmbH, Germany
  * All rights reserved. Created 29.07.2003 at 15:09:53.
  * $Id$
  */
public class MenubarTag extends Panel_GroupTag {

// ///////////////////////////////////////////// constant

  private static final Log LOG = LogFactory.getLog(MenubarTag.class);

// ///////////////////////////////////////////// attribute


// ///////////////////////////////////////////// constructor

// ///////////////////////////////////////////// code

  public String getRendererType() {
    return "Menubar";
  }

  protected void setProperties(UIComponent component) {
    super.setProperties(component);
  }

// ///////////////////////////////////////////// bean getter + setter

}
