/*
 * Copyright (c) 2004 Atanion GmbH, Germany
 * All rights reserved. Created 20.10.2004 11:38:48.
 * $Id$
 */
package org.apache.myfaces.tobago.el;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.faces.context.FacesContext;
import java.security.Principal;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

public class UserWrapper {

  private static final Log LOG = LogFactory.getLog(UserWrapper.class);

  private Map roles;

  public UserWrapper() {
    roles = new RolesMap();
  }

  public Principal getPrincipal() {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    Principal principal = facesContext.getExternalContext().getUserPrincipal();
    if (LOG.isDebugEnabled()) {
      LOG.debug("getPrincipal(): " + principal);
    }
    return principal;
  }

  public Map getRoles() {
    return roles;
  }

  private static class RolesMap implements Map {

    public Object get(Object key) {
      String role = (String) key;
      FacesContext facesContext = FacesContext.getCurrentInstance();
      boolean inRole = facesContext.getExternalContext().isUserInRole(role);
      if (LOG.isDebugEnabled()) {
        LOG.debug("inRole: " + inRole);
      }
      return new Boolean(inRole);
    }

    public int size() {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
      throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
      throw new UnsupportedOperationException();
    }

    public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
    }

    public Collection values() {
      throw new UnsupportedOperationException();
    }

    public void putAll(Map t) {
      throw new UnsupportedOperationException();
    }

    public Set entrySet() {
      throw new UnsupportedOperationException();
    }

    public Set keySet() {
      throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
      throw new UnsupportedOperationException();
    }

    public Object put(Object key, Object value) {
      throw new UnsupportedOperationException();
    }
  }

}
