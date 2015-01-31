# results - web-based timing and scoring software for ski- and running races

Yep, ... ever-lasting project now in the github for everybody to blame. Let's see if publishing this will make me to complete this at some point. ... doubt it...

## Components

### results-model

JAXB annotated data transfer objects (DTOs) for the system. Prefers flat data model - i.e. no deep nesting hierarchies.

### results-data

Maps JAXB annotated JTOs to the database (by using dedicated JPA2.1 ORM mapping file). In addition, results-data provides data access objects (DAOs) for manipulating the results data trhough local java api.

The implementation is based on JPA (hibernate), and JDBC/DBMS (currently HSQLDB).

The latest enlightments hint that this component has only very minor role (in declaring the JPA entity-mappings XML and for providing specific @Converter classes). Instead, the results-rest will likely use JPA directly with the assistance of declarations in the results-data. And in production use, the results-server will supply the persistence-unit defintions (now we have them here and there, but only for testing purposes, which is pretty much acceptable - even a good approach).

### results-rest

REST api for manipulating results data remotely. This is obviously based on JAXB generated XML objects from results-model, and the local DAOs from results-data.

### results-ui

TBD: Will be the js/ajax -oriented application hosted by user's browser and
interacting with the REST apis in results-rest.

### results-server

A thin wrapper that binds everything into a single executable jar. Fundamentally results-server is a lightweight web- and REST service container (Jersey + Grizzly2, or something).

