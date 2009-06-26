package org.apache.myfaces.tobago.component;

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

import org.apache.myfaces.tobago.OnComponentCreated;
import org.apache.myfaces.tobago.layout.Container;
import org.apache.myfaces.tobago.layout.LayoutContext;
import org.apache.myfaces.tobago.layout.LayoutManager;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

// XXX is this a good way?
public abstract class AbstractUITabGroupLayout extends UILayout implements OnComponentCreated, LayoutManager {

  public void onComponentCreated(FacesContext context, UIComponent component) {
  }

  public void collect(LayoutContext layoutContext, Container container, int horizontalIndex, int verticalIndex) {
  }

  public void distribute(LayoutContext layoutContext, Container container) {
  }

  @Override
  public boolean getRendersChildren() {
    return false;
  }

}
