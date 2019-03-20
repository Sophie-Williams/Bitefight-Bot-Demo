import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

/**
 * THREAD class opens ChromeDriver and navigates through game while user
 * controls it with Graphical User Interface.
 * 
 * It also instantiates CALC class when playing 2nd story mode to recalculate
 * Hero aspects.
 * 
 * @author medenko
 * @version 1.10
 */
public class THREAD extends Thread {

	private final File config = MAIN.config; // current user configuration file
	CALC calc = new CALC(); // MODE 2 story will use CALC class calculations

	// initialize user inputs
	private String username;
	private String password;
	private String server;
	private String county;
	private boolean canPauseHP;
	private int pauseHPLimit;
	private boolean canPauseGold;
	private int pauseGoldLimit;
	private boolean canPurchase;
	private int minDelay;
	private int maxDelay;
	private int minPause;
	private int maxPause;
	private SMODE storyMode;

	// initialize Hero info storage
	private int[] status = new int[3]; // gold, AP, HP
	private int[] aspects = new int[8]; // HMN, KNW, ORD, NTR, BST, DST, CHS, CRR
	private int[] aspectsGoals = new int[8]; // HMN, KNW, ORD, NTR, BST, DST, CHS, CRR limits
	private int[] attributes = new int[5]; // STR, DEF, DEX, END, CHA
	private int[] attributesLimits = new int[5]; // STR, DEF, DEX, END, CHA limits
	private int[] story1actions = new int[60]; // custom order
	private int[][] story2actions = new int[10][60]; // aspects order

	// navigation-related variables
	private boolean canUseHuntShortcut = false; // allow [HUNT -> STORY] instead of [CITY -> TAVERN -> STORY] // navigation?
	private boolean returnToStory = false; // allow [STORY -> purchase attributes -> STORY] navigation?
	private boolean navigateError = false;

	// cssSelectors
	private String cookiesClick = "#accept_btn";
	private String usernameInput = "#loginForm > table > tbody > tr:nth-child(1) > td:nth-child(2) > input";
	private String passwordInput = "#loginForm > table > tbody > tr:nth-child(2) > td:nth-child(2) > input";
	private String countyInput = "#loginForm > table > tbody > tr:nth-child(3) > td:nth-child(2) > select";
	private String loginClick = "#loginForm > table > tbody > tr:nth-child(5) > td:nth-child(2) > input";
	private String loginError = "#login > div.wrap-left.clearfix > div > div > div";
	private String overviewClick = "#menuHead > li:nth-child(2) > a";
	private String statusCheck = ".gold";
	private String huntClick = "#menuHead > li:nth-child(6) > a";
	private String cityClick = "#menuHead > li:nth-child(5) > a";
	private String tavernClick = "#addBuddy > div.wrap-left.clearfix > div > div.table-wrap > table > tbody > tr:nth-child(3) > td:nth-child(2) > a";
	private String storyClick = "#newQuest > div:nth-child(2) > div > div.buttonOverlay";
	private String startStoryClick = "#content > div.wrap-left.clearfix > div.wrap-content.wrap-right.clearfix > div > div > div > a";
	private String actionClick1 = "#content > div.wrap-left.clearfix > div > div > div:nth-child(";
	private String actionClick2 = ") > div > a";
	private String missionClick = "#infoPopup > div.wrap-left.clearfix > div > div > div:nth-child(3) > div.button.right > div.buttonOverlay";
	private String continueClick = "#content > div.wrap-left.clearfix > div > div > div.btn-left.center > div > a";
	private String attributeClick = "li.ui-state-default:nth-child(2) > a:nth-child(1)";
	private String plusClick1 = "#skills_tab > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(";
	private String plusClick2 = ") > td:nth-child(3) > div:nth-child(1)";

	/**
	 * Run THREAD.
	 */
	@Override
	public void run() {
		// open WebDriver
		WebDriver driver = openDriver();
		if (driver == null || isAppMode(MODE.STOP)) {
			closeDriver(driver);
			return;
		}

		// login into server
		if (!canContinue(driver, false) || !didLogin(driver)) {
			closeDriver(driver);
			return;
		}

		// wait for full page load
		pause(3500, 5000);

		// loop thread while conditions are good
		threadLoop: while (canContinue(driver, true)) {
			// get Hero status (gold, AP, HP)
			if (!canContinue(driver, true) || !isHeroStatus(driver, true, true, true)) {
				break threadLoop;
			}

			// purchase attributes if allowed
			if (!canContinue(driver, true)) {
				break threadLoop;
			}
			purchaseAttributes(driver);

			// check Hero conditions to play story
			if (!canContinue(driver, true)) {
				break threadLoop;
			}

			if (isHeroCondition(driver)) {
				playStory(driver);
			} 

			// if Hero is in bad conditions to play story, purchase Hero attributes
			// if allowed and then pause program for a longer time or until mode changes
			else {
				if (!canContinue(driver, true)) {
					break threadLoop;
				}
				purchaseAttributes(driver);
				longPause(minPause, maxPause);
			}
		}
		// close WebDriver and stop thread
		closeDriver(driver);
		return;
	}

