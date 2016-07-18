## AutoReply - A Twitter Bot written in Java
AutoReply is a Twitter bot client written in Java. It can be deployed to Heroku.

### Features
* `@<screen name> update_name <new name>`
  * Updates the name of the bot (or the name of the user running the bot) to `new name`
  * ex : `@AutoReplyDemo update_name The AutoReplyDemo`
* `<new name>(@<screen name>)`
  * Just like `update_name`, but in a different format
  * ex : `The AutoReplyDemo(@AutoReplyDemo)`
* `@<screen name> update_location <new location>`
  * Updates the location of the bot (or the user) to `new location`
  * ex : `@AutoReplyDemo update_location GitHub`
* `@<screen name> ␖`
  * Sends an `␆` back to the sender
  * ex : `@<screen name> ␖`
* `@<screen name> rain <some location in Japan>`
  * Gives you an estimate of how much rainfall is expected in that location
  * `@AutoReplyDemo rain 青葉山`
* `@<screen name> coop_food`
  * Just in case you can't be bothered accessing the [University Coop Website](http://gakushoku.coop/setmenu.php?feeling=C&price=500) to figure out what to eat, we'll access it and give you back the results
  * `@AutoReplyDemo coop_food`

### Demo
You can try it out by sending commands to [@AutoReplyDemo](https://twitter.com/AutoReplyDemo)

### How to host
Just set the following environment variables and deploy to Heroku. It would probably run even if it's not on Heroku.
* TwitterAccessToken
* TwitterAccessTokenSecret
* TwitterConsumerKey
* TwitterConsumerSecret
* YahooAppId

### Libraries
This program uses the following libraries
* [Twitter4J](http://twitter4j.org/en/index.html)
* [Java API for JSON Processing](https://jsonp.java.net/)
* [Apache HttpComponents](https://hc.apache.org/)
* [jsoup](https://jsoup.org/)

### License

Copyright 2014-2016 jetkiwi@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.