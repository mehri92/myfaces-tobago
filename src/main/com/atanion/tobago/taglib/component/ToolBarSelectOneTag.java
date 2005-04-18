package com.atanion.tobago.taglib.component;

import com.atanion.util.annotation.Tag;
import com.atanion.tobago.taglib.decl.HasId;
import com.atanion.tobago.taglib.decl.IsDisabled;
import com.atanion.tobago.taglib.decl.HasBinding;
import com.atanion.tobago.taglib.decl.HasCommandType;
import com.atanion.tobago.taglib.decl.IsImmediateCommand;
import com.atanion.tobago.taglib.decl.IsRendered;
import com.atanion.tobago.taglib.decl.HasAction;
import com.atanion.tobago.taglib.decl.HasValue;

/**
 * Created by IntelliJ IDEA.
 * User: weber
 * Date: Apr 11, 2005
 * Time: 2:21:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Tag(name="toolBarSelectOne")
public class ToolBarSelectOneTag extends MenuSelectOneTag
    implements HasId, IsDisabled, HasAction, HasCommandType, HasValue,
               IsRendered, HasBinding, IsImmediateCommand {
  
}