	/**
	 * Open WebDriver.
	 * 
	 * @return WebDriver
	 */
	private WebDriver openDriver() {
		try { // open WebDriver
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			ChromeOptions options = new ChromeOptions().addArguments("--start-maximized");
			return new ChromeDriver(options);
		} catch (WebDriverException e) { // error if browser was manually closed
			infoBox("Cannot control browser tab. \n\n" + "You must have manually closed browser or browser tab.", "ERROR");
			return null;
		} catch (Exception e) { // error if ChromeDriver executable is missing
			infoBox("Cannot launch ChromeDriver. \n\n" + "Do you have Chrome Browser installed on your conputer \n"
					+ "and do you have ChromeDriver executable file in folder? \n", "ERROR");
			return null;
		}
	}

	/**
	 * Close current WebDriver.
	 * 
	 * @param driver WebDriver
	 */
	protected void closeDriver(WebDriver driver) {
		// empty non-editable fields with current Hero data on GUI
		emptyFields("");

		try { // close browser
			driver.close();
		} catch (Exception e) {
		}

		try { // terminate WebDriver process
			driver.quit();
		} catch (Exception e) {
		}

		finally {
			// reset buttons to their default state
			setMode(MODE.STOP);
		}
	}

	/**
	 * Erase non-editable current Hero data on GUI when program stops.
	 */
	private void emptyFields(String s) {
		MAIN.HPCurrent.setText(s);
		MAIN.goldCurrent.setText(s);
		MAIN.strengthCurrent.setText(s);
		MAIN.defenseCurrent.setText(s);
		MAIN.dexterityCurrent.setText(s);
		MAIN.enduranceCurrent.setText(s);
		MAIN.charismaCurrent.setText(s);
		MAIN.humanCurrent.setText(s);
		MAIN.knowledgeCurrent.setText(s);
		MAIN.orderCurrent.setText(s);
		MAIN.natureCurrent.setText(s);
		MAIN.beastCurrent.setText(s);
		MAIN.destructionCurrent.setText(s);
		MAIN.chaosCurrent.setText(s);
		MAIN.corruptionCurrent.setText(s);
		setTextArea("");
	}

	/**
	 * Check for conditions to continue or stop program.
	 * 
	 * @param driver   WebDriver
	 * @param canPause should program pause when requested?
	 * @return condition to continue or stop program
	 */
	private boolean canContinue(WebDriver driver, boolean canPause) {
		try { // check if browser is reachable
			driver.getTitle();
		} catch (WebDriverException e) { // error if browser not reachable
			infoBox("Cannot control browser. \n" + "You must have manually closed browser or browser tab.", "ERROR");
			return false;
		}

		// stop thread if requested or if navigation error occurs
		if (isAppMode(MODE.STOP) || navigateError) {
			return false;
		}

		// loop during pause (if allowed) and check conditions every second
		while (canPause && isAppMode(MODE.PAUSE)) {
			try { // check if browser is reachable
				driver.getTitle();
			} catch (WebDriverException e) { // error if browser not reachable
				infoBox("Cannot control browser. \n" + "You must have manually closed browser or browser tab.",
						"ERROR");
				return false;
			}

			// stop thread if requested or if navigation error occurs
			if (isAppMode(MODE.STOP) || navigateError) {
				return false;
			}

			// if all is good, pause for a second
			pause(1000, 1000);
		}
		// continue program when mode switches from PAUSE to RUN
		return true;
	}

	/**
	 * Check for conditions to proceed program without interruptions.
	 * 
	 * @param driver WebDriver
	 * @return condition to proceed without interruptions
	 */
	private boolean canProceed(WebDriver driver) {
		try { // check if browser is reachable
			driver.getTitle();
		} catch (WebDriverException e) { // error if browser not reachable
			return false;
		}

		// exit if mode is changed from RUN to anything else or if navigation error occurs
		if (!isAppMode(MODE.RUN) || navigateError) {
			return false;
		}

		// otherwise proceed if all is good
		return true;
	}

