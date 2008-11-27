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

package com.sf.ddao.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * psdo: finish implementation
 * psdo: write comments
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Oct 1, 2007
 * Time: 10:55:41 AM
 */
public class MethodSignatureHelper {
    private final Class<?> clazz;

    private MethodSignatureHelper(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static MethodSignatureHelper create(Class<?> clazz) throws IOException {
        String className = clazz.getSimpleName();
        String fName = className + ParameterNameExtractorAnnotationProcessor.METHOD_SIGNATURES_EXT;
        InputStream inputStream = clazz.getResourceAsStream(fName);
        Reader reader = new InputStreamReader(inputStream);
        return create(clazz, reader);
    }

    private static MethodSignatureHelper create(Class<?> clazz, Reader reader) throws IOException {
        MethodSignatureHelper signatureHelper = new MethodSignatureHelper(clazz);
        StringBuilder sb = new StringBuilder();
        String fName = null;
        List<String> typeList = new ArrayList<String>();
        List<String> nameList = new ArrayList<String>();
        int ch;
        while ((ch = reader.read()) != -1) {
            if (ch == '(') {
                fName = sb.toString();
            } else if (ch == ')') {
                nameList.add(sb.toString());
                signatureHelper.addMethodSignature(fName, typeList, nameList);
            } else {
                sb.append((char) ch);
                continue;
            }
            sb.delete(0, sb.length());
        }
        if (sb.length() > 0) {
            signatureHelper.addMethodSignature(sb.toString(), typeList, nameList);
        }
        return signatureHelper;
    }

    private void addMethodSignature(String funcName, List<String> typeList, List<String> nameList) {
        // psdo: implement this method
    }
}
