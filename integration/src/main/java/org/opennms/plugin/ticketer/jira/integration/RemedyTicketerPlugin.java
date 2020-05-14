package org.opennms.plugin.ticketer.jira.integration;

import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.plist.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.opennms.integration.api.v1.ticketing.Ticket;
import org.opennms.integration.api.v1.ticketing.TicketingPlugin;
import org.opennms.plugin.ticketer.jira.integration.remedy.ApplicationContextProvider;
import org.opennms.plugin.ticketer.jira.integration.remedy.RemedyConstants;
import org.opennms.plugin.ticketer.jira.integration.remedy.ResourceConfiguration;
import org.opennms.plugin.ticketer.jira.integration.remedy.bean.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

@Service
public class RemedyTicketerPlugin implements TicketingPlugin {
	
	@Autowired
	private RapidConfiguration rapidConfiguration;
	
	@Autowired
	private ResourceConfiguration resourceConfiguration;
	
	private static final Logger LOG = LoggerFactory.getLogger(RemedyTicketerPlugin.class);
	static Configuration configuration;
	static {

		configuration = new PropertiesConfiguration();
		//configuration.

	}

	@Override
	public Ticket get(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String saveOrUpdate(Ticket ticket) {

		String ticketId = null;
		try {
			if ((ticket.getId() == null)) {
				ticketId = save(ticket);
			} else {
				update(ticket);
			}
		} catch (PluginException e) {

		}
		catch (ParseException e) {

		}
		return ticketId;
	}

	/**
	 * Method which calls RAPID Incident service api and creates ticket
	 * 
	 * @param ticket
	 * @throws PluginException
	 * @throws ParseException 
	 */
	private String save(Ticket ticket) throws PluginException, ParseException {
		
		ResourceConfiguration resourceConfig = getResourceConfig();
		resourceConfig.getTemplates();
		OAuth2RestTemplate restTemplate = resourceConfig.restTemplate();
		OAuth2RestTemplate auth2RestTemplate = resourceConfiguration.restTemplate();

		String serviceType = rapidConfiguration.getServiceType();
		System.out.println("Printing resource type");
		System.out.println(serviceType);
		return "INC123";

		/*
		 * ResourceConfiguration resourceConfig = getResourceConfig();
		 * resourceConfig.getTemplates(); OAuth2RestTemplate restTemplate =
		 * resourceConfig.restTemplate(); HttpHeaders headers = getHeaders(); Incident
		 * incident = getIncidentData(ticket, StatusType.NEW.getStatus(),
		 * configuration);
		 * 
		 * // Set error handler for OAuth2RestTemplate restTemplate.setErrorHandler(new
		 * RestTemplateResponseErrorHandler()); HttpEntity<Incident> httpEntity = new
		 * HttpEntity<>(incident, headers); ResponseEntity<String> response =
		 * restTemplate.exchange(
		 * configuration.getString(RemedyConstants.RAPID_INCIDENT_URI), HttpMethod.POST,
		 * httpEntity, String.class); JSONParser jsonParser = new JSONParser();
		 * JSONObject jsonObject = null; try { jsonObject = (JSONObject)
		 * jsonParser.parse(response.getBody()); } catch
		 * (org.json.simple.parser.ParseException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } String incidentId = (String)
		 * jsonObject.get(RemedyConstants.INCIDENT_STR);
		 * LOG.debug("created new remedy ticket with reported incident number: {}",
		 * incidentId);
		 * 
		 * 
		 * 
		 * //ticket.setId(incidentId); LOG.error("Karaf ticketer plugin :{} ",
		 * ticket.getAlarmId()); return incidentId;
		 */
	}

	/**
	 * Method which updates the ticket
	 * 
	 * @param ticket
	 * @throws PluginException
	 */
	private void update(Ticket ticket) throws PluginException {

	}

	/*
	 * Get Singleton instance of ResourceConfiguration
	 */
	private ResourceConfiguration getResourceConfig() {
		ApplicationContext applicationContext = ApplicationContextProvider.getContext();
		return applicationContext.getBean("resourceConfiguration", ResourceConfiguration.class);

	}

	/**
	 * @return HttpHeaders
	 */
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/*
	 * Get Incident object from Ticket obj
	 * 
	 * @param ticket
	 * 
	 * @param incidentStatus
	 * 
	 * @param configuration
	 * 
	 * @return
	 */
	protected Incident getIncidentData(Ticket ticket, String incidentStatus, Configuration configuration) {

		Map<String, String> attributes = ticket.getAttributes();
		String impact = attributes.get(RemedyConstants.IMPACT);
		String incidentType = configuration.getString(RemedyConstants.SERVICE_TYPE);
		String urgency = attributes.get(RemedyConstants.URGENCY);
		// String summary = fetchSummary(ticket.getSummary());
		String summary = attributes.get(RemedyConstants.REMEDY_SUMMARY);
		String notes = fetchNotes(attributes.get(RemedyConstants.USER_COMMENT), ticket.getDetails());
		String incidentTemplate = attributes.get(RemedyConstants.INCIDENT_TEMPLATE);
		Incident incident = new Incident(impact, incidentStatus, incidentType, urgency);

		// Setting incidentContactID, impersonateUserAs, incidentRequestorID
		// incident.addUser("dp044946");
		incident.addUser(ticket.getUser());

		String targetGroup[] = new String[2];

		targetGroup = configuration.getString(RemedyConstants.REMEDY_TARGET_GROUP).split(":");
		incident.setAssignedGroup(targetGroup[0]);

		if (targetGroup.length != 1)
			incident.setAssigneeLoginId(targetGroup[1]);
		incident.setAssignedSupportCompany(configuration.getString(RemedyConstants.ASSIGNED_SUPPORT_COMPANY));
		incident.setIntegrationID(configuration.getString(RemedyConstants.INTEGRATION_ID));

		// Assignment of ticket data based on template
		assignTemplateData(incident, configuration, incidentTemplate, notes, summary);

		incident.setReportedSource(configuration.getString(RemedyConstants.REPORTED_SOURCE));
		incident.setCareImpactReview(configuration.getString(RemedyConstants.CARE_IMPACT_REVIEW));

		return incident;
	}

	/**
	 * Method which assigns ticket data based on template
	 * 
	 * @param incident
	 * @param configuration
	 * @param incidentRequestTemplate
	 * @param notes
	 * @param summary
	 */
	protected void assignTemplateData(Incident incident, Configuration configuration, String incidentRequestTemplate,
			String notes, String summary) {

		String defaultFirTemplate = configuration.getString(RemedyConstants.DEFAULT_FIR_TEMPLATE);
		String noTemplate = configuration.getString(RemedyConstants.NO_TEMPLATE);

		if (StringUtils.isNotBlank(incidentRequestTemplate)
				&& incidentRequestTemplate.equalsIgnoreCase(defaultFirTemplate)) {
			incident.setSummary(RemedyConstants.PREFIX_FIR.concat(summary));
			incident.setNotes(RemedyConstants.PREFIX_FIR.concat(notes));
			incident.setTemplate(incidentRequestTemplate);
		} else if (StringUtils.isNotBlank(incidentRequestTemplate)
				&& incidentRequestTemplate.equalsIgnoreCase(noTemplate)) {
			incident.setSummary(summary);
			incident.setNotes(notes);
			incident.setOperationalCategorizationTier1(configuration.getString(RemedyConstants.OPS_CATEGORY_TIER1));
			incident.setProductCategorizationTier1(configuration.getString(RemedyConstants.PROD_CATEGORY_TIER1));

		} else {
			incident.setTemplate(incidentRequestTemplate);
		}

	}

	/**
	 * Method which fetchs notes F
	 * 
	 * @param userComment
	 * @param ticketDetails
	 * @return
	 */
	private String fetchNotes(String userComment, String ticketDetails) {

		String comment = "";
		String details = "";
		if (StringUtils.isNotBlank(userComment) && StringUtils.isNotBlank(ticketDetails)) {
			details = ticketDetails.replaceAll("\\<.*?\\>", "");
			comment = userComment.concat(System.getProperty(RemedyConstants.LINE_SEPARATOR));
		}

		return new StringBuilder(comment).append(System.getProperty(RemedyConstants.LINE_SEPARATOR)).append(details)
				.toString();
	}

}
