package ted;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class TedTranslateDialog extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7178318494685933166L;
	private JScrollPane scrollPane;
	private JButton buttonSave;
	private JButton buttonOpen;
	private JPanel buttonPanel;
	private JTable tableKeys;
	private String workingCopyLocation = "";
	private String [] headers = {"ID", "Original", "Translation"};
	private JPanel searchPanel;
	private JButton buttonNext;
	private JComboBox comboColumn;
	private JLabel labelIn;
	private JLabel labelSearch;
	private JTextField textSearch;
	private JButton buttonHelp;
	private JButton downloadPropertiesFile;
	private JButton downloadTranslationsFile;
	private JCheckBox hideTranslatedRows;
	private TreeSet<String> stringIds;
	private Properties originalText;
	private Properties translatedText;
	private MyTableModel tableModel;
	private MyColorRenderModel colorModel;
	
	public TedTranslateDialog()
	{		
		initGUI();
		
		originalText   = new Properties();
		translatedText = new Properties();
		stringIds      = new TreeSet<String>();

		tableModel = new MyTableModel();	
		
		readOriginal();
		fillTable();
		
		tableKeys = new JTable(tableModel);

		colorModel = new MyColorRenderModel();
		TableColumn column0 = tableKeys.getColumnModel().getColumn(0);
	    TableColumn column1 = tableKeys.getColumnModel().getColumn(1);
	    TableColumn column2 = tableKeys.getColumnModel().getColumn(2);
	    column0.setCellRenderer(colorModel);
	    column1.setCellRenderer(colorModel);
	    column2.setCellRenderer(colorModel);

		updateTable();	
	}
	
	private void updateTable()
	{
		scrollPane = new JScrollPane(tableKeys);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	private void initGUI() 
	{
		try {
				scrollPane = new JScrollPane(tableKeys);
				getContentPane().add(scrollPane, BorderLayout.CENTER);
			
			{
				buttonPanel = new JPanel();
				getContentPane().add(buttonPanel, BorderLayout.NORTH);
				{
					buttonOpen = new JButton();
					buttonPanel.add(buttonOpen);
					buttonOpen.setText("Open Translation");
					buttonOpen.addActionListener(this);
					buttonOpen.setActionCommand("open");
				}
				{
					buttonSave = new JButton();
					buttonPanel.add(buttonSave);
					buttonSave.setText("Save Translation");
					buttonSave.addActionListener(this);
					buttonSave.setActionCommand("save");
				}
				{
					buttonHelp = new JButton();
					buttonPanel.add(buttonHelp);
					buttonHelp.setText("Help");
					buttonHelp.addActionListener(this);
					buttonHelp.setActionCommand("help");
				}
				{
					downloadPropertiesFile = new JButton();
					buttonPanel.add(downloadPropertiesFile);
					downloadPropertiesFile.setText("Download Latest Original");
					downloadPropertiesFile.addActionListener(this);
					downloadPropertiesFile.setActionCommand("properties");
				}
				{
					downloadTranslationsFile = new JButton();
					buttonPanel.add(downloadTranslationsFile);
					downloadTranslationsFile.setText("Download Latest Translations");
					downloadTranslationsFile.addActionListener(this);
					downloadTranslationsFile.setActionCommand("translations");
				}
				{
					hideTranslatedRows = new JCheckBox();
					buttonPanel.add(hideTranslatedRows);
					hideTranslatedRows.setText("Hide!");
					hideTranslatedRows.addActionListener(this);
					hideTranslatedRows.setActionCommand("hide");
				}
			}
			{
				searchPanel = new JPanel();
				getContentPane().add(searchPanel, BorderLayout.SOUTH);
				{
					labelSearch = new JLabel();
					searchPanel.add(labelSearch);
					labelSearch.setText("Search for:");
				}
				{
					textSearch = new JTextField();
					searchPanel.add(textSearch);
					textSearch.setPreferredSize(new java.awt.Dimension(108, 20));
				}
				{
					labelIn = new JLabel();
					searchPanel.add(labelIn);
					labelIn.setText("in");
				}
				{
					ComboBoxModel comboColumnModel = new DefaultComboBoxModel(headers);
					comboColumn = new JComboBox();
					searchPanel.add(comboColumn);
					comboColumn.setModel(comboColumnModel);
				}
				{
					buttonNext = new JButton();
					searchPanel.add(buttonNext);
					buttonNext.setText("Find next");
					buttonNext.addActionListener(this);
					buttonNext.setActionCommand("next");
				}
			}
			{
				this.setSize(1024, 350);
				this.setTitle("ted translator v0.1 beta");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void fillTable()
	{
		String data[][] = new String[stringIds.size()][3];
				
		// If there is no data, alert the user.
		if (stringIds.size() == 0)
		{
			// Add keys and values to their own table
		    data = new String[2][3];
		    
		    // Language and Credits at top of table
		    data[0][0] = "Can't find and download the tedLang.properties file";
		    data[1][0] = "Check 'help' to get the translator to function";
		}
		else
		{
			// Otherwise read the data and fill the table.
			int row = 0;
			Iterator<String> keyIterator = stringIds.iterator();
			while (keyIterator.hasNext())
			{
				String key        = keyIterator.next();
				String original   = originalText  .getProperty(key);
				String translated = translatedText.getProperty(key);
				
				if (hideTranslatedRows.isSelected()
				 && translated != null)
				{
					// Only show lines that aren't translated yet.
					continue;
				}
				
				data[row][0] = key;
				
				if(original != null)
				{
					data[row][1] = original;
				}
				if(translated != null)
				{
					data[row][2] = translated;
				}
				row++;
			}
		}
		
		tableModel.setDataVector(data, headers);    
	}
	
	private void readOriginal()
	{
		stringIds = new TreeSet<String>();
		
		File propertiesFile = new File("tedLang.properties");	    	    
		if(propertiesFile.exists())
		{
			// Read properties file.
		    originalText = new Properties();
		    try 
		    {
		        originalText.load(new FileInputStream("tedLang.properties"));
		    } 
		    catch (IOException e) 
		    {
		    	
		    }
		    	  
		    // Get keys from properties
		    Enumeration<?> keys = originalText.keys();
		    
		    // Put keys in a vector so they can be added to the table
		    while(keys.hasMoreElements())
		    {
		    	String key = (String)keys.nextElement();
		    	stringIds.add(key);
		    }
		}
		else
		{
			// If the file isn't present try to download the version
			// from the repository.
			downloadPropertiesFile("");
			
			if(propertiesFile.exists())
			{
				// If we now have the file, call this function again.
				readOriginal();
			}
			else
			{
				// The file can't be downloaded. Alert the user and offer him
				// help on the Wiki.				
			}
		}
	}

	private void readWorkingCopy() 
	{
		try
		{
		    JFileChooser chooser = new JFileChooser();
		    
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties Files", "properties");
		    chooser.setFileFilter(filter);
		    
		    // Set the current directory
		    File f = new File(new File(".").getCanonicalPath());
	        chooser.setCurrentDirectory(f);
		    
	        int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) 
		    {
		    	workingCopyLocation = chooser.getSelectedFile().getAbsolutePath();
			    			    
				// Read properties file.
			    translatedText = new Properties();
			    try 
			    {
			    	translatedText.load(new FileInputStream(workingCopyLocation));			    					 
			    }
			    catch (IOException e) 
			    {
			    	
			    }
		    } 
		}
		catch(IOException e)
		{
			
		}
	}
	
	private void saveWorkingCopy()
	{
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties Files", "properties");
	    chooser.setFileFilter(filter);
	    
		try 
		{
			//Set the current directory
			File f = new File(new File(workingCopyLocation).getCanonicalPath());
		
	        chooser.setCurrentDirectory(f);
	        chooser.setSelectedFile(f);
		    
	        int returnVal = chooser.showSaveDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    {				
				try 
				{
			        translatedText.store(new FileOutputStream(chooser.getSelectedFile().getCanonicalPath()), null);
			    } 
				catch (IOException e) 
				{	
			    }
		    }
		}
	    catch (IOException e1) 
	    {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		String action = arg0.getActionCommand();
		if(action.equals("open"))
		{
			readWorkingCopy();
			fillTable();
		}
		else if(action.equals("save"))
		{
			saveWorkingCopy();
		}
		else if(action.equals("help"))
		{
			// try to open the ted documentation website
			try 
			{
				BrowserLauncher.openURL("http://www.ted.nu/wiki/index.php/Translate_translator"); //$NON-NLS-1$
			} 
			catch (IOException ep) 
			{
				// error launching ted website
				// TODO: add error message
				System.out.println(Lang.getString("TedMainDialog.LogErrorWebsite")); //$NON-NLS-1$
				ep.printStackTrace();
			}	
		}
		else if(action.equals("next"))
		{
			findNextInTable();
		}
		else if(action.equals("properties"))
		{
			// the original properties name has no country code.
			downloadPropertiesFile("");
			readOriginal();
			fillTable();
		}
		else if(action.equals("translations"))
		{
			downloadTranslations();
		}
		else if(action.equals("hide"))
		{
			fillTable();
		}
	}
	
	private void findNextInTable()
	{
		int searchColumn = comboColumn.getSelectedIndex();
		int selectedRow  = Math.max(0, tableKeys.getSelectedRow());
		String searchString = textSearch.getText().toLowerCase();
		
		// first search the part of the table after the selected row
		for(int i=selectedRow+1; i<tableKeys.getRowCount(); i++)
		{
			if(((String)tableKeys.getValueAt(i,searchColumn)).toLowerCase().contains(searchString))
			{
				// select row
				tableKeys.setRowSelectionInterval(i, i);
				
				// adjust scrollbar
				int scrollvalue = (i*scrollPane.getVerticalScrollBar().getMaximum())/tableKeys.getRowCount();
				scrollPane.getVerticalScrollBar().setValue(scrollvalue);
				
				break;
			}			
		}
		
		// and if nothing is found the part before it
		for(int i=0; i<selectedRow; i++)
		{
			if(((String)tableKeys.getValueAt(i,searchColumn)).toLowerCase().contains(searchString))
			{
				tableKeys.setRowSelectionInterval(i, i);
				
				int scrollvalue = (i*scrollPane.getVerticalScrollBar().getMaximum())/tableKeys.getRowCount();
				scrollPane.getVerticalScrollBar().setValue(scrollvalue);
				
				break;
			}			
		}
	}
	
	private void downloadTranslations()
	{
		// The location of the file we want to download.
		URL url;
		try 
		{
			url = new URL("http://ted.svn.sourceforge.net/viewvc/ted/trunk/ted/translations/translations.txt");
		
			// Open a connection to the file.
			URLConnection conn = url.openConnection();
		    conn.setConnectTimeout(10000);
		     
		    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					
			String line;	
			while((line = br.readLine()) != null)
			{
				downloadPropertiesFile(line);
			}
			br.close();	
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void downloadPropertiesFile(String countryName)
	{
		String fileName = "tedLang" + countryName + ".properties";
		
		try 
		{
			// The location of the file we want to download.
			URL url = new URL("http://ted.svn.sourceforge.net/viewvc/ted/trunk/ted/translations/" + fileName);
			
			// Open a connection to the file.
			URLConnection conn = url.openConnection();
		    conn.setConnectTimeout(10000);
		     
		    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			// Write the properties file
			FileWriter fw  = new FileWriter(fileName);
			
			String line;	
			while((line = br.readLine()) != null)
			{
				fw.write(line + "\n");
			}
			fw.close();
			br.close();			
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class MyTableModel extends AbstractTableModel 
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = -3815927013633221088L;
		private String[] columnNames;
	    private String[][] data;

	    public int getColumnCount() 
	    {
	        return columnNames.length;
	    }

	    public int getRowCount() 
	    {
	        return data.length;
	    }

	    public String getColumnName(int col) 
	    {
	        return columnNames[col];
	    }

	    public Object getValueAt(int row, int col) 
	    {
	        return data[row][col];
	    }

	    public boolean isCellEditable(int row, int col) 
	    {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col < 2) 
	        {
	        	return false;
	        }
	        else
	        {
	            return true;
	        }
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * data can change.
	     */
	    public void setValueAt(Object value, int row, int col) 
	    {
	    	String valueString = (String)value; 
	        data[row][col] = valueString;
	        
	        // Get the key from the table. This key can't change so
	        // it's safe to ask it.
	        String key = (String)data[row][0]; 
	        
	        translatedText.setProperty(key, valueString);
	        
	        this.fireTableRowsUpdated(row, row);
	    }
	    
	    public void setDataVector(String[][] data, String[] names)
	    {
	    	columnNames = names;
	    	this.data = data;
	    	
	    	this.fireTableDataChanged();
	    }
	}
	
	final class MyColorRenderModel extends DefaultTableCellRenderer 
	{
		  
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		MyColorRenderModel () {}
		  
		public Component getTableCellRendererComponent(JTable aTable, 
													   Object aNumberValue, 
													   boolean aIsSelected, 
													   boolean aHasFocus, 
													   int aRow, 
													   int aColumn) 
		{      
			Component renderer = super.getTableCellRendererComponent
		    (
		    	aTable, aNumberValue, aIsSelected, aHasFocus, aRow, aColumn
		    );
    
    
		    String value = (String)aTable.getValueAt(aRow, 2);
		    
		    if(value == null)
		    {
		    	renderer.setBackground(Color.red);
		    }
		    else if(value.equals("")) 
		    {		    	
		    	renderer.setBackground(Color.red);
		    }
		    else 
		    {
		    	renderer.setBackground(Color.green);
		    }
		    return this;
		  }
	}
}
