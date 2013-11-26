#LServer#

分为三个模块

* **LClient** 负责client端，记录客户端数据，加密压缩后传给服务端
* **LSdk** 供LClient端调用，提供打log的接口并加密压缩发送到服务端
* **LServer** 接收LClient发送过来的log,解压缩解密

###数据格式###
    // head
    {
        "dataType": "h",
        "uploadNum": "",
        "uploadTime": 3f,
        "persistedTime": 2f,
        "appKey": "",
        "appVersion": "",
        "appChannel": "",
        "sdkVersion": "",
        "deviceUdid": "",
        "deviceAdid": "",
        "devicePlatform": "",
        "deviceOs": "",
        "deviceOsVersion": "",
        "deviceModel": "",
        "deviceMacAddr": "",
        "deviceResolution": "",
        "deviceCarrier": "",
        "deviceNetwork": "wifi/cellular",
        "localeLanguage": "",
        "localeCountry": "",
    }
    //start
    {
        "dataType": "s",
        "sessionUuid": "",
        "sessionStartTime": 2f,
        "sessionNum": 2l,
        "sessionInterval": 2l,
        "latitude": 2.2f,
        "longitude": 1.2f
    }
    // regist
    {
        "dataType":"r",
        "sessionUuid": "1234-342423-232",
        "occurTime", 2l,
        "costTime":2l,
        "realName":"",
        "mail":"",
        "password":""
    }
    // event
    {
        "dataType":"e",
        "sessionUuid":"",
        "occurTime", 2l,
        "costTime":2l,
        "eventId":"",
        "userId": ""
        "latitude": 2.2f,
        "longitude": -2.2f,
        "attributes": {
            "key1": "value1",
            "key2": "value2"
         }
    }
    // message
    {
        "dataType": "m",
        "sessionUuid": "1234-342423-232",
        "messageType": "email",
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
    }
    // close
    {
        "dataType": "c",
        "sessionUuid": "",
        "sessionStartTime": 2f,
        "sessionCloseTime": 2f,
        "sessionTotalLength": 2l,
        "userId": ""
        "latitude": 2.2f,
        "longitude": 2.3f
    }

## version 0.1##
1. 实现基本功能，单发件邮箱工作，暂不考虑邮箱发邮件次数限制的问题。
2. 用户分二种模式:
        
        1，匿名：不保存其所发信息，不提供找回功能。
        2，注册用户：保存所发信息（直接再发一封到其注册邮件，服务端不保存）

### questions ###
* 考虑使用多邮箱工作，避免单邮箱发件次数限制问题
* 服务端保存用户已发消息
* 提供一个模块：tips(供用户给出线索让对方猜出自己)
* 加入微博，微信,qq等，不只是email.
* 