# _Fast_ JSON Input - PDI Plugin

*NOTE* - this repository is *deprecated*. [Jesse Adametz](https://github.com/jadametz) and [James Ebentier](https://github.com/jebentier) from [Graphiq](https://www.graphiq.com/) picked up the code where I left it and improved on it: https://github.com/graphiq-data/pdi-fastjsoninput-plugin. Their fork is more likely to be actively maintained, we suggest that you have a look over there before using this version.

This is an alternate version of the "JSON Input" step that uses [Jayway JsonPath](https://github.com/jayway/JsonPath) instead of a parser based on JavaScript. It is intended to be a drop-in replacement for the "JSON Input" step but should be much faster and memory efficient.

## Features over PDI JSON Input

* [[PDI-10344](http://jira.pentaho.com/browse/PDI-10344)] Replaced JavaScript parsing engine with Jayway JsonPath
* [[PDI-10858](http://jira.pentaho.com/browse/PDI-10858)] Checkbox to "Remove source field from output stream"
* Checkbox to enable JsonPath's `DEFAULT_PATH_LEAF_TO_NULL` [option](https://github.com/jayway/JsonPath#options) which returns `null` for missing leafs:

```python
[
    {
        "name": "Jesse Adametz",
        "gender": "male"
    },
    {
        "name": "Etienne Dube"
    }
]
```

* Tests! There are currently 4 test cases which test the permutations of "Ignore missing path" and "Default path leaf to null"

## Development

### Build
To build (requires Apache Maven 3 or later):

```shell
mvn package
```

### Install

1. Simply create `install.properties` in the root directory with the following line:

    ```
    pdi.home=/path/to/local/data-integration
    ```
2. Then run

    ```shell
    mvn install
    ```

## Authors:
- [Etienne Dube](https://github.com/etdube) - etdube (at) gmail (dot) com
- [Jesse Adametz](https://github.com/jadametz) - jesse (at) graphiq (dot) com
- [James Ebentier](https://github.com/jebentier) - jebentier (at) graphiq (dot) com
