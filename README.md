![BannerSmall](https://user-images.githubusercontent.com/4958241/144576860-6ad1f604-9068-4218-8e57-0b7c9488612b.png)

[![Download](https://img.shields.io/static/v1?label=&message=Download&color=2d2d2d&labelColor=dddddd&style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAALEQAACxEBf2RfkQAAAAd0SU1FB98BHA41LJJkRpIAAAAYdEVYdFNvZnR3YXJlAHBhaW50Lm5ldCA0LjAuMvvhp8YAAAGGSURBVDhPjZK9SgNBFIVHBEUE8QeMhLAzdzdYBFL7CmnstEjjO4iFjWClFiGJm42VFpYSUptKRfABFBQE7UUI2AmGmPXM5k6cjFE8cJjZ3fPduTOzIiJarvl+bPmuLuWS+K/KmcwUoK4pEGoTnetvYTY7GUpZOCTa0sZi+QRyhQ83pkBShKjDUBvzGKNxr6rU8W4uN8FoX4CKEcB6EMR61OZOEtApEqNIndFvAbiNuAB30QeU+sDYBXSCdw3MrzHvlnx/kdFkC3kU6NkwF3iM0ukFnNM8R0UplZquKHWE85nhV0Kg4o4GrDMwre5x5G9h9aaBHL/VPI849rtqRFfwqALaIcd+F87gYnDy1ha0sY12VcoiR0cLq5/ae3e3gyLbHB2tMAgKQ1fXh+z5a1mpDXQyy8hPIdgyAEMDW8+fWOQJHbfgzXUhxhkXYh/3jcC9C7s2V43c+9DPpHXgeXMIXbqQbV7gAV5hbFixEGMVolUEmvjjXuBeRcoOnp/R/hm81hi0LsQX8OcRBvBjZ8YAAAAASUVORK5CYII=)](https://mrcrayfish.com/mods?id=framework) ![Minecraft](https://img.shields.io/static/v1?label=&message=1.18&color=2d2d2d&labelColor=dddddd&style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAZdEVYdFNvZnR3YXJlAHBhaW50Lm5ldCA0LjAuMjCGJ1kDAAACoElEQVQ4T22SeU8aURTF/ULGtNRWWVQY9lXABWldIDPIMgVbNgEVtaa0damiqGBdipXaJcY2ofEf4ycbTt97pVAabzK5b27u+Z377kwXgK77QthRy7OfXbeJM+ttqKSXN8sdwbT/A0L7elmsYqrPHZmROLPh5YkV4oEBwaKuHj+yyJptLDoAhbq3O1V1XCVObY3FL24mfn5oRPrcwSCRfQOyNWcjVjZdCbtcdwcgXrXUspdOKbDN/XE9tiBJMhXHT60gUIT2dMhcDLMc3NVKQklz0QIkf5qlyEcO6Qs7yPhMJB4amDMFimQSmqNlE8SKAZFzDfxHfVILIIZ10sJ3OwIbcqSuiOjchkzNCboHev9o2YhgiUP8mxnLN24I6/3ghYdtQG5iUMpFBuCP9iKwLsfiLyeCp2rMnZgwX3NArGoxW1Ridl+BzLEVKa8KSxOqNmDdz0kFnxaLHhWEgAyZigWhHXL+pEDy2ozsDxv8vAzTnh7w5kcghqCaFmCT10of4iPIT2mRdPUh4HoCcVwBH/8Ac2kzUkEV5r3EfVSOvbAJa5NDyI0r2oDtWb1EClh+OoC3Pg7v/Bw7p939yI4rsRW2Y3lKh01eh7WpIRyKZqzyjjYgPdIvlaMWRqYuG7wWryYHsRM0sFolZiPvQ3jheIwSmSBPdkByG/B6Wi3RYiVmRX7GiAPiUCRisii8D+jZNKvPBrHCW1GY0bAz6WkDCtOaSyKQFsi4K5NqNiZtehN2Y5uAShETqolhBqJXpfdPuPsuWwAaRdHSkxdc11mPqkGnyY4pyKbpl1GyJ0Pel7yqBoFcF3zqno5f+d8ohYy9Sx7lzQpxo1eirluCDgt++00p6uxttrG4F/A39sJGZWZMfrcp6O6+5kaVzXJHAOj6DeSs8qw5o8oxAAAAAElFTkSuQmCC) [![Curseforge](http://cf.way2muchnoise.eu/full_framework_downloads.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/framework)

## About:

Framework is lightweight but powerful library built to enhance the development of mods. Unlike other libraries, Framework focuses on providing powerful yet easy-to-use tools that unlock features which would otherwise be buried in large amounts of code to acheive the same result. Framework also avoids the use of Mixins (a code injection service) in order to gain maximum compatibility with other mods and allow it to safely be included in modpacks.

## Features:

### 🔑 Synced Data Keys
Synced Data Keys are an improvement of Minecraft's entity data accessor system (which tracks and syncs data from an entity on the server to clients). The problem with this system is that it's impossible for modders to safely add additional data accessors to vanilla entities. A common problem that occurs in Minecraft's system is a mismatched key id which means the data sent from the server is applied to the wrong data accessor on the client. Framework solves this problem by syncing the id of every key to the client during the handshake phase. Framework also take's the system even futher by providing the ability to easily save the data, peresist over deaths, and more! **This currently is only designed to work for players and will be expanded soon to include every entity**

An example of adding an aiming property to a player, possibly for weapon mod or similar.
```java
// Create the synced data key
private static final SyncedDataKey<Boolean> AIMING = SyncedDataKey.builder(Serializers.BOOLEAN)
            .id(new ResourceLocation("synced_player_data_test", "aiming"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .saveToFile()
            .build();

// Register it into Framework API
FrameworkAPI.registerSyncedDataKey(TOUCHED_GRASS);  

// Set the value for the synced data key
AIMING.setValue(player, true);

// Get the value for the synced data key
AIMING.getValue(player);
```

### 📦 Easy Login Packets
Forge has the ability to allow developers to create login packets, however implementing it requires a significant amount of code. Framework condenses the required code into a simple registration method and will handle sending your data to clients.

```java
// A class implementing the ILoginData interface, this could be a manager
public static class CustomData implements ILoginData
 {
     @Override
     public void writeData(FriendlyByteBuf buffer)
     {
         // Write your data to the buffer you want to send to clients when they log in
         buffer.writeUtf("Touch Grass");
     }

     @Override
     public Optional<String> readData(FriendlyByteBuf buffer)
     {
         // Read in and handle your data
         String message = buffer.readUtf();
         
         // Return an empty optional if successful or provide a string with the error
         return Optional.empty();
     }
 }

// Register the ILoginData into the Framework API
FrameworkAPI.registerLoginData(new ResourceLocation("your_mod_id", "test"), CustomData::new);

```

**More features coming soon!**

## Developers:

To get started quickly, add the following code into to your mod's `build.gradle` then check out the documentation.

```gradle
repositories {
    maven {
        url "https://cursemaven.com"
    }
}

dependencies {
	implementation fb.deobf("curse.maven:framework-549225:<file_id>")
}
```
