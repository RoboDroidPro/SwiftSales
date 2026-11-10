# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve Room database entities and models from R8 code obfuscation
-keep class com.example.salestracker.data.model.** { *; }

# Preserve Kotlinx Serialization models & companion objects
-keepattributes *Annotation*,ElementValuePairs,Signature
-keepclassmembers class * {
    *** Companion;
}
