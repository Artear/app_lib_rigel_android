# Rigel Library

- An android library useful for common bottom navigation, making easier the launch of fragments, 
 and manipulation of back stack. 
 
 *Rigel is one of the main stars for sky navigation.*
 
Documentation
--------

This library provide a [NavigationActivity](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/NavigationActivity.kt).
 There is a simple AppCompatActivity that have a bottom navigation. 
 It works with a [NavigationProvider](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/NavigationProvider.kt) 
 and [NavigationSection](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/NavigationSection.kt) interface. 

The bottom navigation swap between [MainFragment](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/MainFragment.kt) 
witch act like a base fragment and the owner of your stack fragment child. 

The [NavigationSection](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/NavigationSection.kt)
provide a fragment for instantiate. The MainFragment has the responsibility of launch each child
and [NavigationActivity](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/NavigationActivity.kt) controls that back stack when user go back or switch in the bottom navigation.

Note that [NavigationActivity](https://github.com/Artear/app_lib_rigel_android/blob/master/rigel/src/main/java/com/artear/rigel/NavigationActivity.kt) 
have two navigation horizontal and vertical. 

See entire library documentation [here](https://artear.github.io/app_lib_rigel_android).

Download
--------

via Maven:
```xml
<dependency>
  <groupId>com.artear.rigel</groupId>
  <artifactId>rigel</artifactId>
  <version>0.0.10</version>
</dependency>
```
or Gradle:
```groovy
implementation 'com.artear.rigel:rigel:0.0.10'
```
Rigel Lib requires:

- Minimum Java 7 or Android 4.1.
- Artear ui view library 0.1.7 or higher 

License
=======

    Copyright 2019 Artear S.A.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
