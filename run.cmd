REM @echo off


set CLASSPATH=bin\;lib\javolution.jar;lib\Opus5.jar;lib\junit-4.4.jar;lib\Jama-1.0.2.jar;lib\xmlRPC\commons-logging-1.1.jar;lib\xmlRPC\ws-commons-util-1.0.2.jar;lib\xmlRPC\xmlrpc-client-3.1.jar;lib\xmlRPC\xmlrpc-common-3.1.jar;lib\xmlRPC\xmlrpc-server-3.1.jar;lib\jlink\JLink.jar;lib\commons\commons-math-2.2.jar


java -ms1254m -mx1254m -Ddata.dir=data/ -classpath "%CLASSPATH%" server.AlgorithmsServer
