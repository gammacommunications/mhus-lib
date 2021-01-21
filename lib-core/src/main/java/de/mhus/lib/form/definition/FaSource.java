/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.form.definition;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.core.definition.DefComponent;
import de.mhus.lib.core.definition.IDefAttribute;
import de.mhus.lib.errors.MException;

public class FaSource extends MConfig implements IDefAttribute {

    private static final long serialVersionUID = 1L;
    private String tag;

    public FaSource(String tag, String name) {
        super(tag, null);
        this.tag = tag;
        setString("name", name);
    }

    @Override
    public void inject(DefComponent root) throws MException {
        IConfig sources = root.getObject("sources");
        if (sources == null) {
            sources = root.createObject("sources");
        }
        sources.setObject(tag, this);
    }
}
