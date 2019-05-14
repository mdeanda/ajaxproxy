ajaxproxy
=========

## Summary

By running ajaxproxy you can simplify the development of applications that consume data from REST API services. It runs a webserver that will allow you to work without the burden of configuring and maintaining the real web application.

## Features

* Proxy requests from a given path to a different host at the same path
* Proxy multiple paths to multiple different hosts
* Use variable substitution for paths and host names
* Merge javascript/css files to a single url
* Compress merged javascript/css files
* UI can add fake latency and simulate a slow network connection
* UI can log requests and filter by regular expression

## Running

ajaxproxy can be run as a standalone desktop application by running the following command:
```
java -jar ajaxproxy-1234.jar
```

To run as a build tool to merge files and exit, then run the following:
```
java -jar ajaxproxy-1234.jar --c myconfigfile.json --m outputfolder
```

To see a full list of command line options run the following:
```
java -jar ajaxproxy-1234.jar --help
```


