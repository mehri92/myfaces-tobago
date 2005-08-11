package org.apache.myfaces.tobago.renderkit.fo.scarborough.standard.tag;

import org.apache.myfaces.tobago.renderkit.RenderUtil;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.component.BodyContentHandler;
import org.apache.myfaces.tobago.component.UIPanel;
import org.apache.myfaces.tobago.TobagoConstants;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (c) 2003 Atanion GmbH, Germany. All rights reserved.
 * Created: Nov 30, 2004 6:04:53 PM
 * User: bommel
 * $Id$
 */
public class PanelRenderer extends FoRendererBase {

  public boolean getRendersChildren() {
    return true;
  }
  public void encodeBegin(FacesContext facesContext,
       UIComponent uiComponent) throws IOException {
    Layout.putLayout(uiComponent, Layout.getLayout(uiComponent.getParent()));
    super.encodeBegin(facesContext, uiComponent);
  }

  public void encodeEndTobago(FacesContext facesContext,
       UIComponent uiComponent) throws IOException {

    UIPanel component = (UIPanel) uiComponent ;
    BodyContentHandler bodyContentHandler = (BodyContentHandler)
        component.getAttributes().get(TobagoConstants.ATTR_BODY_CONTENT);

    if (bodyContentHandler != null) {
      ResponseWriter writer = facesContext.getResponseWriter();
      writer.write(bodyContentHandler.getBodyContent());
    }
  }
}
