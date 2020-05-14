/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2013-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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
package org.opennms.plugin.ticketer.jira.integration.remedy;

/**
 * RemedyConstants class which declares constant variables
 * @author Vinay Bhat
 *
 */
public interface RemedyConstants {
	String INCIDENT_STR = "incidentId";
	String SERVICE_TYPE = "remedy.serviceType";
	String IMPACT = "remedy.impact";
	String URGENCY = "remedy.urgency";
	String USER_COMMENT = "remedy.user.comment";
	String INCIDENT_TEMPLATE = "remedy.incident.template";
	String ASSIGNED_SUPPORT_COMPANY = "remedy.assignedSupportCompany";
	String INTEGRATION_ID = "remedy.integrationID";
	String REPORTED_SOURCE = "remedy.reportedSource";
	String CARE_IMPACT_REVIEW = "remedy.careImpactReview";
	String OPS_CATEGORY_TIER1 = "remedy.operationalCategorizationTier1";
	String PROD_CATEGORY_TIER1 = "remedy.productCategorizationTier1";
	String TARGET_DATE = "remedy.targetDate";
	String LINE_SEPARATOR = "line.separator";
	String RAPID_INCIDENT_URI = "remedy.rapid.incident.uri";
	String REMEDY_TARGET_GROUP = "remedy.targetgroups";
	String REMEDY_SUMMARY = "remedy.user.summary";
	String DEFAULT_FIR_TEMPLATE = "remedy.default.fir.template";
	String NO_TEMPLATE = "remedy.default.template";
	String PREFIX_FIR = "FIR : ";
	String OPENNMS_HOME = "opennms.home";
	String REMEDY_PROPERTY_NAME = "/etc/remedy.properties";
	String RAPID_PROPERTY_FILE_NAME = "/etc/opennms.properties.d/rapid.properties";
	String RAPID_ACCESS_TOKEN_URI = "remedy.rapid.accesstoken.uri";
	String RAPID_CLIENT_ID = "remedy.rapid.client.id";
	String RAPID_CLIENT_SECRET = "remedy.rapid.client.secret";
	String CONFIGURED_LANE = "opennms.instance.lane";
	String CONFIGURED_TEMPLATES = "remedy.templates.lane.";
	String RAPID_TEMPLATE_QUERY_URI = "remedy.rapid.template.query.uri";
	String RAPID_INCIDENT_QUERY_URI = "remedy.rapid.incident.query.uri";

}
