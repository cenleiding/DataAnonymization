/*
 * ObjectSelector - Object selection library for Java
 * Copyright (C) 2013 - 2016 Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This package provides the public API for Object Selector.
 * <p>
 * <ul>
 * <li>The interface {@link IAccessor} can be implemented to enable selection of objects.</li>
 * <li>The interface {@link ICallback} can be implemented and passed to a {@link SelectorTokenizer}.</li>
 * <li>The class {@link Parser} parses expressions created with the builder.</li>
 * <li>The class {@link Selector}represents the actual selector.</li>
 * <li>The class {@link SelectorBuilder} implements a builder for selectors</li>
 * <li>The class {@link SelectorBuilderCallback} is a callback used by the builder when parsing query strings</li>
 * <li>The class {@link SelectorTokenizer} is a tokenizer for query strings</li>
 * </ul>
 * 
 * @author Fabian Prasser
 */
package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector;

