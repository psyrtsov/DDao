/**
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.syrtsov.ddao.handler;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.*;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.syrtsov.ddao.JNDIDao;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Pavel Syrtsov
 * Date: Sep 29, 2007
 * Time: 10:47:21 PM
 */
public class ParameterNameExtractorAnnotationProcessor implements AnnotationProcessor {
    public static final String METHOD_SIGNATURES_EXT = ".methodSignatures";
    private final AnnotationProcessorEnvironment env;

    @SuppressWarnings({"UnusedDeclaration"})
    public ParameterNameExtractorAnnotationProcessor(Set<AnnotationTypeDeclaration> annotationTypeDeclarations, AnnotationProcessorEnvironment env) {
        this.env = env;
    }

    public void process() {
        try {
            AnnotationTypeDeclaration daoAnnotationDecl = (AnnotationTypeDeclaration) env.getTypeDeclaration(JNDIDao.class.getName());
            for (Declaration decl : env.getDeclarationsAnnotatedWith(daoAnnotationDecl)) {
                TypeDeclaration typeDecl = (TypeDeclaration) decl;
                File file = new File(getFileName(typeDecl));
                PrintWriter printWriter = env.getFiler().createTextFile(Filer.Location.CLASS_TREE, typeDecl.getPackage().getQualifiedName(), file, "utf8");
                typeDecl.accept(DeclarationVisitors.getDeclarationScanner(new ParamterNameExtarctionVisitor(printWriter), DeclarationVisitors.NO_OP));
            }
        } catch (IOException e) {
            env.getMessager().printError(e.toString());
        }
    }

    private String getFileName(TypeDeclaration typeDecl) {
        return typeDecl.getSimpleName() + METHOD_SIGNATURES_EXT;
    }

    public class ParamterNameExtarctionVisitor extends SimpleDeclarationVisitor {
        private final PrintWriter printWriter;

        public ParamterNameExtarctionVisitor(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
            Collection<? extends MethodDeclaration> methods = typeDeclaration.getMethods();
            for (MethodDeclaration method : methods) {
                Collection<ParameterDeclaration> parameters = method.getParameters();
                printWriter.append(method.getSimpleName()).append('(');
                for (ParameterDeclaration parameter : parameters) {
                    printWriter.append(parameter.getType().toString()).append(' ').append(parameter.getSimpleName());
                }
            }
            printWriter.append(")\n");
            printWriter.close();
        }
    }
}
