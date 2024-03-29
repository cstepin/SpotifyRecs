# SpotifyRecs

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)

## Overview
### Description
An app where users can input several musical artists they like then receive a list of music which is commonly found on playlists with the other artists. Users can then listen to bits of the song then swipe left or right on the music to generate a final playlist.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:**
Entertainment — music
- **Mobile:**
Users can connect on their mobile phones and upload the playlists to Youtube / use mobile capabilities to export playlists.
- **Story:**
An app which ensures that users are able to find new music and also know the music they’re getting before making entirely new playlists.
- **Market:**
People who like listening to music / like discovering new music.
- **Habit:**
Anytime people look for new music they will use this app.
- **Scope:**
This can be used on avid music listeners / maybe then develop a side that can be used by artists to market themselves in a certain way (they’re a combination of artist X and artist Y.)

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can search for music from a selection of artists
* User can create a new account
* User can login
* User can get recommendations for artists that aren’t some of the original two artists
* Users can listen to a preview of music
* User can swipe left or right on each music to either get rid of it or not
* Users can end up with a playlist of music

**Optional Nice-to-have Stories**

* Users can upload their new playlist to Youtube
* Users can use recommendation features to get a specific model-trained recommendation
* User can see past playlists they’ve made
* User can search for music by a hashtag
* Users can also enter from a musician’s side to market themselves / their music.
* Users get encouraging comments when swiping (only 4 more to go! Keep swiping!)

### 2. Screen Archetypes

*Login screen
   * User can create a new account
   * User can login
*Screen asking for User inputs for artists
   * User can search for music from a selection of artists
   * User can get recommendations for artists that aren’t some of the original two artists
*Screen that gives a list of music suggestions
	*Users can listen to a preview of music
	*User can swipe left or right on each music to either get rid of it or not
*Final screen with new playlist
	* Users can end up with a playlist of music
	* User can get recommendations for artists that aren’t some of the original two artists

### 3. Navigation

** Music albums
* Profile
* Past albums (optional)

**Flow Navigation** (Screen to Screen)
* Forced Log-in -> Account creation if no log in is available
* Music Selection -> Song swiping screen -> Final album
* Youtube -> Listening to recently albums if need be.

