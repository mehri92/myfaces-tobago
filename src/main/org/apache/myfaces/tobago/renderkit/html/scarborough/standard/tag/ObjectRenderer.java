package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.context.ResourceManagerUtil;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: weber
 * Date: Jun 20, 2005
 * Time: 5:02:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectRenderer extends RendererBase {
  public void encodeEndTobago(FacesContext facesContext, UIComponent component)
      throws IOException {
    TobagoResponseWriter writer = (TobagoResponseWriter) facesContext.getResponseWriter();
    writer.startElement("iframe", component);
    writer.writeAttribute("src", null, ATTR_TARGET);
    writer.writeComponentClass();
    writer.writeAttribute("style", null, ATTR_STYLE);

    String noframes = ResourceManagerUtil.getProperty(
        facesContext, "tobago", "browser.noframe.message.prefix");
    writer.writeText(noframes + " ", null);
    writer.startElement("a", component);
    writer.writeAttribute("href", null, ATTR_TARGET);
    writer.writeText(null, ATTR_TARGET);
    writer.endElement("a");
    noframes = ResourceManagerUtil.getProperty(
        facesContext, "tobago", "browser.noframe.message.postfix");
    writer.writeText(" " + noframes, null);

    writer.endElement("iframe");
  }
}
