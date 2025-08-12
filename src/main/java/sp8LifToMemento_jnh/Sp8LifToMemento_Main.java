package sp8LifToMemento_jnh;

/** ===============================================================================
* Sp8Lif_To_Memento ImageJ/FIJI Plugin v0.0.11
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation (http://www.gnu.org/licenses/gpl.txt )
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*  
* See the GNU General Public License for more details.
*  
* Copyright (C) Jan Niklas Hansen
* Date: November, 2022 (This Version: August 12, 2025)
*   
* For any questions please feel free to contact me (jan.hansen@scilifelab.se).
* =============================================================================== */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

import javax.imageio.ImageIO;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.WaitForUserDialog;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;

public class Sp8LifToMemento_Main implements PlugIn {
	// Name variables
	static final String PLUGINNAME = "Sp8Lif_To_Memento";
	static final String PLUGINVERSION = "0.0.11";

	// Fix fonts
	static final Font SuperHeadingFont = new Font("Sansserif", Font.BOLD, 16);
	static final Font HeadingFont = new Font("Sansserif", Font.BOLD, 14);
	static final Font SubHeadingFont = new Font("Sansserif", Font.BOLD, 12);
	static final Font TextFont = new Font("Sansserif", Font.PLAIN, 12);
	static final Font InstructionsFont = new Font("Sansserif", 2, 12);
	static final Font RoiFont = new Font("Sansserif", Font.PLAIN, 20);

	// Fix formats
	DecimalFormat dformat6 = new DecimalFormat("#0.000000");
	DecimalFormat dformat3 = new DecimalFormat("#0.000");
	DecimalFormat dformat0 = new DecimalFormat("#0");
	DecimalFormat dformatDialog = new DecimalFormat("#0.000000");

	static final String[] nrFormats = { "US (0.00...)", "Germany (0,00...)" };

	static SimpleDateFormat NameDateFormatter = new SimpleDateFormat("yyMMdd_HHmmss");
	static SimpleDateFormat FullDateFormatter = new SimpleDateFormat("yyyy-MM-dd	HH:mm:ss");
	static SimpleDateFormat FullDateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Progress Dialog
	ProgressDialog progress;
	boolean processingDone = false;
	boolean continueProcessing = true;
	
	// -----------------define params for Dialog-----------------
	int tasks = 1;
	
	String imageType [] = new String [] {".lif 3D"};
	String selectedImageType = imageType [0];
	
	boolean relabelSeries = true;
	
