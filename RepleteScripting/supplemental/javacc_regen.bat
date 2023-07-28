
@echo off

Rem IMPORTANT: Assumes the working directory is the resources
Rem directory in project root.
Rem Example: cd C:\Users\dtrumbo\work\eclipse-main\RepleteMath\resources

SET PROJ_ROOT=%~dp0

cd "%PROJ_ROOT%..\src\replete\scripting\rscript\parser\grammar"
java -classpath "%PROJ_ROOT%..\resources\javacc-5.0\javacc.jar" jjtree math.jjt
java -classpath "%PROJ_ROOT%..\resources\javacc-5.0\javacc.jar" javacc ..\gen\math.jj

cd "%PROJ_ROOT%"
