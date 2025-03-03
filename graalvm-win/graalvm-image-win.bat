@echo off

set option=-H:+UnlockExperimentalVMOptions
set option=%option% -H:+ReportExceptionStackTraces
set option=%option% -H:-ParseRuntimeOptions
set option=%option% -H:+AddAllCharsets
set option=%option% -H:+TraceNativeToolUsage
set option=%option% -H:-CheckToolchain
set option=%option% --enable-http
set option=%option% --enable-https
set option=%option% --no-fallback
set option=%option% --report-unsupported-elements-at-runtime
set option=%option% --allow-incomplete-classpath
@REM set option=%option% --link-at-build-time


set option=%option% --initialize-at-build-time=wxdgaming.tailfn

:: build-time
set option=%option% --initialize-at-run-time=wxdgaming.tailfn


::<!--新增-->

::<!--trace 表示编译时 进行 跟踪，有些情况下可能会报错，比如在这里设置了A类，但是A类没有MAIN方法 会导致报错-->

set option=%option% --trace-class-initialization=com.sun.beans.introspect.ClassInfo
set option=%option% --trace-class-initialization=com.sun.beans.introspect.MethodInfo
set option=%option% --trace-class-initialization=com.sun.beans.TypeResolver
set option=%option% --trace-class-initialization=java.beans.Introspector
set option=%option% --trace-class-initialization=java.beans.ThreadGroupContext
set option=%option% --trace-class-initialization=wxdgaming.tailfn


set option=%option% --add-exports=java.base/java.nio=ALL-UNNAMED
set option=%option% --add-opens java.base/java.nio=ALL-UNNAMED
set option=%option% -Dlogback.configurationFile=logback.xml
set option=%option% -Dfile.encoding=UTF-8
set option=%option% -Dio.netty.tryReflectionSetAccessible=true
set option=%option% -H:NativeLinkerOption=prefs.lib
:: 关闭控制台窗口
set option=%option% -H:NativeLinkerOption=/SUBSYSTEM:WINDOWS
:: 关闭控制台窗口
set option=%option% -H:NativeLinkerOption=/ENTRY:mainCRTStartup

set option=%option% -H:ConfigurationFileDirectories=graalvm-win/config

call "D:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"
@REM call "d:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvarsamd64_x86.bat"

MD target\winfm

cd target

echo %option%
@REM C:\java\graalvm-jdk-21.0.6+8.1\\bin\\native-image.cmd %option% -H:ConfigurationFileDirectories=graalvm-win/config -jar target/tail-jar-with-dependencies.jar target/winfm/tail
C:\java\graalvm-community-openjdk-23.0.2+7.1\bin\native-image.cmd %option% -jar tail.jar winfm\tail