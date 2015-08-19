# _Fast_ JSON Input - Pentaho Data Integration Plugin

This is an alternate version of the "JSON Input" step that uses Jayway JsonPath
(https://github.com/jayway/JsonPath) instead of a parser based on JavaScript. It
is intended to be a drop-in replacement for the "JSON Input" step but should
be much faster and memory efficient.

## Authors:
- Etienne Dube - etdube (at) gmail (dot) com
- Jesse Adametz - jesse (at) graphiq (dot) com

## Build
To build (requires Apache Maven 3 or later) and install:

```shell
mvn package
```

The plugin will be placed under the `plugins/steps/FastJsonInput` subdirectory.
Simply copy the `FastJsonInput` directory to the `plugins/steps` directory in
your Pentaho Data Integration installation and launch Spoon. The new step
should be available under the "Experimental" category.