	/**
	 * Load settings from configuration file.
	 * 
	 * @param file to read
	 */
	private void loadConfig(File file) {
		BufferedReader reader = null;

		try { // locate configuration file
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) { // error if cannot locate configuration file
			infoBox("Cannot locate configuration file. \n"
					+ "Does configuration file exist and do you have read permission?", "WARNING");
		}

		try { // read configuration file
			String line;
			String[] temp;
			int lineCount = 0;

			while ((line = reader.readLine()) != null) {
				switch (++lineCount) {
				case 1:
					username = line;
					break;
				case 2:
					password = line;
					break;
				case 3:
					server = line;
					break;
				case 4:
					county = line;
					break;
				case 5:
					if (line.equalsIgnoreCase("true"))
						canPauseHP = true;
					else
						canPauseHP = false;
					break;
				case 6:
					pauseHPLimit = parseInt(line);
					break;
				case 7:
					if (line.equalsIgnoreCase("true"))
						canPauseGold = true;
					else
						canPauseGold = false;
					break;
				case 8:
					pauseGoldLimit = parseInt(line);
					break;
				case 9:
					if (line.equals("true"))
						canPurchase = true;
					else
						canPurchase = false;
					break;
				case 10:
					attributesLimits[0] = parseInt(line);
					break;
				case 11:
					attributesLimits[1] = parseInt(line);
					break;
				case 12:
					attributesLimits[2] = parseInt(line);
					break;
				case 13:
					attributesLimits[3] = parseInt(line);
					break;
				case 14:
					attributesLimits[4] = parseInt(line);
					break;
				case 15:
					minDelay = parseInt(line);
					break;
				case 16:
					maxDelay = parseInt(line);
					break;
				case 17:
					minPause = parseInt(line);
					break;
				case 18:
					maxPause = parseInt(line);
					break;
				case 19:
					if (line.equalsIgnoreCase("aspects"))
						storyMode = SMODE.ASPECTS;
					else
						storyMode = SMODE.CUSTOM;
				case 20:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story1actions[i] = parseInt(temp[i]);
					break;
				case 21:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[0][i] = parseInt(temp[i]);
					break;
				case 22:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[1][i] = parseInt(temp[i]);
					break;
				case 23:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[2][i] = parseInt(temp[i]);
					break;
				case 24:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[3][i] = parseInt(temp[i]);
					break;
				case 25:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[4][i] = parseInt(temp[i]);
					break;
				case 26:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[5][i] = parseInt(temp[i]);
					break;
				case 27:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[6][i] = parseInt(temp[i]);
					break;
				case 28:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[7][i] = parseInt(temp[i]);
					break;
				case 29:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[8][i] = parseInt(temp[i]);
					break;
				case 30:
					temp = line.split(",");
					for (int i = 0; i < temp.length && i < 60; i++)
						story2actions[9][i] = parseInt(temp[i]);
					break;
				case 31:
					aspectsGoals[0] = parseInt(line);
					break;
				case 32:
					aspectsGoals[1] = parseInt(line);
					break;
				case 33:
					aspectsGoals[2] = parseInt(line);
					break;
				case 34:
					aspectsGoals[3] = parseInt(line);
					break;
				case 35:
					aspectsGoals[4] = parseInt(line);
					break;
				case 36:
					aspectsGoals[5] = parseInt(line);
					break;
				case 37:
					aspectsGoals[6] = parseInt(line);
					break;
				case 38:
					aspectsGoals[7] = parseInt(line);
					break;
				}
			}
		} catch (IOException e) { // error if cannot read configuration file
			infoBox("Cannot read from configuration file. \n"
					+ "Does configuration file exist and do you have read permission?", "WARNING");
		} finally {
			try { // close file reader
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

		// if minimum delay value is higher than maximum delay value, swap values
		int temp;
		if (minDelay > maxDelay) {
			temp = minDelay;
			minDelay = maxDelay;
			maxDelay = temp;
		}

		// if minimum pause value is higher than maximum delay pause, swap values
		if (minPause > maxPause) {
			temp = minPause;
			minPause = maxPause;
			maxPause = temp;
		}
	}

	/**
	 * Login into server.
	 * 
	 * @param driver WebDriver
	 * @return condition of logging into server
	 */
	private boolean didLogin(WebDriver driver) {
		try { // navigate to server page and accept cookies
			loadConfig(config);
			driver.get(server + "/user/login");
			pause(600, 1000);
		} catch (WebDriverException e) { // error if cannot navigate to server page
			infoBox("Cannot navigate to Bitefight server login page.", "ERROR");
			return false;
		}

		try { // accept cookies
			if (driver.findElements(By.cssSelector(cookiesClick)).size() > 0) {
				driver.findElement(By.cssSelector(cookiesClick)).click();
				pause(500, 800);
			}
		} catch (WebDriverException e) {
		}

		try { // login with user credentials
			loadConfig(config);
			driver.findElement(By.cssSelector(usernameInput)).sendKeys(username);
			pause(500, 800);
			loadConfig(config);
			driver.findElement(By.cssSelector(passwordInput)).sendKeys(password);
			pause(500, 800);

			loadConfig(config);
			if (county != null && !county.isEmpty()) {
				try { // select item from dropdown menu on login page if requested
					Select select = new Select(driver.findElement(By.cssSelector(countyInput)));
					select.selectByVisibleText(county);
					pause(500, 800);
				} catch (Exception e) {
				}
			}

			// click login button
			driver.findElement(By.cssSelector(loginClick)).click();

			// catch login error message if user credentials are incorrect
			if (driver.findElements(By.cssSelector(loginError)).size() > 0) {
				infoBox("You provided incorrect user data.", "ERROR");
				return false;
			}
		} catch (WebDriverException e) { // error if cannot login into server
			infoBox("Cannot login into Bitefight server.", "ERROR");
			return false;
		}

		// continue program if login is successful
		return true;
	}

	/**
	 * Get Hero status data.
	 * 
	 * @param driver           WebDriver
	 * @param canClickOverview click OVERVIEW button to refresh Hero status data?
	 * @param canCheckStatus   get Hero status (gold, AP, HP)?
	 * @param canCheckOther    get Hero attributes and aspects?
	 * @return condition of retrieving Hero status data
	 */
	private boolean isHeroStatus(WebDriver driver, boolean canClickOverview, boolean canCheckStatus,
			boolean canCheckOther) {
		// click OVERVIEW button if requested
		if (canClickOverview) {
			try { // click OVERVIEW button
				driver.findElement(By.cssSelector(overviewClick)).click();
			} catch (WebDriverException e) { // error if cannot select OVERVIEW button
				infoBox("Cannot select OVERVIEW button.", "ERROR");
				return false;
			}
		}

		// get Hero GOLD, AP, HP if requested
		if (canCheckStatus) {
			try { // parse page source and collect Hero data
				String text = driver.findElement(By.cssSelector(statusCheck)).getText();
				String[] items = text.replaceAll("\\.", "").replaceAll("/", "").split("\\s+");

				MAIN.HPCurrent.setText(items[5]);
				MAIN.goldCurrent.setText(items[0]);

				status[0] = parseInt(items[0]); // gold
				status[1] = parseInt(items[3]); // AP
				status[2] = parseInt(items[5]); // HP
			} catch (WebDriverException e) { // error if cannot collect Hero data
				infoBox("Cannot retrieve Hero data (gold, AP, HP).", "ERROR");
				return false;
			}
		}

		// get Hero attributes and aspects if requested
		if (canCheckOther) {
			try { // parse page source and collect Hero data
				String[] items = driver.getPageSource().split("[\r\n]+");
				int len = items.length;
				int a = 0;

				for (int i = 1000; i < len && a < 5; i++) {
					if (items[i].contains("<hr /></td></tr><tr><td>")) {
						int index = items[i].indexOf("</td><td align=\"right\">") + 22;
						String temp = items[i].substring(index, index + 6).replaceAll("[^0-9]", "");

						// collect and update Hero attributes
						switch (a) {
						case 0:
							MAIN.strengthCurrent.setText(temp);
							break;
						case 1:
							MAIN.defenseCurrent.setText(temp);
							break;
						case 2:
							MAIN.dexterityCurrent.setText(temp);
							break;
						case 3:
							MAIN.enduranceCurrent.setText(temp);
							break;
						case 4:
							MAIN.charismaCurrent.setText(temp);
							break;
						}

						attributes[a++] = parseInt(temp);
					}
				}

				a = 0;
				for (int i = 1200; i < len && a < 8; i++) {
					if (items[i].contains("<img src=\"/img/story/aspects/Aspekte_pfeil")) {
						String temp = items[i + 4].replaceAll("[^0-9]", "");

						// collect and update Hero aspects
						switch (a) {
						case 0:
							MAIN.humanCurrent.setText(temp);
							break;
						case 1:
							MAIN.knowledgeCurrent.setText(temp);
							break;
						case 2:
							MAIN.orderCurrent.setText(temp);
							break;
						case 3:
							MAIN.natureCurrent.setText(temp);
							break;
						case 4:
							MAIN.beastCurrent.setText(temp);
							break;
						case 5:
							MAIN.destructionCurrent.setText(temp);
							break;
						case 6:
							MAIN.chaosCurrent.setText(temp);
							break;
						case 7:
							MAIN.corruptionCurrent.setText(temp);
							break;
						}

						aspects[a++] = parseInt(temp);
					}
				}
			} catch (WebDriverException e) { // error if cannot retrieve page source
				infoBox("Cannot retrieve Hero attributes and aspects.", "ERROR");
				return false;
			}
		}

		// continue program if Hero data were retrieved
		return true;
	}

	/**
	 * Check Hero conditions to play story.
	 * 
	 * @return condition to play story
	 */
	private boolean isHeroCondition(WebDriver driver) {
		loadConfig(config);
		// stop program if cannot retrieve Hero status
		if (!isHeroStatus(driver, false, true, false)) {
			return false;
		}
		// return condition to play story
		return (isAppMode(MODE.RUN) && status[1] >= 3 && (!canPauseHP || (canPauseHP && status[2] >= pauseHPLimit))
				&& (!canPauseGold || (canPauseGold && status[0] < pauseGoldLimit)));
	}

	/**
	 * Purchase attributes if allowed.
	 * 
	 * @param driver WebDriver
	 */
	private void purchaseAttributes(WebDriver driver) {
		loadConfig(config);

		// determine purchase conditions if purchasing is allowed
		if (canPurchase) {
			boolean canNavigate = false;

			// refresh Hero status
			if (!canProceed(driver) || !isHeroStatus(driver, false, true, true)) {
				return;
			}

			// break loop early if any attributes can be purchased
			for (int i = 0; i < 5; i++) {
				if (isPurchaseCondition(i)) {
					canNavigate = true;
					break;
				}
			}

			// proceed if attributes can be purchased
			if (canNavigate) {
				// navigate to Hero attributes
				if (!canProceed(driver) || !didNavigateAttributes(driver)) {
					return;
				}

				// loop while attributes can be purchased
				loop: while (true) {
					int a = 0;
					int[] candidates = { -1, -1, -1, -1, -1 };

					// refresh Hero status
					if (!canProceed(driver) || !isHeroStatus(driver, false, true, true)) {
						return;
					}

					// collect purchasable attributes
					for (int i = 0; i < 5; i++) {
						if (isPurchaseCondition(i)) {
							candidates[a++] = i;
						}
					}

					// sort purchasable attributes from lowest to highest
					if (a > 0) {
						for (int i = 0; i < 4 && candidates[i + 1] >= 0; i++) {
							if (attributes[candidates[i]] > attributes[candidates[i + 1]]) {
								int temp = candidates[i];
								candidates[i] = candidates[i + 1];
								candidates[i + 1] = temp;
								i = -1;
							}
						}

						// purchase the lowest attribute or return to OVERVIEW page
						if (!canProceed(driver)) {
							return;
						}
						upgradeAttribute(driver, candidates[0]);
					}
					// exit when there is no more purchasable attributes
					else {
						break loop;
					}
				}
			}
		}
	}

	/**
	 * Check for conditions to purchase attributes.
	 * 
	 * @param i index of attribute
	 * @return condition to purchase selected attribute
	 */
	private boolean isPurchaseCondition(int i) {
		loadConfig(config);
		return (canPurchase && (status[0] > (Math.pow(attributes[i] - 4, 2.4) + 1))
				&& (attributes[i] < attributesLimits[i]));
	}

	/**
	 * Purchase attribute by clicking on attribute plus icon.
	 * 
	 * @param driver WebDriver
	 * @param index  of attribute
	 * @return
	 */
	private void upgradeAttribute(WebDriver driver, int index) {
		loadConfig(config);
		try { // upgrade Hero attribute
			driver.findElement(By.cssSelector(plusClick1 + (index + 1) + plusClick2)).click();
			isHeroStatus(driver, false, true, true);
		} catch (WebDriverException d) { // user could manually navigate away from attributes
			try {
				didNavigateAttributes(driver);
				pause(minDelay, maxDelay);
				driver.findElement(By.cssSelector(plusClick1 + (index + 1) + plusClick2)).click();
				isHeroStatus(driver, false, true, true);
			} catch (Exception e) {
				infoBox("Cannot purchase attribute.", "ERROR");
			}
		}
	}

	/**
	 * Play story.
	 *
	 * @param driver WebDriver
	 */
	private void playStory(WebDriver driver) {
		canUseHuntShortcut = false;
		int loops = 0;

		// buy attributes if allowed
		if (!canProceed(driver)) {
			return;
		}
		purchaseAttributes(driver);

		// navigate to story
		if (!canProceed(driver) || !didNavigateStory(driver)) {
			navigateError = true;
			return;
		}

		// Loop while there is no interrupts (e.g. PAUSE) and Hero has good conditions.
		storyLoop: while (canProceed(driver) && isHeroCondition(driver)) {

			loops++;
			// when in STORY MODE 2, Hero aspects will be refreshed on every 40 loops
			if (loops >= 40) {
				// retrieve Hero status and navigate back to story
				if (!canProceed(driver) || !isHeroStatus(driver, true, true, true) || !didNavigateStory(driver)) {
					navigateError = true;
					break storyLoop;
				}
				// reset loops counter
				loops = 0;
			}

			// refresh Hero status
			if (!canProceed(driver) || !isHeroStatus(driver, false, true, false)) {
				break storyLoop;
			}

			// check for exceptional action START STORY
			if (!canProceed(driver)) {
				break storyLoop;
			}
			checkStartStory(driver);

			// check for available story actions
			if (!canProceed(driver)) {
				break storyLoop;
			}
			checkStoryActions(driver);

			// check for exceptional MISSION ACCOMPLISHED pop-up
			if (!canProceed(driver)) {
				break storyLoop;
			}
			checkMissionPopup(driver);

			// check for exceptional action "Continue"
			if (!canProceed(driver)) {
				break storyLoop;
			}
			checkContinue(driver);

			// refresh Hero status
			if (!canProceed(driver) || !isHeroStatus(driver, false, true, false)) {
				break storyLoop;
			}

			// purchase attributes if allowed
			if (!canProceed(driver)) {
				break storyLoop;
			}
			purchaseAttributes(driver);

			// if program navigated to purchase attributes, navigate back to story
			if (!canProceed(driver)) {
				break storyLoop;
			}

			loadConfig(config);

			if (returnToStory) {
				returnToStory = false;
				if (!didNavigateStory(driver)) {
					navigateError = true;
					return;
				}
			}
		}
		// when story loop is interrupted, disable HUNT shortcut and return to OVERVIEW
		// page
		canUseHuntShortcut = false;
		loadConfig(config);
		return;
	}

	/**
	 * Check for CONTINUE button during story.
	 * 
	 * @param driver WebDriver
	 */
	private void checkContinue(WebDriver driver) {
		String[] items = driver.getPageSource().split("[\r\n]+");
		int len = items.length;

		for (int k = 800; k < len; k++) {
			if (items[k].contains("/city/adventure/\"")) {
				try { // try to click CONTINUE button
					loadConfig(config);
					pause(minDelay, maxDelay);
					driver.findElement(By.cssSelector(continueClick)).click();
				} catch (NoSuchElementException e) {
					try { // otherwise try URL
						driver.get(server + "/city/adventure/");
					} catch (WebDriverException d) { // error if cannot select CONTINUE button
						infoBox("Cannot select CONTINUE button.", "ERROR");
						return;
					}
				}
				break;
			}
		}
	}

	/**
	 * Check for MISSION ACCOMPLISHED pop-up during story.
	 * 
	 * @param driver WebDriver
	 */
	private void checkMissionPopup(WebDriver driver) {
		String[] items = driver.getPageSource().split("[\r\n]+");
		int len = items.length;

		for (int k = 800; k < len; k++) {
			if (items[k].contains("div id=\"infoPopup\" class=\"message_screen blackoutdialog\"")) {
				loadConfig(config);
				pause(minDelay, maxDelay);
				try { // try to click CANCEL
					driver.findElement(By.cssSelector(missionClick)).click();
				} catch (NoSuchElementException e) {
					try { // otherwise use URL
						driver.get(server + "/city/adventure/");
					} catch (WebDriverException d) { // error if cannot select MISSION ACCOMPLISHED message
						infoBox("Cannot select MISSION ACCOMPLISHED message.", "ERROR");
						return;
					}
				}
				// break loop after closing pop-up
				break;
			}
		}
	}

	/**
	 * Check for START STORY button to proceed to story.
	 * 
	 * @param driver WebDriver
	 */
	private void checkStartStory(WebDriver driver) {
		String[] items = driver.getPageSource().split("[\r\n]+");
		int len = items.length;

		for (int i = 900; i < len; i++) {
			if (items[i].contains("/city/adventure/startquest")) {
				try { // click on START STORY button
					loadConfig(config);
					pause(minDelay, maxDelay);
					driver.findElement(By.cssSelector(startStoryClick)).click();
				} catch (WebDriverException e) { // error if cannot select START STORY button
					infoBox("Cannot select START STORY button.", "ERROR");
					return;
				}
			}
		}
	}

	/**
	 * Check for available story actions and select one of those if exist.
	 * 
	 * @param driver WebDriver
	 */
	private void checkStoryActions(WebDriver driver) {
		// retrieve indexes of story actions if those exist
		int[] actions = { 0, 0, 0, 0 };
		int chosen = 0;

		// collect indexes of story actions
		String[] items = driver.getPageSource().split("[\r\n]+");
		int len = items.length;
		int a = 0;

		for (int i = 900; i < len && a < 4; i++) {
			if (items[i].contains("/city/adventure/decision/")) {
				int index = items[i].indexOf("/city/adventure/decision/") + 23;
				items[i] = items[i].substring(index, index + 6).replaceAll("[^0-9]", "");
				actions[a++] = Integer.parseInt(items[i]);
			}
		}

		// choose available actions
		if (a > 0) {
			loadConfig(config);

			// go here if story mode is set to MODE1
			if (storyMode == SMODE.CUSTOM) {
				// select one of available actions
				actionLoop: for (int i = 0; i < story1actions.length; i++) {
					for (int j = 0; j < a; j++) {
						if (actions[j] == story1actions[i]) {
							pause(minDelay, maxDelay);
							try { // try with cssSelector
								driver.findElement(By.cssSelector(actionClick1 + (4 + j) + actionClick2)).click();
							} catch (NoSuchElementException e) { // otherwise use URL
								try {
									driver.get(server + "/city/adventure/" + actions[j]);
								} catch (WebDriverException d) { // error if cannot select story action
									infoBox("Cannot select story action.", "ERROR");
									return;
								}
							}
							chosen = actions[j];
							break actionLoop;
						}
					}
				}
			}

			// or go here if story mode is set to MODE2
			else if (storyMode == SMODE.ASPECTS) {
				// prioritize aspects from most wanted to least wanted
				int[] priorities = calc.prioritizeAspects(aspects, aspectsGoals);

				// select one of available actions
				actionLoop: for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 15; j++) {
						for (int k = 0; k < a; k++) {
							if (actions[k] == story2actions[priorities[i]][j]) {
								pause(minDelay, maxDelay);
								try { // try with cssSelector
									driver.findElement(By.cssSelector(actionClick1 + (4 + k) + actionClick2)).click();
								} catch (NoSuchElementException e) { // otherwise use URL
									try {
										driver.get(server + "/city/adventure/" + actions[k]);
									} catch (WebDriverException d) { // error if cannot select story actions
										infoBox("Cannot select story action.", "ERROR");
										return;
									}
								}
								chosen = actions[k];
								break actionLoop;
							}
						}
					}
				}
			}

			// recalculate aspects (to avoid frequent refresh of Hero aspects)
			calc.recalculate(aspects, chosen);

			// notify user about the number of required steps, but only when summary
			// of wanted aspects is above 0 and below 8000 (otherwise infinite loop occurs)
			int sum = 0;
			for (int i = 0; i < 8; i++) {
				sum += aspectsGoals[i];
			}

			if (sum > 0 && sum <= 8000) {
				int steps = calc.numSteps(aspects.clone(), aspectsGoals);
				setTextArea(steps + " steps required to reach your wanted aspects.");
			} else if (sum < 0) {
				setTextArea("Summary of your wanted aspects should not be below 0!");
			} else if (sum >= 8000) {
				setTextArea("Summary of your wanted aspects should not be above 8000!");
			} else {
				setTextArea("");
			}

			// update Hero aspects on program GUI
			MAIN.humanCurrent.setText(Integer.toString(aspects[0]));
			MAIN.knowledgeCurrent.setText(Integer.toString(aspects[1]));
			MAIN.orderCurrent.setText(Integer.toString(aspects[2]));
			MAIN.natureCurrent.setText(Integer.toString(aspects[3]));
			MAIN.beastCurrent.setText(Integer.toString(aspects[4]));
			MAIN.destructionCurrent.setText(Integer.toString(aspects[5]));
			MAIN.chaosCurrent.setText(Integer.toString(aspects[6]));
			MAIN.corruptionCurrent.setText(Integer.toString(aspects[7]));
		}
	}

