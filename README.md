#LServer#

分为三个模块

* **LClient** 负责client端，记录客户端数据，加密压缩后传给服务端
* **LSdk** 供LClient端调用，提供打log的接口并加密压缩发送到服务端
* **LServer** 接收LClient发送过来的log,解压缩解密
