# doppelte

## Trivia
"Doppelte" means duplicates in german.

## Teaser
This command line tool supports you with the backup of your files.

Do you know this situation?
You already copied some image files from your phone to your hard drive
but you wanted to keep them on your phone too.

After a while you don't remember which files you already backed up.
You once copied and sorted your images neatly in a folder structure.
Image by image you would try to find the files in there.
And what if you've even renamed the image back then?


## What does it do?
Checks if the files in the given source directory (subdirectories are ignored)
already exist in the given backup directory. (subdirectories are considered to a depth of 20)

If the content of the files equal, the file from the source directory is copied to
a subdirectory "doppelte".
As a result only the files which were not found in the backup folder remain in the source folder
ready for being sorted and copied over to the backup folder by you.
All the duplicates are moved to the "doppelte" folder which can be easily deleted later on.


## Usage
maven package
Find the jar (with dependencies) and run it with
```
java -jar doppelte-and-so-on.jar [sourceDir] [backupDir]
```
