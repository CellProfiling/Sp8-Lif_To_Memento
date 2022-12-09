# Sp8-Lif To Memento FIJI plugin
FIJI plugin that reads .lif files from the Sp8 confocal microscope (including 3D stack images) and outputs a file system with pngs for Memento.

## Copyright
(c) 2022, Jan N. Hansen

Contact: jan.hansen (at) scilifelab.se

## Licenses
The plugin and the source code are published under the GNU General Public License v3.0 contained in this repository.

## How to use this plugin?
This software is a plugin for FIJI (an extended version of the open-source image-analysis software ImageJ, including the BioFormats library from OME, which is needed to properly run this plugin). Thus to use the plugin, you need to first have FIJI downloaded to your computer. FIJI is freely available for dowbload [here](https://imagej.net/downloads). 

### Installation
The plugin can be installed as follows: 
- Download the .jar file from the latest release at the [release section](https://github.com/CellProfiling/Sp8-Lif_To_Memento/releases).
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206779100-c94326f6-7dee-4199-a886-7e1c345884e8.PNG" width=500>
</p>

- Launch FIJI and install the plugin by drag and drop into the FIJI status bar (red marked region in the screenshot below) 
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/201358020-c3685947-b5d8-4127-88ec-ce9b4ddf0e56.png" width=500>
</p>

- Confirm the installations by pressing save in the upcoming dialog(s).
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206777597-c128e10c-57e9-492e-972a-de32e7083079.png" width=500>
</p>

- Next, ImageJ requires to be restarted (close it and start it again)

- You can now verify that the plugin is installed by launching it through the menu entry: Plugins > CellProfiling > 

<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206778750-cb6979ab-3416-47c8-a000-d22ee5674a75.png" width=450>
</p>

### Using the plugin
Launch the plugin via
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206778750-cb6979ab-3416-47c8-a000-d22ee5674a75.png" width=450>
</p>

A settings dialog is displayed that also guides you through the settings
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206779381-b950312a-1854-47c9-a1da-a64307f93147.png" width=450>
</p>

Notes on the settings:
- If you select ![image](https://user-images.githubusercontent.com/27991883/206779545-900d241b-4b13-47bf-a060-26bbcbf0d8f8.png) make sure to also set the number of channels that are contained in your image. When this option is selected after the current settings dialog is confirmed by clicking OK, another dialog will pop up that allows you to modify the colors in which the individual channels are displayed in the output files. The color "ORIGINAL" should be selected if you want to keep the color profile assigned in the .lif file.

<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206780376-a48c9b4f-6d67-4ece-9703-437092e857df.png" width=300>
</p>

- If you select ![image](https://user-images.githubusercontent.com/27991883/206779748-423001f2-24e3-412e-b927-78426d4a364e.png) the software will ask you for a comma-separated csv file after having selected the files to be analyzed. This file should be organized as shown in the screenshot below. This table is then used to link the images in the input .lif file to an Antibody ID, Plate ID, and Protein name. The plugin will retrieve the well coordinate for each image contained in the .lif file and then look up Antibody ID, Plate ID, and Protein name in the table.csv file for the specific well coordinate to produce an output folder strucutre based on Antibody ID, Plate ID, and Protein name.
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206780186-cad3e1b8-de05-4757-a8c7-74d781ab4b16.png" width=150>
</p>

- The ![image](https://user-images.githubusercontent.com/27991883/206779688-e41850d4-3fa5-43a1-8ae0-e0207d364fcb.png) function is only useful for debugging the software.

When confirming the settings dialog (and eventually also the channel-color-settings dialog), a dialog pops up that allows you to list the files you want to process. Use it to make a list of files to process. Note that you can only process files from the same plate at the same time since you can only submit one table.csv file which may contain only the information for one plate!
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206780747-f8f488a1-7ee2-4fc3-9171-a66901347ea5.png" width=300>
</p>

Next a dialog pops up asking you to select the table.csv file in the next dialog. Confirm this dialog.

![image](https://user-images.githubusercontent.com/27991883/206784843-e185295d-c010-41c0-a01d-7ab65c900418.png)

In the next dialog select the csv file you want to provide.

To be continued....

