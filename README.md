ajaxproxy
=========

## Summary

Ajax Proxy was created to help simplify the development of ajax-based web applications. It provides a simple webserver that can be used by web and flash developers to allow them work without the burden of configuring and maintaining the real webserver that feeds them data.

## Feature

* Proxy requests from a given path to a different host at the same path
* Proxy multiple paths to multiple different hosts
* Use variable substitution for paths and host names
* Merge javascript/css files to a single url
* Compress merged javascript/css files
* UI can add fake latency and simulate a bitrate
* UI can log requests and filter by regular expression

## Running

Ajax Proxy can be run as a swing application by running the following command:
```
java -jar ajaxproxy-1234.jar
```

To run as a build tool to merge files and exit, then use the following:
```
java -jar ajaxproxy-1234.jar --c myconfigfile.json --m outputfolder
```

To see a full list of command line options try the following:
```
java -jar ajaxproxy-1234.jar --help
```


