
@echo off

Rem IMPORTANT: Assumes the working directory is the resources
Rem directory in project root.
Rem Example: cd C:\Users\dtrumbo\work\eclipse-main\RepleteBash\resources

SET PROJ_ROOT=%~dp0

cd "%PROJ_ROOT%..\src\replete\bash\parser\grammar"
java -classpath "%PROJ_ROOT%..\resources\javacc-5.0\javacc.jar" jjtree bash.jjt
java -classpath "%PROJ_ROOT%..\resources\javacc-5.0\javacc.jar" javacc ..\gen\bash.jj

cd "%PROJ_ROOT%"
