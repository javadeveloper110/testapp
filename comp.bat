:start

call variables

call clear

call %AAPT_PATH% package -f -m -S %DEV_HOME%/res -J %DEV_HOME%/src -M %DEV_HOME%/AndroidManifest.xml -I %ANDROID_JAR%

call %JAVA_HOME%/bin/javac -d %DEV_HOME%/obj -cp %ANDROID_JAR% -sourcepath %DEV_HOME%/src %DEV_HOME%/src/%PACKAGE_PATH%/*.java

call %DX_PATH% --dex --output=%DEV_HOME%/bin/classes.dex %DEV_HOME%/obj

call %AAPT_PATH% package -f -M %DEV_HOME%/AndroidManifest.xml -S %DEV_HOME%/res -I %ANDROID_JAR% -F %DEV_HOME%/bin/AndroidTest.unsigned.apk %DEV_HOME%/bin

call %JAVA_HOME%/bin/keytool -genkey -validity 10000 -dname "CN=AndroidDebug, O=Android, C=US" -keystore %DEV_HOME%/AndroidTest.keystore -storepass android -keypass android -alias androiddebugkey -keyalg RSA -v -keysize 2048
call %JAVA_HOME%/bin/jarsigner -sigalg SHA1withRSA -digestalg SHA1 -keystore %DEV_HOME%/AndroidTest.keystore -storepass android -keypass android -signedjar %DEV_HOME%/bin/AndroidTest.signed.apk %DEV_HOME%/bin/AndroidTest.unsigned.apk androiddebugkey

call %ADB% uninstall %PACKAGE%
call %ADB% install %DEV_HOME%/bin/AndroidTest.signed.apk
call %ADB% shell am start %PACKAGE%/%PACKAGE%.%MAIN_CLASS%

pause

goto start