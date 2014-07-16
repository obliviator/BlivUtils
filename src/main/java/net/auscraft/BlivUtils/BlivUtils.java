package net.auscraft.BlivUtils;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
//import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import org.bukkit.scheduler.BukkitScheduler;

public final class BlivUtils extends JavaPlugin {
	
    Logger log;
    public BlivUtils plugin;
    private static HashMap<String, Integer> promoteCount = new HashMap<String, Integer>();
    static PermissionManager pex;

    public void onEnable()
    {
    	//Command Declaration
        getCommand("rank").setExecutor(new RankHelpExecuter());
        getCommand("say").setExecutor(new GenericExecuter());
        getCommand("wstop").setExecutor(new GenericExecuter());
        getCommand("sudo").setExecutor(new GenericExecuter());
        getCommand("buyrank").setExecutor(new PromoteExecuter(this));
        getCommand("promoteme").setExecutor(new PromoteExecuter(this));
        getCommand("timeleft").setExecutor(new PromoteExecuter(this));
        //Other Stuff
        log = getLogger();
        pex = PermissionsEx.getPermissionManager();
        
        //Scheduling
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
    		public void run() {
    			for(Player p : getServer().getOnlinePlayers()) {
    				PermissionUser entity = pex.getUser(p);
    				String rank = "";
    				rank = getActiveRank(p);
    				log.info("Rank = " + rank);
    				if(rank != "") {
    					log.info("Rank didnt equal \"\"!");
    					// Debug. Hope this doesn't end up in the final.
        				//if((Integer.parseInt(entity.getOption("group-" + rank + "-until", null)) > (((int) (System.currentTimeMillis() / 1000L))))) {
    					//	log.info("Time left of Endermite for " + p.getName() + ": " + ((Integer.parseInt(entity.getOption("group-" + rank + "-until", null))) - ((int) (System.currentTimeMillis() / 1000L))));
    					//}
        				if((Integer.parseInt(entity.getOption("group-" + rank + "-until", null)) < (((int) (System.currentTimeMillis() / 1000L))))) {
    						entity.removeGroup(rank, null);
    						entity.setOption("group-" + rank + "-until", ""); // Reset the option's value so that the scheduler doesn't get past the first if.
    						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mail send " + p.getName() + " Your " + rank + " has EXPIRED!");
    					    p.sendMessage(ChatColor.GOLD + "Your " + rank + " has " + ChatColor.DARK_RED + "EXPIRED!");
    						log.info("Player " + p.getName() + "'s " + rank + " has EXPIRED! This means it's working!");
    					}
    				}
    			}
    		}
        }, 0L, 72000L);
    }

    public void onDisable()
    {
    }

    public Permission setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        Permission perms = (Permission)rsp.getProvider();
        return perms;
    }

    public Economy setupEconomy()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp != null);
        Economy econ = (Economy)rsp.getProvider();
        return econ;
    }

    public static HashMap<String, Integer> getPromote()
    {
        return promoteCount;
    }

    public BlivUtils getPlugin()
    {
        return plugin;
    }
    
    public static int getTimeLeft(Player p, String rank) {
    	PermissionUser entity = pex.getUser(p);
    	if((entity.getOption("group-" + rank + "-until") != null) && (entity.getOption("group-" + rank + "-until") != "")) {
    		int timeleft = ((Integer.parseInt(entity.getOption("group-" + rank + "-until", null))) - ((int) (System.currentTimeMillis() / 1000L)));
    		return timeleft;
    	}
    	else {
    		return -1;
    	}
    }
    public static String getActiveRank(Player p) {
    	PermissionUser entity = pex.getUser(p);
    	String rank = "";
    	if((entity.getOption("group-Endermite-until") != null) && (entity.getOption("group-Endermite-until") != "")) {
    		rank = "Endermite";
    	}
    	else if((entity.getOption("group-Enderman-until") != null) && (entity.getOption("group-Enderman-until") != "")) {
    		rank = "Enderman";
    	}
    	else if((entity.getOption("group-Enderdragon-until") != null) && (entity.getOption("group-Enderdragon-until") != "")) {
    		rank = "Enderdragon";
    	}
    	else if((entity.getOption("group-Admin-until") != null) && (entity.getOption("group-Admin-until") != "")) {
    		rank = "Admin";
    	}
    	else {
    		rank = "";
    	}
    	return rank;
    }

}
