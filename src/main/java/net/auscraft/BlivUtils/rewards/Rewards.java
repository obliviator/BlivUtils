package net.auscraft.BlivUtils.rewards;

import net.auscraft.BlivUtils.util.BUtil;
import net.auscraft.BlivUtils.util.FlatFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;


public class Rewards implements CommandExecutor
{
	
	public static final String prefix = "Birthday";
	
	private RewardContainer[][] rewardsTable;
	private FlatFile cfg;
	
	//Gift specific global variables
	private int[] totalGiftPool = {0,0,0,0,0}; //Initialize the counts to 0, and add to them in the constructor.
	
	public Rewards()
	{
		this.cfg = FlatFile.getInstance();
		loadRewards();
	}
	
	public RewardContainer[][] loadInitialRewards()
	{	
		int ii; //Counts number of rewards in a pool.
		
		//Retrieve the list of rewards for each category.
		int rewardPoolNum = cfg.getInt("options.rewards.count");
		//util.logInfo("Pool Count: " + rewardPoolNum);
		
		//Reset the rewards table, as this function will be used to reload the table too.
		RewardContainer[][] rewardsTable = new RewardContainer[rewardPoolNum][50]; //Hardcoded 50 reward max.
		
		try
		{
			//Loop through each pool
			for(int j = 0;j < rewardPoolNum;j++)
			{
				ii = 0;
				List<String> pool = cfg.getStringList("options.rewards.pool" + (j+1));
				for(String i : pool)
				{
					String name, itemid, lore;
					String enchantsList;
					double chance;
					int[][] enchants = {{-1,-1,-1},{-1,-1,-1},{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}}; //5 Enchantments per item, 3 Available Enchantment Variables
					
					try
					{
						String[] rewards = i.split("[,]");
						
						if(rewards.length == 5) //4 Variables
						{
							chance = Double.parseDouble(rewards[0]);
							name = rewards[1];
							itemid = rewards[2];
						    enchantsList = rewards[3];
						    
						    
						    if(!enchantsList.contains("-"))
						    {
						    	String[] encSets = enchantsList.split("[|]");
						    	String[] encVars;
						    	for(int encGroup = 0;encGroup < (encSets.length);encGroup++)
						    	{
						    		encVars = encSets[encGroup].split("[:]");
						    		enchants[encGroup][0] = Integer.parseInt(encVars[0]);
						    		enchants[encGroup][1] = Integer.parseInt(encVars[1]);
						    		enchants[encGroup][2] = Integer.parseInt(encVars[2]);
						    	}
						    }
						    else
						    {
						    	enchants = null;
						    	//log.info("No Enchantments Defined. Skipping...");
						    }
						    
						    if(!rewards[3].contains("-"))
						    {
							    lore = rewards[4];
						    }
						    else
						    {
						    	lore = "LEFT_BLANK";
						    }
						    
						}
						else //2 variables are required to input into the table. Might as well cut them off and make them fix it.
						{
							//util.logError("[2] Syntax error in rewards config. Disabling rewards features.");
							rewardsTable = null;
							return null;
						}
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						//util.logError("[3] Syntax error in rewards config. Disabling rewards features.");
						rewardsTable = null;
						e.printStackTrace();
						return null;
					}
				    
					if(enchants != null)
					{
						int encLoop = 0;
						int[] defaultEnc = {0,0,0};
						while((enchants[encLoop] != defaultEnc) && (encLoop < 4))
						{
							enchantsList += enchants[encLoop][0] + ":" + enchants[encLoop][1] + ":" + enchants[encLoop][2] + " ";
							encLoop++;
						}
					}
					
					//log.info("Entry " + ii + ": " + chance + " " + name + " " + itemid + " " + enchantsList + " " + lore);
					RewardContainer reward = new RewardContainer(chance, name, itemid, enchants, lore);
					rewardsTable[j][ii] = reward;
					
					ii++;
				    
				    //Reset the strings, or wrong values may be passed back.
					/*if(enchants != null)
				    {
				    }*/

				}
			}
		}
		catch(Exception e)
		{
			//fallback to let the plugin load properly.
			e.printStackTrace();
		}
		return rewardsTable;
	}
	