	/**
	 * Navigate to attributes.
	 * 
	 * @param driver WebDriver
	 * @return success of navigating to ATTRIBUTES tab
	 */
	private boolean didNavigateAttributes(WebDriver driver) {
		try { // click OVERVIEW button
			loadConfig(config);
			pause(minDelay, maxDelay);
			driver.findElement(By.cssSelector(overviewClick)).click();
		} catch (WebDriverException e) { // error if cannot select OVERVIEW button
			infoBox("Cannot select OVERVIEW button.", "ERROR");
			return false;
		}

		try { // click ATTRIBUTES tab
			loadConfig(config);
			driver.findElement(By.cssSelector(attributeClick)).click();
			pause(minDelay, maxDelay);
		} catch (WebDriverException e) { // error if cannot select ATTRIBUTES tab
			infoBox("Cannot select ATTRIBUTES button.", "ERROR");
			return false;
		}

		// continue if all is good; if program navigated from STORY to ATTRIBUTES,
		// allow program to use HUNT shortcut to navigate back from ATTRIBUTES to STORY
		returnToStory = true;
		return true;
	}

	/**
	 * Navigate to story.
	 * 
	 * @param driver WebDriver
	 * @return condition of navigating to story
	 */
	private boolean didNavigateStory(WebDriver driver) {
		loadConfig(config);

		// navigate to story with HUNT button shortcut
		if (canUseHuntShortcut) {
			try { // select HUNT button
				driver.findElement(By.cssSelector(huntClick)).click();
			} catch (WebDriverException e) { // error if cannot select HUNT button
				infoBox("Cannot select HUNT button.", "ERROR");
				return false;
			}
		}
		// or navigate to story in standard way [CITY => TAVERN => START STORY]
		else {
			try { // select CITY button
				driver.findElement(By.cssSelector(cityClick)).click();
				pause(minDelay, maxDelay);
			} catch (WebDriverException e) { // error if cannot select CITY button
				infoBox("Cannot select CITY button.", "ERROR");
				return false;
			}

			try { // select TAVERN link
				driver.findElement(By.cssSelector(tavernClick)).click();
				pause(minDelay, maxDelay);
			} catch (WebDriverException e) { // error if cannot select TAVERN link
				infoBox("Cannot select TAVERN link. \n"
						+ "Make sure you are not occupied \n"
						+ "(e.g. hiding, working, participating in clan wars)", "ERROR");
				navigateError = true;
				return false;
			}

			try { // select START STORY button
				driver.findElement(By.cssSelector(storyClick)).click();
				canUseHuntShortcut = true; // set flag for entering story
			} catch (WebDriverException e) { // error if cannot select START STORY button
				infoBox("Cannot select START STORY button.", "ERROR");
				navigateError = true;
				return false;
			}
		}
		// continue if navigation to story is successful
		return true;
	}

