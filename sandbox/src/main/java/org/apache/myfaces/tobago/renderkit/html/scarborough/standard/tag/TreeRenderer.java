package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Created 07.02.2003 16:00:00.
 * $Id$
 */

import static org.apache.myfaces.tobago.TobagoConstants.ATTR_STYLE;
import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.component.UITree;
import org.apache.myfaces.tobago.component.UITreeNode;
import org.apache.myfaces.tobago.config.TobagoConfig;
import org.apache.myfaces.tobago.context.ResourceManagerUtil;
import org.apache.myfaces.tobago.model.TreeState;
import org.apache.myfaces.tobago.renderkit.RenderUtil;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlConstants;
import org.apache.myfaces.tobago.renderkit.html.HtmlRendererUtil;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;

public class TreeRenderer extends RendererBase {

  /**
   * Resources to display the tree.
   */
  private static final String[] TREE_IMAGES = {
      "openfoldericon.gif",
      "foldericon.gif",
      "unchecked.gif",
      "uncheckedDisabled.gif",
      "checked.gif",
      "checkedDisabled.gif",
      "new.gif",
      "T.gif",
      "L.gif",
      "I.gif",
      "Lminus.gif",
      "Tminus.gif",
      "Rminus.gif",
      "Lplus.gif",
      "Tplus.gif",
      "Rplus.gif",
      "treeMenuOpen.gif",
      "treeMenuClose.gif",
  };


  @Override
  public void decode(FacesContext facesContext, UIComponent component) {
    if (ComponentUtil.isOutputOnly(component)) {
      return;
    }

    UITree tree = (UITree) component;
    TreeState state = tree.getState();

    if (state != null) {
      if ("Tree".equals(tree.getRendererType())) {
        state.clearExpandState();
      }
      if (isSelectable(tree)) {
        state.clearSelection();
      }
    }
    tree.setValid(true);
  }

  public static boolean isSelectable(UITree tree) {
    return tree.isSelectableTree();
  }

  public static String createJavascriptVariable(String clientId) {
    return clientId == null
        ? null
        : clientId.replace(NamingContainer.SEPARATOR_CHAR, '_');
  }

  @Override
  public void encodeEnd(FacesContext facesContext,
      UIComponent component) throws IOException {

    UITree tree = (UITree) component;

    String clientId = tree.getClientId(facesContext);
    UITreeNode root = tree.getRoot();

    TobagoResponseWriter writer = (TobagoResponseWriter) facesContext.getResponseWriter();

    writer.startElement(HtmlConstants.DIV, tree);
    writer.writeComponentClass();
    writer.writeAttribute(HtmlAttributes.STYLE, null, ATTR_STYLE);

    writer.startElement(HtmlConstants.INPUT, tree);
    writer.writeAttribute(HtmlAttributes.TYPE, "hidden", null);
    writer.writeNameAttribute(clientId);
    writer.writeIdAttribute(clientId);
    writer.writeAttribute(HtmlAttributes.VALUE, ";", null);
    writer.endElement(HtmlConstants.INPUT);

    writer.startElement(HtmlConstants.INPUT, tree);
    writer.writeAttribute(HtmlAttributes.TYPE, "hidden", null);
    writer.writeNameAttribute(clientId + UITree.MARKER);
    writer.writeIdAttribute(clientId + UITree.MARKER);
    writer.writeAttribute(HtmlAttributes.VALUE, "", null);
    writer.endElement(HtmlConstants.INPUT);

    if (isSelectable(tree)) {
      writer.startElement(HtmlConstants.INPUT, tree);
      writer.writeAttribute(HtmlAttributes.TYPE, "hidden", null);
      writer.writeNameAttribute(clientId + UITree.SELECT_STATE);
      writer.writeIdAttribute(clientId + UITree.SELECT_STATE);
      writer.writeAttribute(HtmlAttributes.VALUE, ";", null);
      writer.endElement(HtmlConstants.INPUT);
    }

//    writer.startElement(HtmlConstants.DIV, null);
    writer.startElement(HtmlConstants.TABLE, tree);
    writer.writeAttribute(HtmlAttributes.CELLPADDING, "0", null);
    writer.writeAttribute(HtmlAttributes.CELLSPACING, "0", null);
    writer.writeAttribute(HtmlAttributes.BORDER, "0", null);
    writer.writeAttribute(HtmlAttributes.SUMMARY, "", null);
    writer.writeComponentClass();
    writer.startElement(HtmlConstants.TR, null);
    writer.startElement(HtmlConstants.TD, null);
    writer.writeIdAttribute(clientId + "-cont");
    writer.writeComment("placeholder for treecontent");
    writer.endElement(HtmlConstants.TD);
    writer.endElement(HtmlConstants.TR);
    writer.endElement(HtmlConstants.TABLE);
//    writer.endElement(HtmlConstants.DIV);

    String[] scriptTexts = createJavascript(facesContext, clientId, root);

    String[] scripts = {"script/tobago-tree.js"};
    List<String> scriptFiles = ComponentUtil.findPage(tree).getScriptFiles();
    for (String script : scripts) {
      scriptFiles.add(script);
    }

    if (!TobagoConfig.getInstance(facesContext).isAjaxEnabled()) {
      HtmlRendererUtil.startJavascript(writer);
      for (String scriptText : scriptTexts) {
        writer.writeText(scriptText, null);
        writer.writeText('\n', null);
      }
      HtmlRendererUtil.endJavascript(writer);
    } else {
      HtmlRendererUtil.writeScriptLoader(facesContext, scripts, scriptTexts);
    }

    RenderUtil.encode(facesContext, root);

    writer.endElement(HtmlConstants.DIV);
  }

