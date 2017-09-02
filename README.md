<H4> This app developed for education purposes only! </H4>

# TickCheck

TickCheck is an Android application that gives you the option to track your past and future music shows by Zappa.<br>
The app also give you the option to check what are the available shows for specfic performer or specific date.<br>
For each show you can check for available tickets (and the available zones in the arena), add to your private list, navigate to the arena and much more!
 
<H2> How does it work? </H2>

The app get the requested data from Zappa website on realtime (there is no API for this site).<br>
In the <a href="https://github.com/SagiBarak/TickCheck/blob/master/app/src/main/java/sagib/edu/tickcheck/ShowDataSource.java">ShowsDataSource</a> and <a href="https://github.com/SagiBarak/TickCheck/blob/master/app/src/main/java/sagib/edu/tickcheck/ShowByDateDataSource.java">ShowsByDateDataSource</a> classes there is a parsing proccess that get the data for each show (by request).<br>
There is also a caching of the ArrayList to save the user's bandwidth.<br>
All the private data (Users, MyShows lists, Chats) stored in Firebase Database and Firebase Storage (for the profile images).
<br><br>
The project includes 3 Activities and 17 Fragments (Dialogs and BottomSheet included).

<H2> Tools </H2>

**Android Studio** - for the whole Java code. <br>
**Visual Studio Code** - for the JS function (Firebase Database notifications).

<H2> Versions </H2>

Work in progress...
Currently in beta testing.

<H2> Using libraries </H2>
<a href="https://github.com/jhy/jsoup">Jsoup</a>, 
<a href="https://github.com/square/picasso">Picasso</a>, 
<a href="https://github.com/Bearded-Hen/Android-Bootstrap">Android-Bootstrap</a>, 
<a href="https://github.com/JakeWharton/butterknife">Butter Knife</a>, 
<a href="https://github.com/firebase/FirebaseUI-Android">FirebaseUI-Android</a>, 
<a href="https://github.com/JodaOrg/joda-time">Joda-Time</a>, 
<a href="https://github.com/lopspower/CircularImageView">CircularImageView</a>, 
<a href="https://github.com/MasayukiSuda/BubbleLayout">BubbleLayout</a>, 
<a href="https://github.com/ViksaaSkool/AwesomeSplash">AwesomeSplash</a>, 
<a href="https://github.com/jkwiecien/EasyImage">EasyImage</a>, 
<a href="https://github.com/apl-devs/AppIntro">AppIntro</a>, 
<a href="https://github.com/facebook/fresco">Fresco</a>.