### 4. Rough Wireframe
![The wireframe diagram](https://github.com/cstepin/CapstoneProject/blob/main/Capstone%20Project%20Wireframe.jpg)

### 5. Week-by-week walkthrough

Week 0 — plot out course of project

Week 1 — 
- Create sign-up or login
- Activity that brings you to an “input artist” window
- Start researching how to create models to give recommendations based on the TWO artists inputted
- For now, try to bring up other music suggestions in the same genre
- Implement another activity that has a recycler view of the song suggestions 

Week 2 *Goal* — have the ability to swipe on song suggestions
- Figure out how to have “swipe left” or “swipe right” technology
- Keep the list of swipe rights to be there in the next activity
- Try to build a model / work on recommendations?
- Updated:
	- Build a Collaborative Filtering Algorithm using users in Back4App database
		- This will be fake data as the users in Back4App are also to test the database

Week 3
*Update*
- Try to include PyTorch model
	[x] Figure out how to input multiple arguments into JavaFile
	[x] Figure out how to calibrate users (to find similar users)
	[x] Figure out how to translate users' opinions into matrix
	[x] Figure out how to get song recommendation from what the PyTorch model gives back.

GOAL: Get the above end-to-end complete by the end of this week
- Start researching how to do comparison matrix for algorithms
- Also start working on Unit tests for algorithms
	- Especially for the Pytorch model, unit tests are very important

Week 4 *Goal* — have a mostly-complete app
- [x] Figure out how to play bits of sound / YouTube or Spotify music in the swiping window
- [x] Figure out how to export the album into a different window

Week 5
- Style the app to make it look nice
- Run both algorithms through the recommendation matrix -- see whether one outperforms the other one.

Week 6 *Goal* have the app look like a real app and have at least one stretch goal finished
- [x] Stretch goal: Trying to look at past albums
- [x] Develop several PyTorch models (one control and several others to test) to see which one predicts most accurately.
- [] Allow users to delete playlists as well

Week 7:
- [] Allow users to delete playlists as well
- [] Implement a way to keep track of user ID and use that with the simple and better nn models

### 6. Models

Models

User
Property	Type	Description
objectId	String	unique id for the user (default field)
Username           	String	User’s username to log in
Password	String	User’s password to log in
Artists	Array of String	List of artists that user listens to
createdAt	DateTime	date when User is created (default field)
updatedAt	DateTime	date when post is last updated (default field)

Artist
Property	Type	Description
objectId	String	unique id for the artist (default field)
Top Songs	Array of String	Top 5 songs of the artist.
createdAt	DateTime	date when User is created (default field)
updatedAt	DateTime	date when post is last updated (default field)

Outline Parse Network Requests

* Log-in Screen
    * (Read/GET) Log in a user if the user enters correct username and password
    * (Create/POST) Create a new user if they choose to sign up
* Entering-artists screen
    * (Update/PUT) Update user’s artists if they enter new artists
* Generating songs screen
    * (Read/GET) Check the artists the user has to try not duplicate artists
    * (Read/GET) Check the artists other users have to try and suggest some of the artists not in the overlap
    * (Read/GET) Use Spotify / Deezer API to find top x songs associated with the new artists.
* Final finished album Screen 
    * (Update/PUT) Update user artists with the new artists to whom they like to listen.

### 7. Profiler numbers:

Memory Profiler:
![](https://github.com/cstepin/SpotifyRecs/blob/master/MemoryProfiler.png)

CPU Profiler:
![](https://github.com/cstepin/SpotifyRecs/blob/master/CPUProfiler.png)

### 8. Loss for three algorithms and Links to Notebooks:
Link to Cosine Similarity notebook:
https://colab.research.google.com/drive/1Gw6BYINN8iAcAI4WVpLKiL5kl0DFuMLl#scrollTo=ti8YxB4tJBn6

(loss not evaluated, because dataset is too small)

MSE for Fastai (the control):
About 2.9 after first run
![](https://github.com/cstepin/SpotifyRecs/blob/master/FastAICollabFiltering.png)

Link to notebook:
https://colab.research.google.com/drive/1jZEX67I2BVd962PbeOINo5FXdGhmdhLy#scrollTo=o_wi229b5nlW

2.95 after second run:
![](https://github.com/cstepin/SpotifyRecs/blob/master/Screen%20Shot%202022-07-19%20at%201.16.57%20PM.png)

MSE for Naive Neural Networks:
test loss 673.166 

Link to notebook:
https://colab.research.google.com/drive/14lGLmvGP3aVGJazOFwpe7cPMsipxAVUp#scrollTo=9H1D9BJMZe2E

MSE for Better Neural Networks using more training data:
Final RMSE: 1.3319 (after both training 1 epoch and 5 epochs.)

![](https://github.com/cstepin/SpotifyRecs/blob/master/dataAfterEpochs.png)

Link to notebook:
https://colab.research.google.com/drive/190VSpMsalLnobAXcj_6kT-XEK5H6zxDU#scrollTo=fac5b3e8

Conclusion:

Better Neural Networks is the most accurate predictor for recommendations for music.

Download memory usage numbers:

![](https://github.com/cstepin/SpotifyRecs/blob/master/DownloadSizes%2C%20Assets%2C%20and%20Lib.png)

### 9. Database Schema:

Used back4app.

Structure of a "User" object:

![](https://github.com/cstepin/SpotifyRecs/blob/master/userObject1.png)
![](https://github.com/cstepin/SpotifyRecs/blob/master/userObject2.png)

Structure of a "Playlist" object:

![](https://github.com/cstepin/SpotifyRecs/blob/master/playlistObject.png)

## Walkthrough of App:

![](https://github.com/cstepin/SpotifyRecs/blob/master/SpotifyRecsWalkthrough_AdobeExpress.gif)

## Future Ideas for App:

There are several possible areas of improvement for the app.

First, is recommendation ratings.
I added several recommendation algorithms to be used with a/b testing purposes. To fully complete the a/b testing, I would need to also have users rate their satisfaction based on several metrics (genre of music, liveliness of songs, etc), and alter the next series of recommendations based on the ratings. These ratings could also help show which recommendation algorithm is preferred by users.

Second, is recommendations based on other users in the network.
I could also allow users to see what other users there are in the network and what their general music tastes are (and maybe give a score for how related their music interests are.) This would allow users who want music recommendations based on other users to choose which users they would want to influence their music.
