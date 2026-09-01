# Windows Setup

Java 17 is required.

If IntelliJ has downloaded a JDK under `%USERPROFILE%\.jdks`, the included launcher attempts to find a Java 17 JDK automatically.

To set one manually for the current PowerShell session:

```powershell
$env:JAVA_HOME="C:\Users\<you>\.jdks\temurin-17.x.x"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
javac -version
```

Then build with:

```powershell
.\gradlew.bat clean build
```