	/**
	 * Compare current application mode to a specified mode state.
	 * 
	 * @param compare mode state to current mode
	 * @return mode match
	 */
	private boolean isAppMode(MODE compare) {
		return (MAIN.appMode == compare);
	}

	/**
	 * Display message dialog box.
	 * 
	 * @param message in message dialog box
	 * @param title   of message dialog box
	 */
	private void infoBox(String message, String title) {
		MAIN.infoBox(message, title);
	}

	/**
	 * Apply new mode and toggle button effects.
	 * 
	 * @param newMode new mode to apply
	 */
	private void setMode(MODE newMode) {
		MAIN.setMode(newMode);
	}

	/**
	 * Set and display user notice in large text area.
	 * 
	 * @param string to set and display
	 */
	private void setTextArea(String string) {
		MAIN.textArea.setText(string);
	}

	/**
	 * Convert string into number.
	 * 
	 * @param string to be converted into number
	 * @return converted number (0 if NaN)
	 */
	private int parseInt(String text) {
		return MAIN.parseInt(text);
	}

	/**
	 * Pause for a random time interval.
	 * 
	 * @param minDelay min delay in milliseconds
	 * @param maxDelay max delay in milliseconds
	 */
	private void pause(int minDelay, int maxDelay) {
		int randomNum = ThreadLocalRandom.current().nextInt(minDelay, maxDelay + 1);
		try {
			Thread.sleep(randomNum);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Pause for a longer time when Hero has bad conditions to play story.
	 * 
	 * @param minPause min seconds to pause
	 * @param maxPause max seconds to pause
	 */
	private void longPause(int minPause, int maxPause) {
		// disable HUNT shortcut and PAUSE program
		canUseHuntShortcut = false;
		setMode(MODE.PAUSE);
		loadConfig(config);

		// loop for a longer time during PAUSE
		int randomNum = ThreadLocalRandom.current().nextInt(minPause, maxPause + 1);
		for (int i = 0; i < randomNum; i++) {
			loadConfig(config);

			// exit immediately if mode changes
			if (!isAppMode(MODE.PAUSE)) {
				setTextArea("");
				return;
			}

			// otherwise build Hero condition message as user notice
			String condition = "";
			if (status[1] < 3) {
				condition += "Your Hero has too low AP";
			}
			if (canPauseHP && status[2] < pauseHPLimit) {
				if (condition.isEmpty()) {
					condition += "Your Hero has too low HP";
				} else {
					condition += " and too low HP";
				}
			}
			if (canPauseGold && status[0] > pauseGoldLimit) {
				if (condition.isEmpty()) {
					condition += "Your Hero has too much gold";
				} else {
					condition += " and too much gold";
				}
			}
			if (!condition.isEmpty()) {
				condition += "! ";
			}

			// display user notice on GUI text area and wait one second
			setTextArea(condition + "Retrying in " + (randomNum - i) + " seconds. \n"
					+ "If your Hero has good conditions to continue, you can \n"
					+ "resume immediately by clicking RUN button.");
			pause(1000, 1000);
		}

		// auto-resume program if timeout
		setTextArea("");
		MAIN.setMode(MODE.RUN);
	}

}
