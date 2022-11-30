package sp8LifToMemento_jnh;

/** ===============================================================================
* Sp8Lif_To_Memento ImageJ/FIJI Plugin v0.0.1
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
* Date: November, 2022 (This Version: November 30, 2022)
*   
* For any questions please feel free to contact me (jan.hansen@scilifelab.se).
* =============================================================================== */

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.UIManager;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.WaitForUserDialog;
import ij.plugin.PlugIn;
import loci.formats.*;
import ome.units.*;

public class Sp8LifToMemento_Main implements PlugIn {
	// Name variables
	static final String PLUGINNAME = "Sp8Lif_To_Memento";
	static final String PLUGINVERSION = "0.0.1";

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
	
	String outPath = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "Sp8LifToMemento"
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
		
		GenericDialog gd = new GenericDialog(PLUGINNAME + " - set parameters");	
		//show Dialog-----------------------------------------------------------------
		gd.setInsets(0,0,0);	gd.addMessage(PLUGINNAME + ", Version " + PLUGINVERSION + ", \u00a9 2022 JN Hansen", SuperHeadingFont);	
		

		gd.setInsets(15,0,0);	gd.addMessage("Notes:", SubHeadingFont);
		
		gd.setInsets(0,0,0);	gd.addMessage("The plugin processes .lif files from 'TileScans' acquired with the Leica Sp8. TileScans here refers to automated acquisition of many", InstructionsFont);
		gd.setInsets(0,0,0);	gd.addMessage("images on a multi-well plates, all stored in one .lif file.", InstructionsFont);
		gd.setInsets(0,0,0);	gd.addMessage("The plugin generates an output directory with PNGs, which can be imported to Memento.", InstructionsFont);	
		gd.setInsets(20,0,0);	gd.addMessage("This plugin runs only in FIJI (not in a blank ImageJ, where there is not OME BioFormats integration).", InstructionsFont);		
					
		gd.setInsets(15,0,0);	gd.addMessage("Processing Settings", SubHeadingFont);		
		gd.setInsets(0,0,0);	gd.addChoice("Image type", imageType, selectedImageType);
		gd.setInsets(20,0,0);	gd.addStringField("Filepath to output file", outPath);
		gd.setInsets(0,0,0);	gd.addMessage("This path defines where outputfiles are stored.", InstructionsFont);
		gd.setInsets(0,0,0);	gd.addMessage("Make sure this path does not contain similarly named files - the program will overwrite identically named files!.", InstructionsFont);
		
		gd.setInsets(15,0,0);	gd.addMessage("Input files", SubHeadingFont);
		gd.setInsets(0,0,0);	gd.addMessage("A dialog will be shown when you press OK that allows you to list the .lif files to be processed.", InstructionsFont);
				
		gd.showDialog();
		//show Dialog-----------------------------------------------------------------

		//read and process variables--------------------------------------------------	
		selectedImageType = gd.getNextChoice();
		outPath = gd.getNextString();
		//read and process variables--------------------------------------------------
		if (gd.wasCanceled()) return;
		
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
				series[task] = "";
				name[task] = od.filesToOpen.get(task).getName();
				dir[task] = od.filesToOpen.get(task).getParent();

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

		for (int task = 0; task < tasks; task++) {
			running: while (continueProcessing) {
				Date startDate = new Date();
				progress.updateBarText("in progress...");

				{
					/**
					 * TODO Write code for opening lif and storing as Memento format
					 * */
					
					try {
						
						
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
				}

				processingDone = true;
				progress.updateBarText("finished!");
				progress.setBar(1.0);
				break running;
			}
			progress.moveTask(task);
		}
	}	
}// end main class