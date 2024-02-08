**TransFlow** is an object-oriented framework for uploading your audios
(for example VK) to your device, in this case in MP3 format

The author was very inspired by the new feature - Fibers (Virtual Threads)
Hence the requirement jdk 21+
In this situation, you could get by with your own ForkJoinPool with asyncMode (FIFO)
In addition, virtual threads use FJP as a scheduler

This killer feature can kill the asynchronous programming paradigm


## Get access token:
https://id.vk.com/about/business/go/docs/ru/vkid/latest/oauth-vkontakte/implicit-flow-user

## Required dependencies
[**JDK**](https://www.oracle.com/java/technologies/downloads/) jdk 21+

## Config:
- access_token=Your access token
- playlist=ID of the playlist we want to download (any public)
- save_files_to=Save location

## Build
`mvn clean package`
