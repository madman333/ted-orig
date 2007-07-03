package ted.ui.editshowdialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ted.Lang;
import ted.TedDailySerie;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


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
/**
 * Class to display the settings for a daily show in the edit show dialog
 * @author roel
 *
 */
public class DailyPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7912966362467037259L;
	DatePanel datePanel;
	private JLabel maxEpisodesLabel2;
	private JLabel maxEpisodesLabel1;
	private JSpinner episodeSpinner;
	private SpinnerNumberModel episodeSpinnerModel = new SpinnerNumberModel();

	public DailyPanel ()
	{
		this.initGUI();
	}
	
	private void initGUI() {
		try {
			{
				
				FormLayout thisLayout = new FormLayout(
					"max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), 5dlu, 15dlu:grow, max(p;5dlu)",
					"max(p;5dlu), max(p;5dlu), max(p;5dlu)");
				this.setLayout(thisLayout);
				this.setPreferredSize(new java.awt.Dimension(290, 67));
			}
			{
				datePanel = new DatePanel();
				this.add(datePanel, new CellConstraints("2, 3, 5, 1, left, default"));
				
				episodeSpinner = new JSpinner();
				this.add(episodeSpinner, new CellConstraints("4, 2, 1, 1, default, default"));
				episodeSpinner.setModel(episodeSpinnerModel);
				episodeSpinner.setPreferredSize(new java.awt.Dimension(62, 21));
				Integer value = new Integer(1);
				this.episodeSpinnerModel.setMinimum(value);
			}
			{
				maxEpisodesLabel2 = new JLabel();
				this.add(maxEpisodesLabel2, new CellConstraints("6, 2, 1, 1, default, default"));
				maxEpisodesLabel2.setText(Lang.getString("TedEpisodeDialog.LabelDailyMaxEpisodes2"));
			}
			{
				maxEpisodesLabel1 = new JLabel();
				this.add(maxEpisodesLabel1, new CellConstraints("2, 2, 1, 1, default, default"));
				maxEpisodesLabel1.setText(Lang.getString("TedEpisodeDialog.LabelDailyMaxEpisodes1"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the values of the daily show in the panel
	 * @param serie
	 */
	public void setValues(TedDailySerie serie) 
	{
		// set latest download date
		this.datePanel.setDate(serie.getLatestDownloadDateInMillis());
		
		// get number of episodes to download
		Integer episodes = new Integer (serie.getMaxDownloads());
		this.episodeSpinner.setValue(episodes);		
	}
	
	/**
	 * Save the values of the panel in the daily show
	 * @param serie a daily show
	 */
	public void saveValues(TedDailySerie serie) 
	{
		// set latest download date
		int day = this.datePanel.getDay();
		int month = this.datePanel.getMonth();
		int year = this.datePanel.getYear();
		serie.setLatestDownloadDate(day, month, year);
		
		// get number of episodes to download
		serie.setMaxDownloads(episodeSpinnerModel.getNumber().intValue());
		
	}

	/**
	 * Set the date to display in the panel
	 * @param time
	 */
	public void setDate (long time) 
	{
		this.datePanel.setDate(time);
		
	}

}
