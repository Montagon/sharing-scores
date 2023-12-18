# Sharing Scores with Dropbox

The aim of this project is to have a tool to automatize the process of generating an HTML code containing a music score, just ready to be shared in Wordpress.

This project has been desinged to be used together with the Wordpress Plugin: [Restricted User Access](https://es.wordpress.org/plugins/restrict-user-access/). 
More details in how to use in the [setup](#setup) section.

A score is composed by:
  - Title.
  - Link to the song of it.
  - A list of instruments with their corresponding Dropbox link.
  
## Setup

### Dropbox API

This project uses the [Google Cloud API for Java]([https://www.dropbox.com/developers/documentation/python](https://developers.google.com/drive/api/guides/about-sdk?hl=es-419)). 
You will need to create an application and setup the following environments variables:

```powershell
$env:AUTH_URI = "https://accounts.google.com/o/oauth2/auth"
$env:CLIENT_ID = "<CLIENT_ID>"
$env:CLIENT_SECRET = "<CLIENT_SECRET>"
$env:TOKEN_URI = "https://oauth2.googleapis.com/token"
```

### Restrict User Access

As mentioned before, this project has been designed to be used with the Wordpress Plugin [Restricted User Access](https://es.wordpress.org/plugins/restrict-user-access/).
Basically, the plugins offers the possibility to add access rules to the users, allowing them to see or not specific content by specifiying restrict-levels.

#### Config

The [instruments-config.json](src/main/resources/instruments-config.json) contains a JSON object that contains a list of items. Each item have the following properties:
- `name`. Represents the name that will be used to identify in the folder the correct file/folder to be shared.
- `restrict-level`. Represents the restrict level defined in the plugin that reffers to this item. It will be necessary to use the same values as defined in the plugin.
- `publishName`. Represents the name that will appear in the Wordpress entry.

## Run

```powershell
> ./gradlew sharing-scores
```
