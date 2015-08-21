# _Fast_ JSON Input - Pentaho Data Integration Plugin

This is an alternate version of the "JSON Input" step that uses Jayway JsonPath
(https://github.com/jayway/JsonPath) instead of a parser based on JavaScript. It
is intended to be a drop-in replacement for the "JSON Input" step but should
be much faster and memory efficient.

## Authors:
- Etienne Dube - etdube (at) gmail (dot) com
- Jesse Adametz - jesse (at) graphiq (dot) com
- James Ebentier - jebentier (at) graphiq (dot) com

## Build
To build (requires Apache Maven 3 or later) and install:

```shell
mvn package
```

## Install
Simply create `install.properties` in the root directory and put the following line:
```
pdi.home=/path/to/local/data-integration
```
Then run
```shell
mvn install
```