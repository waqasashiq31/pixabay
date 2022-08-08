### App Architecture

Android application is built on MVVM with repository pattern. Added following libraries to help built the app.

- Retrofit for network call
- Hilt as a dependency injection tool
- Kotlin coroutines for background tasks with flows
- Android Navigation Component
- Android official data binding library
- DiffUtil for list comparison
- Glide for image loading
- Truth library for assertions in test cases
- Mockito to mock objects for testing

### MVVM 

MVVM was chosen due to following reasons

- It helps minimize coupling in the code.
- It helps in writing test cases, as we can individually add test cases for repository, viewmodel and activity/fragments
- Although MVC can also be used for this application, as we don't have much of the features in it, but maintainability and scalability will be a problem eventually as the app will grow.
- It's recommended by Google and android provides different Architecture components with it like viewmodel


### Android Config

- CompileSDK = 32
- TargetSDK = 32
- MinSDK = 21

### Tested on Devices

- Samsung S22 Plus (Android 12)
- Android emulator (Pixel 3, Android API 30)
- Android emulator (Pixel 2, Android API 27)

### Design

- Added list for images with column span 2 to show more data in the view port
- Added android alert dialog to show only the intimation for detail screen
- Used Poppins font
- On pressing done button on soft keyboard, image search will be initiated.

### Security

- Added Pixabay api key to local.properties. To secure this we can add this file to gitignore to prevent it uploading to Github

### Cache

- As there was no way in the api to create a local query using local database, so cached the api responses using retrofit cache