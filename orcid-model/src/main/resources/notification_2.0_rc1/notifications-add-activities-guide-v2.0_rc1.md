
#ORCID API v2.0_rc1 Notifications Add Activities Guide
Starting in v2.0_rc1, the ORCID API supports new functionality to enable member organizations to add permission requests to a user's ORCID Inbox. These requests provide a "snapshot" example of the type of activities that will be added to the user's ORCID record as a result of granting the permission.

_**User-friendly implementation detail**: Several fields described below are displayed directly to the end user. The ORCID user interface is currently available in 10+ languages, and emails sent to the user are also presented in the user's language of choice. The language preference of the user is available via the ORCID API, and is always public. We strongly recommend that you read and consider the user's language when providing messages to them, providing user-displayed fields in their preferred language when feasible._

##Notifications Add Activities XML
XML for the ```ADD_ACTIVITIES``` notifications follows the [notification-add-activities-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/notification_2.0_rc1/notification-add-activities-2.0_rc1.xsd) and consists of the following sections:

- **notification:notification-type**: The type of notification - for this type of notification, the value is always ```ADD_ACTIVITIES```. 

- **notification:activity-type**: DISPLAYED TO END USER. A plain text value for describing to the user the type of items that you'll plan to add to his/her record once permission is granted. This value should be short (fewer than 25 characters), and can be a bit more descriptive, for example, "your recent publications", or "validated affiliations", or it can simply mirror the value used for ```notification: activity-type``` below (education, employment, etc. You should assume that this text will be used in the middle of a sentence _(for example, the message subject line of "Add &lt;notification:activity-type&gt; to your ORCID record")_, so you should use discretion on capitalization and length.

- **notification:intro-message**: DISPLAYED TO END USER. A short message that will be included in the inbox message to the end-user. This message will be presented before the list of activities that you have included in the message. Your intro message should not exceed 1000 characters.

- **notification:authorization-url**: This field is the URL string that you would call if you were asking for the permission from the user from your own application. It is formatted as a URL string, using the format found for the call [GET OAUTH/AUTHORIZE](http://members.orcid.org/api/get-oauthauthorize). _Note: one may specify only the URL path if desired. This approach is useful if you want to use identical code in multiple environments, for example on the ORCID Sandbox and Production environments._

**notification:activities block**

Consists of one or more ```notification:activity``` elements, which contain the following sub-elements:

- **notification:activity-type**: The type of ORCID activity represented by this ```activity``` element. Chosen from the following: ```EDUCATION```, ```EMPLOYMENT```, ```FUNDING```, or ```WORK```.

- **notification:activity-name**: DISPLAYED TO END USER. The name that should be shown to the end user to describe the activity. This field is usually the name of the activity that might be added once permission is granted (for example, the title of an article), but it also could include other identifying information, for example, _"Article Title (as published in journal ABC)"_, where "(as published in journal ABC)" is additional information that the client application would like to present to the user.

- **notification:external-identifier**: DISPLAYED TO END USER. An external identifier for the activity. While this field is not required, it is very helpful information to provide to the end user, as it distinguishes the activity from others that may be similar. Note that, when adding the activity to the ORCID record, at least one external identifier is required, even if an internal reference identifier is used for this purpose. 

For an example XML file, see [notification-add-activities-2.0_rc1.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/notification_2.0_rc1/samples/notification-add-activities-2.0_rc1.xml)

***Note:*** *Sample files contain system-generated elements/attributes that are returned when reading items from ORCID. The following items should not be included when posting items to ORCID. These fields will be present when reading notifications using this API:*

- **put-code** - ORCID_internal identifier for this notification. _(exception: you must include the put-code when updating items using the PUT method to update an item previously added)_
- **source** - the system will include your client application as the source
- **created-date** - date that you created this notification; automatically assigned by the system
- **sent-date** - date that the notification was sent to the end user from the inbox _(note that usually inbox messages are sent in batch to the user at an interval that the user specifies.)_
- **read-date** - date that the user read your notification in the ORCID Inbox, OR took action on the notification sent through the users email
- **archived-date** - date that the user archived your notification


##Notifications Add Activities Reference
```ADD_ACTIVITIES``` notifications are available only in ORCID API v2.0_rcX, which uses a slightly different data structure from previous API versions. 

In v2.0_rcX, items are read, added, and modified on an individual basis (rather than as a list), using a ```put-code```, which is a system-generated identifier used within the ORCID database.

The ```put-code``` for a specific item can be obtained by reading notifications.

Other notable differences between v2.0_rcX previous versions include:

- An explicit ```DELETE``` method is used to remove record items

###Request a notification access token

Authorized client applications may obtain a notification access token from the ORCID server (using a 2-legged, or "client_credentials" OAuth flow) as described below. If your client is not yet authorized, please [contact support](http://orcid.org/help/contact-us) to request this feature to be turned on for your client. 

_Please note that ORCID reserves the right to limit the client applications with authorization to send messages directly to ORCID iD holders using the ORCID Inbox._

**Resource URL**

| Environment | Resource URL |
| ----------- | ------------ |
|Developers Sandbox | https://api.sandbox.orcid.org/oauth/token |
| Production Registry | https://api.orcid.org/oauth/token |

**Paramaters**

| Field | Value|
| ---- | ---- |
| client_id<br/>*required* | The client id value &lt;client-id&gt; from ORCID client application registration |
| client_secret<br/>*required* | The client secret value &lt;client-secret&gt; from ORCID client application registration |
| scope<br/>*required* | The authorization scope for the requested access token. Value must be ```/premium-notification``` |
| grant_type<br/>*required* | Specifies the authorization mechanism for the granting the access token. Value for this call must be ```client_credentials``` |

**Header**

| Field | Value|
| ---- | ---- |
| Accept<br/>_required_ | The format for the call response. Must be ```Accept: application/json``` |

###REST API for notifications
| Action                   | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Add a notification | POST | /premium-notification | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/add-activities |
| Read a notification | GET | /premium-notification | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/add-activities/[PUT-CODE] |
| Flag an unread notification as archived | POST | /premium-notification | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/add-activities/[PUT-CODE]/archive |

- **[ORCID]** is the ORCID iD for the record, formatted as XXXX-XXXX-XXXX-XXXX
- **[PUT-CODE]** is the ```put-code``` attribute for the specific ```notification``` that you wish to read or modify.

###Example cURL Statements
####Request a notification access token
```
curl -i -L -H 'Accept: application/json' \
	-d 'client_id=APP-...' \
	-d 'client_secret=...' \
	-d 'scope=/premium-notification' \
	-d 'grant_type=client_credentials' \
	'http://api.sandbox.orcid.org/oauth/token'
```

####Add a notification
```
curl -i -H 'Authorization: Bearer ...' \
	-H 'Content-Type: application/orcid+xml' \
	-X POST -d '@[FILE-PATH]/notification-add-activities.xml' \
	https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/notifications/add-activities
```

####Read a notification
```
curl -i -H 'Authorization: Bearer ...' \
	-H 'Content-Type: application/orcid+xml' \
	https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/notifications/add-activities/[PUT-CODE]
```

####Flag an unread notification as archived
```
curl -i -H 'Authorization: Bearer ...' \
	-H 'Content-Type: application/orcid+xml' \
	-X POST https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/notifications/add-activities/[PUT-CODE]/archive 
```