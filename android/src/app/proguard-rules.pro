# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\JPMoreto\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 10
-allowaccessmodification
-optimizations */*/*
# obfuscation will break reflection and
# make these rules much more complex
#-dontobfuscate

#-keep class com.androidplot.** { *; }
#-keep class android.support.design.widget.** { *; }
#-keep interface android.support.design.widget.** { *; }

#######################################################################################
#-keep class kotlin.internal.JRE8PlatformImplementations
#-keep class kotlin.internal.JRE7PlatformImplementations
#-keep class kotlin.reflect.jvm.internal.ReflectionFactoryImpl

# keep all ros classes with names
#-keep class org.ros.** { *; }
#-keepnames class org.ros.** { *; }
#-dontwarn org.ros.**

# keep ros messages (not part of the org.ros namespace)
#-keep class rosgraph_msgs.** { *; }
#-keep class sensor_msgs.** { *; }
#-keep class std_msgs.** { *; }
#-keep class tf2_msgs.** { *; }

#-dontwarn org.apache.**
#-dontwarn org.jboss.netty.**
#-dontwarn com.google.common.**
#-dontwarn org.xbill.**