	static final String[] Colors = {"ORIGINAL", "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "Grays"};
	int numberOfChannels = 5;
	String selectedChannelColors [] = new String[] {"ORIGINAL", "ORIGINAL", "ORIGINAL", "ORIGINAL", "ORIGINAL", "ORIGINAL"};
	boolean changeCColors = false;
	
	boolean diagnosisLogging = false;

	boolean writePNG = false;
	
	String outPathPNG = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "Sp8LifToMemento"
			+ System.getProperty("file.separator") + "PNG" + System.getProperty("file.separator");

	boolean writeJPG = false;
	
	String outPathJPG = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "Sp8LifToMemento"
			+ System.getProperty("file.separator") + "JPG" + System.getProperty("file.separator");

	boolean writeTif = true;
	
	String outPathTif = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "Sp8LifToMemento"
			+ System.getProperty("file.separator") + "TIF" + System.getProperty("file.separator");
	

	boolean writeTifStacks = false;
	
	String outPathTifStacks = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "Sp8LifToMemento_Stacks"
			+ System.getProperty("file.separator");
	
	// -----------------define params for Dialog-----------------

	@Override
	public void run(String arg) {

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// ---------------------------------INIT JOBS----------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		
		dformat6.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dformat3.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dformat0.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dformatDialog.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

		String series[] = { "", "" };
		String name[] = { "", "" };
		String dir[] = { "", "" };
		String fullPath[] = { "", "" };

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// --------------------------REQUEST USER-SETTINGS-----------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		GenericDialog gd;
		
		while(true) {
			gd = new GenericDialog(PLUGINNAME + " - set parameters");	
			//show Dialog-----------------------------------------------------------------
			gd.setInsets(0,0,0);	gd.addMessage(PLUGINNAME + ", Version " + PLUGINVERSION + ", \u00a9 2022-2024 JN Hansen", SuperHeadingFont);	
			gd.addHelp("https://github.com/CellProfiling/Sp8-Lif_To_Memento/");

			gd.setInsets(0,0,0);	gd.addMessage("Notes", SubHeadingFont);
			
			gd.setInsets(0,0,0);		gd.addMessage("The plugin processes .lif files from 'TileScans' acquired with the Leica Sp8. TileScans here refers to automated", InstructionsFont);
			gd.setInsets(0,0,0);		gd.addMessage("   acquisition of many images on a multi-well plates, all stored in one .lif file.", InstructionsFont);
			gd.setInsets(0,0,0);		gd.addMessage("The plugin generates an output directory with PNGs or JPGs, either of which can be imported to Memento, or with Tiffs", InstructionsFont);
			gd.setInsets(0,0,0);		gd.addMessage("   for analysis or further processing before importing into memento (e.g., adjustments on the well or column level).", InstructionsFont);		
			gd.setInsets(0,0,0);	gd.addMessage("For JPG/PNG output, this plugin will adjust each individual image stack's intensity scale individually. For well", InstructionsFont);		
			gd.setInsets(0,0,0);	gd.addMessage("   column, or plate level adjustments, please export as Tif and use https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/", InstructionsFont);	
			gd.setInsets(0,0,0);	gd.addMessage("   to create JPGs or PNGs.", InstructionsFont);		
			gd.setInsets(10,0,0);	gd.addMessage("This plugin runs only in FIJI (not in a blank ImageJ, where there is not OME BioFormats integration).", InstructionsFont);		
						
			gd.setInsets(10,0,0);	gd.addMessage("Processing Settings", SubHeadingFont);		
			gd.setInsets(0,0,0);		gd.addChoice("Image input type", imageType, selectedImageType);

			gd.setInsets(5,0,0);		gd.addCheckbox("Export PNG files for direct Memento upload", writePNG);
			gd.setInsets(0,0,0);		gd.addStringField("Filepath for PNG output folders (if selected)", outPathPNG, 40);
			gd.setInsets(5,0,0);		gd.addCheckbox("Export JPG files for direct Memento upload", writeJPG);
			gd.setInsets(0,0,0);		gd.addStringField("Filepath for JPG output folders (if selected)", outPathJPG, 40);
			gd.setInsets(5,0,0);		gd.addCheckbox("Export single-channel tif files for analysis / intensity-scaling", writeTif);
			gd.setInsets(0,0,0);		gd.addStringField("Filepath for single-channel tif output folders (if selected)", outPathTif, 40);
			gd.setInsets(5,0,0);		gd.addCheckbox("Export tif hyperstacks for analysis / intensity-scaling", writeTifStacks);
			gd.setInsets(0,0,0);		gd.addStringField("Filepath for tif hyperstacks output folders (if selected)", outPathTifStacks, 40);
			
			
			gd.setInsets(10,0,0);		gd.addCheckbox("Change colors of the individual channels | number of channels", changeCColors);
			gd.setInsets(-23,250,0);	gd.addNumericField("", numberOfChannels, 0);
			gd.setInsets(-2,10,0);	gd.addMessage("When activating this checkbox you will receive a separate dialog to modify channel colors in the next step.", InstructionsFont);
			
			gd.setInsets(10,0,0);	gd.addMessage("Naming of output files", SubHeadingFont);
			gd.setInsets(0,0,0);		gd.addCheckbox("Relabel seriesnames based on table", relabelSeries);		
			gd.setInsets(0,10,0);	gd.addMessage("When enabling this function you will be requested to select a csv file that contains the columns ", InstructionsFont);
			gd.setInsets(0,10,0);	gd.addMessage("Antibody,Protein,Plate,Well. This file is used for creating the folder structure where output files", InstructionsFont);
			gd.setInsets(0,10,0);	gd.addMessage("are placed in: Antibody_Protein/Plate_Well_FieldOfView/zXX/[C1.png,C2.png,...]", InstructionsFont);
			
			gd.setInsets(10,0,0);	gd.addMessage("Input files", SubHeadingFont);
			gd.setInsets(0,0,0);		gd.addMessage("A dialog will be shown after this dialog, which allows you to list the .lif files to be processed.", InstructionsFont);
			
			gd.setInsets(10,0,0);	gd.addMessage("Extended modes", SubHeadingFont);
			gd.setInsets(0,0,0);		gd.addCheckbox("Extended logging for diagnosis of errors", diagnosisLogging);
					
			gd.showDialog();
			//show Dialog-----------------------------------------------------------------

			//read and process variables--------------------------------------------------	
			selectedImageType = gd.getNextChoice();
			writePNG = gd.getNextBoolean();
			outPathPNG = gd.getNextString();
			writeJPG = gd.getNextBoolean();
			outPathJPG = gd.getNextString();
			writeTif = gd.getNextBoolean();
			outPathTif = gd.getNextString();
			writeTifStacks = gd.getNextBoolean();
			outPathTifStacks = gd.getNextString();
			changeCColors = gd.getNextBoolean();
			numberOfChannels = (int) gd.getNextNumber();	
			relabelSeries = gd.getNextBoolean();
			diagnosisLogging = gd.getNextBoolean();
			//read and process variables--------------------------------------------------
			if (gd.wasCanceled()) return;
			
			boolean leave = true;
			if (!writePNG && !writeJPG && !writeTif && !writeTifStacks) {
				new WaitForUserDialog("You need to select at least one output format (tiff or png or jpg).\nPlease modfiy the settings accordingly!").show();
				leave = false;
			}
			
			// Validation of file paths specified
			if (writePNG) {
				if(!new File(outPathPNG).exists()) {
					leave = false;
					new WaitForUserDialog("The specified PNG output file path does not exist.\nMake sure to create the specified folder or correct the path!\n\nSpecified path:\n"+outPathPNG).show();
				}
			}
			if (writeJPG) {
				if(!new File(outPathJPG).exists()) {
					leave = false;
					new WaitForUserDialog("The specified JPG output file path does not exist.\nMake sure to create the specified folder or correct the path!\n\nSpecified path:\n"+outPathJPG).show();
				}			
			}
			if (writeTif) {
				if(!new File(outPathTif).exists()) {
					leave = false;
					new WaitForUserDialog("The specified Tif output file path does not exist.\nMake sure to create the specified folder or correct the path!\n\nSpecified path:\n"+outPathTif).show();
				}
			}
			if (writeTifStacks) {
				if(!new File(outPathTifStacks).exists()) {
					leave = false;
					new WaitForUserDialog("The specified hyperstack-Tif output file path does not exist.\nMake sure to create the specified folder or correct the path!\n\nSpecified path:\n"+outPathTifStacks).show();
				}
			}
			
			if(leave) {
				break;
			}
		}
		
		
		
		if(changeCColors) {
			selectedChannelColors = new String[numberOfChannels];
			for(int c = 0; c < numberOfChannels; c++) {
				selectedChannelColors [c] = "ORIGINAL";
			}
			
			gd = new GenericDialog(PLUGINNAME + " - set parameters");	
			//show Dialog-----------------------------------------------------------------
			gd.setInsets(0,0,0);	gd.addMessage(PLUGINNAME + ", Version " + PLUGINVERSION + ", \u00a9 2022-2024 JN Hansen", SuperHeadingFont);	
			
			gd.setInsets(15,0,0);	gd.addMessage("Modify channel colors for output", SubHeadingFont);
			for(int c = 0; c < numberOfChannels; c++) {
				gd.setInsets(0,0,0);		gd.addChoice("Channel " + (c+1) 
					+ " - Color:", Colors, selectedChannelColors [c]);				
			}
			
			gd.showDialog();
			//show Dialog-----------------------------------------------------------------

			//read and process variables--------------------------------------------------	
			for(int c = 0; c < numberOfChannels; c++) {
				selectedChannelColors [c] = gd.getNextChoice();
			}			
			//read and process variables--------------------------------------------------
			if (gd.wasCanceled()) return;
		}	
		
		
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// -------------------------------LOAD FILES-----------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		
		{
			/*
			 * Loading .lif files and identifying the content
			 * */
			OpenFilesDialog od = new OpenFilesDialog(false);
			od.setLocation(0, 0);
			od.setVisible(true);

			od.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(WindowEvent winEvt) {
					return;
				}
			});

			// Waiting for od to be done
			while (od.done == false) {
				try {
					Thread.currentThread().sleep(50);
				} catch (Exception e) {
				}
			}

			tasks = od.filesToOpen.size();
			series = new String[tasks];
			name = new String[tasks];
			dir = new String[tasks];
			fullPath = new String[tasks];
			for (int task = 0; task < tasks; task++) {
				fullPath[task] = od.filesToOpen.get(task).toString();
				if(diagnosisLogging) IJ.log("ORIGINAL: " + fullPath[task]);
				name[task] = od.filesToOpen.get(task).getName();
				series[task] = name[task];
				if(series[task].contains(".")) {
					series[task] = series[task].substring(0,series[task].lastIndexOf("."));
				}
				dir[task] = od.filesToOpen.get(task).getParent() + System.getProperty("file.separator");
			}
		}
		
		
		// Request csv file if needed
		String tableFileDir = "";
		String tableFileName = "";
		String [][] lookUpTable = null;
		if(relabelSeries){
			boolean leave = true;
			while(true) {
				new WaitForUserDialog("Please open the table .csv file with columns Antibody,Protein,Plate,Well in the following dialog!\n"
						+"Make sure there is no duplicate entries in the well column and that you provide only information for one plate (the specific plate analyzed)!").show();
							
		    	OpenDialog odTable;
		    	odTable = new OpenDialog("Open table file with columns Antibody,Protein,Plate,Well", null);
		    	tableFileDir = odTable.getDirectory();
	    		tableFileName = odTable.getFileName();
	    		
	    		if (tableFileName == null) {
	    		    IJ.showMessage("Process canceled", "No file was selected.");
	    		    return;
	    		}
	    		
	    		try {
	        		lookUpTable = getTableFromCSV(tableFileDir + System.getProperty("file.separator") + tableFileName);
	        		if(lookUpTable == null) {
	        			leave = false;
		    			new WaitForUserDialog("Processing table .csv file failed. Too many columns in table").show();
	        		}
	    		}catch(IOException e){
	    			leave = false;
	    			new WaitForUserDialog("Loading table .csv file failed.\nFile path does not exist:\n" 
	    					+ tableFileDir + System.getProperty("file.separator") + tableFileName 
	    					+ "\nError trace printed in LOG window.").show();
	    			String out = "";
					for (int err = 0; err < e.getStackTrace().length; err++) {
						out += " \n " + e.getStackTrace()[err].toString();
					}
					IJ.log("ERROR when loading file " + tableFileDir + System.getProperty("file.separator") + tableFileName 
							+ "\nError message: " + e.getMessage()
							+ "\nError localized message: " + e.getLocalizedMessage()
							+ "\nError cause: " + e.getCause() 
							+ "\nDetailed message:"
							+ "\n" + out);
	    		}catch(Exception e) {
	    			leave = false;
	    			new WaitForUserDialog("Error during processing table .csv\nMake sure to load a valid file." 
	    					+ "\nError trace printed in LOG window.").show();
	    			String out = "";
					for (int err = 0; err < e.getStackTrace().length; err++) {
						out += " \n " + e.getStackTrace()[err].toString();
					}
					IJ.log("ERROR when loading file " + tableFileDir + System.getProperty("file.separator") + tableFileName 
							+ "\nError message: " + e.getMessage()
							+ "\nError localized message: " + e.getLocalizedMessage()
							+ "\nError cause: " + e.getCause() 
							+ "\nDetailed message:"
							+ "\n" + out);   			
	    		}
	    		
	    		if(diagnosisLogging) {
	    			IJ.log("Log of read in csv file:");
	    			for(int i = 0; i < lookUpTable[0].length; i++) {
	            		IJ.log(lookUpTable[0][i] + "	" + lookUpTable[1][i] + "	" + lookUpTable[2][i] + "	" + lookUpTable[3][i]);    	    				
	    			}		
	    		}
	    		if(leave) break;
			}
		}
		
