
@ECHO OFF

Rem Usage: addsource.bat path\to\JAR\file.jar path\to\source\dir
Rem 
Rem This script is used to add Java source code to a Java JAR file
Rem that only includes compiled .class files.  This is merely a
Rem helpful but optional tool.  In Eclipse, if you start debugging
Rem down into one of libraries contained inside of these .class-only
Rem JARs, Eclipse will not be able to show you the source code, 
Rem but rather some near-useless bytecode visualization.  In Eclipse
Rem you can solve this in either one of two ways: 1) "Attach" the 
Rem source code or 2) have the .java files already inside the JAR file.
Rem Attaching the source code in Eclipse is a great feature, but it
Rem still is only a local setting.  Everyone else on the team has
Rem to download and attach their own source code for every JAR.  By
Rem simply including the source in the JAR file, and checking that
Rem augmented JAR file back into source control, everyone will have
Rem the same source-included JAR and Eclipse will display the source
Rem file instead of the bytecode view when debugging takes you into
Rem those classes.  Note: This script was written and tested for
Rem cmd.exe on Windows 7. -dtrumbo

set WASERROR=0

set argC=0
for %%x in (%*) do Set /A argC+=1
if NOT %argC%==2 (
    echo Invalid arguments.
    echo Usage: addsource.bat path\to\JAR\file.jar path\to\source\dir
    echo [Error]
    goto EndOfScript
)

set AS_JARFILE=%~f1
set AS_SRCDIR=%~f2
set AS_TEMPDIR=.\temp-add-source
set AS_NEWNAME=%~n1-with-src%~x1

echo 1. Creating temp directory.
mkdir %AS_TEMPDIR% 2> nul
cd %AS_TEMPDIR%

echo 2. Extracting JAR contents.
jar xf %AS_JARFILE%

if %ERRORLEVEL% NEQ 0 (
    set WASERROR=1
    goto EndOfScript
)

echo 3. Copying source files.
xcopy /S /Y /Q %AS_SRCDIR% . > nul

if %ERRORLEVEL% NEQ 0 (
    set WASERROR=1
    goto EndOfScript
)

Rem I'd like this tool to ONLY copy .java files, 
Rem but unfortunately xcopy doesn't natively support
Rem that kind of filtering.  That leaves a recursive
Rem file-by-file copy loop, but the logic to do so
Rem eludes me at this moment.
Rem for /R %AS_SRCDIR% %%A in (*.java) do (
Rem     echo %%~A
Rem )

echo 4. Creating new JAR file.
jar cf ..\%AS_NEWNAME% *

if %ERRORLEVEL% NEQ 0 (
    set WASERROR=1
    goto EndOfScript
)

echo 5. Removing temp directory.
cd ..
rmdir /S /Q %AS_TEMPDIR%
echo [Done]

:EndOfScript

if %WASERROR%==1 (
    echo Error occurred, exiting script...
    cd ..
    rmdir /S /Q %AS_TEMPDIR%
    echo [Error]
)
