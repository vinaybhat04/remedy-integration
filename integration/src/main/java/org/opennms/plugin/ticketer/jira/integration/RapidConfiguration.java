package org.opennms.plugin.ticketer.jira.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "org.opennms.plugin.ticketer.jira.*" })
@PropertySource("classpath:rapid.properties")
public class RapidConfiguration {

	@Value("${remedy.serviceType}")
	private String serviceType;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	

}
