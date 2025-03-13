@echo off
setlocal enabledelayedexpansion

:: Set the path to the pom.xml file
set "pomFile=pom.xml"

:: Check if the file exists
if not exist "%pomFile%" (
    echo The specified file does not exist: %pomFile%
    exit /b 1
)

:: Initialize variable to store the version value
set "versionValue="

:: Read pom.xml file and find the version tag
for /f "tokens=*" %%a in ('type "%pomFile%" ^| findstr /i "<version>"') do (
    set "line=%%a"
    :: Extract version number, remove XML tags and spaces
    set "line=!line:*<version>=!"
    set "versionValue=!line:</version>=!"
    set "versionValue=!versionValue: =!"
    :: Exit the loop after finding the first version number (this is the project version)
    goto foundVersion
)

:foundVersion
if not defined versionValue (
    echo Version number not found in pom.xml.
    exit /b 1
) else (
    echo Project version: %versionValue%
    :: Build jar file path
    set jarFile=target\util-%versionValue%.jar
    echo Jar file path: !jarFile!
    
    if not exist "!jarFile!" (
        echo The specified jar file does not exist: !jarFile!
        echo Trying to generate jar file by executing mvn clean install...
        :: Execute mvn clean install command
        call mvn clean install
        echo Checking if jar file exists after build...
        if not exist "!jarFile!" (
            echo After checking again, the jar file was not found: !jarFile!
            exit /b 1
        )
    )
    
    :: Build and store the mvn command in a variable
    set mvnCommand=mvn install:install-file -Dfile="!jarFile!" -DgroupId=github.ag777 -DartifactId=util -Dversion=!versionValue! -Dpackaging=jar
    
    :: Output the command that will be executed
    echo Command to execute:
    echo !mvnCommand!
    
    :: Ask the user whether to execute
    set /p Execute="Do you want to execute this command? (Y/N):"
    if /i "!Execute!"=="Y" (
        :: Execute the mvn command
        !mvnCommand!
    ) else (
        echo Execution cancelled.
    )
)

endlocal