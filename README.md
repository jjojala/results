# results - web-based timing and scoring software for ski- and running races

Yep, ... ever-lasting project now in the github for everybody to blame. Let's see if publishing this will make me to complete this at some point. ... doubt it...

## Building and running

After getting the sources from git, let's say to /results:
```
# cd <dir>/results
# mvn install
```

(The compilation likely requires JDK7)

Once built, likely the most easiest way to get it running is to do as I do,
as I constantly use that approach while I'am developing it. For that,
I use NetBeans. From netbeans I'll  open the project results-server,
and then right-click Main.java and choose 'Run File'. 

Alternatively it should run fine from the command line by typing:
```
# java -jar <dir>/results/server/target/results-server-1.0.0-SNAPSHOT.jar
```

(or whatever the present version is). This trick is however rarely used if
at all (so far), but eventually this will be the way to use it.

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

Single page javascript application that retrieves the raw data from the
"backend" by using AJAX and rest. During the build-phase, result-ui will
also aggregate bunch of javascript libraries wrapped into the jars as
dependencies. These files can be then retrieved from the HTTP server
(results-server).

In this application, results-ui (the frontend) is doing a lot. The backend
is relatively dummy engine for storing and retrieving related data once somehow figured out. The front-end on the other hand just reads that data, makes
something clever with it (like add or remove stuff, order it in appropriate
way, calculate results etc)... and finally dump the results back to
backend for storing. This approach keeps things pretty clear - on both
ends in fact, and reduces the unnecessary interactions and the backend load
too (one example of "edge computing" approach).

### results-server

A thin wrapper that binds everything into a single executable jar. Fundamentally results-server is a lightweight web- and REST service container (Jersey + Grizzly2, or something). Fundamentally results-server hosts two kind of stuff:
a REST service for playing around with the results's data, and bunch of static
files that set up the browser side application (results-ui).

