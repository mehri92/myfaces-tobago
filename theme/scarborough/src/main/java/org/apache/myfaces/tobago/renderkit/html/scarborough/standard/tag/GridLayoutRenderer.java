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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.tobago.component.AbstractUIForm;
import org.apache.myfaces.tobago.component.AbstractUIGridLayout;
import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.component.UICell;
import org.apache.myfaces.tobago.component.UIGridLayout;
import org.apache.myfaces.tobago.component.UILayout;
import org.apache.myfaces.tobago.layout.FixedLayoutToken;
import org.apache.myfaces.tobago.layout.HideLayoutToken;
import org.apache.myfaces.tobago.layout.LayoutToken;
import org.apache.myfaces.tobago.layout.LayoutTokens;
import org.apache.myfaces.tobago.layout.MinimumLayoutToken;
import org.apache.myfaces.tobago.layout.PixelLayoutToken;
import org.apache.myfaces.tobago.layout.RelativeLayoutToken;
import org.apache.myfaces.tobago.renderkit.LayoutInformationProvider;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlConstants;
import org.apache.myfaces.tobago.renderkit.html.HtmlStyleMap;
import org.apache.myfaces.tobago.renderkit.html.StyleClasses;
import org.apache.myfaces.tobago.renderkit.html.util.HtmlRendererUtil;
import org.apache.myfaces.tobago.renderkit.util.RenderUtil;
import org.apache.myfaces.tobago.util.ComponentUtil;
import org.apache.myfaces.tobago.util.LayoutInfo;
import org.apache.myfaces.tobago.util.LayoutUtils;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GridLayoutRenderer extends DefaultLayoutRenderer {

  private static final Log LOG = LogFactory.getLog(GridLayoutRenderer.class);

  public int getFixedHeight(FacesContext facesContext, UIComponent component) {
    int height = calculateLayoutHeight(facesContext, component, false);

    LayoutInformationProvider containerRenderer =
        ComponentUtil.getRenderer(facesContext, component);
    height += containerRenderer.getHeaderHeight(facesContext, component);
    height += containerRenderer.getPaddingHeight(facesContext, component);
    return height;
  }

  public int getFixedWidth(FacesContext facesContext, UIComponent component) {
    int width = calculateLayoutWidth(facesContext, component, false);

    LayoutInformationProvider containerRenderer =
         ComponentUtil.getRenderer(facesContext, component);
    width += containerRenderer.getPaddingWidth(facesContext, component);
    return width;
  }

  private int calculateLayoutHeight(
      FacesContext facesContext, UIComponent component, boolean minimum) {
    UIGridLayout layout = (UIGridLayout) UILayout.getLayout(component);
    final List<AbstractUIGridLayout.Row> rows = layout.ensureRows();

    LayoutTokens layoutTokens = layout.getRowLayout();

    if (layoutTokens == null && !minimum && LOG.isDebugEnabled()) {
      LOG.debug("No rowLayout found using " + (minimum ? "'minimum'" : "'fixed'")
          + " for all " + rows.size() + " rows."
          + " (clientId='" + layout.getClientId(facesContext) + "')");
    }
    if (rows.size() != layoutTokens.getSize()) {
      LOG.warn("Unbalanced layout: rows.size()=" + rows.size()
          + " != layoutTokens.length=" + layoutTokens.getSize()
          + " rowLayout='" + layoutTokens + "'"
          + " (clientId='" + layout.getClientId(facesContext) + "')");
      layoutTokens.ensureSize(rows.size(), new RelativeLayoutToken(1));
    }
    // TODO alternative? rows.size() == 1 ? new RelativeLayoutToken(1) : new FixedLayoutToken()
    //new FixedLayoutToken() );
    //String[] layoutTokens
    //    = LayoutInfo.createLayoutTokens(rowLayout, rows.size(),
    //        minimum ? "minimum" : "fixed");


    int size = Math.min(rows.size(), layoutTokens.getSize());

    int height = 0;
    height += getMarginAsInt(layout.getMarginTop());
    height += getMarginAsInt(layout.getMarginBottom());
    boolean first = true;
    for (int i = 0; i < size; i++) {
      if (!rowIsRendered(rows.get(i))) {
        continue;
      }
      height += getCellPadding(facesContext, layout,  first);
      first = false;
      LayoutToken token = layoutTokens.get(i);
      if (token instanceof PixelLayoutToken) {
        height += ((PixelLayoutToken) token).getPixel();
      } else if (token instanceof FixedLayoutToken) {
        height += getMaxHeight(facesContext, rows.get(i), false);
      } else if (token instanceof MinimumLayoutToken) {
        height += getMaxHeight(facesContext, rows.get(i), true);
      } else {
        if (!minimum && LOG.isWarnEnabled()) {
          if (layout.getRows() != null) {
            // TODO: this is only an error if the token was explicitly set by application
            LOG.warn("Unable to calculate Height for token '" + token
                + "'! using " + (minimum ? "'minimum'" : "'fixed'") + " , component-id="
                + component.getClientId(facesContext) + " is "
                + component.getRendererType());
          }
        }
        height += getMaxHeight(facesContext, rows.get(i), minimum);
      }
    }

    return height;
  }


  public int calculateLayoutWidth(
      FacesContext facesContext, UIComponent component, boolean minimum) {
    UIGridLayout layout = (UIGridLayout) UILayout.getLayout(component);
    final List<AbstractUIGridLayout.Row> rows = layout.ensureRows();
    AbstractUIGridLayout.Row row = rows.get(0);


    LayoutTokens layoutTokens = layout.getColumnLayout();

    if (layoutTokens == null && !minimum && LOG.isDebugEnabled()) {
      LOG.debug("No rowLayout found using " + (minimum ? "'minimum'" : "'fixed'")
          + " for all " + rows.size() + " rows of "
          + layout.getClientId(facesContext) + " !");
    }

    if (row.getColumns() != layoutTokens.getSize()) {
      LOG.warn("Unbalanced layout: rows.size()=" + rows.size()
          + " != layoutTokens.length=" + layoutTokens.getSize()
          + " columnLayout='" + layoutTokens + "'"
          + " (clientId='" + layout.getClientId(facesContext) + "')");
      layoutTokens.ensureSize(row.getColumns(), new FixedLayoutToken());
    }

    int size = Math.min(rows.size(), layoutTokens.getSize());

    int width = 0;
    width += getMarginAsInt(layout.getMarginLeft());
    width += getMarginAsInt(layout.getMarginRight());
    boolean first = true;
    for (int i = 0; i < size; i++) {
      if (!columnIsRendered(rows,  i)) {
        continue;
      }
      width += getCellPadding(facesContext, layout,  first);
      first = false;
      LayoutToken token = layoutTokens.get(i);
      if (token instanceof PixelLayoutToken) {
        width += ((PixelLayoutToken) token).getPixel();
      } else if (token instanceof FixedLayoutToken) {
        width += getMaxWidth(facesContext, rows, i, false);
      } else if (token instanceof MinimumLayoutToken) {
        width += getMaxWidth(facesContext, rows, i, true);
      } else {
        if (!minimum && LOG.isWarnEnabled()) {
          LOG.warn("Unable to calculate Width for token '" + token
              + "'! using " + (minimum ? "'minimum'" : "'fixed'") + " , component-id="
              + component.getClientId(facesContext) + " is "
              + component.getRendererType());
        }
        width += getMaxWidth(facesContext, rows,  i, minimum);
      }
    }

    return width;
  }

  private boolean columnIsRendered(List<UIGridLayout.Row> rows, int column) {
    for (AbstractUIGridLayout.Row row : rows) {
      Object object = row.getElements().get(column);
      if (object instanceof UIComponent) {
        if (object instanceof UICell) {
          UICell cell = (UICell) object;
          if (cell.getSpanX() > 1) {
            return false;
          }
        }
        UIComponent component = (UIComponent) object;
        if (component.isRendered()) {
          return true;
        }
        // XXX ????
      } else if (UIGridLayout.USED.equals(object)) {
        return true;
      }
    }
    return false;
  }

  private boolean rowIsRendered(UIGridLayout.Row row) {
    for (Object element : row.getElements()) {
      if (element instanceof UIComponent) {
        UIComponent component = (UIComponent) element;
        if (component.isRendered()) {
          return true;
        }
      }
    }
    return false;
  }

  public void prepareRender(FacesContext facesContext, UIComponent component) throws IOException {
    super.prepareRender(facesContext, component);    
    layoutEnd(facesContext, component);
    layoutMargins((UIGridLayout) component);

  }

  void prepareDimension(FacesContext facesContext, UIComponent component) {

  }

  public void encodeChildrenOfComponent(FacesContext facesContext, UIComponent component)
      throws IOException {
    // encode table with component's children

    UIGridLayout layout =  (UIGridLayout) UILayout.getLayout(component);
    //prepareRender(facesContext, layout);
    //HtmlRendererUtil.prepareRender(facesContext, layout);

    //layoutEnd(facesContext, layout);
    //layoutMargins(layout);

    final Map attributes = layout.getAttributes();
    List columnWidths =  (List) attributes.get(Attributes.WIDTH_LIST);


    TobagoResponseWriter writer = HtmlRendererUtil.getTobagoResponseWriter(facesContext);
    writer.startElement(HtmlConstants.TABLE, layout);
    writer.writeAttributeFromComponent(HtmlAttributes.BORDER, Attributes.BORDER);
    writer.writeClassAttribute();
    writer.writeStyleAttribute();
    writer.writeAttribute(HtmlAttributes.CELLSPACING, 0);
    writer.writeAttribute(HtmlAttributes.CELLPADDING, 0);
    writer.writeAttribute(HtmlAttributes.SUMMARY, "", false);

    boolean first = true;
    if (columnWidths != null) {
      writer.startElement(HtmlConstants.COLGROUP, null);
      for (int i = 0; i < columnWidths.size(); i++) {
        int cellWidth = ((Integer) columnWidths.get(i)).intValue();
        if (cellWidth != LayoutInfo.HIDE) {
          cellWidth += getCellPadding(facesContext, layout, first);
          first = false;
          writer.startElement(HtmlConstants.COL, null);
          writer.writeAttribute(HtmlAttributes.WIDTH, cellWidth);
          writer.endElement(HtmlConstants.COL);
        }
      }
      writer.endElement(HtmlConstants.COLGROUP);
    }


    List<AbstractUIGridLayout.Row> rows = layout.ensureRows();
    boolean firstRenderedRow = true;
    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
      AbstractUIGridLayout.Row row = rows.get(rowIndex);
      if (!row.isHidden()) {
        writer.startElement(HtmlConstants.TR, null);

        List cells = row.getElements();
        boolean firstRenderedColum = true;
        for (int columnIndex = 0; columnIndex < cells.size(); columnIndex++) {
          boolean hide = false;

          if (columnWidths != null) {
            Integer columWidth = ((Integer) columnWidths.get(columnIndex));
            hide = columWidth.intValue() == LayoutInfo.HIDE;
          }
          if (!hide) {

            Object object = cells.get(columnIndex);
            if (object.toString().equals(UIGridLayout.USED)) {
              firstRenderedColum = false;
              continue; // ignore the markers UIGridLayout.Used
            } else if (object.equals(UIGridLayout.FREE)) {
              if (LOG.isWarnEnabled() && !layout.isIgnoreFree()) {
                LOG.warn("There are free blocks in the layout: id='"
                    + layout.getClientId(facesContext)
                    + "'");
              }
              firstRenderedColum = false;
              continue;
            }
            UIComponent cell = (UIComponent) object;


            int spanX = UIGridLayout.getSpanX(cell);
            int spanY = UIGridLayout.getSpanY(cell);
            StyleClasses classes = StyleClasses.ensureStyleClassesCopy(layout);
            if (firstRenderedRow) {
              classes.addClass("gridLayout", "first-row"); // XXX not a standard compliant name
            }
            if (firstRenderedColum) {
              classes.addClass("gridLayout", "first-column"); // XXX not a standard compliant name
            }

            int cellWidth = -1;
            if (columnWidths != null) {
              cellWidth = 0;
              for (int i = columnIndex;
                   i < columnIndex + spanX && i < columnWidths.size(); i++) {
                cellWidth += ((Integer) columnWidths.get(i)).intValue()
                    + getCellPadding(facesContext, layout, firstRenderedColum);
                if (firstRenderedColum) {
                  firstRenderedColum = false;
                }
              }
            }


            int cellHeight = -1;
            try {
              Integer layoutHeight = LayoutUtils.getLayoutHeight(cell);
              if (layoutHeight != null) {
                cellHeight = layoutHeight.intValue();
              }
            } catch (Exception e) {
              // ignore
            } // ignore, use 0

            int topPadding = getCellPadding(facesContext, layout, firstRenderedRow);
            //("Cellheight " + cellHeight + " topPadding "+ topPadding + " firstRendered "
            //    + firstRenderedRow + " RowIndex " + rowIndex + " Column " + columnIndex);
            String cellStyle =
                (cellWidth != -1 ? "width: " + cellWidth + "px;" : "")
                + (cellHeight != -1 ? " height: " + (cellHeight + topPadding) + "px;" : "");
            cellStyle += getOverflow(cell);


            writer.startElement(HtmlConstants.TD, null);
            writer.writeClassAttribute("tobago-gridLayout-cell-td");
            writer.writeAttribute(HtmlAttributes.STYLE, cellStyle, false);
            if (spanX > 1) {
              writer.writeAttribute(HtmlAttributes.COLSPAN, spanX);
            }
            if (spanY > 1) {
              writer.writeAttribute(HtmlAttributes.ROWSPAN, spanY);
            }

            writer.flush();

            if (ComponentUtil.getAttribute(layout, Attributes.CELLSPACING) != null) {
              cellStyle += " padding: " + getCellSpacing(facesContext, layout) + "px;";
            }

            writer.startElement(HtmlConstants.DIV, null);
            writer.writeClassAttribute(classes);
            writer.writeAttribute(HtmlAttributes.STYLE, cellStyle, false);
            writer.flush();
            RenderUtil.encode(facesContext, cell);

            writer.endElement(HtmlConstants.DIV);
            writer.endElement(HtmlConstants.TD);

            firstRenderedColum = false;
            HtmlRendererUtil.removeStyleClasses(cell);
          }
        }

        writer.endElement(HtmlConstants.TR);
        firstRenderedRow = false;
      }
    }
    writer.endElement(HtmlConstants.TABLE);
  }

  @Override
  public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
    TobagoResponseWriter writer = HtmlRendererUtil.getTobagoResponseWriter(facesContext);
    UIGridLayout gridLayout = (UIGridLayout) component;
    writer.startElement(HtmlConstants.DIV, gridLayout);
    writer.writeClassAttribute();
