package com.liu.msg;

import java.util.Map;

/*
 *      "dataType": "e",
        "sessionUuid": "1234-342423-232",
        "eventId": "email",
        "occurTime", 2l,
        "costTime":2l,
        "userId": ""
        "latitude": 2.2f,
        "longitude": -2.2f,
        "to":"...",
        "content":"...",
        "attributes": {
            "subject": "...",
            "key2": "value2"
         }
 */

public class Event {
	private String sessionUuid;
	private String eventId;
	private long occurTime;
	private long costTime;
	private String userId;
	private float latitude;
	private float longitude;
	private String to;
	private String content;
	private Map<String, String> attributes;
}
