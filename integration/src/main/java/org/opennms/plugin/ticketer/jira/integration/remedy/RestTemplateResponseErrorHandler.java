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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * RestTemplateResponseErrorHandler class which handles RestTemplate errors
 * @author Vinay Bhat
 *
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
				|| httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		String response = getResponseBody(httpResponse);
		if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			LOG.error("Internal Server error from API {}", response);
			throw new HttpClientErrorException(httpResponse.getStatusCode(), httpResponse.getStatusText());
		} else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

			// handle CLIENT_ERROR
			if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {

				LOG.error("Api not found {}", response);
			} else if (httpResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				LOG.error("Bad Oauth access token sent to Rapid incident-service: {}", response);

			} else if (httpResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				LOG.error("Bad Request to Rapid Incident-service api: {}", response);
			} else if (httpResponse.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED) {
				System.out.println(response);
			}
			throw new HttpClientErrorException(httpResponse.getStatusCode(), httpResponse.getStatusText());
		}
	}

	private String getResponseBody(ClientHttpResponse httpResponse) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getBody(), "UTF-8"));
		String line = bufferedReader.readLine();
		while (line != null) {
			response.append(line);
			response.append('\n');
			line = bufferedReader.readLine();
		}
		return response.toString();
	}

}
