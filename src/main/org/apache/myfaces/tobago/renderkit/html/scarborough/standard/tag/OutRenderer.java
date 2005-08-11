/*
 * Copyright (c) 2003 Atanion GmbH, Germany
 * All rights reserved. Created 07.02.2003 16:00:00.
 * $Id$
 */
package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.StringTokenizer;

public class OutRenderer extends RendererBase {

// ------------------------------------------------------------------ constants

// ----------------------------------------------------------------- interfaces


// ---------------------------- interface TobagoRenderer

  public void encodeEndTobago(FacesContext facesContext,
      UIComponent component) throws IOException {
    String text = ComponentUtil.currentValue(component);
    if (text == null) {
      text = "";
    }

    TobagoResponseWriter writer = (TobagoResponseWriter) facesContext.getResponseWriter();

    boolean escape
        = ComponentUtil.getBooleanAttribute(component, ATTR_ESCAPE);
    boolean createSpan = ComponentUtil.getBooleanAttribute(
        component, ATTR_CREATE_SPAN);

    if (createSpan) {
      writer.startElement("span", component);
      writer.writeAttribute("style", null, ATTR_STYLE);
      writer.writeComponentClass();
      writer.writeAttribute("title", null, ATTR_TIP);
    }
    if (escape) {
      StringTokenizer tokenizer = new StringTokenizer(text, "\n\r");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        writer.writeText(token, null);
        if (tokenizer.hasMoreTokens()) {
          writer.write("<br>");
        }
      }
    } else {
      writer.write(text);
    }
    if (createSpan) {
      writer.endElement("span");
    }
  }
}
