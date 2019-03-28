import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/**
 * MAIN class implements Graphical User Interface (GUI) so users can customize
 * settings for this application and indirectly control THREAD class.
 * 
 * THREAD class is started by clicking on START button, paused by clicking on
 * PAUSE button or if Hero meets bad conditions, and stopped by clicking on STOP
 * button.
 * 
 * @author medenko
 * @version 1.10
 */
public class MAIN {

	protected static final File config = new File("config"); // current user configuration file
	private final File dconfig = new File("default"); // default user configuration file
	static MODE appMode; // current global application mode (1-RUN, 2-PAUSE, 3-STOP)

	// GUI window frame
	private JFrame frame;

	// editable text fields
	private JTextField username;
	private JTextField password;
	private JTextField server;
	private JTextField county;
	private JTextField pauseHPLimit;
	private JTextField pauseGoldLimit;
	private JTextField strength;
	private JTextField defense;
	private JTextField dexterity;
	private JTextField endurance;
	private JTextField charisma;
	private JTextField minDelay;
	private JTextField maxDelay;
	private JTextField minPause;
	private JTextField maxPause;
	private JTextField customActions;
	private JTextField humanActions;
	private JTextField knowledgeActions;
	private JTextField orderActions;
	private JTextField natureActions;
	private JTextField beastActions;
	private JTextField destructionActions;
	private JTextField chaosActions;
	private JTextField corruptionActions;
	private JTextField neutralActions;
	private JTextField badActions;
	private JTextField humanGoal;
	private JTextField knowledgeGoal;
	private JTextField orderGoal;
	private JTextField natureGoal;
	private JTextField beastGoal;
	private JTextField destructionGoal;
	private JTextField chaosGoal;
	private JTextField corruptionGoal;

	// non-editable text fields
	static JTextArea textArea;
	static JTextField HPCurrent;
	static JTextField goldCurrent;
	static JTextField strengthCurrent;
	static JTextField defenseCurrent;
	static JTextField dexterityCurrent;
	static JTextField enduranceCurrent;
	static JTextField charismaCurrent;
	static JTextField humanCurrent;
	static JTextField knowledgeCurrent;
	static JTextField orderCurrent;
	static JTextField natureCurrent;
	static JTextField beastCurrent;
	static JTextField destructionCurrent;
	static JTextField chaosCurrent;
	static JTextField corruptionCurrent;

	// toggle buttons
	private JToggleButton tglbtnPauseHP;
	private JToggleButton tglbtnPauseGold;
	private JToggleButton tglbtnPurchase;

	// radio buttons
	private JRadioButton rdbtnStory1;
	private JRadioButton rdbtnStory2;
	private ButtonGroup grbtnStory;

	// mode-changing buttons
	static JButton btnStart;
	static JButton btnRun;
	static JButton btnPause;
	static JButton btnStop;

	/**
	 * Launch the application and display Graphical User Interface (GUI).
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MAIN window = new MAIN();
					window.frame.setVisible(true);
				} catch (Exception e) {
					infoBox("Cannot display Graphical User Interface.", "ERROR");
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Initialize GUI components when MAIN class is instantiated.
	 */
	public MAIN() {
		initialize();
	}

