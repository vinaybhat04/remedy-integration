# Jira Ticketer Plugin

Jira Ticketer Plugin that can be installed and run on OpenNMS Karaf Container


## Getting Started

git clone https://github.com/cgorantla/jira-ticketer.git

### Prerequisites

Maven

Java8

Jira Service Desk

OpenNMS Horizon > 24.0.0

* Update /opt/opennms/opennms.properties to enable trouble ticketer.

```
opennms.ticketer.plugin=org.opennms.netmgt.ticketd.OSGiBasedTicketerPlugin
opennms.alarmTroubleTicketEnabled = true
```
* Update /opt/opennms/etc/jira.properties with specific jira server configuration


### Installing


```
git clone https://github.com/cgorantla/jira-ticketer.git
```

```
cd jira-ticketer
```

```
mvn install
```

### Deployment on OpenNMS


```
cp kar/target/opennms-jira-ticketer-plugin.kar ~/opt/opennms/deploy/
```


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Acknowledgments

* This code is a copy of jira ticketer from OpenNMS codebase.
* Modified to make use of OIA.
