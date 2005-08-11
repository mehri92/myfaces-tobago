/*
 * Copyright (c) 2002 Atanion GmbH, Germany
 * All rights reserved. Created Oct 31, 2002 at 1:24:42 PM.
 * $Id$
 */
package org.apache.myfaces.tobago.taglib.component;

import org.apache.myfaces.tobago.context.ResourceManagerUtil;
import org.apache.myfaces.tobago.taglib.decl.HasId;
import org.apache.myfaces.tobago.taglib.decl.HasStringValue;
import org.apache.myfaces.tobago.apt.annotation.BodyContent;
import org.apache.myfaces.tobago.apt.annotation.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

@Tag(name="include", bodyContent=BodyContent.EMPTY)
public class IncludeTag extends TagSupport implements HasId, HasStringValue {
// ----------------------------------------------------------- class attributes

  private static final Log LOG = LogFactory.getLog(IncludeTag.class);

// ----------------------------------------------------------------- attributes

  private String value;

// ----------------------------------------------------------- business methods

  public int doStartTag() throws JspException {
    String pageName = null;
    try {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (UIComponentTag.isValueReference(value)) {
        ValueBinding valueBinding
            = facesContext.getApplication().createValueBinding(value);
        pageName = (String) valueBinding.getValue(facesContext);
      } else {
        pageName = value;
      }

      pageName = ResourceManagerUtil.getJsp(facesContext, pageName);

      if (LOG.isDebugEnabled()) {
        LOG.debug("include start pageName = '" + pageName + "'");
      }
      pageContext.include(pageName);
      if (LOG.isDebugEnabled()) {
        LOG.debug("include end   pageName = '" + pageName + "'");
      }
    } catch (Throwable e) {
      LOG.error("pageName = '" + pageName + "' " + e, e);
      throw new JspException(e);
    }
    return super.doStartTag();
  }

  public void release() {
    value = null;
    super.release();
  }

// ------------------------------------------------------------ getter + setter

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}