//    writer.writeStyleAttribute();
  }

  @Override
  public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
    UIComponent container = component.getParent();
    RenderUtil.encodeChildrenWithoutLayout(facesContext, container);
  }

  @Override
  public void encodeEnd(FacesContext facesContext,
      UIComponent component) throws IOException {
    TobagoResponseWriter writer = HtmlRendererUtil.getTobagoResponseWriter(facesContext);
//    UIGridLayout gridLayout = (UIGridLayout)component;
    writer.endElement(HtmlConstants.DIV);

//    if (component.getParent() instanceof UIPage) {
//      LOG.error("XXXXXXXXXXXXXXXXXXXXXXX  never XXXXXXXXXXXXXXXXXXXXXX", new Exception());
//    } else {
//      encodeChildrenOfComponent(facesContext, component.getParent());
//    }
  }

  private String getOverflow(UIComponent cell) {
    String overflow = "";

    String scrollbars = (String) cell.getAttributes().get(Attributes.SCROLLBARS);
    if (scrollbars != null) {
      if (scrollbars.equals("false")) {
        overflow = " overflow: hidden;";
      } else if (scrollbars.equals("true")) {
        overflow = " overflow: scroll;";
      } else if (scrollbars.equals("auto")) {
        overflow = " overflow: auto;";
      } else {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Illegal value for attribute 'scrollbars' : " + scrollbars);
        }
      }
    }

    return overflow;
  }

  private int getCellPadding(
      FacesContext facesContext, UIComponent component, boolean first) {
    return first ? 0 : getCellSpacing(facesContext, component);
  }

  private int getBorder(UIComponent component) {
    int border = 0;
    String borderWidth
        = (String) component.getAttributes().get(Attributes.BORDER);
    try {
      if (borderWidth != null) {
        border = Integer.parseInt(borderWidth);
      }
    } catch (NumberFormatException e) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("Can't parse border, using 0!");
      }
    }

    return border;
  }

  private int getSpacingSum(UIComponent component, FacesContext facesContext,
      int count) {
    int space = 0;
    int border = getBorder(component);
    int cellSpacing = getCellSpacing(facesContext, component);
    if (border != 0) {
      space = (border * 2) + count * 2;
    }
    space += (count - 1) * cellSpacing;
    if (LOG.isDebugEnabled()) {
      LOG.debug("extra count =" + count + ",  space = " + space);
    }
    return space;
  }

  private int getWidthSpacingSum(UIGridLayout component,
      FacesContext facesContext, int renderedColumnCount) {
    int spacingSum = getSpacingSum(component, facesContext, renderedColumnCount);
    spacingSum += getComponentExtraWidth(facesContext, component);
    return spacingSum;
  }

  private int getCellSpacing(FacesContext facesContext, UIComponent component) {
    String cellspacing = (String) component.getAttributes().get(Attributes.CELLSPACING);
    if (cellspacing != null) {
      try {
        return Integer.parseInt(cellspacing);
      } catch (NumberFormatException e) {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Illegal value for cellspacing : " + cellspacing
              + " using default");
        }
        // ignore and return defaut value
      }
    }
    return getConfiguredValue(facesContext,  component, "cellSpacing");
  }

  public void layoutEnd(FacesContext facesContext, UIComponent component) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("doLayout end");
    }
    UIGridLayout layout = (UIGridLayout) component;
    final Map attributes = layout.getParent().getAttributes();


    boolean needVerticalScroolbar = false;
    Integer innerHeight =
          (Integer) attributes.get(Attributes.INNER_HEIGHT);
    if (innerHeight != null && innerHeight.intValue() > 0) {
      int value = innerHeight.intValue();
      int minimum = calculateLayoutHeight(facesContext, layout.getParent(), true);
      if (minimum > value) {
        value = minimum;
        needVerticalScroolbar = true;
      }
      layoutHeight(Integer.valueOf(value), layout, facesContext);
    }


    Integer innerWidth =
          (Integer) attributes.get(Attributes.INNER_WIDTH);
    if (innerWidth != null && innerWidth.intValue() != -1) {
      int value = innerWidth.intValue();
      if (needVerticalScroolbar) {
        value -= getConfiguredValue(facesContext, component, "scrollbarWidth");
        HtmlRendererUtil.replaceStyleAttribute(layout, "width", value);
      }
      layoutWidth(Integer.valueOf(value), layout, facesContext);
    }

  }

  private int getHeightSpacingSum(UIGridLayout layout,
      FacesContext facesContext, int renderedRowCount) {
    int spacingSum = getSpacingSum(layout, facesContext, renderedRowCount);
    spacingSum += getComponentExtraHeight(facesContext, layout);
    return spacingSum;
  }

  private void layoutWidth(Integer innerWidth, UIGridLayout layout,
      FacesContext facesContext) {

    final List<AbstractUIGridLayout.Row> rows = layout.ensureRows();
    //final int columnCount = layout.getColumnCount();

    final LayoutTokens layoutTokens = layout.getColumnLayout();
    //layoutTokens.ensureSize(columnCount, new RelativeLayoutToken(1));

    //LayoutInfo.createLayoutTokens((String)
        //layout.getAttributes().get(COLUMNS), columnCount);

    int renderedColumnCount = 0;
    if (!rows.isEmpty()) {
      AbstractUIGridLayout.Row row = rows.get(0);
      final List cells = row.getElements();
      renderedColumnCount = cells.size();
      for (int i = 0; i < cells.size(); i++) {
        Object cell = cells.get(i);
        boolean hidden = false;
        if (isHidden(cell)) {
          hidden = true;
          for (int j = 1; j < rows.size(); j++) {
            hidden &= isHidden(rows.get(j).getElements().get(i));
          }
        }
        if (hidden) {
          layoutTokens.set(i, new HideLayoutToken());
          renderedColumnCount--;
        }
      }
    }

    innerWidth -= getWidthSpacingSum(layout, facesContext, renderedColumnCount);
    LayoutInfo layoutInfo =
        new LayoutInfo(layoutTokens.getSize(), innerWidth.intValue(), layoutTokens, layout.getClientId(facesContext),
            layout.isIgnoreFree());

    parseFixedWidth(layoutInfo, layout, facesContext);
    layoutInfo.parseColumnLayout(innerWidth.doubleValue(),
        getCellSpacing(facesContext, layout));

    setColumnWidths(layout, layoutInfo, facesContext);
    layout.getAttributes().put(Attributes.WIDTH_LIST,
        layoutInfo.getSpaceList());
  }

  private boolean isHidden(Object cell) {
    return (cell instanceof UIComponent && !((UIComponent) cell).isRendered())
              || (cell instanceof UIGridLayout.Marker
                  && !((UIGridLayout.Marker) cell).isRendered());
  }

  private void layoutHeight(Integer innerHeight, UIGridLayout layout,
      FacesContext facesContext) {

    final List<AbstractUIGridLayout.Row> rows = layout.ensureRows();
    LayoutTokens layoutTokens = layout.getRowLayout();
    layoutTokens.ensureSize(rows.size(), rows.size() == 1 ? new RelativeLayoutToken(1) : new FixedLayoutToken());
        /*LayoutInfo.createLayoutTokens(
        (String) layout.getAttributes().get(ROWS),
        rows.size(), rows.size() == 1 ? "1*" : "fixed");*/

    int renderedRowCount = rows.size();
    for (int i = 0; i < rows.size(); i++) {
      boolean hidden = true;
      AbstractUIGridLayout.Row row = rows.get(i);
      List cells = row.getElements();
      for (Object cell : cells) {
        hidden &= isHidden(cell);
      }
      row.setHidden(hidden);
      if (hidden) {
        layoutTokens.set(i, new HideLayoutToken());
        renderedRowCount--;
      }
    }

    innerHeight -= getHeightSpacingSum(layout, facesContext, renderedRowCount);

    LayoutInfo layoutInfo
        = new LayoutInfo(rows.size(), innerHeight.intValue(), layoutTokens, layout.getClientId(facesContext));

    if (!layoutTokens.isEmpty()) {
      parseFixedHeight(layoutInfo, layout, facesContext);
      layoutInfo.parseColumnLayout(innerHeight.doubleValue(),
          getCellSpacing(facesContext, layout));
    }

    setColumnHeights(layout, layoutInfo, facesContext);

  }

  private void parseFixedWidth(LayoutInfo layoutInfo, UIGridLayout layout,
      FacesContext facesContext) {
    parseFixedSpace(layoutInfo, layout, true, facesContext);
  }
  private void parseFixedHeight(LayoutInfo layoutInfo, UIGridLayout layout,
      FacesContext facesContext) {
    parseFixedSpace(layoutInfo, layout, false, facesContext);
  }

  public void parseFixedSpace(LayoutInfo layoutInfo, UIGridLayout layout,
      LayoutTokens layoutTokens, boolean width, FacesContext facesContext) {

    //String[] tokens = layoutInfo.getLayoutTokens();
    for (int i = 0; i < layoutTokens.getSize(); i++) {
      LayoutToken token = layoutTokens.get(i);
      if (token instanceof FixedLayoutToken) {
        int max = 0;
        final List<AbstractUIGridLayout.Row> rows = layout.ensureRows();
        if (!rows.isEmpty()) {
          if (width) {
            max = getMaxWidth(facesContext, rows, i, false);
          } else {
            if (i < rows.size()) {      //
              AbstractUIGridLayout.Row row = rows.get(i);
              max = getMaxHeight(facesContext, row, false);
            } else {
              layoutInfo.update(0, i);
              if (LOG.isWarnEnabled()) {
                LOG.warn("More LayoutTokens found than rows! skipping! tokens = "
                    + token + "  components = "
                    + rows.size());
              }
            }
          }
          layoutInfo.update(max, i);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("set column " + i + " from fixed to with " + max);
        }
      }
    }
  }
  private void parseFixedSpace(LayoutInfo layoutInfo, UIGridLayout layout,
                               boolean width, FacesContext facesContext) {

    LayoutTokens tokens = layoutInfo.getLayoutTokens();
    for (int i = 0; i < tokens.getSize(); i++) {
      LayoutToken token = tokens.get(i);
      if (token instanceof FixedLayoutToken) {
        int max = 0;
        final List<AbstractUIGridLayout.Row> rows = layout.ensureRows();
        if (!rows.isEmpty()) {
          if (width) {
            max = getMaxWidth(facesContext, rows, i, false);
          } else {
            if (i < rows.size()) {      //
              AbstractUIGridLayout.Row row = rows.get(i);
              max = getMaxHeight(facesContext, row, false);
            } else {
              layoutInfo.update(0, i);
              if (LOG.isWarnEnabled()) {
                LOG.warn("More LayoutTokens found than rows! skipping! tokens = "
                    + token + "  components = "
                    + rows.size());
              }
            }
          }
          layoutInfo.update(max, i);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("set column " + i + " from fixed to with " + max);
        }
      }
    }
  }

  private int getMaxHeight(FacesContext facesContext, UIGridLayout.Row row, boolean minimum) {
    int maxHeight = 0;
    List cells = row.getElements();
    for (Object cell : cells) {
      if (cell instanceof UIComponent) {
        UIComponent component = (UIComponent) cell;
        int height = -1;
        if (minimum) {
          height = (int) LayoutUtils.getMinimumHeight(facesContext, component);
        } else {
          LayoutInformationProvider renderer = ComponentUtil.getRenderer(facesContext, component);
          if (renderer != null) {
            height = renderer.getFixedHeight(facesContext, component);
          }
        }
        maxHeight = Math.max(maxHeight, height);
      } else if (cell instanceof UIGridLayout.Marker) {
        // ignore 
      } else {
        // TODO is this needed?
        LOG.error("Object is not instanceof UIComponent " + cell.getClass().getName());
      }
    }
    return maxHeight;
  }

  private int getMaxWidth(FacesContext facesContext, List<UIGridLayout.Row> rows, int column, boolean minimum) {
    int maxWidth = 0;

    for (AbstractUIGridLayout.Row row : rows) {
      if (column < row.getElements().size()) {
        Object object = row.getElements().get(column);

        if (object instanceof UIComponent) {
          UIComponent component = (UIComponent) object;
          if (component instanceof UICell) {
            if (((UICell) component).getSpanX() > 1) {
              continue;
            }
          }
          int max = -1;
          if (minimum) {
            max = (int) LayoutUtils.getMinimumWidth(facesContext, component);
          } else {
            LayoutInformationProvider renderer = ComponentUtil.getRenderer(facesContext, component);
            if (renderer != null) {
              max = renderer.getFixedWidth(facesContext, component);
            }
          }
          maxWidth = Math.max(maxWidth, max);
        } else if (object instanceof UIGridLayout.Marker) {
        // ignore 
        } else {
          // TODO is this needed?
          LOG.error("Object is not instanceof UIComponent " + object.getClass().getName());
        }
      }
    }
    return maxWidth;
  }

  private void setColumnWidths(UIGridLayout layout, LayoutInfo layoutInfo,
      FacesContext facesContext) {
    List<AbstractUIGridLayout.Row> rows = layout.ensureRows();

    for (Iterator iter = rows.iterator(); iter.hasNext();) {
      AbstractUIGridLayout.Row row = (UIGridLayout.Row) iter.next();
      List cells = row.getElements();
      int columnCount = layout.getColumnCount();
      for (int i = 0; i < columnCount; i++) {
        if (cells.get(i) instanceof UIComponent) {
          UIComponent cell = (UIComponent) cells.get(i);
          int spanX = UIGridLayout.getSpanX(cell);
          int cellWidth = 0;
          for (int j = i; j < i + spanX; j++) {
            cellWidth += layoutInfo.getSpaceForColumn(j);
          }
          if (spanX > 1 && getBorder(layout) > 0) {
            cellWidth += ((spanX - 1) * 2);
          }
          cellWidth += (spanX - 1) * getCellSpacing(facesContext, layout);
          LayoutUtils.maybeSetLayoutAttribute(cell,
              Attributes.LAYOUT_WIDTH, Integer.valueOf(cellWidth));
        }
      }
    }
  }

  private void setColumnHeights(UIGridLayout layout, LayoutInfo layoutInfo,
      FacesContext facesContext) {
    List<AbstractUIGridLayout.Row> rows = layout.ensureRows();

    for (int i = 0; i < rows.size(); i++) {
      AbstractUIGridLayout.Row row = rows.get(i);
      List cells = row.getElements();
      int columnCount = layout.getColumnCount();
      for (int j = 0; j < columnCount; j++) {
        if (cells.get(j) instanceof UIComponent) {
          UIComponent cell = (UIComponent) cells.get(j);
          int spanY = UIGridLayout.getSpanY(cell);
          int cellHeight = 0;
          for (int k = i; k < i + spanY; k++) {
            cellHeight += layoutInfo.getSpaceForColumn(k);
          }
          if (spanY > 1 && getBorder(layout) > 0) {
            cellHeight += ((spanY - 1) * 2);
          }
          cellHeight += (spanY - 1) * getCellSpacing(facesContext, layout);
          if (LOG.isDebugEnabled()) {
            LOG.debug("set height of " + cellHeight + "px to "
                + cell.getRendererType() + " layoutInfo " + layoutInfo.toString());
          }
          cell.getAttributes().put(Attributes.LAYOUT_HEIGHT, Integer.valueOf(cellHeight));
          cell.getAttributes().remove(Attributes.INNER_HEIGHT);
          if (cell instanceof UICell || cell instanceof AbstractUIForm) {
            List children = LayoutUtils.addChildren(new ArrayList<UIComponent>(), cell);
            for (Iterator childs = children.iterator(); childs.hasNext();) {
              UIComponent component = (UIComponent) childs.next();
              if (LOG.isDebugEnabled()) {
                LOG.debug("set height of " + cellHeight + "px to "
                    + component.getRendererType());
              }
              component.getAttributes().put(Attributes.LAYOUT_HEIGHT, Integer.valueOf(cellHeight));
              component.getAttributes().remove(Attributes.INNER_HEIGHT);

            }
          }
        }
      }
    }
  }


  public void layoutBegin(FacesContext facesContext, UIComponent component) {
    if (LOG.isInfoEnabled()) {
      LOG.info("############################## layoutBegin +++++++++++++++++++++++++++++++++++++++++");
    }
    HtmlRendererUtil.layoutSpace(facesContext, component, true);
    HtmlRendererUtil.layoutSpace(facesContext, component, false);

    if (component instanceof UIGridLayout) {
      layoutMargins((UIGridLayout) component);
    }
  }

  private void layoutMargins(UIGridLayout layout) {

    HtmlStyleMap style = (HtmlStyleMap) layout.getAttributes().get(Attributes.STYLE);

    if (style != null) {
      int marginTop = getMarginAsInt(layout.getMarginTop());
      int marginRight = getMarginAsInt(layout.getMarginRight());
      int marginBottom = getMarginAsInt(layout.getMarginBottom());
      int marginLeft = getMarginAsInt(layout.getMarginLeft());
      if(marginTop > 0) {
        style.put("margin-top", marginTop);
      }
      if(marginRight > 0) {
        style.put("margin-right", marginRight);
        Integer width = style.getInt("width");
        if (width != null) {
          width -= marginRight;
          style.put("width", width);
        }
      }
      if(marginBottom > 0) {
        style.put("margin-bottom", marginBottom);
      }
      if(marginLeft > 0) {
        style.put("margin-left", marginLeft);
        Integer width = style.getInt("width");
        if (width != null) {
          width -= marginLeft;
          style.put("width", width);
        }
      }

      //layout.getAttributes().put(ATTR_LAYOUT_TABLE_STYLE, style);
    }
  }

  public int getComponentExtraWidth(FacesContext facesContext,
      UIComponent component) {
    int extra = 0;
    UIGridLayout layout = (UIGridLayout) component;

    extra += getMarginAsInt(layout.getMarginRight());
    extra += getMarginAsInt(layout.getMarginLeft());

    return extra;
  }

  public int getComponentExtraHeight(FacesContext facesContext,
      UIComponent component) {
    int extra = 0;
    UIGridLayout layout = (UIGridLayout) component;

    extra += getMarginAsInt(layout.getMarginTop());
    extra += getMarginAsInt(layout.getMarginBottom());
    return extra;
  }

  private int getMarginAsInt(String margin) {
    if (margin != null) {
      margin = removePx(margin);
      try {
        return Integer.parseInt(margin);
      } catch (NumberFormatException e) {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Illegal Margin : " + margin
              + " exception : " + e.getMessage(), e);
        }
      }
    }
    return 0;
  }

  private String removePx(String margin) {
    if (margin!=null&&margin.endsWith("px")) {
      margin = margin.substring(0, margin.length() - 2);
    }
    return margin;
  }
}

