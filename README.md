# Java-Video-Chat
Java-Video Chat
- Author: Haoxuan Zhang
- Version: 1.0.1
- Date: 2018-12-31
- Abstruct: Java Video Chat

## Description
使用Java完成两台机器的视频聊天.</br>
此代码尚未完成, 目前只能在自己电脑上跑, 一个服务端, 一个客户端. (已解决, 可以在局域网内运行, 要求wifi稳定, 否则画面会卡顿)</br>
局域网内由于上传与下载速率不一致导致无法运行.(已经解决, 问题1: 一次传输的字节太少, 为1K, 设置为100K后无问题, 问题2: 设置为100K后不需要自己写握手程序, 直接发送即可)
目前进度: 已经实现在局域网内的视频聊天.
下一阶段: 用树莓派搭建服务器, 两个客户端, 一个服务器在局域网内运行. 

## Depend Libraries
- OpenCV (Version: 4.0.0)
