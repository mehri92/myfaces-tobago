package org.apache.myfaces.tobago.taglib.decl;

import org.apache.myfaces.tobago.apt.annotation.TagAttribute;

/**
 * Copyright (c) 2003 Atanion GmbH, Germany. All rights reserved.
 * Created: Apr 9, 2005 2:52:15 PM
 * User: bommel
 * $Id$
 */
public interface IsRequired {

  @TagAttribute void setRequired(String required);
}
