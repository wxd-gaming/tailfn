@echo off
set option=-agentlib:native-image-agent=config-merge-dir=../graalvm-win/config
set option=%option% -Dfile.encoding=UTF-8
set option=%option% -Dbuild.graalvm=true
cd target
C:\java\graalvm-community-openjdk-23.0.2+7.1\bin\java %option% -jar tail.jar