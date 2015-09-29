# _Fast_ JSON Input - Help

The Fast JSON Input step extracts relevant portions out of JSON structures, files or incoming fields, and outputs rows.

__Note:__ The following Help documentation has been ported over from Pentaho's [JSON Input](http://infocenter.pentaho.com/help/index.jsp?topic=%2Fpdi_user_guide%2Freference_step_json_input.html) wiki where relevant. Additional information for Fast JSON Input specific fields has been added in-line.

## File Tab

The __File__ tab is where you enter basic connection information for accessing a resource.

| Option                                 | Definition                                                                                |
|----------------------------------------|-------------------------------------------------------------------------------------------|
| Step name                              | Name of this step as it appears in the transformation workspace                           |
| Source is defined in a field           | Retrieves the source from a previously defined field                                      |
| Source is a filename                   | Indicates source is a filename                                                            |
| Read source as URL                     | Indicates a source should be accessed as a URL                                            |
| Get source from field                  | Indicates the field to retrieve a source from                                             |
| Remove source field from output stream | Removes the source field from the streams output                                          |
| File or directory                      | Indicates the location of the source if the source is not defined in a field              |
| Regular expression                     | All filenames that match this regular expression are selected if a directory is specified |
| Exclude regular expression             | All filenames that match this regular expression are selected if a directory is specified |
| Show filename                          | Displays the file names of the connected source                                           |

## Content Tab

The __Content__ tab enables you to configure which data to collect.

| Option                            | Definition                                                                                                                                       |
|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| Ignore empty file                 | When checked, indicates to skip empty files—when unchecked, instances of empty files causes the process fail and stop                            |
| Do not raise an error if no files | When unchecked, causes the transformation to fail when there is no file to process—then checked, avoids failure when there is no file to process |
| Ignore missing path               | When unchecked, causes the transformation to fail when the JSON path is missing—then checked, avoids failure when there is no JSON path          |
| Default path leaf to null         | When checked, JSON path leafs that do not exist will return as null                                                                              |
| Limit                             | Sets a limit on the number of records generated from the step when set greater than zero                                                         |
| Include filename in output        | Adds a string field with the filename in the result                                                                                              |
| Rownum in output                  | Adds an integer field with the row number in the result                                                                                          |
| Add filenames to result           | If checked, adds processed files to the result file list                                                                                         |

## Fields Tab

The __Fields__ tab displays field definitions to extract values from the JSON structure. This step uses [JSONPath](http://goessner.net/articles/JsonPath/) to extract fields from JSON structures.

## Additional Output Fields Tab

The __Additional output fields__ tab enables you to provide additional information about the file to process.
