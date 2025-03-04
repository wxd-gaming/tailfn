JLog.print("开始执行热更");
JRuntime.sleep(1000);
JCmd.exec(false, "./", " curl https://www.baidu.com2");
JRuntime.sleep(1000);
JLog.print("热更执行结束");