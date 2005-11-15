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
package org.apache.myfaces.tobago.apt;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitors;
import org.apache.commons.io.IOUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;


/**
 * Created: Apr 27, 2005 4:37:16 PM
 * User: bommel
 * $Id: TobagoAnnotationProcessor.java,v 1.1 2005/05/11 15:20:34 bommel Exp $
 */
public class TobagoAnnotationProcessor implements AnnotationProcessor {

  protected final AnnotationProcessorEnvironment env;
  protected final Set<AnnotationTypeDeclaration> atds;

  public TobagoAnnotationProcessor(Set<AnnotationTypeDeclaration> atds,
      AnnotationProcessorEnvironment env) {
    this.atds = atds;
    this.env = env;
    this.env.getMessager().printNotice("Starting annotation process");

  }

  public void process() {
    TobagoAnnotationVisitor visitor = new TobagoAnnotationVisitor(env);

    for (AnnotationTypeDeclaration atd : atds) {
      env.getMessager().printNotice("Collecting annotation "+atd);
      Collection<Declaration> decls = env.getDeclarationsAnnotatedWith(atd);
      for (Declaration decl : decls) {
        decl.accept(DeclarationVisitors.getDeclarationScanner(visitor, DeclarationVisitors.NO_OP));
      }
    }
    PrintWriter writer = null;
    try {
      for (DocumentAndFileName documentAndFileName: visitor.createDom()) {
        env.getMessager().printNotice("Create DOM");
        String fileName =
            documentAndFileName.getFileName().substring(0, documentAndFileName.getFileName().length()-3)+"xml";

        writer = env.getFiler().createTextFile(Filer.Location.SOURCE_TREE,
                documentAndFileName.getPackageName(), new File(fileName), null);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        // TODO transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
        //   "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN");
        // TODO transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
        //   "http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(documentAndFileName.getDocument()), new StreamResult(writer));
        env.getMessager().printNotice("Write to file " +documentAndFileName.getPackageName()+ "."+fileName);
        IOUtils.closeQuietly(writer);
      }
    } catch (ParserConfigurationException e) {
      // TODO
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

}
