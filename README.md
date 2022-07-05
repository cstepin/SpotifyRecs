# SpotifyRecs

Original App Design Project - README Template
===

# SpotifyRecs

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

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
* Music Selection -> Tinder swiping screen -> Final album
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
	- Figure out how to input multiple arguments into JavaFile
	- Figure out how to calibrate users (to find similar users)
	- Figure out how to translate users' opinions into matrix
	- Figure out how to get song recommendation from what the PyTorch model gives back.

- Start working on second algorithm (pulling information from multiple APIs)
	- The algorithm will pull results from multiple API's then try to find the maximum intersection (if any exists)
	of artists similar to the the users' inputted artists. 
- Start researching how to do comparison matrix for algorithms

Week 4 *Goal* — have a mostly-complete app
- Figure out how to play bits of sound / YouTube or Spotify music in the swiping window
- (Maybe) figure out how to export the album into a different window

Week 5
- Style the app to make it look nice
- Run both algorithms through the recommendation matrix -- see whether one outperforms the other one.

Week 6 *Goal* have the app look like a real app and have at least one stretch goal finished
- Stretch goal: Trying to look at past albums
- Stretch goal: Trying to play the “most repeated” part of videos

Week 7:
- Stretch goal: Make the artists “anonymous” when playing the snippets
- Polishing the app

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

let query = PFQuery(className:”User”)
query.order(byDescending: "createdAt")
query.findObjectsInBackground { (artists: [PFObject]?, error: Error?) in
   if let error = error { 
      print(error.localizedDescription)
   } else if let artists = artists {
      print("Successfully retrieved \(artists.count) artists.”)
  // TODO: Copy the array over to then check against other users’ artists
   }
}

    * (Read/GET) Check the artists other users have to try and suggest some of the artists not in the overlap
    * (Read/GET) Use Spotify / Deezer API to find top x songs associated with the new artists.
* Final finished album Screen 
    * (Update/PUT) Update user artists with the new artists to whom they like to listen.