		// Bioformats option - screen for series in lif file
		//For BioFormats - screen for series and add tasks accordingly
		ImporterOptions bfOptions = null;
		ImportProcess process = null;
		String previousFile = "";
		int seriesID [] = new int [tasks];
		int totSeries [] = new int [tasks];
		int nOfSeries = -1;
		Arrays.fill(seriesID, 0);
		Arrays.fill(totSeries, 1);
		
		for(int i = tasks-1; i >= 0; i--){
			IJ.showProgress((tasks-i)/tasks);
			if(name [i].substring(name[i].lastIndexOf(".")).equals(".tif")
					|| name [i].substring(name[i].lastIndexOf(".")).equals(".TIF")
					|| name [i].substring(name[i].lastIndexOf(".")).equals(".tiff")
					|| name [i].substring(name[i].lastIndexOf(".")).equals(".TIFF")) {
				continue;
			}
			try {
				if(!((dir[i]+name[i]).equals(previousFile))){
					previousFile = dir[i]+name[i];
					bfOptions = new ImporterOptions();
					bfOptions.setId(""+dir[i]+name[i]+"");
					bfOptions.setVirtual(true);
					process = new ImportProcess(bfOptions);
					nOfSeries = getNumberOfSeries(process);
					if(diagnosisLogging) {
						IJ.log(dir[i]+name[i] + " - nSeries: " + nOfSeries);						
					}
				}				
				
				if(nOfSeries > 1) {
					String [] nameTemp = new String [name.length+nOfSeries-1], 
							dirTemp = new String [name.length+nOfSeries-1];
					int [] seriesTemp = new int [nameTemp.length],
							totSeriesTemp = new int [nameTemp.length]; 
					String [] seriesNameTemp = new String [nameTemp.length];
					
					for(int j = 0; j < i; j++) {
						nameTemp [j] = name [j]; 
						dirTemp [j] = dir [j];
						seriesTemp [j] = seriesID [j];
						seriesNameTemp [j] = series [j];
						totSeriesTemp [j] = totSeries [j];
						
					}
					for(int j = 0; j < nOfSeries; j++) {
						nameTemp [i+j] = name [i]; 
						dirTemp [i+j] = dir [i];
						seriesTemp [i+j] = j;
						seriesNameTemp [i+j] = getSeriesName(process, j);
						totSeriesTemp [i+j] = nOfSeries;
					}
					for(int j = i+1; j < name.length; j++) {
						nameTemp [j+nOfSeries-1] = name [j]; 
						dirTemp [j+nOfSeries-1] = dir [j];
						seriesTemp [j+nOfSeries-1] = seriesID [j];
						seriesNameTemp [j+nOfSeries-1] = series [j];
						totSeriesTemp [j+nOfSeries-1] = totSeries [j];
					}
					
					//copy arrays
					tasks = nameTemp.length;
					name = new String [tasks];
					dir = new String [tasks];
					seriesID = new int [tasks];
					series = new String [tasks];
					totSeries = new int [tasks];
					fullPath = new String [tasks];
					
					for(int j = 0; j < nameTemp.length; j++) {						
						name [j] = nameTemp [j];
						dir [j] = dirTemp [j];
						fullPath [j] = dir [j] + System.getProperty("file.separator") + name [j] ; 
						seriesID [j] = seriesTemp [j];
						series [j] = seriesNameTemp [j];
						totSeries [j] = totSeriesTemp [j];
					}
				}
			} catch (Exception e) {
				String out = "" + e.getMessage();
				out += "\n" + e.getCause();
				for(int err = 0; err < e.getStackTrace().length; err++){
					out += " \n " + e.getStackTrace()[err].toString();
				}			
				IJ.log("error: " + name [i] +": "+ out);
			}
		}		
		
