/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.tobago.model;

import java.util.EnumSet;
import java.util.Set;

public enum Selectable {

  /**
   * Not selectable.
   */
  none,

  /**
   * Multi selection possible. No other limitations.
   */
  multi,

  /**
   * Only one item is selectable.
   */
  single,

  /**
   * Only one of no item is selectable.
   */
  singleOrNone,

  /**
   * Only leafs are selectable.
   */
  multiLeafOnly,

  /**
   * Only one item is selectable and it must be a leaf.
   */
  singleLeafOnly,

  /**
   * Only siblings are selectable.
   */
  sibling,

  /**
   * Only siblings are selectable and they have to be leafs.
   */
  siblingLeafOnly,

  /**
   * Multi selection possible. When selecting or deselecting an item, the subtree will also
   * be selected or unselected.
   */
  multiCascade;

  /** @deprecated  */
  @Deprecated
  public static final Selectable NONE = none;

  /** @deprecated  */
  @Deprecated
  public static final Selectable MULTI = multi;

  /** @deprecated  */
  @Deprecated
  public static final Selectable SINGLE = single;

  /** @deprecated  */
  @Deprecated
  public static final Selectable SINGLE_OR_NONE = singleOrNone;

  /** @deprecated  */
  @Deprecated
  public static final Selectable MULTI_LEAF_ONLY = multiLeafOnly;

  /** @deprecated  */
  @Deprecated
  public static final Selectable SINGLE_LEAF_ONLY = singleLeafOnly;

  /** @deprecated  */
  @Deprecated
  public static final Selectable SIBLING = sibling;

  /** @deprecated  */
  @Deprecated
  public static final Selectable SIBLING_LEAF_ONLY = siblingLeafOnly;

  /** @deprecated  */
  @Deprecated
  public static final Selectable MULTI_CASCADE = multiCascade;

  public static final String STRING_NONE = "none";

  public static final String STRING_MULTI = "multi";

  public static final String STRING_SINGLE = "single";

  public static final String STRING_SINGLE_OR_NONE = "singleOrNone";

  public static final String STRING_MULTI_LEAF_ONLY = "multiLeafOnly";

  public static final String STRING_SINGLE_LEAF_ONLY = "singleLeafOnly";

  public static final String STRING_SIBLING = "sibling";

  public static final String STRING_SIBLING_LEAF_ONLY = "siblingLeafOnly";

  public static final String STRING_MULTI_CASCADE = "multiCascade";

  public static final Set<Selectable> SHEET_VALUES = EnumSet.of(
      none,
      multi,
      single,
      singleOrNone);

  public static final Set<Selectable> TREE_VALUES = EnumSet.of(
      none,
      multi,
      single,
      multiLeafOnly,
      singleLeafOnly,
      multiCascade);

  public static final Set<Selectable> TREE_LISTBOX_VALUES = EnumSet.of(
      single,
      singleLeafOnly,
      multiLeafOnly);

  /**
   * @param name Name of the Selectable
   * @return The matching tree selection (can't be null).
   * @throws IllegalArgumentException When the name doesn't match any Selectable.
   */
  public static Selectable parse(final Object name) throws IllegalArgumentException {
    if (name == null) {
      return null;
    }
    if (name instanceof Selectable) {
      return (Selectable) name;
    }
    return valueOf(name.toString());
  }

  public boolean isLeafOnly() {
    return this == singleLeafOnly || this == multiLeafOnly || this == siblingLeafOnly;
  }

  public boolean isSingle() {
    return this == single || this == singleOrNone || this == singleLeafOnly;
  }

  public boolean isSupportedBySheet() {
    return SHEET_VALUES.contains(this);
  }

  public boolean isSupportedByTree() {
    return TREE_VALUES.contains(this);
  }

  public boolean isSupportedByTreeListbox() {
    return TREE_LISTBOX_VALUES.contains(this);
  }
}
