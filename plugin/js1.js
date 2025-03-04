JLog.print("开始执行热更");
JRuntime.sleep(1000);
JCmd.exec(false, "./", " curl https://www.baidu.com2");
var result = JHttp.get("https://www.baidu.com2");
JLog.print(result);
result = JHttp.get("https://www.baidu.com");
JLog.print(result);
JRuntime.sleep(1000);
JLog.print("热更执行结束");