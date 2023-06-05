@echo off
set p1=%1
set p2=%2
set p3=%3
rem throw the first parameter away
shift
shift
shift
set params=%1
:loop
shift
if [%1]==[] goto afterloop
set params=%params% %1
goto loop
:afterloop
echo on
java -cp "i:\program\lib\xalan-j_2_7_1\serializer.jar;i:\program\lib\xalan-j_2_7_1\xalan.jar;i:\program\lib\xalan-j_2_7_1\xercesImpl.jar;i:\program\lib\xalan-j_2_7_1\xml-apis.jar;i:\program\lib\xalan-j_2_7_1\xsltc.jar" org.apache.xalan.xslt.Process -IN %p1% -XSL %p2% -OUT %p3% %params%