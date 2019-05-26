-injars C:\dev\netbeans\jSync\dist\jSync.jar
-outjars C:\dev\netbeans\jSync\dist\jSync_out.jar

-libraryjars 'C:\Program Files\Java\jdk1.6.0_24\jre\lib\rt.jar'
-libraryjars 'C:\Program Files\Java\jdk1.6.0_24\jre\lib\jce.jar'
-libraryjars 'C:\dev\netbeans\jSync\lib\'

-printseeds C:\dev\netbeans\jSync\proguard\seeds.txt
-printusage C:\dev\netbeans\jSync\proguard\usage.txt
-printmapping C:\dev\netbeans\jSync\proguard\mapping.txt

-overloadaggressively
-defaultpackage ''
-dontskipnonpubliclibraryclasses

-keep public class com.adlitteram.jsync.Main {
    public static void main(java.lang.String[]);
}
