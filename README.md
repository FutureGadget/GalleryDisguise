# GKey
Find me if you can.

https://infosecurityterm.slack.com

## Testing
1. Importing Project

![](https://github.com/FutureGadget/GalleryDisguise/blob/master/1.PNG)
![](https://github.com/FutureGadget/GalleryDisguise/blob/master/2.png)
![](https://github.com/FutureGadget/GalleryDisguise/blob/master/3.PNG)
![](https://github.com/FutureGadget/GalleryDisguise/blob/master/4.png)

2. Running

  - Before testing
    - To test this app without a real android device, download NOX app player [android emulator](https://www.bignox.com/).
    - [Emulator Setting guide](https://www.bignox.com/blog/how-to-connect-android-studio-with-nox-app-player-for-android-development-and-debug/)
  - Running the app
![](https://github.com/FutureGadget/GalleryDisguise/blob/master/5.png)
![](https://github.com/FutureGadget/GalleryDisguise/blob/master/6.PNG)
<br>If the device is not showing, try rebooting the emulator.

3. Testing
  - Rooting the emulator
  ![](https://github.com/FutureGadget/GalleryDisguise/blob/master/7.png)
  - Locate Photos under the following path : /storage/emulated/0/DCIM
    - Any folder name under the DCIM is okay.
    - EX) /storage/emulated/0/DCIM/test1, /storage/emulated/0/DCIM/testtest ...
  - Result files(encrypted files) will be saved on /data/data/com.jikheejo.ku.gallarydisguise/files/{original_dirname + tag}.
    - EX) encrypt screenshot folder with a tag "cat" => results will be stored on /data/data/com.jikheejo.ku.gallarydisguise/files/screenshotcat
    
4. Bug reporting
  - [Submit Issues](https://github.com/FutureGadget/GalleryDisguise/issues)

Thank you!
