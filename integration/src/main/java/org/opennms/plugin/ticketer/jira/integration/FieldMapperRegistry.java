/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.plugin.ticketer.jira.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.opennms.plugin.ticketer.jira.integration.fieldmapper.CascadingSelectFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.ComponentFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.DefaultFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.FieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.GroupFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.IssueTypeFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.LabelsFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.MultiSelectFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.NumberFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.PriorityFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.ProjectFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.SingleSelectFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.StringFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.UserFieldMapper;
import org.opennms.plugin.ticketer.jira.integration.fieldmapper.VersionFieldMapper;

import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

/**
 * The {@link FieldMapperRegistry} maps String input values to a JIRA ReST API representation.
 *
 * @see IssueInputBuilder#setFieldValue(String, Object)
 *
 * @author mvrueden
 */
public class FieldMapperRegistry {

    private final List<FieldMapper> fieldMapperList = new ArrayList<>();

    private final Map<String, String> alternativeOptionKeyLookupMap;

    public FieldMapperRegistry(Properties properties) {
        Objects.requireNonNull(properties);
        alternativeOptionKeyLookupMap = buildLookupMap(properties);

        final Supplier<Map<String, String>> lookupMapSupplier = () -> alternativeOptionKeyLookupMap;

        // Order matters, first entry which matches is used to do the mapping!
        fieldMapperList.add(new CascadingSelectFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new ComponentFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new GroupFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new IssueTypeFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new LabelsFieldMapper());
        fieldMapperList.add(new MultiSelectFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new NumberFieldMapper());
        fieldMapperList.add(new PriorityFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new ProjectFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new SingleSelectFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new StringFieldMapper());
        fieldMapperList.add(new UserFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new VersionFieldMapper(lookupMapSupplier));
        fieldMapperList.add(new DefaultFieldMapper());
    }

    /**
     * The jira.properties may contain properties to define the lookup strategy for fields.
     * Usually the default is to lookup a (custom) field by it's name. This can be overwritten, e.g.
     * jira.attributes.project.resolution=id to overwrite it's default key lookup to id lookup.
     *
     * @param properties
     * @return
     */
    protected static Map<String, String> buildLookupMap(Properties properties) {
        final Map<String, String> lookupMap = new HashMap<>();
        for (String eachKey : properties.stringPropertyNames()) {
            if (eachKey.length() > "jira.attributes.resolution".length() && eachKey.startsWith("jira.attributes.") && eachKey.endsWith("resolution")) {
                lookupMap.put(eachKey.substring("jira.attributes.".length(), eachKey.length() - "resolution".length() - 1), properties.getProperty(eachKey));
            }
        }
        return lookupMap;
    }

    /**
     * Returns the FieldMapper, which is registered with <code>schema</code>.
     *
     * @param schema The schema to lookup a {@link FieldMapper} for.
     * @return the FieldMapper, which is registered with <code>schema</code>.
     */
    public FieldMapper lookup(FieldSchema schema) {
        Optional<FieldMapper> firstMapper = fieldMapperList.stream().filter(f -> f.matches(schema)).findFirst();
        return firstMapper.get();
    }
}
