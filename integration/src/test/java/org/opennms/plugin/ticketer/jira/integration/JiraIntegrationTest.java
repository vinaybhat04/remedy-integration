/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
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

import static junit.framework.TestCase.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennms.integration.api.v1.ticketing.Ticket;

public class JiraIntegrationTest {


    JiraTicketerPlugin m_ticketer;

    @Before
    public void setUp() throws Exception {
        final File opennmsHome = Paths.get("src", "test", "resources", "opennms-home").toFile();
        assertTrue(opennmsHome + " must exist.", opennmsHome.exists());
        System.setProperty("opennms.home", opennmsHome.getAbsolutePath());

        m_ticketer = new JiraTicketerPlugin();
    }

    @Test
    @Ignore
    public void getTicket() throws PluginException, InterruptedException {

        JiraTicketerPlugin plugin = new JiraTicketerPlugin();
        Ticket ticket = plugin.get("TRIAL-4");
        System.out.print(ticket.getSummary() + "\n");
    }
}
