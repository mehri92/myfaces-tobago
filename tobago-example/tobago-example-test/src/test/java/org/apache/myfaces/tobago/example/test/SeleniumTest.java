package org.apache.myfaces.tobago.example.test;

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

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.SeleniumException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public abstract class SeleniumTest {

  private static final String CONTEXT_PATH = "";
  //  private static final String CONTEXT_PATH = "tobago-example-test";
  private static final String SERVLET_MAPPING = "faces";

  public static final String ERROR_ON_SERVER = "error on server";
  public static final String HAS_ERROR_SEVERITY = "has error severity";
  public static final String IS_BROKEN = "is broken";

  @Deprecated
  private static DefaultSelenium selenium;
  private static CommandProcessor commandProcessor;

  @BeforeClass
  public static void setUp() throws Exception {
    selenium = createSeleniumClient();
    selenium.start();
//todo alternative   commandProcessor.start();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    selenium.stop();
//todo alternative   commandProcessor.stop();
  }

  @Deprecated
  protected DefaultSelenium getSelenium() {
    return selenium;
  }

  protected CommandProcessor getCommandProcessor() {
    return commandProcessor;
  }

  protected static DefaultSelenium createSeleniumClient() throws Exception {
    commandProcessor = new HttpCommandProcessor("localhost", 4444, "*firefox", "http://localhost:8080/");
    return new DefaultSelenium(commandProcessor);
  }

  protected void waitForAjaxComplete() {
    getSelenium().waitForCondition(
        "selenium.browserbot.getCurrentWindow().document.getElementsByClassName('tobago-page-overlay').length == 0",
        "5000"
    );
  }

  protected void checkPage() {
    final String location = selenium.getLocation();
    final String html = getHtmlSource();
    if (errorOnServer()) {
      Assert.fail(format(ERROR_ON_SERVER, location, html, ""));
    }
    if (location.endsWith(".xhtml") || location.endsWith(".jspx")) {
      try {
        if (isErrorOnPage()) {
          Assert.fail(format(HAS_ERROR_SEVERITY, location, html, getErrors()));
        }
      } catch (SeleniumException e) {
        Assert.fail(format(IS_BROKEN, location, html, "Not a Tobago page? Exception=" + e));
      }
    }
  }

  public String format(String error, String location, String html, String options) {
    StringBuilder b = new StringBuilder();
    b.append(error);
    b.append("\nPage URL: ");
    b.append(location);
    b.append("\n");
    b.append(options);
    b.append("\n---------------------------------------------------------------------------------------------------\n");
    b.append(html);
    b.append("\n---------------------------------------------------------------------------------------------------\n");
    return b.toString();
  }

  /**
   * Happen an exception from MyFaces on the page?
   */
  protected boolean errorOnServer() {
    return selenium.getHtmlSource().contains("An Error Occurred");
  }

  /**
   * Checks the page for the Tobago JavaScript Logging Framework and tests its severity.
   *
   * @return True if the severity level of the page is error
   * @throws com.thoughtworks.selenium.SeleniumException
   *          If the page is not a Tobago page, or any other problem with JavaScrpt or the page.
   */
  protected boolean isErrorOnPage() throws SeleniumException {
    String errorSeverity = selenium.getEval("window.LOG.getMaximumSeverity() >= window.LOG.ERROR");
    return Boolean.parseBoolean(errorSeverity);
  }

  protected String getErrors() throws SeleniumException {
    String errors = selenium.getEval("window.LOG.getMessages(window.LOG.INFO)");
    return errors;
  }

  protected String getHtmlSource() {
    return selenium.getHtmlSource();
  }

  protected static String createUrl(String page) {
    Assert.assertTrue("Page name must start with a slash.", page.startsWith("/"));
    if (CONTEXT_PATH.length() == 0) {
      return '/' + SERVLET_MAPPING + page;
    } else {
      return '/' + CONTEXT_PATH + '/' + SERVLET_MAPPING + page;
    }
  }
}
