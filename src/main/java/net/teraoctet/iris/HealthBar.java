package net.teraoctet.iris;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthBar
{
  private Objective health;
  private final Scoreboard board;
  private DisplaySlot displayslot = DisplaySlot.BELOW_NAME;
  private String displayname = ChatColor.RED + "❤";
  
  public HealthBar(Scoreboard board)
  {
    this.board = board;
  }
  
  public void hidehealth()
  {
    if (this.board.getObjective("showhealth") != null)
    {
      this.health = this.board.getObjective("showhealth");
      this.health.unregister();
    }
  }
  
  public void showHealth()
  {
    registerObjective();
    
    this.health.setDisplaySlot(this.displayslot);
    this.health.setDisplayName(this.displayname);
  }
  
  private void registerObjective()
  {
    if (this.board.getObjective("showhealth") == null) 
    {
      this.health = this.board.registerNewObjective("showhealth", "health");
    } 
    else 
    {
      this.health = this.board.getObjective("showhealth");
    }
  }
  
  public DisplaySlot getDisplaySlot()
  {
    return this.displayslot;
  }
  
  public void setDisplaySlot(DisplaySlot displayslot)
  {
    this.displayslot = displayslot;
    showHealth();
  }
  
  public String getDisplayStyle()
  {
    return this.displayname;
  }
  
  public void setDisplayStyle(String displayname)
  {
    this.displayname = displayname;
    showHealth();
  }
}