	/**
	 * Initialize GUI components.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 560, 550);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setTitle("Bitefight Story Bot");

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(0, 0, 378, 426);
		frame.getContentPane().add(tabbedPane);

		JPanel panelGeneral = new JPanel();
		tabbedPane.addTab("General", null, panelGeneral, null);
		panelGeneral.setLayout(null);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(10, 10, 70, 16);
		panelGeneral.add(lblUsername);

		username = new JTextField();
		username.setBounds(80, 7, 110, 22);
		username.setColumns(10);
		panelGeneral.add(username);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 35, 70, 16);
		panelGeneral.add(lblPassword);

		password = new JTextField();
		password.setBounds(80, 32, 110, 22);
		password.setColumns(10);
		panelGeneral.add(password);

		JLabel lblServer = new JLabel("Server URL");
		lblServer.setBounds(10, 60, 70, 16);
		panelGeneral.add(lblServer);

		server = new JTextField();
		server.setBounds(80, 57, 281, 22);
		server.setColumns(10);
		server.setToolTipText("<html>e.g. <b>https://s202-en.bitefight.gameforge.com</b></html>");
		panelGeneral.add(server);

		JLabel lblCounty = new JLabel("County (optional)");
		lblCounty.setBounds(10, 85, 105, 16);
		panelGeneral.add(lblCounty);

		county = new JTextField();
		county.setBounds(115, 82, 246, 22);
		county.setColumns(10);
		county.setToolTipText(
			  "<html>Optional (intended for users on merged servers). To select <b>County 1 (Now county 3)</b> <br>"
			+ "from dropmenu on login page, type case-sensitive text <b>County 1 (Now county 3)</b>.</html>");
		panelGeneral.add(county);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 110, 373, 2);
		panelGeneral.add(separator);

		tglbtnPauseHP = new JToggleButton("Pause when HP below");
		tglbtnPauseHP.setBounds(10, 116, 180, 25);
		tglbtnPauseHP.setSelected(false);
		tglbtnPauseHP.setToolTipText(
			  "<html>Should program pause when your Hero HP drops below your specified limit? <br>"
			+ "Program periodically checks your Hero condition until your Hero has enough <br>"
			+ "HP to continue. You can also manually heal your Hero, change your HP limit <br>"
			+ "or disable this feature and then click on RUN button to continue.</html>");
		panelGeneral.add(tglbtnPauseHP);

		pauseHPLimit = new JTextField();
		pauseHPLimit.setBounds(195, 117, 80, 22);
		pauseHPLimit.setColumns(10);
		pauseHPLimit.setHorizontalAlignment(SwingConstants.CENTER);
		pauseHPLimit.setToolTipText("Program shall pause when your Hero HP drops below this limit.");
		panelGeneral.add(pauseHPLimit);

		HPCurrent = new JTextField();
		HPCurrent.setBounds(281, 117, 80, 22);
		HPCurrent.setColumns(10);
		HPCurrent.setEditable(false);
		HPCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(HPCurrent);

		tglbtnPauseGold = new JToggleButton("Pause when gold above");
		tglbtnPauseGold.setBounds(10, 140, 180, 25);
		tglbtnPauseGold.setSelected(false);
		tglbtnPauseGold.setToolTipText(
			  "<html>Should program pause when your Hero collects gold above your specified limit? <br>"
			+ "Program periodically checks your Hero condition until your Hero has less gold than <br>"
			+ "your gold limit. You can also manually spend your gold, change your gold limit or <br>"
			+ "disable this feature and then click on RUN button to continue.</html>");
		panelGeneral.add(tglbtnPauseGold);

		pauseGoldLimit = new JTextField();
		pauseGoldLimit.setBounds(195, 142, 80, 22);
		pauseGoldLimit.setColumns(10);
		pauseGoldLimit.setHorizontalAlignment(SwingConstants.CENTER);
		pauseGoldLimit.setToolTipText("Program shall pause when your Hero collects gold above this limit.");
		panelGeneral.add(pauseGoldLimit);

		goldCurrent = new JTextField();
		goldCurrent.setBounds(281, 141, 80, 22);
		goldCurrent.setColumns(10);
		goldCurrent.setEditable(false);
		goldCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(goldCurrent);

		JSeparator separator1 = new JSeparator();
		separator1.setBounds(0, 170, 373, 1);
		panelGeneral.add(separator1);

		tglbtnPurchase = new JToggleButton("Purchase attributes");
		tglbtnPurchase.setBounds(10, 176, 180, 25);
		tglbtnPurchase.setSelected(false);
		tglbtnPurchase.setToolTipText(
			  "<html>Should program purchase attributes (STR, DEF, DEX, END, CHA) up to your <br>"
			+ "specified limits? If you enable this feature and set <b>Strength</b> to <b>500</b>, program <br>"
			+ "will attempt to purchase <b>Strength</b> until <b>500</b>.</html>");
		panelGeneral.add(tglbtnPurchase);

		JLabel lblStrength = new JLabel("Strength");
		lblStrength.setBounds(25, 210, 65, 16);
		panelGeneral.add(lblStrength);

		strength = new JTextField();
		strength.setBounds(95, 207, 80, 22);
		strength.setColumns(10);
		strength.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(strength);

		strengthCurrent = new JTextField();
		strengthCurrent.setBounds(180, 207, 80, 22);
		strengthCurrent.setColumns(10);
		strengthCurrent.setEditable(false);
		strengthCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(strengthCurrent);

		JLabel lblDefense = new JLabel("Defense");
		lblDefense.setBounds(25, 235, 65, 16);
		panelGeneral.add(lblDefense);

		defense = new JTextField();
		defense.setBounds(95, 232, 80, 22);
		defense.setColumns(10);
		defense.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(defense);

		defenseCurrent = new JTextField();
		defenseCurrent.setBounds(180, 232, 80, 22);
		defenseCurrent.setColumns(10);
		defenseCurrent.setEditable(false);
		defenseCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(defenseCurrent);

		JLabel lblDexterity = new JLabel("Dexterity");
		lblDexterity.setBounds(25, 260, 65, 16);
		panelGeneral.add(lblDexterity);

		dexterity = new JTextField();
		dexterity.setBounds(95, 257, 80, 22);
		dexterity.setColumns(10);
		dexterity.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(dexterity);

		dexterityCurrent = new JTextField();
		dexterityCurrent.setBounds(180, 257, 80, 22);
		dexterityCurrent.setColumns(10);
		dexterityCurrent.setEditable(false);
		dexterityCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(dexterityCurrent);

		JLabel lblEndurance = new JLabel("Endurance");
		lblEndurance.setBounds(25, 285, 65, 16);
		panelGeneral.add(lblEndurance);

		endurance = new JTextField();
		endurance.setBounds(95, 282, 80, 22);
		endurance.setColumns(10);
		endurance.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(endurance);

		enduranceCurrent = new JTextField();
		enduranceCurrent.setBounds(180, 282, 80, 22);
		enduranceCurrent.setColumns(10);
		enduranceCurrent.setEditable(false);
		enduranceCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(enduranceCurrent);

		JLabel lblCharisma = new JLabel("Charisma");
		lblCharisma.setBounds(25, 310, 65, 16);
		panelGeneral.add(lblCharisma);

		charisma = new JTextField();
		charisma.setBounds(95, 307, 80, 22);
		charisma.setColumns(10);
		charisma.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(charisma);

		charismaCurrent = new JTextField();
		charismaCurrent.setBounds(180, 307, 80, 22);
		charismaCurrent.setColumns(10);
		charismaCurrent.setEditable(false);
		charismaCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelGeneral.add(charismaCurrent);

		JSeparator separator2 = new JSeparator();
		separator2.setBounds(0, 335, 373, 2);
		panelGeneral.add(separator2);

		JLabel lblDelay = new JLabel("Delay between");
		lblDelay.setBounds(10, 345, 90, 16);
		panelGeneral.add(lblDelay);

		minDelay = new JTextField();
		minDelay.setBounds(105, 342, 60, 22);
		minDelay.setColumns(10);
		minDelay.setHorizontalAlignment(SwingConstants.CENTER);
		minDelay.setToolTipText("<html>Program shall delay navigation to imitate normal human play.</html>");
		panelGeneral.add(minDelay);

		JLabel lblAnd = new JLabel("and");
		lblAnd.setBounds(171, 345, 25, 16);
		panelGeneral.add(lblAnd);

		maxDelay = new JTextField();
		maxDelay.setBounds(200, 342, 60, 22);
		maxDelay.setColumns(10);
		maxDelay.setHorizontalAlignment(SwingConstants.CENTER);
		maxDelay.setToolTipText("<html>Program shall delay navigation to imitate normal human play.</html>");
		panelGeneral.add(maxDelay);

		JLabel lblMilliseconds = new JLabel("milliseconds");
		lblMilliseconds.setBounds(270, 345, 75, 16);
		panelGeneral.add(lblMilliseconds);

		JLabel lblPause = new JLabel("Pause between");
		lblPause.setBounds(10, 370, 90, 16);
		panelGeneral.add(lblPause);

		minPause = new JTextField();
		minPause.setBounds(105, 367, 60, 22);
		minPause.setColumns(10);
		minPause.setHorizontalAlignment(SwingConstants.CENTER);
		minPause.setToolTipText(
			  "<html>Program shall pause for a certain time when your Hero falls <br>"
			+ "into bad conditions (<b>low AP</b>, <b>low HP</b>, <b>high gold</b>).</html>");
		panelGeneral.add(minPause);

		JLabel lblAnd1 = new JLabel("and");
		lblAnd1.setBounds(171, 370, 25, 16);
		panelGeneral.add(lblAnd1);

		maxPause = new JTextField();
		maxPause.setBounds(200, 367, 60, 22);
		maxPause.setColumns(10);
		maxPause.setHorizontalAlignment(SwingConstants.CENTER);
		maxPause.setToolTipText(
			  "<html>Program shall pause for a certain time when your Hero falls <br>"
			+ "into bad conditions (<b>low AP</b>, <b>low HP</b>, <b>high gold</b>).</html>");
		panelGeneral.add(maxPause);

		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setBounds(270, 370, 75, 16);
		panelGeneral.add(lblSeconds);

		JPanel panelStory = new JPanel();
		tabbedPane.addTab("Story", null, panelStory, null);
		panelStory.setLayout(null);

		rdbtnStory1 = new JRadioButton("MODE 1 - Play story with actions in your custom order");
		rdbtnStory1.setActionCommand("custom");
		rdbtnStory1.setBounds(10, 10, 350, 20);
		rdbtnStory1.setSelected(true);
		rdbtnStory1.setToolTipText(
			  "<html>MODE 1 will play story with actions in your custom order, <br>"
			+ "starting from left (highest priority) to right (lowest priority).</html>");
		panelStory.add(rdbtnStory1);

		JLabel lblMode1Story = new JLabel("Story actions");
		lblMode1Story.setBounds(10, 40, 80, 16);
		panelStory.add(lblMode1Story);

		customActions = new JTextField();
		customActions.setBounds(95, 37, 266, 22);
		customActions.setColumns(10);
		panelStory.add(customActions);

		JSeparator separator3 = new JSeparator();
		separator3.setBounds(0, 65, 373, 2);
		panelStory.add(separator3);

		rdbtnStory2 = new JRadioButton("MODE 2 - Play story to raise your wanted aspects");
		rdbtnStory2.setActionCommand("aspects");
		rdbtnStory2.setBounds(10, 75, 350, 20);
		rdbtnStory2.setToolTipText(
			  "<html>MODE 2 will play story to raise your wanted aspects which you specify under <br>"
			+ "GOAL. Actions are grouped by aspects and are sorted from most reasonable <br>"
			+ "to least reasonable to choose from. Recommended to use when your wanted <br>"
			+ "aspects become harder to reach. </html>");
		panelStory.add(rdbtnStory2);

		grbtnStory = new ButtonGroup();
		grbtnStory.add(rdbtnStory1);
		grbtnStory.add(rdbtnStory2);

		JLabel lblActions = new JLabel("Actions");
		lblActions.setBounds(110, 106, 120, 16);
		lblActions.setHorizontalAlignment(SwingConstants.CENTER);
		lblActions.setToolTipText("MODE 2 story actions are grouped by aspects.");
		panelStory.add(lblActions);

		JLabel lblGoal = new JLabel("Goal");
		lblGoal.setBounds(235, 105, 60, 16);
		lblGoal.setHorizontalAlignment(SwingConstants.CENTER);
		lblGoal.setToolTipText("Specify your wanted aspects.");
		panelStory.add(lblGoal);

		JLabel lblCurrent = new JLabel("Current");
		lblCurrent.setBounds(300, 105, 60, 16);
		lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(lblCurrent);

		JLabel lblHuman = new JLabel("Human");
		lblHuman.setBounds(15, 127, 90, 16);
		panelStory.add(lblHuman);

		humanActions = new JTextField();
		humanActions.setBounds(110, 125, 120, 22);
		humanActions.setColumns(10);
		panelStory.add(humanActions);

		humanGoal = new JTextField();
		humanGoal.setBounds(235, 125, 60, 22);
		humanGoal.setColumns(10);
		humanGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(humanGoal);

		humanCurrent = new JTextField();
		humanCurrent.setBounds(300, 125, 60, 22);
		humanCurrent.setColumns(10);
		humanCurrent.setEditable(false);
		humanCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(humanCurrent);

		JLabel lblKnowledge = new JLabel("Knowledge");
		lblKnowledge.setBounds(15, 152, 90, 16);
		panelStory.add(lblKnowledge);

		knowledgeActions = new JTextField();
		knowledgeActions.setBounds(110, 150, 120, 22);
		knowledgeActions.setColumns(10);
		panelStory.add(knowledgeActions);

		knowledgeGoal = new JTextField();
		knowledgeGoal.setBounds(235, 150, 60, 22);
		knowledgeGoal.setColumns(10);
		knowledgeGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(knowledgeGoal);

		knowledgeCurrent = new JTextField();
		knowledgeCurrent.setBounds(300, 150, 60, 22);
		knowledgeCurrent.setColumns(10);
		knowledgeCurrent.setEditable(false);
		knowledgeCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(knowledgeCurrent);

		JLabel lblOrder = new JLabel("Order");
		lblOrder.setBounds(15, 177, 90, 16);
		panelStory.add(lblOrder);

		orderActions = new JTextField();
		orderActions.setBounds(110, 175, 120, 22);
		orderActions.setColumns(10);
		panelStory.add(orderActions);

		orderGoal = new JTextField();
		orderGoal.setBounds(235, 175, 60, 22);
		orderGoal.setColumns(10);
		orderGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(orderGoal);

		orderCurrent = new JTextField();
		orderCurrent.setBounds(300, 175, 60, 22);
		orderCurrent.setColumns(10);
		orderCurrent.setEditable(false);
		orderCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(orderCurrent);

		JLabel lblNature = new JLabel("Nature");
		lblNature.setBounds(15, 202, 90, 16);
		panelStory.add(lblNature);

		natureActions = new JTextField();
		natureActions.setBounds(110, 200, 120, 22);
		natureActions.setColumns(10);
		panelStory.add(natureActions);

		natureGoal = new JTextField();
		natureGoal.setBounds(235, 200, 60, 22);
		natureGoal.setColumns(10);
		natureGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(natureGoal);

		natureCurrent = new JTextField();
		natureCurrent.setBounds(300, 200, 60, 22);
		natureCurrent.setColumns(10);
		natureCurrent.setEditable(false);
		natureCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(natureCurrent);

		JLabel lblBeast = new JLabel("Beast");
		lblBeast.setBounds(15, 227, 90, 16);
		panelStory.add(lblBeast);

		beastActions = new JTextField();
		beastActions.setBounds(110, 225, 120, 22);
		beastActions.setColumns(10);
		panelStory.add(beastActions);

		beastGoal = new JTextField();
		beastGoal.setBounds(235, 225, 60, 22);
		beastGoal.setColumns(10);
		beastGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(beastGoal);

		beastCurrent = new JTextField();
		beastCurrent.setEditable(false);
		beastCurrent.setBounds(300, 225, 60, 22);
		beastCurrent.setColumns(10);
		beastCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(beastCurrent);

		JLabel lblDestruction = new JLabel("Destruction");
		lblDestruction.setBounds(15, 252, 90, 16);
		panelStory.add(lblDestruction);

		destructionActions = new JTextField();
		destructionActions.setBounds(110, 250, 120, 22);
		destructionActions.setColumns(10);
		panelStory.add(destructionActions);

		destructionGoal = new JTextField();
		destructionGoal.setBounds(235, 250, 60, 22);
		destructionGoal.setColumns(10);
		destructionGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(destructionGoal);

		destructionCurrent = new JTextField();
		destructionCurrent.setBounds(300, 250, 60, 22);
		destructionCurrent.setColumns(10);
		destructionCurrent.setEditable(false);
		destructionCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(destructionCurrent);

		JLabel lblChaos = new JLabel("Chaos");
		lblChaos.setBounds(15, 277, 90, 16);
		panelStory.add(lblChaos);

		chaosActions = new JTextField();
		chaosActions.setBounds(110, 275, 120, 22);
		chaosActions.setColumns(10);
		panelStory.add(chaosActions);

		chaosGoal = new JTextField();
		chaosGoal.setBounds(235, 275, 60, 22);
		chaosGoal.setColumns(10);
		chaosGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(chaosGoal);

		chaosCurrent = new JTextField();
		chaosCurrent.setBounds(300, 275, 60, 22);
		chaosCurrent.setColumns(10);
		chaosCurrent.setEditable(false);
		chaosCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(chaosCurrent);

		JLabel lblCorruption = new JLabel("Corruption");
		lblCorruption.setBounds(15, 302, 90, 16);
		panelStory.add(lblCorruption);

		corruptionActions = new JTextField();
		corruptionActions.setBounds(110, 300, 120, 22);
		corruptionActions.setColumns(10);
		panelStory.add(corruptionActions);

		corruptionGoal = new JTextField();
		corruptionGoal.setBounds(235, 300, 60, 22);
		corruptionGoal.setColumns(10);
		corruptionGoal.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(corruptionGoal);

		corruptionCurrent = new JTextField();
		corruptionCurrent.setBounds(300, 300, 60, 22);
		corruptionCurrent.setColumns(10);
		corruptionCurrent.setEditable(false);
		corruptionCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		panelStory.add(corruptionCurrent);

		JLabel lblNeutral = new JLabel("Neutral");
		lblNeutral.setBounds(15, 327, 90, 16);
		panelStory.add(lblNeutral);

		neutralActions = new JTextField();
		neutralActions.setBounds(110, 325, 120, 22);
		neutralActions.setColumns(10);
		neutralActions.setToolTipText(
			  "<html>Actions with no effect on aspects, <br>"
			+ "e.g. location changes (forest, city, mountain).</html>");
		panelStory.add(neutralActions);

		JLabel lblBad = new JLabel("Bad / Avoid");
		lblBad.setBounds(15, 352, 90, 16);
		panelStory.add(lblBad);

		badActions = new JTextField();
		badActions.setBounds(110, 350, 120, 22);
		badActions.setColumns(10);
		badActions.setToolTipText(
			  "<html>Actions you want to avoid, to prevent <br>"
			+ "losing large amounts of Hero HP during story.</html>");
		panelStory.add(badActions);

		btnStart = new JButton("Start");
		btnStart.setBounds(415, 25, 100, 25);
		btnStart.setEnabled(true);
		frame.getContentPane().add(btnStart);

		btnRun = new JButton("Run");
		btnRun.setBounds(415, 55, 100, 25);
		btnRun.setEnabled(false);
		frame.getContentPane().add(btnRun);

		btnPause = new JButton("Pause");
		btnPause.setBounds(415, 85, 100, 25);
		btnPause.setEnabled(false);
		frame.getContentPane().add(btnPause);

		btnStop = new JButton("Stop");
		btnStop.setBounds(415, 115, 100, 25);
		btnStop.setEnabled(false);
		frame.getContentPane().add(btnStop);

		JButton btnCheck = new JButton("Check actions");
		btnCheck.setBounds(235, 327, 125, 45);
		btnCheck.setToolTipText("Check for missing story actions.");
		panelStory.add(btnCheck);

		JButton btnImport = new JButton("Import settings");
		btnImport.setBounds(395, 175, 140, 25);
		btnImport.setToolTipText("Import settings from custom configuration file.");
		frame.getContentPane().add(btnImport);

		JButton btnExport = new JButton("Export settings");
		btnExport.setBounds(395, 205, 140, 25);
		btnExport.setToolTipText("Export settings into custom configuration file.");
		frame.getContentPane().add(btnExport);

		JButton btnSave = new JButton("Save settings");
		btnSave.setBounds(395, 265, 140, 25);
		btnSave.setToolTipText("Save your current changes.");
		frame.getContentPane().add(btnSave);

		JButton btnReload = new JButton("Reload settings");
		btnReload.setBounds(395, 295, 140, 25);
		btnReload.setToolTipText("Discard your unsaved changes.");
		frame.getContentPane().add(btnReload);

		JButton btnDefault = new JButton("Default settings");
		btnDefault.setBounds(395, 325, 140, 25);
		btnDefault.setToolTipText("Reset settings to default.");
		frame.getContentPane().add(btnDefault);

		JSeparator separator4 = new JSeparator();
		separator4.setBounds(376, 424, 177, 1);
		frame.getContentPane().add(separator4);

		textArea = new JTextArea();
		textArea.setBounds(0, 428, 554, 89);
		textArea.setEditable(false);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setToolTipText(
			  "<html>This message box informs you of your Hero bad conditions <br>"
			+ "(<b>too low AP</b>, <b>too low HP</b>, <b>too much gold</b>) and the progress <br>"
			+ "of raising your wanted aspects.</html>");
		frame.getContentPane().add(textArea);

		// CHECK ACTIONS
		btnCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkActions(config);
			}
		});

		// START
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(MODE.RUN);
				THREAD thread = new THREAD();
				if (thread.getState() == Thread.State.NEW) {
					thread.start();
				}
			}
		});

		// RUN
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(MODE.RUN);
			}
		});

		// PAUSE
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(MODE.PAUSE);
			}
		});

		// STOP
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMode(MODE.STOP);
			}
		});

		// IMPORT SETTINGS
		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importSettings();
			}
		});

		// EXPORT SETTINGS
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportSettings();
			}
		});

		// SAVE SETTINGS
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSettings(config);
				loadSettings(config);
			}
		});

		// RELOAD SETTINGS
		btnReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadSettings(config);
			}
		});

		// DEFAULT SETTINGS
		btnDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadSettings(dconfig);
				saveSettings(config);
			}
		});

		// auto-load existing settings
		loadSettings(config);
	}

	/**
	 * Open dialog window to import user settings.
	 */
	protected void importSettings() {
		JFileChooser chooser = new JFileChooser();
		File directory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(directory);
		chooser.setDialogTitle("Import settings");
		int result = chooser.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			loadSettings(file);
			saveSettings(config);
		}
	}

	/**
	 * Open dialog window to export user settings.
	 */
	protected void exportSettings() {
		JFileChooser chooser = new JFileChooser();
		File directory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(directory);
		chooser.setDialogTitle("Export settings");
		int result = chooser.showSaveDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			loadSettings(config);
			saveSettings(file);
		}
	}

	/**
	 * Load settings from configuration file to GUI.
	 * 
	 * @param file to read
	 */
	private void loadSettings(File file) {
		BufferedReader reader = null;

		try { // create configuration file if not exist
			file.createNewFile();
		} catch (IOException e) { // warning if cannot create configuration file
			infoBox("Cannot create configuration file. \n" + "Do you have write permission?", "WARNING");
		}

		try { // locate configuration file
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) { // warning if cannot locate configuration file
			infoBox("Cannot locate configuration file. \n" + "Does file exist and do you have read permission?",
					"WARNING");
		}

		try { // read configuration file
			String line = "";
			int lineCount = 0;

			while ((line = reader.readLine()) != null) {
				switch (++lineCount) {
				case 1:
					username.setText(line);
					break;
				case 2:
					password.setText(line);
					break;
				case 3:
					server.setText(line);
					break;
				case 4:
					county.setText(line);
					break;
				case 5:
					if (line.equalsIgnoreCase("true"))
						tglbtnPauseHP.setSelected(true);
					else
						tglbtnPauseHP.setSelected(false);
					break;
				case 6:
					pauseHPLimit.setText(line);
					break;
				case 7:
					if (line.equalsIgnoreCase("true"))
						tglbtnPauseGold.setSelected(true);
					else
						tglbtnPauseGold.setSelected(false);
					break;
				case 8:
					pauseGoldLimit.setText(line);
					break;
				case 9:
					if (line.equalsIgnoreCase("true"))
						tglbtnPurchase.setSelected(true);
					else
						tglbtnPurchase.setSelected(false);
					break;
				case 10:
					strength.setText(line);
					break;
				case 11:
					defense.setText(line);
					break;
				case 12:
					dexterity.setText(line);
					break;
				case 13:
					endurance.setText(line);
					break;
				case 14:
					charisma.setText(line);
					break;
				case 15:
					minDelay.setText(line);
					break;
				case 16:
					maxDelay.setText(line);
					break;
				case 17:
					minPause.setText(line);
					break;
				case 18:
					maxPause.setText(line);
					break;
				case 19:
					if (line.equalsIgnoreCase("aspects"))
						rdbtnStory2.setSelected(true);
					else
						rdbtnStory1.setSelected(true);
					break;
				case 20:
					customActions.setText(line);
					break;
				case 21:
					humanActions.setText(line);
					break;
				case 22:
					knowledgeActions.setText(line);
					break;
				case 23:
					orderActions.setText(line);
					break;
				case 24:
					natureActions.setText(line);
					break;
				case 25:
					beastActions.setText(line);
					break;
				case 26:
					destructionActions.setText(line);
					break;
				case 27:
					chaosActions.setText(line);
					break;
				case 28:
					corruptionActions.setText(line);
					break;
				case 29:
					neutralActions.setText(line);
					break;
				case 30:
					badActions.setText(line);
					break;
				case 31:
					humanGoal.setText(line);
					break;
				case 32:
					knowledgeGoal.setText(line);
					break;
				case 33:
					orderGoal.setText(line);
					break;
				case 34:
					natureGoal.setText(line);
					break;
				case 35:
					beastGoal.setText(line);
					break;
				case 36:
					destructionGoal.setText(line);
					break;
				case 37:
					chaosGoal.setText(line);
					break;
				case 38:
					corruptionGoal.setText(line);
					break;
				}
			}
		} catch (IOException e) { // warning if cannot read configuration file
			infoBox("Cannot read configuration file. \n"
				  + "Does configuration file exist and do you have read permission?", "WARNING");
		} finally {
			try { // close file reader
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {}
		}
	}

	/**
	 * Save configuration from GUI to configuration file.
	 *
	 * @param file to edit
	 */
	protected void saveSettings(File file) {
		try { // create configuration file if not exist
			file.createNewFile();
		} catch (IOException e) { // warning if cannot create configuration file
			infoBox("Cannot create configuration file. \n" + "Do you have write permission?", "WARNING");
		}

		try { // write user settings into configuration file
			FileWriter writer = new FileWriter(file);

			// swap values if minimum delay value is higher than maximum delay value
			int temp;
			int minD = parseInt(minDelay.getText());
			int maxD = parseInt(maxDelay.getText());

			if (minD > maxD) {
				temp = minD;
				minD = maxD;
				maxD = temp;
			}

			// swap values if minimum pause value is higher than maximum pause value
			int minP = parseInt(minPause.getText());
			int maxP = parseInt(maxPause.getText());

			if (minP > maxP) {
				temp = minP;
				minP = maxP;
				maxP = temp;
			}

			// write settings into configuration file
			writer.write(username.getText() + "\n");
			writer.write(password.getText() + "\n");
			writer.write(server.getText() + "\n");
			writer.write(county.getText() + "\n");
			writer.write(tglbtnPauseHP.isSelected() + "\n");
			writer.write(parseInt(pauseHPLimit.getText()) + "\n");
			writer.write(tglbtnPauseGold.isSelected() + "\n");
			writer.write(parseInt(pauseGoldLimit.getText()) + "\n");
			writer.write(tglbtnPurchase.isSelected() + "\n");
			writer.write(parseInt(strength.getText()) + "\n");
			writer.write(parseInt(defense.getText()) + "\n");
			writer.write(parseInt(dexterity.getText()) + "\n");
			writer.write(parseInt(endurance.getText()) + "\n");
			writer.write(parseInt(charisma.getText()) + "\n");
			writer.write(minD + "\n");
			writer.write(maxD + "\n");
			writer.write(minP + "\n");
			writer.write(maxP + "\n");
			writer.write(grbtnStory.getSelection().getActionCommand() + "\n");
			writer.write(customActions.getText() + "\n");
			writer.write(humanActions.getText() + "\n");
			writer.write(knowledgeActions.getText() + "\n");
			writer.write(orderActions.getText() + "\n");
			writer.write(natureActions.getText() + "\n");
			writer.write(beastActions.getText() + "\n");
			writer.write(destructionActions.getText() + "\n");
			writer.write(chaosActions.getText() + "\n");
			writer.write(corruptionActions.getText() + "\n");
			writer.write(neutralActions.getText() + "\n");
			writer.write(badActions.getText() + "\n");
			writer.write(parseInt(humanGoal.getText()) + "\n");
			writer.write(parseInt(knowledgeGoal.getText()) + "\n");
			writer.write(parseInt(orderGoal.getText()) + "\n");
			writer.write(parseInt(natureGoal.getText()) + "\n");
			writer.write(parseInt(beastGoal.getText()) + "\n");
			writer.write(parseInt(destructionGoal.getText()) + "\n");
			writer.write(parseInt(chaosGoal.getText()) + "\n");
			writer.write(parseInt(corruptionGoal.getText()) + "\n");
			writer.close();
		} catch (IOException e) { // warning if cannot edit configuration file
			infoBox("Cannot edit configuration file. \n" + "Do you have write permission?", "WARNING");
		}
	}

	/**
	 * Check for missing story actions in both story modes.
	 * 
	 * @param file to read
	 */
	protected void checkActions(File file) {
		BufferedReader reader = null;
		try { // locate configuration file
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) { // warning if cannot locate configuration file
			infoBox("Cannot locate configuration file. \n"
				  + "Does configuration file exist and do you have read permission?", "WARNING");
			return;
		}

		int[] saved1 = new int[60];
		int[][] saved2 = new int[10][60];
		int[] savedGoals = new int[8];
		
		try { // read configuration file
			int lineCount = 0;
			String line = "";
			String[] temp;

			while ((line = reader.readLine()) != null) {
				switch (++lineCount) {
				case 20:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved1[i] = parseInt(temp[i]);
					break;
				case 21:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[0][i] = parseInt(temp[i]);
					break;
				case 22:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[1][i] = parseInt(temp[i]);
					break;
				case 23:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[2][i] = parseInt(temp[i]);
					break;
				case 24:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[3][i] = parseInt(temp[i]);
					break;
				case 25:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[4][i] = parseInt(temp[i]);
					break;
				case 26:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[5][i] = parseInt(temp[i]);
					break;
				case 27:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[6][i] = parseInt(temp[i]);
					break;
				case 28:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[7][i] = parseInt(temp[i]);
					break;
				case 29:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[8][i] = parseInt(temp[i]);
					break;
				case 30:
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++)
						saved2[9][i] = parseInt(temp[i]);
					break;
				case 31:
					savedGoals[0] = parseInt(line);
					break;
				case 32:
					savedGoals[1] = parseInt(line);
					break;
				case 33:
					savedGoals[2] = parseInt(line);
					break;
				case 34:
					savedGoals[3] = parseInt(line);
					break;
				case 35:
					savedGoals[4] = parseInt(line);
					break;
				case 36:
					savedGoals[5] = parseInt(line);
					break;
				case 37:
					savedGoals[6] = parseInt(line);
					break;
				case 38:
					savedGoals[7] = parseInt(line);
					break;
				}
			}
		} catch (IOException e) { // warning if cannot read configuration file
			infoBox("Cannot read configuration file. \n"
					+ "Does configuration file exist and do you have read permission?", "WARNING");
			return;
		} finally {
			try { // close file reader
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {}
		}

		// collect missing MODE 1 story actions
		int[] allActions = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
				33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 49, 52, 53, 55 };
		int[] missing1 = new int[60];
		int a = 0;

		for (int i = 0; i < allActions.length; i++) {
			boolean match = false;
			for (int j = 0; j < saved1.length; j++) {
				if (saved1[j] == allActions[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				missing1[a++] = allActions[i];
			}
		}

		// collect missing MODE 2 story actions
		int b = 0;
		int[] missing2 = new int[60];

		for (int i = 0; i < allActions.length; i++) {
			boolean match = false;
			for (int j = 0; j < saved2.length; j++) {
				for (int k = 0; k < saved2[j].length; k++) {
					if (saved2[j][k] == allActions[i]) {
						match = true;
						break;
					}
				}
			}
			if (!match) {
				missing2[b++] = allActions[i];
			}
		}

		String message = "";
		if (a > 0) {
			message += "Missing story actions in MODE 1: \n";
			for (int i = 0; i < missing1.length && missing1[i] > 0; i++) {
				message += missing1[i] + "   ";
			}
			message += "\n";
		}

		if (b > 0) {
			if (!message.isEmpty()) {
				message += "\n";
			}
			message += "Missing story actions in MODE 2: \n";
			for (int i = 0; i < missing2.length && missing2[i] > 0; i++) {
				message += missing2[i] + "   ";
			}
		}

		// check sum of wanted aspects, which should not be below 0 or above 8000 
		int sum = 0;
		for (int i = 0; i < 8; i++) {
			sum += savedGoals[i];
		}
		if (sum > 8000) {
			if (!message.isEmpty()) {
				message += "\n";
			}
			message += "Summary of your wanted aspects should not be more than 8000!";
		} else if (sum < 0) {
			if (!message.isEmpty()) {
				message += "\n";
			}
			message += "Summary of your wanted aspects should not be less than 0!";
		}

		if (message.isEmpty()) {
			infoBox("All story actions are present.", "CHECK");
		} else {
			infoBox(message, "CHECK");
		}
	}

	/**
	 * Apply new mode and toggle button effects.
	 * 
	 * @param newMode new mode to apply
	 */
	protected static void setMode(MODE newMode) {
		switch (newMode) {
		case RUN:
			btnEffect(MODE.RUN, false, true, false, true);
			break;
		case PAUSE:
			btnEffect(MODE.PAUSE, false, true, true, false);
			break;
		default:
			btnEffect(MODE.STOP, true, false, false, false);
			break;
		}
	}

	/**
	 * Toggle button effects.
	 * 
	 * @param newMode new mode to apply
	 * @param isStart enable START button?
	 * @param isStop  enable STOP button?
	 * @param isRun   enable RUN button?
	 * @param isPause enable PAUSE button?
	 */
	private static void btnEffect(MODE newMode, boolean isStart, boolean isStop, boolean isRun, boolean isPause) {
		appMode = newMode;
		btnStart.setEnabled(isStart);
		btnStop.setEnabled(isStop);
		btnRun.setEnabled(isRun);
		btnPause.setEnabled(isPause);
	}

	/**
	 * Display message dialog box.
	 * 
	 * @param message in message dialog box
	 * @param title   of message dialog box
	 */
	static void infoBox(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Convert string into number.
	 * 
	 * @param string to be converted into number
	 * @return converted number (0 if NaN)
	 */
	static int parseInt(String string) {
		try {
			int i = Integer.parseInt(string);
			if (i < 0) {
				i = 0;
			}
			return i;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
}
