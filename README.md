# PictureAttachment Plugin for Cordova/PhoneGap 3.0 (iOS and Android)


## Installation

1) Make sure that you have [Node](http://nodejs.org/) and [Cordova CLI](https://github.com/apache/cordova-cli) or [PhoneGap's CLI](https://github.com/mwbrooks/phonegap-cli) installed on your machine.

2) Add a plugin to your project using Cordova CLI:

```bash
cordova plugin add https://github.com/VitaliiBlagodir/cordova-plugin-datepicker
```
Or using PhoneGap CLI:

```bash
phonegap local plugin add https://github.com/VitaliiBlagodir/cordova-plugin-datepicker
```

## Usage

```js
var date = new Date().getTime();
var minDate = new Date('November 18, 2014 11:15:00').getTime();
var maxDate = new Date('March 25, 2015 23:55:00').getTime();var minuteInterval = 5;
var positiveButtonText = 'Ok';
var negativeButtonText = 'Annuleer';
var setDateTitle = 'Datum instellen';
var setTimeTitle = 'Tijd instellen';
            
var options = {
	mode: mode,
    date: date,
    minDate: minDate,
    maxDate: maxDate,
    minuteInterval: minuteInterval,
    positiveButtonText: positiveButtonText,
    negativeButtonText: negativeButtonText,
    setDateTitle: setDateTitle,
    setTimeTitle: setTimeTitle
};

datePicker.show(options, function(date){
	alert("date result " + date);  
});
```

## Options

### mode - iOS, Android
The mode of the date picker.

Type: String

Values: `date` | `time` | `datetime`

Default: `date`

### date - iOS, Android
Selected date.

Type: long

Default: `-1 (will use current date/time)`

### minDate - iOS, Android
Minimum date.

Type: long

Default: `-1`

### maxDate - iOS, Android
Maximum date.

Type: long

Default: `-1` 

### minuteInterval - iOS, Android
Interval between options in the minute section of the date picker.

Type: Integer

Default: `1`

### positiveButtonText - iOS, Android
Label of possitive button.

Typ: String

Default: `Set`

### negativeButtonText - iOS, Android
Label of negative button.

Type: String

Default: `Cancel`

### setDateTitle - Android
Title when user must select a date

Type: String

Default: `Set date`

### setTimeTitle - Android
Title when user must select a time

Type: String

Default: `Set time`

### x - iOS (iPad only)
X position of date picker. The position is absolute to the root view of the application.

Type: String

Default: `0`

### y - iOS (iPad only)
Y position of date picker. The position is absolute to the root view of the application.

Type: String

Default: `0`

## Requirements
- PhoneGap 3.0 or newer / Cordova 3.0 or newer
- Android 2.3.1 or newer / iOS 5 or newer

## Example

```js
var date = new Date().getTime();
var minDate = new Date('November 18, 2014 11:15:00').getTime();
var maxDate = new Date('March 25, 2015 23:55:00').getTime();var minuteInterval = 5;
var minuteInterval = 5;
var positiveButtonText = 'Ok';
var negativeButtonText = 'Annuleer';
var setDateTitle = 'Datum instellen';
var setTimeTitle = 'Tijd instellen';
            
var options = {
	mode: mode,
    date: date,
    minDate: minDate,
    maxDate: maxDate,
    minuteInterval: minuteInterval,
    positiveButtonText: positiveButtonText,
    negativeButtonText: negativeButtonText,
    setDateTitle: setDateTitle,
    setTimeTitle: setTimeTitle
};

datePicker.show(options, function(date){
  alert("date result " + date);  
});
```