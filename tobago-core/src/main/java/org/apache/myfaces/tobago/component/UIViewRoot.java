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
package org.apache.myfaces.tobago.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.tobago.context.ClientProperties;
import org.apache.myfaces.tobago.context.ResourceManagerImpl;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

/**
 * User: weber
 * Date: Jun 13, 2005
 * Time: 5:19:31 PM
 */
public class UIViewRoot extends javax.faces.component.UIViewRoot {
  // ------------------------------------------------------ Manifest Constants



  /**
   * <p>Create a new {@link UIViewRoot} instance with default property
   * values.</p>
   */
  public UIViewRoot() {
    super();

    updateRendererCachePrefix();
  }


  private ClientProperties clientProperties;

  public ClientProperties getClientProperties() {
    return clientProperties;
  }

  public void setClientProperties(ClientProperties clientProperties) {
    this.clientProperties = clientProperties;
    updateRendererCachePrefix();
  }

  private ResourceManagerImpl.CacheKey rendererCacheKey;

  public ResourceManagerImpl.CacheKey getRendererCacheKey() {
    return rendererCacheKey;
  }

  private static final Log LOG = LogFactory.getLog(UIViewRoot.class);

  public void updateRendererCachePrefix() {
    rendererCacheKey = ResourceManagerImpl.getRendererCacheKey(
        clientProperties != null ? clientProperties.getId() : "null", getLocale());
//    LOG.info("updateRendererCachePrefix :" + rendererCachePrefix);
  }

  public void broadcastEventsForPhase(FacesContext context, PhaseId phaseId) {
    _broadcastForPhase(phaseId);
    if (context.getRenderResponse() || context.getResponseComplete())
    {
        clearEvents();
    }
  }
  
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
//
//  The following code is copied from myfaces implementation!
//  In suns jsf-api 1.1.01 are the events not cleared if renderResponse is true
//  after processUpdates, seems to be a bug. This is fixed at least in
//  Nightly Snapshot from 15.08.2005, but not in stable yet.
//  Events are private member of UIViewRoot, so we have to copy anny code
//  accessing them.
//
  // TODO: remove if fixed in stable release!


  public static final int ANY_PHASE_ORDINAL = PhaseId.ANY_PHASE.getOrdinal();
  private List _events = null;


  public void queueEvent(FacesEvent event)
  {
      if (event == null) throw new NullPointerException("event");
      if (_events == null)
      {
          _events = new ArrayList();
      }
      _events.add(event);
  }



private void _broadcastForPhase(PhaseId phaseId)
{
    if (_events == null) return;

    boolean abort = false;

    int phaseIdOrdinal = phaseId.getOrdinal();
    for (ListIterator listiterator = _events.listIterator(); listiterator.hasNext();)
    {
        FacesEvent event = (FacesEvent) listiterator.next();
        int ordinal = event.getPhaseId().getOrdinal();
        if (ordinal == ANY_PHASE_ORDINAL ||
            ordinal == phaseIdOrdinal)
        {
            UIComponent source = event.getComponent();
            try
            {
                source.broadcast(event);
            }
            catch (AbortProcessingException e)
            {
                // abort event processing
                // Page 3-30 of JSF 1.1 spec: "Throw an AbortProcessingException, to tell the JSF implementation
                //  that no further broadcast of this event, or any further events, should take place."
                abort = true;
                break;
            } finally {

              try
              {
                    listiterator.remove();
              }
              catch(ConcurrentModificationException cme)
              {
                int eventIndex = listiterator.previousIndex();
                _events.remove(eventIndex);
                listiterator = _events.listIterator();
              }
            }
        }
    }

    if (abort) {
        // TODO: abort processing of any event of any phase or just of any event of the current phase???
        clearEvents();
    }
}


private void clearEvents()
{
    _events = null;
}


public void processDecodes(FacesContext context)
{
    if (context == null) throw new NullPointerException("context");
    super.processDecodes(context);
    _broadcastForPhase(PhaseId.APPLY_REQUEST_VALUES);
    if (context.getRenderResponse() || context.getResponseComplete())
    {
        clearEvents();
    }
}

public void processValidators(FacesContext context)
{
    if (context == null) throw new NullPointerException("context");
    super.processValidators(context);
    _broadcastForPhase(PhaseId.PROCESS_VALIDATIONS);
    if (context.getRenderResponse() || context.getResponseComplete())
    {
        clearEvents();
    }
}

public void processUpdates(FacesContext context)
{
    if (context == null) throw new NullPointerException("context");
    super.processUpdates(context);
    _broadcastForPhase(PhaseId.UPDATE_MODEL_VALUES);
    if (context.getRenderResponse() || context.getResponseComplete())
    {
        clearEvents();
    }
}

public void processApplication(FacesContext context)
{
    if (context == null) throw new NullPointerException("context");
    _broadcastForPhase(PhaseId.INVOKE_APPLICATION);
    if (context.getRenderResponse() || context.getResponseComplete())
    {
        clearEvents();
    }
}



}
