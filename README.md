authexample-android-tutorial
============================

AuthExample Android App created for the tutorial posted on [lucatironi.github.io](http://lucatironi.github.io).

[Ruby on Rails and Android Authentication Part One](http://lucatironi.github.io/tutorial/2012/10/15/ruby_rails_android_app_authentication_devise_tutorial_part_one)

In this three-part tutorial you'll learn how to build an authentication API that can allow external users to register, login and logout through JSON requests, with Ruby On Rails.

[Ruby on Rails and Android Authentication Part Two](http://lucatironi.github.io/tutorial/2012/10/16/ruby_rails_android_app_authentication_devise_tutorial_part_two)

The second part of the tutorial will let you code an Android app that uses the JSON authentication API developed in the previous tutorial.

[Ruby on Rails and Android Authentication Part Three](http://lucatironi.github.io/tutorial/2012/12/07/ruby_rails_android_app_authentication_devise_tutorial_part_three)

I wanted to add some more features to the Android app and its Rails backend. Now the user can create and complete tasks just like in a real ToDo mobile application.

## Dependencies

[ActionBarSherlock](http://actionbarsherlock.com): see [usage](http://actionbarsherlock.com/usage.html) on how to install it as a library for this project.

## Installation

- Clone or [download](https://github.com/lucatironi/authexample-android-tutorial/archive/master.zip) the project
- Import it in your Eclipse's workspace:  _"File > Import > Existing Android Code Into Workspace"_
- Browse for the directory and check _"Copy projects into workspace"_
- [Download](http://actionbarsherlock.com/download.html) ActionBarSherlock
- Extract the zip file and import it as a library: _"File > New > Other ... > Android Project from Existing Code"_
- Navigate the ActionBarSherlock directory and select the "actionbarsherlock" dir.
- Ignore the warning/error that may arise and right-click on the new project and select "Refactor > Rename" and rename the project "ABS" (or whatever you like).
- Right-click on the AuthExample project and select _"Properties"_: click on the _"Android"_ section and add the just added ABS library in the _"Library"_ sub-section.
