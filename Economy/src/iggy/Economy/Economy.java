package iggy.Economy;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

//import net.minecraft.server.CraftWorld;

public class Economy extends JavaPlugin{
	public static Economy plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public Map<String,Long> playerBanks = new HashMap<String,Long>();
	PluginDescriptionFile pdFile;
	String pluginName;
	String pluginTitle;
	
	@Override
	public void onDisable() {

		saveMoney();
		this.logger.info(pluginTitle + " version " + pdFile.getVersion() +" is disabled");
	}

	@Override
	public void onEnable() {
		pdFile = this.getDescription();
		pluginName = pdFile.getName();
		pluginTitle = "[\033[0;32m"+pluginName+"\033[0m]";
		loadMoney();	
		this.logger.info(pluginTitle+ " version " + pdFile.getVersion() +" is enabled");
	}

	public void saveMoney() {
		this.getConfig().set("banks", "");
		Iterator<Entry<String, Long>> bankIterator =  playerBanks.entrySet().iterator();
		while (bankIterator.hasNext()) {
			Entry<String, Long> pairs = bankIterator.next();
			this.getConfig().set("banks."+pairs.getKey(), pairs.getValue());
		}
		this.saveConfig();
	}
	//TODO
	public void loadMoney() {
		playerBanks.clear();
		ConfigurationSection bankConfig = this.getConfig().getConfigurationSection("banks");
		
		if (bankConfig == null) {
			this.logger.severe(pluginTitle+" Failed to read the Configuration File");
			return;
		}
		
		Set<String> players = bankConfig.getKeys(false);
		
		if (players == null) {
			this.logger.severe(pluginTitle+" Failed to read the Configuration File");
			return;
		}
		
		Iterator<String> it = players.iterator();
		while (it.hasNext()) {
			String player = it.next();
			
			long money = this.getConfig().getLong("banks."+player);
			playerBanks.put(player, money);
		}
		
	}
	
	public long getMoney(String player) {
		long playerMoney = 0;
		if (playerBanks.containsKey(player)){
			playerMoney = playerBanks.get(player);
		}
		else {
			playerMoney = createAccount (player);
		}
		return playerMoney;
	}
	public void setMoney(String player, long money) {
		playerBanks.put(player, money);
		saveMoney();
	}
	
	
	/*
	* returns the ammount of money placed in the new player's account
	*/
	public long createAccount(String player) {
		long money = 500;
		playerBanks.put(player, money);
		saveMoney();
		return money;
	}
	/******************************** CHARGE MONEY ********************************\
	| This function will charge money from the player, it will first check to see  |
	| if the player has enough money and will return true if the money was         |
	| successfully charged to the account. It will return false if it was not. If  |
	| a player account cannot be found then it creates an account with the default |
	| amount of money, then attempts to charge it                                  |
	\******************************************************************************/
	public boolean chargeMoney (Player player, long money) {
		return chargeMoney(player.getName(),money);
	}
	public boolean chargeMoney(String player, long money) {
		long playerMoney = getMoney(player);
		
		if (playerMoney > money) {
			playerBanks.put(player, playerMoney-money);
			saveMoney();
			return true;
		}
		return false;
	}
	/********************************* GIVE MONEY *********************************\
	| This function gives money to the player, it will not first check the ammount |
	| of money in the players account (only a problem if the player has more then  |
	|
	\******************************************************************************/
	public boolean giveMoney (Player player,long money) {
		return giveMoney(player.getName(),money);
	}
	public boolean giveMoney (String player,long money) {
		setMoney(player,getMoney(player)+money);
		return false;
	}
}
