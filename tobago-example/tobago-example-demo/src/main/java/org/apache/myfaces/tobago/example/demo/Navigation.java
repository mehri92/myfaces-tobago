package org.apache.myfaces.tobago.example.demo;

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

import org.apache.myfaces.tobago.context.ResourceManagerUtils;
import org.apache.myfaces.tobago.example.demo.jsp.JspFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Navigation {

  private static final Logger LOG = LoggerFactory.getLogger(Navigation.class);

  private Node tree;

  private Node currentNode;

  public Navigation() {

    ServletContext servletContext
        = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

    tree = locateResourcesInWar(servletContext);
    currentNode = tree;
/*
    tree = new Node("Root", null);

    Node overview = new Node("overview", "/overview/intro");
    overview.add(new Node("basic", "/overview/basic"));
    overview.add(new Node("sheet", "/overview/sheet"));
    final Node tree = new Node("tree", "/overview/tree");
    overview.add(tree);
    tree.add(new Node("treeEditor", "/overview/tree-editor"));
    overview.add(new Node("tab", "/overview/tab"));
    overview.add(new Node("toolbar", "/overview/toolbar"));
    Node validation = new Node("validation", "/overview/validation");
    validation.add(new Node("validationSeverity", "/overview/validation-severity"));
    overview.add(validation);
    overview.add(new Node("form", "/overview/form"));
    overview.add(new Node("theme", "/overview/theme"));
    overview.add(new Node("browser", "/overview/browser"));
    overview.add(new Node("locale", "/overview/locale"));
    overview.add(new Node("layout", "/overview/layout"));
    this.tree.add(overview);

    Node bestPractice = new Node("bestPractice", "best-practice/intro");
    bestPractice.add(new Node("error", "best-practice/error"));
    bestPractice.add(new Node("theme", "best-practice/theme"));
    bestPractice.add(new Node("transition", "best-practice/transition"));
    bestPractice.add(new Node("nonFacesResponse", "best-practice/non-faces-response"));
    bestPractice.add(new Node("toolBarCustomizer", "best-practice/tool-bar-customizer"));
    bestPractice.add(new Node("faceletsAsResources", "best-practice/facelets-as-resources"));
    this.tree.add(bestPractice);

    Node reference = new Node("reference_intro", "reference/intro");
    reference.add(new Node("reference_command", "reference/command"));
    reference.add(new Node("reference_container", "reference/container"));
    reference.add(new Node("reference_input", "reference/input"));
    reference.add(new Node("reference_inputSuggest", "reference/inputSuggest"));
    reference.add(new Node("reference_menu", "reference/menu"));
    reference.add(new Node("reference_output", "reference/output"));
    reference.add(new Node("reference_object", "reference/object"));
    reference.add(new Node("reference_popup", "reference/popup"));
    reference.add(new Node("reference_progress", "reference/progress"));
    reference.add(new Node("reference_select", "/reference/select"));
    reference.add(new Node("reference_sheet", "reference/sheet"));
    reference.add(new Node("reference_tab", "reference/tab"));
    reference.add(new Node("reference_time", "reference/time"));
    reference.add(new Node("reference_tree", "/reference/tree/tree-normal"));
    reference.add(new Node("reference_tool", "reference/tool"));
    reference.add(new Node("reference_partial", "reference/partial"));
    reference.add(new Node("reference_upload", "reference/upload"));
    this.tree.add(reference);

    this.tree.setExpanded(true);
    overview.setExpanded(true);
    bestPractice.setExpanded(true);

    currentNode = overview;
*/
  }

  public void selectByViewId(String viewId) {
    Enumeration enumeration = tree.depthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
      Node node = ((Node) enumeration.nextElement());
      if (node.getOutcome() != null && viewId.contains(node.getOutcome())) {
        currentNode = node;
        break;
      }
    }
  }

  public Node getTree() {
    return tree;
  }

  public Node getCurrentNode() {
    return currentNode;
  }

  public String gotoFirst() {
    currentNode = tree.getNextNode();
    return currentNode.getOutcome();
  }

  public String gotoPrevious() {
    final Node previousNode = currentNode.getPreviousNode();
    if (previousNode != null) {
      currentNode = previousNode;
      return currentNode.getOutcome();
    } else {
      LOG.warn("Strange navigation behavior");
      return null;
    }
  }

  public String gotoNext() {
    final Node nextNode = currentNode.getNextNode();
    if (nextNode != null) {
      currentNode = nextNode;
      return currentNode.getOutcome();
    } else {
      LOG.warn("Strange navigation behavior");
      return null;
    }
  }

  public boolean isFirst() {
    final Node previousNode = currentNode.getPreviousNode();
    return previousNode == null || previousNode.isRoot();
  }

  public boolean isLast() {
    final Node nextNode = currentNode.getNextNode();
    return nextNode == null;
  }

  public String viewSource() {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    String viewId = facesContext.getViewRoot().getViewId();
    HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
    response.setContentType("text/html;charset=UTF-8");

    try {
      InputStream resourceAsStream = externalContext.getResourceAsStream(viewId);
      InputStreamReader reader = new InputStreamReader(resourceAsStream);
      JspFormatter.writeJsp(reader, new PrintWriter(response.getOutputStream()));
    } catch (IOException e) {
      LOG.error("", e);
      return "error";
    }

    facesContext.responseComplete();
    return null;
  }

  private Node locateResourcesInWar(ServletContext servletContext) {

    String content = "/content";

    Set<String> resourcePaths = servletContext.getResourcePaths(content);
    List<String> list = new ArrayList<String>();

    for (String path : resourcePaths) {
      list.add(path);
    }

    return buildNodes(list);
  }

  protected Node buildNodes(List<String> list) {
    List<Node> nodes = new ArrayList<Node>();
    for (String path : list) {
      try {
        nodes.add(new Node(path));
      } catch (IllegalStateException e) {
        LOG.error("Found file with wrong pattern: '{}'", path);
      }
    }

    Collections.sort(nodes);

    Map<String, Node> map = new HashMap<String, Node>();
    Node root = new Node();
    map.put(root.getBranch(), root);

    for (Node node : nodes) {
      map.put(node.getBranch(), node);
      String parent = node.getBranch().substring(0, Math.max(node.getBranch().length() - 3, 0));
      map.get(parent).add(node);
    }

    return root;
  }


  public class Node extends DefaultMutableTreeNode implements Comparable {

    private String name;
    private String id;
    private String branch;
    private String title;
    private String outcome;
    private boolean expanded;

    public Node() {
      name = "root";
      branch = "";
    }

    public Node(String path) {

      outcome = path;
//      final Pattern pattern = Pattern.compile("(\\d\\d-\\d\\d)-(.*)\\.(xhtml)");
      final Pattern pattern = Pattern.compile("([\\d\\d|]*\\d\\d)-(.*)\\.(xhtml)");
      final Matcher matcher = pattern.matcher(path);
      matcher.find();
      branch = matcher.group(1);
      name = matcher.group(2);
      String extension = matcher.group(3);

/*
      final StringTokenizer tokenizer = new StringTokenizer(path, "-.", true);
      final StringBuilder branchBuilder = new StringBuilder();
      while (tokenizer.hasMoreElements()) {
        String next = tokenizer.nextToken();
        if (next.matches("\\d\\d") || next.matches("-")) {
          branchBuilder.append(next);
        } else {
          break;
        }
      }
      if (branchBuilder.length() < 2) {
        throw new ParseException(path, 0);
      }
      branch = branchBuilder.toString();

      final StringBuilder nameBuilder = new StringBuilder();
      while (tokenizer.hasMoreElements()) {
        String next = tokenizer.nextToken();
        nameBuilder.append(next);
      }
      name = nameBuilder.toString();
*/

/*
      path.matches("")
      this.title = ResourceManagerUtils.getProperty(FacesContext.getCurrentInstance(), "overview", key);
      this.id = key;
      this.outcome = outcome;
*/
      this.title = ResourceManagerUtils.getProperty(FacesContext.getCurrentInstance(), "overview", name);

    }

    public Node(String key, String outcome) {
      this.title = ResourceManagerUtils.getProperty(FacesContext.getCurrentInstance(), "overview", key);
      this.id = key;
      this.outcome = outcome;
    }

    public int compareTo(Object o) {
      Node other = (Node) o;
      return branch.compareTo(other.getBranch());
    }

    public String action() {
      LOG.info("Navigate to '" + outcome + "'");
      currentNode = this;

      return outcome;
    }

    public String getMarkup() {
      return currentNode == this ? "marked" : null;
    }

    public Node getNextNode() {
      return (Node) super.getNextNode();
    }

    public Node getPreviousNode() {
      return (Node) super.getPreviousNode();
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getBranch() {
      return branch;
    }

    public void setBranch(String branch) {
      this.branch = branch;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getOutcome() {
      return outcome;
    }

    public void setOutcome(String outcome) {
      this.outcome = outcome;
    }

    public boolean isExpanded() {
      return expanded;
    }

    public void setExpanded(boolean expanded) {
      this.expanded = expanded;
    }
  }
}