	private void loadRewards()
	{
		totalGiftPool[0] = 0;	//Reset the total gift pool
		totalGiftPool[1] = 0;	//If not, more rewards than
		totalGiftPool[2] = 0;	//exists are attempted to be
		totalGiftPool[3] = 0;	//picked from, resulting in
		totalGiftPool[4] = 0;	//fun NPEs for the whole family.
		
		//Read this in from file or something based on how many there actually are.
		//rewardsTable = cfg.loadRewards();
		rewardsTable = loadInitialRewards();
		if(rewardsTable == null)
		{
			throw new NullPointerException("Rewards Table is blank!");
		}
		try
		{
			for(int i = 0;i < rewardsTable.length;i++)
			{
				//log.info("|Loading Pool " + (i+1));
				for(int ii = 0;ii < rewardsTable[i].length;ii++)
				{
					//log.info(" - Loading Reward " + (ii+1));
					if(rewardsTable[i][ii] != null)
					{
						totalGiftPool[i]++;
						//log.info("   - " + rewardsTable[i][ii].getName());
					}
					else
					{
						break;
					}
				}
			}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		BUtil.logInfo((totalGiftPool[0] + totalGiftPool[1] + totalGiftPool[2] + totalGiftPool[3] + totalGiftPool[4]) + " rewards imported");
	}
	
	
	public static String translatePrefix(String string)
	{
		String fixedString;
		Pattern GiftPattern = Pattern.compile("[@]");
		fixedString = GiftPattern.matcher(string).replaceAll(prefix);
		return fixedString;
	}
	
	public static String translatePlayerName(CommandSender sender, String string)
	{
		String fixedString;
		Pattern GiftPattern = Pattern.compile("[%]");
		fixedString = GiftPattern.matcher(string).replaceAll(sender.getName());
		return fixedString;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) 
	{
		if (cmd.getName().equalsIgnoreCase("present"))
		{
			PermissionUser user = PermissionsEx.getUser(sender.getName());
			if(user.has("blivutils.present.birthday.done"))
			{
				BUtil.printError(sender, "You can only open one Gift for this celebration!");
				return true;
			}
			
			if(args.length >= 1)
			{
				if(args[0].equals("open") && (sender instanceof Player))
				{
					//5 Gifts will be randomized later?
					int numberGifts = rewardsTable.length;
					RewardContainer[] rolledGift = new RewardContainer[numberGifts];
					String rewardString = "";
					Random rand = new Random(System.currentTimeMillis());
					
					//Should I bother randomising if they're good or bad?
					sender.sendMessage(ChatColor.AQUA + "Thanks for joining in " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Aus" + ChatColor.WHITE + ChatColor.BOLD + "Craft's " + ChatColor.AQUA + prefix + " Celebrations!");
					
					//Start gifting.
					try
					{
						ChatColor hyphen = ChatColor.DARK_BLUE;
						for(int i = 0;i < numberGifts;i++)
						{
								//Get the chance, round it down, and roll a number.
								//Example:
								// - Chance: 0.6
								// 0.6 * 100 = 60
								// Roll, if number between 0 and 60, prize is won.
								// If roll is 70-100, prize is not won.
								//    - Re-roll another gift.
								boolean won = false;
								int chance, reroll, numRolls = 0;
								do
								{
									rolledGift[i] = rewardsTable[i][rand.nextInt(totalGiftPool[i])];
									chance = (int) Math.ceil(( rolledGift[i].getChance() * 100));
									if(chance == 0)
									{
										//Chance is wrong.
										//Infinite loop will occur, just kill it.
										break;
									}
									reroll = rand.nextInt(100);
									//log.info("Rolled a " + reroll + " | and needed a " + chance + " or lower.");
									if(reroll < chance)
									{
										won = true;
										BUtil.logInfo("Reward: " + rolledGift[i].getName() + " was won! Congratulations!");
									}
									//else
									//{
										//log.info("Reward: " + rolledGift[i].getName() + " was not won.");
									//}
									if(numRolls >= 20)
									{
										//Too many rolls.
										//Give the first entry.
										rolledGift[i] = rewardsTable[i][0];
										won = true;
									}
									numRolls++;
								} while((!won));
								
								BUtil.logInfo(rolledGift[i].getName());
								
								if(hyphen == ChatColor.AQUA)
								{
									hyphen = ChatColor.WHITE;
								}
								else if(hyphen == ChatColor.WHITE)
								{
									hyphen = ChatColor.AQUA;
								}
								rewardString += hyphen + " - " + ChatColor.GOLD + rolledGift[i].getName() + "\n";
						}
						rewardString = BUtil.translateColours(rewardString.substring(0, (rewardString.length() - 1)));
						
						rewardString = translatePrefix(rewardString);
						
						sender.sendMessage(ChatColor.YELLOW + "Congratulations!" + ChatColor.GREEN + " You've opened:\n" + rewardString);
						
						//Oman -- It was in a flippidy floop the whole time.
						BUtil.logtoFile("------------------------------------------\n" + sender.getName() + "has won:", "rewardslog");
						BUtil.logtoFile(rewardString, "rewardslog");
						
						
						user.addPermission("blivutils.present.birthday.done"); //Player can no longer type /present open
					}
					catch(NullPointerException e)
					{
						e.printStackTrace();
						BUtil.logtoFile("Player " + sender.getName() + " had problems with their gift.", "rewardslog");
						BUtil.printError(sender, "Oops! Your gift had trouble opening. Send a /modreq for the Musketeers.");
					}
					
					giveRewards(sender, rolledGift);
					
					return true;
				}
				else if(args[0].equals("reload"))
				{
					if(sender.hasPermission("blivutils.rewards.admin"))
					{
						rewardsTable = null; //Hopefully this allows for reloads.
						//cfg.loadRewards();
						loadRewards();
						sender.sendMessage(ChatColor.GREEN + "Loaded " + ((totalGiftPool[0] + totalGiftPool[1] + totalGiftPool[2] + totalGiftPool[3] + totalGiftPool[4]) + " rewards"));
					}
				}
				return true;
			}
			return true;
		}
		
		return false;
	}
	
	public void giveRewards(CommandSender sender, RewardContainer[] rolledGift)
	{
		Player p = (Player) sender;
		for(RewardContainer aRolledGift : rolledGift)
		{
			try
			{
				BUtil.logInfo("Giving Reward: " + aRolledGift.getName());
				if(aRolledGift.getAction().startsWith("-"))    //Reward is a command
				{
					String action = aRolledGift.getAction().substring(1, aRolledGift.getAction().length());
					action = action.replaceAll("%", p.getName()); //Replace % with players name
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), action);
					//p.sendMessage("Reward given!");
					BUtil.logInfo("Command: '" + action + "'");
				}
				else    // Reward is an item
				{
					Random rand = new Random(System.currentTimeMillis()); //Define random here, so the rewards are varied, and aren't done by the same seed.
					Material item = Material.getMaterial(Integer.parseInt(aRolledGift.getAction()));
					ItemStack reward = new ItemStack(item);

					if(aRolledGift.getName() != null)
					{
						ItemMeta meta = reward.getItemMeta();

						String lore = translatePlayerName(sender, translatePrefix(BUtil.translateColours(aRolledGift.getName())));

						meta.setDisplayName(lore);
						reward.setItemMeta(meta);
					}

					if((aRolledGift.getLore() != null) && !(aRolledGift.getLore().equals("LEFT_BLANK")))    //Lore is not empty
					{
						List<String> lore = new ArrayList<>();
						lore.add(translatePlayerName(sender, BUtil.translateColours(aRolledGift.getLore())));
						ItemMeta meta = reward.getItemMeta();
						meta.setLore(lore);
						reward.setItemMeta(meta);
					}

					if(aRolledGift.getEnchantmentOpts() != null) //Enchantment listings are not empty
					{
						for(int ii = 0; ii < aRolledGift.getEnchantmentOpts().length; ii++)
						{
							int[] enchants = aRolledGift.getEnchantmentOpts()[ii];
							//enchants[0] = Enchantment ID
							//enchants[1] = Base Enchantment Level
							//enchants[2] = Enchantment Variation (In Levels)

							//Level randomisation

							int level = enchants[1], variation;
							boolean valid = false;
							if(enchants[0] >= 0) //If the Enchantment ID is above 0 (Valid)
							{
								if(enchants[2] > 0) //If the variation is above 0, apply the variation possibility
								{
									//Final level = Base + (RANDOM(Variation))
									variation = rand.nextInt(enchants[2]);
									level = enchants[1] + (variation);
								}

								valid = level > 0;
							}

							if(valid)
							{
								//log.info("Enchantment " + enchants[0] + " with level " + level + " is valid.");
							/*
							0	minecraft:protection	Protection	Armor	4
							1	minecraft:fire_protection	Fire Protection	Armor	4
							2	minecraft:feather_falling	Feather Falling	Boots	4
							3	minecraft:blast_protection	Blast Protection	Armor	4
							4	minecraft:projectile_protection	Projectile Protection	Armor	4
							5	minecraft:respiration	Respiration	Helmets	3
							6	minecraft:aqua_affinity	Aqua Affinity	Helmets	1
							7	minecraft:thorns	Thorns	Armor	3
							8	minecraft:depth_strider	Depth Strider	Boots	3
							16	minecraft:sharpness	Sharpness	Swords, Axes	5
							17	minecraft:smite	Smite	Swords, Axes	5
							18	minecraft:bane_of_arthropods	Bane of Arthropods	Swords, Axes	5
							19	minecraft:knockback	Knockback	Swords	2
							20	minecraft:fire_aspect	Fire Aspect	Swords	2
							21	minecraft:looting	Looting	Swords	3
							32	minecraft:efficiency	Efficiency	Pickaxes, Shovels, Axes, Shears	5
							33	minecraft:silk_touch	Silk Touch	Pickaxes, Shovels, Axes, Shears	1
							34	minecraft:unbreaking	Unbreaking	Any Item with Durability	3
							35	minecraft:fortune	Fortune	Pickaxes, Shovels, Axes	3
							48	minecraft:power	Power	Bow	5
							49	minecraft:punch	Punch	Bow	2
							50	minecraft:flame	Flame	Bow	1
							51	minecraft:infinity	Infinity	Bow	1
							61	minecraft:luck_of_the_sea	Luck of the Sea	Fishing Rod	3
							62	minecraft:lure
							 */
								switch(enchants[0])
								{
									case 0:
										reward.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
										break;
									case 1:
										reward.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, level);
										break;
									case 2:
										reward.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, level);
										break;
									case 3:
										reward.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, level);
										break;
									case 4:
										reward.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, level);
										break;
									case 5:
										reward.addUnsafeEnchantment(Enchantment.OXYGEN, level);
										break;
									case 6:
										reward.addUnsafeEnchantment(Enchantment.WATER_WORKER, level);
										break;
									case 7:
										reward.addUnsafeEnchantment(Enchantment.THORNS, level);
										break;
									case 8:
										reward.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, level);
										break;
									case 16:
										reward.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level);
										break;
									case 17:
										reward.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, level);
										break;
									case 18:
										reward.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, level);
										break;
									case 19:
										reward.addUnsafeEnchantment(Enchantment.KNOCKBACK, level);
										break;
									case 20:
										reward.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, level);
										break;
									case 21:
										reward.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level);
										break;
									case 32:
										reward.addUnsafeEnchantment(Enchantment.DIG_SPEED, level);
										break;
									case 33:
										reward.addUnsafeEnchantment(Enchantment.SILK_TOUCH, level);
										break;
									case 34:
										reward.addUnsafeEnchantment(Enchantment.DURABILITY, level);
										break;
									case 35:
										reward.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, level);
										break;
									case 48:
										reward.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, level);
										break;
									case 49:
										reward.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, level);
										break;
									case 50:
										reward.addUnsafeEnchantment(Enchantment.ARROW_FIRE, level);
										break;
									case 51:
										reward.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, level);
										break;
									case 61:
										reward.addUnsafeEnchantment(Enchantment.LUCK, level);
										break;
									case 62:
										reward.addUnsafeEnchantment(Enchantment.LURE, level);
										break;
									
									default:
										break;
								}
							}
						}
					}


					p.getInventory().addItem(reward);
				}
			}
			catch(CommandException e)
			{
				BUtil.logSevere("Error giving rewards! Check the config for misplaced ','");
				e.printStackTrace();
			}
			
		}
	}
	
}
