# Configuration Guide

## Summary

This SDK uses a class called "QSConfig" to store and manage configuration, read 
comments of public functions in ["QSConfig.scala"](https://github.com/cheerx/qingstor-sdk-scala/blob/master/src/main/scala/com/qingstor/sdk/config/QSConfig.scala) 
for details.

Except for Access Key, you can also configure the API endpoint for private cloud 
usage scenario. All available configurable items are listed in the default 
configuration file.

___Default Configuration File:___

```yaml
# QingStor services configuration

access_key_id: 'ACCESS_KEY_ID'
secret_access_key: 'SECRET_ACCESS_KEY'

host: 'qingstor.com'
port: 443
protocol: 'https'
connection_retries: 3

# Valid log levels are "debug", "info", "warn", "error", and "fatal".
log_level: 'warn'
```

## Usage

Just create an instance `QSConfig` with your API Access Key, and initialize services 
you need with `apply()` functions of the target service.

### Code Snippet

Create default configuration

```scala
val defaultConfig = QSConfig()
```

Create configuration from Access Key

```scala
val configuration = QSConfig("ACCESS_KEY_ID", "SECRET_ACCESS_KEY")
```

Load user configuration

```scala
val userConfig = QSConfig.loadUserConfig()
```

Load configuration from config file

```scala
val configFromFile = QSConfig.loadConfigFromFilepath("PATH/TO/FILE")
```

Change API endpoint

```scala
val moreConfiguration = QSConfig()

moreConfiguration.protocol("https")
moreConfiguration.host("api.private.com")
moreConfiguration.port(4433)
```
