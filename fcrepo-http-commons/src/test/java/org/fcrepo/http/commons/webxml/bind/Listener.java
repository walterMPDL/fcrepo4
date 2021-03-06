/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.http.commons.webxml.bind;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://java.sun.com/xml/ns/javaee",
        name = "listener")
public class Listener extends Displayable {

    public Listener() {
    }

    public Listener(String displayName, String className) {
        this.displayName = displayName;
        this.className = className;
    }

    @XmlElement(namespace = "http://java.sun.com/xml/ns/javaee",
            name = "listener-class")
    String className;

    public String className() {
        return this.className;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Listener) {
            Listener that = (Listener) object;
            boolean className =
                    (this.className == null) ? that.className == null
                            : this.className.equals(that.className);
            boolean displayName =
                    (this.displayName == null) ? that.displayName == null
                            : this.displayName.equals(that.displayName);
            return className && displayName;
        }
        return false;
    }

}
