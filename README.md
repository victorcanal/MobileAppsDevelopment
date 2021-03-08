# MobileAppsDevelopment
Repository for the A4 project of the Mobile Apps Development module


- Explain how you ensure user is the right one starting the app:<br/>
To ensure the user starting the app is the correct one (I assume the owner of the device), I implemented a biometric authentication, requiring any fingerprint saved in the phone. As long as the owner of the device is the only one knowing its pin code and the only one with a registered fingerprint, the display of the account is safe. Also, resuming the app reinstantiate the first activity, with the required biometric authentication.

- How do you securely save user's data on your phone ?<br/>
The content retrieved from the online API is saved in the app-specific files on the device. Android encrypts the location of the app-specific files, and I personally didn't manage do find the saved data in my emulator's storage.
- How did you hide the API url ?<br/>
The API url is not hidden in my code. I tried to use CMake and NDK to try to hide the url in a C++ library, but Gradle either is taking too much time to migrate and build my project with the new tools, either I forgot to add or to update a file for the migration.

- Screenshots of your application<br/>

![image](https://user-images.githubusercontent.com/63497586/110257581-c31dc300-7f9e-11eb-89d4-9c828ce8e1e1.png)
First screen of the app

![image](https://user-images.githubusercontent.com/63497586/110257587-cb75fe00-7f9e-11eb-952e-9ad4d108218a.png)
The app asking for biometric authentication

![image](https://user-images.githubusercontent.com/63497586/110258348-73d99180-7fa2-11eb-8510-55dddf674b7f.png)
First access to the account display and no Internet connection is available (so no saved data on the device)

![image](https://user-images.githubusercontent.com/63497586/110258383-a4213000-7fa2-11eb-8815-853e303fd5a3.png)
After having turned cellular data on and pressed the refresh button/restarting the app
