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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.opennms.plugin.ticketer.jira.integration.remedy.bean.RemedyTemplateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * ResourceConfiguration class which initializes spring's rest template
 * 
 * @author Vinay Bhat
 *
 */
@org.springframework.context.annotation.Configuration
@EnableOAuth2Client
@EnableScheduling
public class ResourceConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceConfiguration.class);
	//static Configuration configuration;
	static List<String> templateList;
	static Map<String, String> urgencyMap = new HashMap<String, String>();
	static Map<String, String> impactMap = new HashMap<String, String>();

	static {}
	
	@PostConstruct
	public void populateConfiguredTemplates() {
		//String lane = configuration.getString(RemedyConstants.CONFIGURED_LANE);
		String lane = "Virtualization";
		String templateLane = RemedyConstants.CONFIGURED_TEMPLATES.concat(lane.toLowerCase());
		System.out.println("printing templateLane " + templateLane);
		String[] configuredTemplates= "No Template;FIR - Network LOR;FIR - Host lost connection;FIR - Storage Availability;FIR - Incident Hardware;First Instance Resolution".split(";");
		templateList = Arrays.asList(configuredTemplates);
		getUrgencyImpactData();
	}
	
	private void getUrgencyImpactData() {
		String [] urgencyList = "1-Critical:1000;2-High:2000;3-Medium:3000;4-Low:4000".split(";");
		String [] impactList = "1-Extensive/Widespread:1000;2-Significant/Large:2000;3-Moderate/Limited:3000;4-Minor/Localized:4000".split(";");
		
		for (String urgency : urgencyList) {
			String [] keyValue = urgency.split(":");
			urgencyMap.put(keyValue[1], keyValue[0]);
		}
		
		for (String impact : impactList) {
			String [] keyValue = impact.split(":");
			impactMap.put(keyValue[1], keyValue[0]);			
		}
		
	}

	@Bean
	public ClientCredentialsResourceDetails resource() {
		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri("https://rapid-staging.cerner.com:8243/token");
		resource.setClientId("6E7vfUfvSysn_p2eJOJf3pN4KGIa");
		resource.setClientSecret("2za7S9tGylCRjHr9VmegiztYMGsa");
		return resource;
	}

	@Bean
	public OAuth2RestTemplate restTemplate() {
		DefaultAccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
		OAuth2ClientContext auth2ClientContext = new DefaultOAuth2ClientContext(accessTokenRequest);
		OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resource(), auth2ClientContext);

		return oAuth2RestTemplate;	
	}

	// Schedule every 24 hours from the start of opennms boot up
	@Scheduled(fixedRate = 86400000)
	public void getTemplates() {
		/*
		 * try { LOG.info("Invoking template fetcher"); OAuth2RestTemplate restTemplate
		 * = restTemplate();
		 * 
		 * UriComponentsBuilder builder = UriComponentsBuilder
		 * .fromHttpUrl(configuration.getString(RemedyConstants.RAPID_TEMPLATE_QUERY_URI
		 * )) .queryParam("assignedGroup",
		 * configuration.getString(RemedyConstants.REMEDY_TARGET_GROUP).split(":")[0]);
		 * 
		 * 
		 * 
		 * HttpHeaders headers = getHeaders(); restTemplate.setErrorHandler(new
		 * RestTemplateResponseErrorHandler()); HttpEntity<Object> httpEntity = new
		 * HttpEntity<Object>(headers); ResponseEntity<String> response =
		 * restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity,
		 * String.class);
		 * 
		 * Gson gson = new Gson(); JsonElement content =
		 * gson.fromJson(response.getBody(), JsonElement.class); Type type = new
		 * TypeToken<List<RemedyTemplateDTO>>() {}.getType(); List<RemedyTemplateDTO>
		 * remedyTemplateDTOs = gson.fromJson(content.get, type);
		 * 
		 * System.out.println(remedyTemplateDTOs); //Properties properties =
		 * Vault.getProperties(); for (RemedyTemplateDTO remedyTemplateDTO :
		 * remedyTemplateDTOs) { if (remedyTemplateDTO != null &&
		 * templateList.contains(remedyTemplateDTO.getTemplateName().trim())) { String
		 * templateName = remedyTemplateDTO.getTemplateName();
		 * remedyTemplateDTO.setTemplateName(templateName.trim());
		 * 
		 * assignUrgencyAndImpact(remedyTemplateDTO);
		 * 
		 * String obj = new Gson().toJson(remedyTemplateDTO);
		 * properties.setProperty(remedyTemplateDTO.getTemplateName(), obj);
		 * System.out.println("templates printing "); System.out.println(obj); } }
		 * 
		 * //Vault.setProperties(properties);
		 * 
		 * }
		 * 
		 * catch (Exception e) { }
		 */}


	private void assignUrgencyAndImpact(RemedyTemplateDTO remedyTemplateDTO) {
		
		String urgency = urgencyMap.get(remedyTemplateDTO.getUrgency());
		remedyTemplateDTO.setUrgency(urgency);
		
		String impact = impactMap.get(remedyTemplateDTO.getImpact());
		remedyTemplateDTO.setImpact(impact);
		
	}

	/**
	 * @return HttpHeaders
	 */
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

}
