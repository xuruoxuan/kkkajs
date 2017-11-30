CircleDisplay
=============

Android View for displaying and selecting (by touch) values / percentages in a circle-shaped View, with animations.

Features
=======

**Core features:**
 - Displaying values in a beautiful circle shaped View
 - Supports percentage and normal values
 - Selecting / Choosing values with touch gestures (including callbacks)
 - Fully customizeable
 - Animated drawing (bar representig the value fills up animated)

<img src="https://raw.github.com/PhilJay/CircleDisplay/master/screenshots/circle_display.png" width="450">
![alt tag](https://raw.github.com/PhilJay/CircleDisplay/master/screenshots/circledisplay_demo.gif) 

Usage
=======

Simply **copy the CircleDisplay.java** file into your project. No annoying library imports, you **ONLY** need that single file.

For using the <code>CircleDisplay</code>, define it in .xml:
```xml
    <com.philjay.circledisplay.CircleDisplay
        android:id="@+id/circleDisplay"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
``` 
```java
    CircleDisplay cd = (CircleDisplay) findViewById(R.id.circleDisplay);
``` 

or create it in code:
```java
    CircleDisplay cd = new CircleDisplay(Context);
```   


**Style** your <code>CircleDisplay</code>, and **show values**.

**Styling methods:**
 - <code>setColor(int color)</code>: Use this method to set the color for the arc/bar that represents the value. You can either use <code>Color.COLORNAME</code> as a parameter or <code>getColor(resid)</code>.
 - <code>setStartAngle(float angle)</code>: Set the starting angle of your arc/bar. By default, it starts at the top of the view (270°).
 - <code>setAnimDuration(int millis)</code>: Set the duration in milliseconds it takes to animate/build up the bar.
 - <code>setTextSize(float size)</code>: Set the size of the text in the center of the view.
 - <code>setValueWidthPercent(float percentFromTotalWidth)</code>: Set the width of the value bar/arc in percent of the circle radius.
 - <code>setFormatDigits(int digits)</code>: Sets the number of digits to use for the value in the center of the view.
 - <code>setDimAlpha(int alpha)</code>: Value between 0 and 255 indicating the alpha value used for the remainder of the value-arc.
 - <code>setDrawText(boolean enabled)</code>: If enabled, center text (containing value and unit) is drawn.
 - <code>setPaint(int which, Paint p)</code>: Sets a new <code>Paint</code> object instead of the default one. Use <code>CircleDisplay.PAINT_TEXT</code> for example to change the text paint used.
 - <code>setUnit(String unit)</code>: Sets a unit that is displayed in the center of the view. E.g. "%" or "€" or whatever it is you want the circle-display to represent.
 - <code>setStepSize(float stepsize)</code>: Sets the stepsize (minimum selection interval) of the circle display,
default 1f. It is recommended to make this value not higher than 1/5 of the maximum selectable value, and not lower than 1/200 of the maximum selectable value. For example, if a maximum of 100 has been chosen, a stepsize between 0.5 and 20 is recommended.
 - <code>setCustomText(String[] custom)</code>: Sets an array of custom Strings to be drawn instead of the actual value in the center of the CircleDisplay. If set to null, the custom text will be reset and the value will be drawn. Make sure the length of the array corresponds with the maximum number of steps (maxvalue / stepsize).

**Showing stuff:**
 - <code>public void showValue(float toShow, float total, boolean animated)</code>: Shows the given value. A maximumvalue also needs to be provided. Set animated to true to animate the displaying of the value.

 
**Selecting values:**
 - **IMPORTANT** for selecting values <code>onTouch()</code>: Make sure to call <code>showValue(...)</code> at least once before trying to select values by touching. This is needed to set a maximum value that can be chosen on touch. Calling <code>showValue(0, 1000, false)</code> before touching as an example will allow the user to choose a value between 0 and 1000, default 0.
 - <code>setTouchEnabled(boolean enabled)</code>: Set this to true to allow touch-gestures / selecting.
 - <code>setSelectionListener(SelectionListener l)</code>: Set a <code>SelectionListener</code> for callbacks when selecting values with touch-gestures. 


**Full example:**
```java
    CircleDisplay cd = (CircleDisplay) findViewById(R.id.circleDisplay);
    cd.setAnimDuration(3000);
    cd.setValueWidthPercent(55f);
    cd.setTextSize(36f);
    cd.setColor(Color.GREEN);
    cd.setDrawText(true);
    cd.setDrawInnerCircle(true);
    cd.setFormatDigits(1);
    cd.setTouchEnabled(true);
    cd.setSelectionListener(this);
    cd.setUnit("%");
    cd.setStepSize(0.5f);
    // cd.setCustomText(...); // sets a custom array of text
    cd.showValue(75f, 100f, true);
``` 

License
=======
Copyright 2014 Philipp Jahoda

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
