# slider-view

[![](https://jitpack.io/v/kAvEh--/slider-view.svg)](https://jitpack.io/#kAvEh--/slider-view)

Four Progress Bar Implementation in Android:
- _Arc Progress Bar Slider
- _Deep Progress Bar
- _Ascending Progress Bar
- _Stepper Ascending Progress Bar

## Usage

1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
2. Add the dependency to app build.gradle
```groovy
dependencies {
        implementation 'com.github.kAvEh--:slider-view:Tag'
}
```
### Arc Progress Bar
1. Insert `ArcProgressView` widget in your layout.
```xml
<com.kaveh.sliderview.ArcProgressView
        android:id="@+id/arc_progress"
        android:layout_width="200dp"
        android:layout_height="80dp"
        android:layout_margin="30dp"
        android:padding="30dp" />
```
### Deep Progress Bar
1. Insert `DeepProgressView` widget in your layout.
```xml
<com.kaveh.sliderview.DeepProgressView
        android:id="@+id/deep_progress"
        android:layout_width="200dp"
        android:layout_height="80dp"
        android:layout_margin="30dp"
        android:padding="30dp" />
```
### Ascending Slider
1. Insert `StepperSlider` widget in your layout.
```xml
<com.kaveh.sliderview.StepperSlider
        android:id="@+id/stepper"
        android:layout_width="200dp"
        android:layout_height="80dp"
        android:layout_margin="30dp"
        android:padding="30dp" />
```
2. Set the stepper count for converting it to stepper slider
```kotlin
    findViewById<StepperSlider>(R.id.stepper).stepper = 5 //Number of slices in Slider
```

## Requirements
Android 4.4+ (API 19)

## Developed By
* Kaveh Fazaeli - <kaveh.fazaeli@gmail.com>

## License

    Copyright 2020 kAvEh--

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

