/*
 * Copyright 2002-2005 The Apache Software Foundation.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/*
 * Created 19.08.2002 at 16:07:05.
 * $Id$
 */
package org.apache.myfaces.tobago.taglib.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.myfaces.tobago.TobagoConstants.ATTR_STATE;
import static org.apache.myfaces.tobago.TobagoConstants.ATTR_SWITCH_TYPE;
import org.apache.myfaces.tobago.apt.annotation.BodyContentDescription;
import org.apache.myfaces.tobago.apt.annotation.Tag;
import org.apache.myfaces.tobago.apt.annotation.TagAttribute;
import org.apache.myfaces.tobago.apt.annotation.UIComponentTagAttribute;
import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.component.UITabGroup;
import static org.apache.myfaces.tobago.component.UITabGroup.SWITCH_TYPE_CLIENT;
import static org.apache.myfaces.tobago.component.UITabGroup.SWITCH_TYPE_RELOAD_PAGE;
import static org.apache.myfaces.tobago.component.UITabGroup.SWITCH_TYPE_RELOAD_TAB;
import org.apache.myfaces.tobago.taglib.decl.HasDimension;
import org.apache.myfaces.tobago.taglib.decl.HasIdBindingAndRendered;
import org.apache.myfaces.tobago.taglib.decl.HasState;

import javax.faces.component.UIComponent;

/**
 * Renders a tabpanel.
 */
@Tag(name = "tabGroup")
@BodyContentDescription(anyTagOf = "(<tc:tab>* ")
public class TabGroupTag extends TobagoTag
    implements HasIdBindingAndRendered, HasDimension, HasState {

  private static final Log LOG = LogFactory.getLog(TabGroupTag.class);

  private String serverside;
  private String state;
  private String switchType;

  public String getComponentType() {
    return UITabGroup.COMPONENT_TYPE;
  }

  protected void setProperties(UIComponent component) {
    super.setProperties(component);
    ComponentUtil.setValueBinding(component, ATTR_STATE, state);
    if (switchType != null) {

      if (SWITCH_TYPE_RELOAD_PAGE.equals(switchType)) {
        ComponentUtil.setStringProperty(component, ATTR_SWITCH_TYPE, SWITCH_TYPE_RELOAD_PAGE);
      } else if (SWITCH_TYPE_RELOAD_TAB.equals(switchType)) {
        ComponentUtil.setStringProperty(component, ATTR_SWITCH_TYPE, SWITCH_TYPE_RELOAD_TAB);
      } else if (SWITCH_TYPE_CLIENT.equals(switchType)) {
        ComponentUtil.setStringProperty(component, ATTR_SWITCH_TYPE, SWITCH_TYPE_CLIENT);
      } else {
        LOG.warn("Illegal switchType value: \"" + switchType + "\" using default: \"" + SWITCH_TYPE_CLIENT + "\"");
        ComponentUtil.setStringProperty(component, ATTR_SWITCH_TYPE, SWITCH_TYPE_CLIENT);
      }
    } else {
      if (Boolean.valueOf(serverside)) {
        ComponentUtil.setStringProperty(component, ATTR_SWITCH_TYPE, SWITCH_TYPE_RELOAD_PAGE);
      } else {
        ComponentUtil.setStringProperty(component, ATTR_SWITCH_TYPE, SWITCH_TYPE_CLIENT);
      }
    }

  }

  public void release() {
    super.release();
    serverside = null;
    state = null;
  }

  public String getServerside() {
    return serverside;
  }


  /**
   * Flag indicating that tab switching is done by server request.
   * @deprecated
   */
  @TagAttribute
  @UIComponentTagAttribute(type = "java.lang.Boolean", defaultValue = "false")
  @Deprecated
  public void setServerside(String serverside) {
    this.serverside = serverside;
  }

  public void setState(String state) {
    this.state = state;
  }

  /**
   * Flag indicating how tab switching should be done.
   *
   * Possible values are:
   *   "client"     : Tab switching id done on client, no server Request. This is default.
   *   "reloadPage" : Tab switching id done by server request. Full page is reloaded.
   *   "reloadTab"  : Tab switching id done by server request. Only the Tab is reloaded.
   */
  @TagAttribute
  @UIComponentTagAttribute(type = "java.lang.String", defaultValue = SWITCH_TYPE_CLIENT)
  public void setSwitchType(String switchType) {
    this.switchType = switchType;
  }
}