  private String[] createJavascript(FacesContext facesContext, String clientId,
                                  UITreeNode root)
  throws IOException {
    StringBuilder sb = new StringBuilder();

    sb.append("{\n");

    sb.append("  var treeResourcesHelp = new Object();\n");
    for (String images : TREE_IMAGES) {
      sb.append("  treeResourcesHelp[\"");
      sb.append(images);
      sb.append("\"] = \"");
      sb.append(ResourceManagerUtil.getImageWithPath(facesContext,
          "image/" + images));
      sb.append("\";\n");
    }
    sb.append(" \n  treeResourcesHelp.getImage = function (name) {\n");
    sb.append("    var result = this[name];\n");
    sb.append("    if (result) {\n");
    sb.append("      return result;\n");
    sb.append("    } else {\n");
    sb.append("      return \"");
    sb.append(ResourceManagerUtil.getImageWithPath(facesContext, "image/blank.gif"));
    sb.append("\";\n");
    sb.append("    }\n");
    sb.append("  };\n \n");

    sb.append("/* disabled!!! \n");

//    sb.append(getNodesAsJavascript(facesContext, root));

    sb.append("  var treeDiv = document.getElementById('");
    sb.append(clientId);
    sb.append("-cont');\n");
    sb.append("  treeDiv.innerHTML = ");
    String rootNode = createJavascriptVariable(root.getClientId(facesContext));
    sb.append(rootNode);
    sb.append(".toString(0, true);\n  ");

    sb.append(rootNode);
    sb.append(".initSelection();\n");

    sb.append("disabled!!! */");

    sb.append("}");
//    return sb.toString();
    StringTokenizer tokenizer = new StringTokenizer(sb.toString(), "\n");
    String[] strings = new String[tokenizer.countTokens()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = tokenizer.nextToken();
    }
    return strings;
  }

  protected String getNodesAsJavascript(FacesContext facesContext, UITreeNode root) throws IOException {
    TobagoResponseWriter writer = (TobagoResponseWriter) facesContext.getResponseWriter();
    StringWriter stringWriter = new StringWriter();
    facesContext.setResponseWriter(writer.cloneWithWriter(stringWriter));
    RenderUtil.encode(facesContext, root);
    facesContext.setResponseWriter(writer);
    return stringWriter.toString();
  }

  protected String nodeStateId(FacesContext facesContext, UITreeNode node) {
    // this must do the same as nodeStateId() in tree.js
    String clientId = node.getClientId(facesContext);
    int last = clientId.lastIndexOf(':') + 1;
    return clientId.substring(last);
  }
}