		if(diagnosisLogging) {
			for (int task = 0; task < tasks; task++) {
				IJ.log("ORIGINAL: " + fullPath[task]);
				IJ.log("series:" + series[task]);
				IJ.log("name:" + name[task]);
				IJ.log("dir:" + dir[task]);
			}			
		}
		
		if (tasks == 0) {
			new WaitForUserDialog("No folders selected!").show();
			return;
		}

		// add progressDialog
		progress = new ProgressDialog(name, series);
		progress.setLocation(0, 0);
		progress.setVisible(true);
		progress.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if (processingDone == false) {
					IJ.error("Script stopped...");
				}
				continueProcessing = false;
				return;
			}
		});

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// ---------------------------------RUN TASKS----------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

		String [] namingInfo;
		String region;
		for (int task = 0; task < tasks; task++) {
			running: while (continueProcessing) {
				progress.updateBarText("in progress...");

				/**
				 * Open Image
				 * */
				ImagePlus imp;
		   		try{			   		
		   			if(name [task].substring(name[task].lastIndexOf(".")).equals(".tif")
							|| name [task].substring(name[task].lastIndexOf(".")).equals(".TIF")
							|| name [task].substring(name[task].lastIndexOf(".")).equals(".tiff")
							|| name [task].substring(name[task].lastIndexOf(".")).equals(".TIFF")) {
		   				//TIFF file
		   				imp = IJ.openImage(""+dir[task]+name[task]+"");		
		   			}else{
		   				//READ WITH BIOFORMATS
		   				bfOptions = new ImporterOptions();
		   				bfOptions.setId(""+dir[task]+name[task]+"");
		   				bfOptions.setVirtual(false);
		   				bfOptions.setAutoscale(true);
		   				bfOptions.setColorMode(ImporterOptions.COLOR_MODE_COMPOSITE);
		   				for(int i = 0; i < totSeries[task]; i++) {
		   					if(i==seriesID[task]) {
		   						bfOptions.setSeriesOn(i, true);
		   					}else {
		   						bfOptions.setSeriesOn(i, false);
		   					}
		   				}
		   				ImagePlus [] imps = BF.openImagePlus(bfOptions);
		   				imp = imps [0];	
		   				imp.setDisplayMode(IJ.COMPOSITE);
		   			}
		   			imp.hide();
					imp.deleteRoi();
			   	}catch (Exception e) {
			   		progress.notifyMessage("Task " + (task+1) + "/" + tasks + ": file is no image - could not be processed!", ProgressDialog.ERROR);
					progress.moveTask(task);	
					break running;
				}
		   		
		   		/**
				 * Processing
				 * */		
				{
					try {
						if(relabelSeries) {
							namingInfo = receiveWellInformationFromTable(series[task], lookUpTable);
							region = getRegionIDFromSeriesName(series[task]);
							
							if(namingInfo == null) {
								progress.notifyMessage("Task " + (task+1) + " - Could not find well information in csv file for " + series[task] + "! Will use series name instad.", ProgressDialog.LOG);
								namingInfo = new String[] {"UNKNOWN","UNKNOWN","UNKNOWN","UNKNOWN"};
							}
							if(region == null) {
								progress.notifyMessage("Task " + (task+1) + " - Could not find region information in series name '" + series[task] + "'! Will use series ID instead.", ProgressDialog.LOG);
								region = "Series_" + seriesID[task];
							}
							if(diagnosisLogging) {
								progress.notifyMessage("Task " + (task+1) + " - found well information (Antibody: " + namingInfo [0] 
										+ "; Protein: " + namingInfo [1] 
												+ "; Plate: "  + namingInfo [2] 
														+ "; Well: "  + namingInfo [3] + ") from series name (" + series[task] + ")", ProgressDialog.LOG);							
							}
						}else {
							namingInfo = new String[] {"UNKNOWN","UNKNOWN","UNKNOWN","UNKNOWN"};
							region = "Series_" + seriesID[task];
						}						
					} catch (Exception e) {
						String out = "";
						for (int err = 0; err < e.getStackTrace().length; err++) {
							out += " \n " + e.getStackTrace()[err].toString();
						}
						progress.notifyMessage("Task " + (task + 1) + "/" + tasks + ": Could not process "
								+ series[task] + " - Error " + e.getCause() + " - Detailed message:\n" + out,
								ProgressDialog.ERROR);
						break running;
					}
					
					autoAdjustMinMax(imp,0.1);
					
					//Create folder structure and save images
					//"Antibody/FieldOfView/PlaneZ/[channelC1, channelC2, channelC3...]"
					
					File newFPNG = null;
					File newFJPG = null;
					File newFTif = null;
					String filename;
					if(namingInfo[0].equals("UNKNOWN")) {
//						if(writePNG) {
							newFPNG = new File(outPathPNG + System.getProperty("file.separator") + name[task] + System.getProperty("file.separator") + region + System.getProperty("file.separator"));
//						}
//						if(writeJPG) {
							newFJPG = new File(outPathJPG + System.getProperty("file.separator") + name[task] + System.getProperty("file.separator") + region + System.getProperty("file.separator"));
//						}
//						if(writeTif) {
							newFTif = new File(outPathTif + System.getProperty("file.separator") + name[task] + System.getProperty("file.separator") + region + System.getProperty("file.separator"));
//						}
						filename = "";
					}else {
//						if(writePNG) {
							newFPNG = new File(outPathPNG + System.getProperty("file.separator") + namingInfo[0] +"_" + namingInfo[1] 
									+ System.getProperty("file.separator") + namingInfo[2] + "_" + namingInfo[3] + "_"+ region
									+ System.getProperty("file.separator"));
//						}
//						if(writeJPG) {
							newFJPG = new File(outPathJPG + System.getProperty("file.separator") + namingInfo[0] +"_" + namingInfo[1] 
									+ System.getProperty("file.separator") + namingInfo[2] + "_" + namingInfo[3] + "_"+ region
									+ System.getProperty("file.separator"));
//						}
//						if(writeTif) {
							newFTif = new File(outPathTif + System.getProperty("file.separator") + namingInfo[0] +"_" + namingInfo[1] 
									+ System.getProperty("file.separator") + namingInfo[2] + "_" + namingInfo[3] + "_"+ region
									+ System.getProperty("file.separator"));
//						}
						filename = "";
					}
					if(writePNG && !newFPNG.exists()) {
						newFPNG.mkdirs();
					}

					if(writeJPG && !newFJPG.exists()) {
						newFJPG.mkdirs();
					}
					if(writeTif && !newFTif.exists()) {
						newFTif.mkdirs();
					}
					
					//TODO
//					if(writeTif && writePNG) {
						saveIndividualImages(imp, newFPNG.getAbsolutePath() + System.getProperty("file.separator"),
								newFJPG.getAbsolutePath() + System.getProperty("file.separator"), 
								newFTif.getAbsolutePath() + System.getProperty("file.separator"), 
							filename, task);							
//					}else if(!writeTif && writePNG){
//						saveIndividualImages(imp, newFPNG.getAbsolutePath() + System.getProperty("file.separator"),
//							newFPNG.getAbsolutePath() + System.getProperty("file.separator"), 
//							filename);							
//					}else if(writeTif && !writePNG){
//						saveIndividualImages(imp, newFTif.getAbsolutePath() + System.getProperty("file.separator"),
//								newFTif.getAbsolutePath() + System.getProperty("file.separator"), 
//								filename);
//					}
					
					if(writeTifStacks) {
						if(namingInfo[0].equals("UNKNOWN")) {
							newFTif = new File(outPathTifStacks + System.getProperty("file.separator") + name[task] + System.getProperty("file.separator") + region + System.getProperty("file.separator"));
						}else {							
							newFTif = new File(outPathTifStacks + System.getProperty("file.separator") + namingInfo[0] +"_" + namingInfo[1] 
									+ System.getProperty("file.separator") + namingInfo[2] + "_" + namingInfo[3] + System.getProperty("file.separator"));
						}
						filename = "";
						if(!newFTif.exists()) {
							newFTif.mkdirs();
						}
						//save tif image
						IJ.saveAs(imp, "Tif", newFTif.getAbsolutePath() + System.getProperty("file.separator") + "R" + region + ".tif");
					}
				}

				imp.changes = false;
				imp.close();
				
				processingDone = true;
				progress.updateBarText("finished!");
				progress.setBar(1.0);
				break running;
			}
			progress.moveTask(task);
			System.gc();
		}
	}	

	/**
	 * get number of series 
	 * */
	private int getNumberOfSeries(ImporterOptions options) throws FormatException, IOException{
		ImportProcess process = new ImportProcess(options);
		if (!process.execute()) return -1;
		return process.getSeriesCount();
	}

	/**
	 * get number of series 
	 * */
	private int getNumberOfSeries(ImportProcess process) throws FormatException, IOException{
		if (!process.execute()) return -1;
		return process.getSeriesCount();
	}

	/**
	 * @return name of the @param series (0 <= series < number of series)
	 * */
	private String getSeriesName(ImporterOptions options, int series) throws FormatException, IOException{
		ImportProcess process = new ImportProcess(options);
		if (!process.execute()) return "NaN";
		return process.getSeriesLabel(series);
	}
	
	/**
	 * @return name of the @param series (0 <= series < number of series)
	 * */
	private String getSeriesName(ImportProcess process, int series) throws FormatException, IOException{
		if (!process.execute()) return "NaN";
		return process.getSeriesLabel(series);
	}
	
	private static String [][] getTableFromCSV(String filePath) throws IOException{		
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			LinkedList<String> lines = new LinkedList<String>();
			reading: while(true){
				try{
					line = br.readLine();
					if(line.equals(null)){
						break reading;
					}
					lines.add(line);
				}catch(Exception e){
					break reading;
				}				
			}			
			br.close();
			fr.close();
			
			String [][] out = new String [4][lines.size()];
			for(int i = 0; i < out[0].length; i++) {
				line = lines.get(i);
				out [3][i] = line.substring(line.lastIndexOf(",")+1);
				line = line.substring(0, line.lastIndexOf(","));
				out [2][i] = line.substring(line.lastIndexOf(",")+1);
				line = line.substring(0, line.lastIndexOf(","));
				out [1][i] = line.substring(line.lastIndexOf(",")+1);
				line = line.substring(0, line.lastIndexOf(","));
				out [0][i] = line;
				if(line.contains(",")) {
					return null;
				}
			}
			lines.clear();
			lines = null;
			
			return out;
	}
	
	private static String [] receiveWellInformationFromTable(String seriesName, String [][] table) {
		String wellID;
		// Fourth column is well information
		for(int i = 0; i < table[3].length; i++) {
			//Example seriesName from a .lif tilescan on Sp8: "Series_10: TileScan 1/H/5/R4: 2048 x 2048; 60 planes (5C x 12Z)"
			wellID = "/" + table[3][i].substring(0,1) + "/" + table[3][i].substring(1) + "/";
			if(seriesName.contains(wellID)) {
				return new String[] {table[0][i],table[1][i],table[2][i],table[3][i]};
			}
			
			//Example seriesName from a .lif tilescan on Stellaris 8: "Series_6: TileScan 2/B3 Position3: 2048 x 2048; 44 planes (4C x 11Z)"
			wellID = "/" + table[3][i] + " ";
			if(seriesName.contains(wellID)) {
				return new String[] {table[0][i],table[1][i],table[2][i],table[3][i]};
			}
		}		
		return null;
	}
	
	private static String getRegionIDFromSeriesName(String seriesName) {
		//Example seriesName from a .lif tilescan on Sp8: "Series_10: TileScan 1/H/5/R4: 2048 x 2048; 60 planes (5C x 12Z)"
		if(seriesName.contains("/R") && seriesName.contains(": ") && seriesName.contains("; ") && seriesName.contains("(")) {			
			return seriesName.substring(seriesName.lastIndexOf("/R")+1,seriesName.lastIndexOf(": "));
		}
		
		//Example seriesName from a .lif tilescan on Stellaris 8: "Series_6: TileScan 2/B3 Position3: 2048 x 2048; 44 planes (4C x 11Z)"
		if(seriesName.contains(" Position") && seriesName.contains(": ") && seriesName.contains("; ") && seriesName.contains("(")) {			
			return seriesName.substring(seriesName.lastIndexOf(" Position")+1,seriesName.lastIndexOf(": "));
		}
		
		//Example seriesName from a .lif tilescan on Stellaris 8: "Series_153: TileScan 2/G/7/P3: 2048 x 2048; 168 planes (4C x 42Z)"
		if(seriesName.contains("/P") && seriesName.contains(": ") && seriesName.contains("; ") && seriesName.contains("(")) {			
			return seriesName.substring(seriesName.lastIndexOf("/P")+1,seriesName.lastIndexOf(": "));
		}
				
		return null;
	}
	
	/**
	 * @param channel: 1 <= channel,slice,frame <= # channels,slices,frames
	 * Added jpg option in version 0.0.9
	 * */
	private void saveIndividualImages(ImagePlus imp, String saveFolderPNG, String saveFolderJPG, String saveFolderTif, String fileName, int task) {		
		//Making the folders required
		String indivOutPathPNG;
		String indivOutPathJPG;
		String indivOutPathTif;
		String indivOutAddPath;
		File indivOutFilePNG, indivOutFileJPG, indivOutFileTif;
		
		for(int s = 0; s < imp.getNSlices(); s++) {			
			for(int t = 0; t < imp.getNFrames(); t++) {
				indivOutAddPath = System.getProperty("file.separator");				
				if(s < 10) {
					indivOutAddPath += "z0" + s;
				}else {
					indivOutAddPath += "z" + s;
				}
				if(imp.getNFrames()>1) {
					if(t < 10 && imp.getNFrames()<=100) {
						indivOutAddPath += "_t0" + t;
					}else {
						indivOutAddPath += "_t" + t;
					}
				}
				if(writePNG) {
					indivOutPathPNG = saveFolderPNG + indivOutAddPath;
					indivOutFilePNG = new File(indivOutPathPNG);
					if(!indivOutFilePNG.exists()) indivOutFilePNG.mkdir();
				}
				if(writeJPG) {
					indivOutPathJPG = saveFolderJPG + indivOutAddPath;
					indivOutFileJPG = new File(indivOutPathJPG);
					if(!indivOutFileJPG.exists()) indivOutFileJPG.mkdir();
				}
				if(writeTif) {
					indivOutPathTif = saveFolderTif + indivOutAddPath;
					indivOutFileTif = new File(indivOutPathTif);
					if(!indivOutFileTif.exists()) indivOutFileTif.mkdir();					
				}
			}			
		}

		
		ImagePlus impNew;
		String color;
		BufferedImage bi;
		Image img;
		for(int c = 0; c < imp.getNChannels(); c++){
			for(int s = 0; s < imp.getNSlices(); s++) {
				for(int t = 0; t < imp.getNFrames(); t++) {
					//Determine specific output path
					indivOutAddPath = System.getProperty("file.separator");
					if(s < 10) {
						indivOutAddPath += "z0" + s;
					}else {
						indivOutAddPath += "z" + s;
					}
					if(imp.getNFrames()>1) {
						if(t < 10 && imp.getNFrames()<=100) {
							indivOutAddPath += "_t0" + t;
						}else {
							indivOutAddPath += "_t" + t;
						}
					}					
					indivOutAddPath += System.getProperty("file.separator") + fileName;
					if(!fileName.equals("")) {
						indivOutAddPath += "_";
					}					
					indivOutAddPath += "C" + c + ".png";
					
					if(changeCColors && c < selectedChannelColors.length){
						color = selectedChannelColors [c];
					}else {
						color = "ORIGINAL";
					}
										
					//retrieve image
					impNew = getIndividualImage(imp, c+1, s+1, t+1, false, color);
					if(writeTif) {
						indivOutPathTif = saveFolderTif + indivOutAddPath;
						
						//save tif image
						IJ.saveAs(impNew, "Tif", indivOutPathTif);
					}
					if(writePNG) {
						indivOutPathPNG = saveFolderPNG + indivOutAddPath;
						
						//add transparency, and write image
						bi = impNew.getBufferedImage();
						img = colorToTransparent(bi, Color.BLACK);
						bi = imageToBuffered(img, bi.getWidth(), bi.getHeight());						
						try {
							saveBufferedImageAsPNG(bi, indivOutPathPNG);
						}catch (IOException e) {							
							progress.notifyMessage("Task " + (task+1) + "/" + tasks + ": ERROR - PNG file could not be saved (Path: "
									+ indivOutPathPNG + ")!", ProgressDialog.ERROR);
						}
					}
					if(writeJPG) {
						indivOutPathJPG = saveFolderJPG + indivOutAddPath;
						bi = impNew.getBufferedImage();
						try {
							saveBufferedImageAsJPG(bi, indivOutPathJPG);
						}catch (IOException e) {							
					   		progress.notifyMessage("Task " + (task+1) + "/" + tasks + ": ERROR - JPG file could not be saved (Path: "
					   				+ indivOutPathJPG + ")!", ProgressDialog.ERROR);
						}
					}
					impNew.changes = false;
					impNew.close();
				}
			}
		}
	}
	
	private static void saveBufferedImageAsPNG(BufferedImage bi, String path) throws IOException {
		ImageIO.write(bi, "png", new File(path));
	}
	

	private static void saveBufferedImageAsJPG(BufferedImage bi, String path) throws IOException {
		ImageIO.write(bi, "jpg", new File(path));
	}

	private BufferedImage imageToBuffered (Image inImage, int width, int height){
		BufferedImage outBI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = outBI.createGraphics();
		g.drawImage(inImage, 0, 0, null);
		g.dispose();
		return outBI;
	}

	public static Image colorToTransparent(BufferedImage bi, final Color color) {
		ImageFilter filter = new RGBImageFilter() {
			//Set alpha bits to opaque for the color we want to use
			public int marker = color.getRGB() | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == marker) {
					//Assign zero to the alpha bits to make it transparent
					return 0x00FFFFFF & rgb;
				} else {
					return rgb;
				}
			}
		};

		ImageProducer iProd = new FilteredImageSource(bi.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(iProd);
	}
	  
	  
	/**
	 * @param channel: 1 <= channel,slice,frame <= # channels,slices,frames
	 * @param color: "ORIGINAL" (no change of Color), "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "Grays"
	 * */
	private static ImagePlus getIndividualImage (ImagePlus imp, int channel, int slice, int frame, boolean copyOverlay, String color){
		ImagePlus impNew = IJ.createHyperStack("channel image", imp.getWidth(), imp.getHeight(), 1, 1, 1, imp.getBitDepth());
		int index = 0, indexNew = 0;
		
		index = imp.getStackIndex(channel, slice, frame)-1;
		indexNew = impNew.getStackIndex(1, 1, 1)-1;
		for(int x = 0; x < imp.getWidth(); x++){
			for(int y = 0; y < imp.getHeight(); y++){
				impNew.getStack().setVoxel(x, y, indexNew, imp.getStack().getVoxel(x, y, index));		
			}
		}
		
		imp.setC(channel);
		imp.setT(frame);
		imp.setSlice(slice);
		
		impNew.setDisplayRange(imp.getDisplayRangeMin(),imp.getDisplayRangeMax());
		
		if(color.equals("ORIGINAL")) {
			impNew.setLut(imp.getLuts()[channel-1]);
		}else {
			IJ.run(impNew,"" + color,"");
		}
		
		impNew.setCalibration(imp.getCalibration());
		
		if(copyOverlay)	impNew.setOverlay(imp.getOverlay().duplicate());
		
		return impNew;
	}
	
	/**
	 * Enhances the contrast by oversaturating the top @param "percenOversaturated" percent.
	 * 
	 * */
	private void autoAdjustMinMax(ImagePlus imp, double percentOversaturated) {
		double max;
		for(int c = 0; c < imp.getNChannels(); c++){
			max = Double.NEGATIVE_INFINITY;
			imp.setC(c+1);
			for(int t = 0; t < imp.getNFrames(); t++) {
				imp.setT(t+1);
				for(int s = 0; s < imp.getNSlices(); s++) {
					imp.setZ(s+1);
					IJ.run(imp, "Enhance Contrast...", "saturated=" + percentOversaturated);
					if(imp.getDisplayRangeMax()>max) {
						max = imp.getDisplayRangeMax();
					}							
				}
			}
			if(max < 255.0)	max = 255.0;
			imp.setDisplayRange(0, max);
		}
	}
	
}// end main class