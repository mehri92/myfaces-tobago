package org.apache.myfaces.tobago.internal.mock.faces;

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

import org.apache.myfaces.tobago.context.RendererConfig;
import org.apache.myfaces.tobago.context.RenderersConfig;

import java.util.Collection;

public class MockRenderersConfig implements RenderersConfig {

  /**
   * To keep it simple, we allow every value shorter than 10 characters.
   */
  public boolean isMarkupSupported(String rendererName, String markup) {
    return markup.length() < 10;
  }

  public Collection<RendererConfig> getRendererConfigs() {
    return null;
  }
}
