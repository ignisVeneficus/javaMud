call xalan.bat "%1" graphml2bat.xsl "result/%~n1.bat" -TEXT -PARAM pathExec "i:\program\java\javaMud3\others\room-gen\graphml2room.bat" -PARAM pathSource "..\%~nx1" -PARAM path "%2" -PARAM withMap "%3"
cd result
call %~n1.bat
cd ..
rem call i:\prg\xalan-j_2_7_0\xalan.bat -IN "%1" -XSL ivb2ivb.xsl -OUT "tmp/%~n1.ivb"

