CircleDisplay
=============

Android View for displaying values / percentages in a circle-shaped View, with animations.

Features
=======

**Core features:**
 - Displaying values in a beautiful circle shaped View
 - Supports percentage and normal values
 - Animated drawing (bar representig the value fills up animated)

![alt tag](https://raw.github.com/PhilJay/CircleDisplay/master/screenshots/demo.png) 

Usage
=======

Simply **copy the CircleDisplay.java** file into your project. 

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
 - <code>setColor(int color)</code>: Use this method to set the color for the arc/bar that represents the value.
 - <code>setStartAngle(float angle)</code>: Set the starting angle of your arc/bar. By default, it starts at the top of the view (270°).
 - <code>setAnimDuration(int millis)</code>: Set the duration in milliseconds it takes to animate/build up the bar.
 - <code>setTextSize(float size)</code>: Set the size of the text in the center of the view.
 - <code>setValueWidthPercent(float percentFromTotalWidth)</code>: Set the width of the value bar/arc in percent of the circle radius.


 **Showing values:**
 - <code>public void showValue(float toShow, float total, boolean animated)</code>: Shows the given value. A maximumvalue also needs to be provided. Set animated to true to animate the displaying of the value.
 - <code>public void showPercentage(float percentage, boolean animated)</code>: Shows the given percentage value. Set animated to true to animate the displaying of the value.

**Full example:**
```java
    CircleDisplay cd = (CircleDisplay) findViewById(R.id.circleDisplay);
    cd.setAnimDuration(3000);
    cd.setValueWidthPercent(55f);
    cd.setTextSize(36f);
    cd.setColor(Color.GREEN);
    cd.setDrawText(true);
    cd.setDrawInnerCircle(true);
    cd.showPercentage(75f, true);
``` 
