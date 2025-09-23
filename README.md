# .lif To Memento FIJI plugin
FIJI plugin that reads .lif files from the Sp8 confocal microscope (including 3D stack images) and outputs a file system with .ome.tif files, png files, jpg files, or multi-page tif files. The png files can be directly used for setting up a memento project. Note however that in this plugin the image intensity scaling is done on the image level. If you prefer to scale intensities for all images of a well jointly, making images within a well more comparable, you should rather first export as single-channel tif files with this plugin and then convert them to JPGs or PNGs by [another tool](https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG) before uploading these files to Memento or using them for another further analysis purpose.

## Copyright
(c) 2022-2025, Jan N. Hansen

Contact: jan.hansen (at) scilifelab.se

## Licenses
The plugin and the source code are published under the GNU General Public License v3.0 contained in this repository.

## Archived versions and referencing
The code and software releases of this software are archived in zenodo at the repository https://zenodo.org/records/15778558

Specific versions can be cited through finding the version sepcific zenodo entry, e.g.,
- Version v0.0.14: <a href="https://doi.org/10.5281/zenodo.17179314"><img src="https://zenodo.org/badge/DOI/10.5281/zenodo.17179314.svg" alt="doi:10.5281/zenodo.17179314"></a>
- Version v0.0.12: <a href="https://doi.org/10.5281/zenodo.16906976"><img src="https://zenodo.org/badge/DOI/10.5281/zenodo.16906976.svg" alt="doi:10.5281/zenodo.16906976"></a>
- Version v0.0.11: <a href="https://doi.org/10.5281/zenodo.16815800"><img src="https://zenodo.org/badge/DOI/10.5281/zenodo.16815800.svg" alt="doi:10.5281/zenodo.16815800"></a>
- Version v0.0.10: <a href="https://doi.org/10.5281/zenodo.16809133"><img src="https://zenodo.org/badge/DOI/10.5281/zenodo.16809133.svg" alt="doi:10.5281/zenodo.16809133"></a>
- Version v0.0.9: <a href="https://doi.org/10.5281/zenodo.16782445"><img src="https://zenodo.org/badge/DOI/10.5281/zenodo.16782445.svg" alt="doi:10.5281/zenodo.16782445"></a>
- For more versions, browse the [zenodo records](https://zenodo.org/records/17179314).

To generally cite the plugin version independent, you can refer to the general DOI including all versions:
<p align="center">
   <a href="https://doi.org/10.5281/zenodo.15778558"><img src="https://zenodo.org/badge/DOI/10.5281/zenodo.15778558.svg" alt="DOI"></a>
</p>
 
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
   <img width=500 alt="menu-entry" src="https://github.com/user-attachments/assets/e95f0cd4-35eb-4d6a-968b-625e2384be9a" />
</p>

### Using the plugin
Launch the plugin via
<p align="center">
   <img width=500 alt="menu-entry" src="https://github.com/user-attachments/assets/e95f0cd4-35eb-4d6a-968b-625e2384be9a" />
</p>

A settings dialog is displayed that also guides you through the settings
<p align="center">
   <img src="https://github.com/user-attachments/assets/1936cca5-58d3-4f10-82f5-721334582ee8" width=600>
</p>

Notes on the settings:
- You can decide whether you want to export PNGs, JPGs, TIFs, multi-plane and multi-channel tifs (Hyperstacks) or multiple of these output types.
- The exported background-transparent PNG images (![png-setting](https://github.com/CellProfiling/Sp8-Lif_To_Memento/assets/27991883/44a15941-2e78-42ed-b6bb-835c2ad7d53e)) or non-transparent JPG images can be directly imported into Memento, where these can be installed to allow you to toggle on / off channels. The display ranges (maximum intensity displayed as maximum color brightness) of the images will be based on the intensity in the images. The plugin will set the LUT of the image in a way that the display range is from 0 to the 99.9% percentile of the intensities in the channel, meaning that the conversion to PNG will be created in a way that 0.1% of the pixels in the image will be oversaturated. Note that the percentile calculation and conversion is done on the image level, so different images from the same well may look different, especially if you have large variance in intensity among the regions / cells. Thus, and for improved speed in memento, it is more recommended to export to Tif files (![tif-export](https://github.com/CellProfiling/Sp8-Lif_To_Memento/assets/27991883/749ddbe9-c05a-4d42-8f81-043150d59eb7)) (which you can also use for image analysis) and then use another tool to convert the Tiffs to PNGs or JPEGs allowing intensity adjustments on the level of a well or column (see [TifCs_To_HPA-PNG-JPEG](https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG)).

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

Next a dialog pops up asking you to select the csv file in the next dialog. Confirm this dialog.
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/206784843-e185295d-c010-41c0-a01d-7ab65c900418.png" width=500>
</p>

In the next dialog select the csv file you want to provide and press "Open".
<p align="center">
   <img src="https://github.com/CellProfiling/Sp8-Lif_To_Memento/assets/27991883/1e3dd30a-7185-427f-ad5f-1690a7aa3db7" width=450>
</p>

Now the plugin will automatically run and populate the output folders. Note that there can be a significant delay until you see a progress dialog informing you about progress. This delay appears when you load a big lif file (of several to 10s or 100s of GB). In that case the plugin is opening this large file during the delay, which is taking a lot of time for large files. During this delay, a console may pop up and show a lot of warnings. These warnings can just be ignored. You can minimize the console or close it. The warnings come from problems during metadata import from the .lif into the ome-xml scheme.
<p align="center">
   <img src="https://github.com/CellProfiling/Sp8-Lif_To_Memento/assets/27991883/57abc7da-3e2f-488b-9d54-1cf5e5c6b5e5" width=450>
</p>

Ones all files are preexplored and the plugin runs you are getting informed about the status by another window showing also a progress bar and the series that have been already processes / the series that still need to be processed.
<p align="center">
   <img src="https://github.com/CellProfiling/Sp8-Lif_To_Memento/assets/27991883/80cf4142-e102-447b-8ad5-28cab8a83013" width=450>
</p>


The progress dialog will let you know when it is done in a message written on the progress bar (on Windows the progress bar will turn green when the plugin has finished processing).

#### Common errors
If the processing failed you will see a message in the progress bar alerting you about errors and you can see information about the errors in the "Notifications" list. On Windows, the progress bar also turns red when errors occur.
<p align="center">
   <img src="https://github.com/CellProfiling/Sp8-Lif_To_Memento/assets/27991883/148cfb16-c861-4bd3-b375-54f348004f80" width=650>
</p>

If you cannot conclude on the error reason based on the messages, mark the messages in the log window, copy them to the clipboard (Cmd+C on mac, Ctrl+C in windows) and paste them into an email or document and send it to the developer and ask for help (jan.hansen at scilifelab.se).

### Updating the plugin version
Download the new version's .jar file from the [release page](https://github.com/CellProfiling/Sp8-Lif_To_Memento/releases). Make sure FIJI is closed - if still open, close it. Next, locate the FIJI software file / folder on your computer and go on below depending on your OS.

#### Windows
In Windows or Linux, FIJI is a directory called FIJI.app. Enter this directory and navigate to the "plugins" folder and enter it. Find the old version of the Sp8Lif_To_Memento_JNH-X.X.X-SNAPSHOT.jar file and delete it. Then place the new plugin version in the "plugins" folder. Exit the FIJI.app folder. Start FIJI.

#### Mac
In Mac OS, FIJI is just a software file (FIJI.app). Right click on the FIJI icon (or hold option and do normal click on it), then select "Show Package Content". A folder will open, which contains the contents of the FIJI.app. Navigate to the "plugins" folder folder and enter it. Find the old version of the Sp8Lif_To_Memento_JNH-X.X.X-SNAPSHOT.jar file and delete it. Then place the new plugin version in the "plugins" folder . Exit the FIJI.app folder. Start FIJI.

