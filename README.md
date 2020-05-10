# Texture repacker
Tool to repack images with grid layout to texture pack with another grid size

While developing my <a href="https://github.com/VahanChaMaka/king-of-the-dump">game</a> I picked texture packs from a lot of various sources. Not all of them are packed with the required spacings, so I created this simple tool.

<table>
  <tr>
    <td vlign="center"><img src="meta/flame.png"></td>
    <td vlign="center"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Arrow_east.svg/800px-Arrow_east.svg.png" width="30px"></td>
    <td vlign="center"><img src="meta/flame_processed.png"></td>
  </tr>
</table>

# How to use
You need Java installed on your computer
1. Download <a href="texture-repacker.jar">
2. Go to download directory, open it directly or in terminal by command
```
java -jar texture-repacker.jar
```
3. Select image
4. Adjust grid to cut
5. Adjust new desirable grid (bigger than original one so far)
6. Click "Save" button. 
Processed image will be saved in folder "processed" in the original image location with the same name
<img src="meta/screenshot.png">
