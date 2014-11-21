Foursquare_Explorer
===================

The application mainly consists of a map view, once opened it gets user GPS or Network location and requests for nearby venues from Foursquare APIs. Application should cache the retrieved venues so that on next application start up, cached venues are displayed until fresh new venues are retrieved from server. It then places pins of each venue on the map, pin images should be venues images retrieved from Foursquare. Clicking on any pin should open an info window with the name of the venue, when clicking on the info window allow user to check-in in this venue.  For user to check-in, this user should be logged in using  OAuth implementation of Foursquare login. 

•	I am using Job queue manager library to easily schedule jobs (tasks) that run in the background, improving UX and application stability. You can find it here (https://github.com/path/android-priority-jobqueue)  

•	I am using EventBus that simplifies communication between Activities, Fragments, Threads, Services, etc you can find it here (https://github.com/greenrobot/EventBus)  

•	I am using Retrofit that turns your REST API into a Java interface. It depend on Okhttp and Gson library  You can find it here (https://github.com/square/retrofit) or (http://square.github.io/retrofit/ ) 

•	I am using foursquare-android-oauth You can find it here (https://github.com/foursquare/foursquare-android-oauth)  

•	I am  using google play services
