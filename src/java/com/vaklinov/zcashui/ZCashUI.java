/***********************************************************************************
 *  _________          _       _   _ ___ 
 * |__  / ___|__ _ ___| |__   | | | |_ _|
 *   / / |   / _` / __| '_ \  | | | || | 
 *  / /| |__| (_| \__ \ | | | | |_| || | 
 * /____\____\__,_|___/_| |_|  \___/|___|
 *                                       
 * Copyright (c) 2016 Ivan Vaklinov <ivan@vaklinov.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 **********************************************************************************/
package com.vaklinov.zcashui;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;
import com.vaklinov.zcashui.ZCashInstallationObserver.InstallationDetectionException;


/**
 * Main ZCash Window.
 * 
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class ZCashUI 
	extends JFrame
{
	private ZCashInstallationObserver installationObserver;
	private ZCashClientCaller clientCaller;
	
	private JMenuItem menuItemExit;
	private JMenuItem menuItemAbout;

	private DashboardPanel dashboard;
	
	public ZCashUI()
		throws IOException, InterruptedException, WalletCallException
	{
		super("ZCash Swing Wallet UI 0.1 (beta)");
		Container contentPane = this.getContentPane();
		
		installationObserver = new ZCashInstallationObserver(OSUtil.getProgramDirectory());
		clientCaller = new ZCashClientCaller(OSUtil.getProgramDirectory());
		
		// Build content
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Dashboard", dashboard = new DashboardPanel(installationObserver, clientCaller));
		tabs.add("Adresses", new JPanel());
		tabs.add("Send cash", new JPanel());
		contentPane.add(tabs);
		
		this.setSize(new Dimension(800, 500));
		
		// Build menu
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("File");
		file.add(menuItemAbout = new JMenuItem("About..."));
		file.addSeparator();
		file.add(menuItemExit = new JMenuItem("Exit"));
		mb.add(file);
		this.setJMenuBar(mb);
		
		// Add listeners etc.
		menuItemExit.addActionListener(
			new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					ZCashUI.this.exit();
				}
			}
		);
		
		menuItemAbout.addActionListener(
			new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					AboutDialog ad = new AboutDialog(ZCashUI.this);
					ad.setVisible(true);
				}
			}
		);
		
		// Close operation
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() 
		{	
			@Override
			public void windowClosing(WindowEvent e) 
			{
				ZCashUI.this.exit();				
			}
		});
	}
	
	private void exit()
	{
		System.out.println("Exiting ...");
		ZCashUI.this.setVisible(false);
		ZCashUI.this.dispose();
		System.exit(0);
	}
	
	public static void main(String argv[])
		throws IOException
	{		
		try 
		{
			System.out.println("Starting ZCash Swing Wallet ...");
			System.out.println("Current directory: " + new File(".").getCanonicalPath());
			//System.out.println("System properties: " + System.getProperties().toString().replace(",", "\n"));
						
			////////////////////////////////////////////////////////////
		    for (LookAndFeelInfo ui : UIManager.getInstalledLookAndFeels()) 
		    {
		    	System.out.println("Available look and feel: " + ui.getName() + " " + ui.getClassName());
		        if (ui.getName().equals("Nimbus")) 
		        {
		            UIManager.setLookAndFeel(ui.getClassName());
		            break;
		        }
		    }
		    
		    /////////////////////////////////////////////////////
			ZCashUI ui = new ZCashUI();
			ui.setVisible(true);

		} catch (InstallationDetectionException ide) 
		{
			ide.printStackTrace();
			JOptionPane.showMessageDialog(
				null,
				"This program was started in directory: " + OSUtil.getProgramDirectory() + "\n" +
				ide.getMessage() + "\n" +
				"See the console output for more detailed error information!",
				"Installation error",
				JOptionPane.ERROR_MESSAGE);
		} catch (WalletCallException wce) 
		{
			wce.printStackTrace();
			JOptionPane.showMessageDialog(
				null,
				"There was a problem communicating with the ZCash daemon/wallet. \n" +
				"Please ensure that zcashd is started. Error message is: \n" +		
				 wce.getMessage() +
				"See the console output for more detailed error information!",
				"Wallet communication error",
				JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(
				null,
				"A general unexpected critical error has occurred: \n" + e.getMessage() + "\n" +
				"See the console output for more detailed error information!",
				"Error",
				JOptionPane.ERROR_MESSAGE);
		} 	
	}
}