package org.apache.myfaces.tobago.taglib.sandbox;

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

import static org.apache.myfaces.tobago.TobagoConstants.ATTR_EXPANDED;
import static org.apache.myfaces.tobago.TobagoConstants.ATTR_TIP;
import static org.apache.myfaces.tobago.TobagoConstants.ATTR_TARGET;
import static org.apache.myfaces.tobago.TobagoConstants.ATTR_VALUE;
import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.component.UITreeNode;
import org.apache.myfaces.tobago.taglib.component.AbstractCommandTag;

import javax.faces.component.UIComponent;

public class TreeNodeTag extends AbstractCommandTag implements TreeNodeTagDeclaration {

  private String value;
  private String markup;
  private String tip;
  private String target;
  private String expanded;

  @Override
  public String getComponentType() {
    return UITreeNode.COMPONENT_TYPE;
  }

  @Override
  protected void setProperties(UIComponent component) {
    super.setProperties(component);

    ComponentUtil.setStringProperty(component, ATTR_VALUE, value);
    ComponentUtil.setMarkup(component, markup);
    ComponentUtil.setStringProperty(component, ATTR_TIP, tip);
    ComponentUtil.setStringProperty(component, ATTR_TARGET, target);
    ComponentUtil.setBooleanProperty(component, ATTR_EXPANDED, expanded);
  }

  @Override
  public void release() {
    super.release();
    value = null;
    markup = null;
    tip = null;
    target = null;
    expanded = null;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getMarkup() {
    return markup;
  }

  public void setMarkup(String markup) {
    this.markup = markup;
  }

  public void setTip(String tip) {
    this.tip = tip;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getExpanded() {
    return expanded;
  }

  public void setExpanded(String expanded) {
    this.expanded = expanded;
  }
}
