# AuditHelper
> A simple tool to extract info from document

[![Build Status](https://travis-ci.com/NickLi0605/AuditHelper.svg?branch=master)](https://travis-ci.com/NickLi0605/AuditHelper)

Although many documents are now electronic, there's still a lot of hard copy in real world.  
The goal of this app is trying to reduce the effort about converting hard copy into electronic records 

## TODO

* Android
  * Modify the UI flow about opening camera
  * Compare with google's kotlin sample to make sure we use camera correcty
  * Implement the image capture button
  * Implement the JNI interface and data structure
  * Implement the template UI after receive output from native
  * Define the UI flow
    * Open app
    * Setting
    * Start preview
    * Preview layout
    * UI after capture image
    * Start preview for next image
    * Finish capture image
    * Output preview
    * Export output
* Native
  * Detection engine (if necessary)
  * OCR engine
* Model
  * Tesseract OCR
  * Google doc OCR
  * ROI detector (if necessary)
