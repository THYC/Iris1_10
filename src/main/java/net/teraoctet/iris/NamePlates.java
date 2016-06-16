package net.teraoctet.iris;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class NamePlates
{
    private ScoreboardManager manager;
    private static Scoreboard board;
    private boolean modifiedtab;
    private boolean cleanTab;
    private boolean modifyTab;
    private boolean showHealth;
    private boolean autoRefresh;
    private int refreshInterval = 60000;
    private final ArrayList<Team> teams = new ArrayList();
    private HealthBar health;
    private Timer timer;
    private String StaffColor;
    private String VIPColor;
    private String PremiumColor;
    private static final ConfigFile conf = new ConfigFile();

    public void load()
    {
        this.autoRefresh = conf.getBooleanYAML("config.yml","autorefresh",true);
        this.refreshInterval = conf.getIntYAML("config.yml","refreshinterval",60000);
        this.modifyTab = conf.getBooleanYAML("config.yml","modifytab",true);
        this.cleanTab = conf.getBooleanYAML("config.yml","cleantab",true);
        this.StaffColor = ChatColor.translateAlternateColorCodes("&".charAt(0), conf.getStringYAML("config.yml","StaffColour","&4"));
        this.VIPColor = ChatColor.translateAlternateColorCodes("&".charAt(0), conf.getStringYAML("config.yml","VIPColour","&6"));
        this.PremiumColor = ChatColor.translateAlternateColorCodes("&".charAt(0), conf.getStringYAML("config.yml","PremiumColour","&5"));
        this.showHealth = conf.getBooleanYAML("config.yml","showhealth",true);
        this.manager = Bukkit.getScoreboardManager();
        setBoard(this.manager.getMainScoreboard());

        this.health = new HealthBar(board);
        if (this.showHealth)
        {
            this.health.showHealth();
        }
        else
        {
            this.health.hidehealth();
        }

        refreshPlates();
        if (this.autoRefresh) 
        {
            autoRefresh();
        }
    }

    private void autoRefresh()
    {
        if (this.timer != null) 
        {
            this.timer.cancel();
        }
        this.timer = new Timer();

        this.timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                NamePlates.this.refreshPlates();
            }
        }, 0L, this.refreshInterval);
    }

    public void refreshPlates()
    {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) 
        {
            setPlayer(player);
        }
    }

    public Team newTeam(String name, String color)
    {
        Team team = null;
        if (board.getTeam(name) == null) 
        {
            team = board.registerNewTeam(name);
            team.setPrefix(color);
        } 
        else 
        {
            team = board.getTeam(name);
            team.setPrefix(color);
        }

        this.teams.add(team);

        return team;
    }

    public void setPlayer(Player player)
    {
        try
        {
            player.setScoreboard(board);
        }
        catch (IllegalArgumentException | IllegalStateException localException) {}
        Team team = null;

        if (player.hasPermission("iris.nameplates.staff"))
        {
            team = newTeam("Operator",StaffColor);
            team.addPlayer(player.getPlayer());
        }
        else if (player.hasPermission("iris.nameplates.vip"))
        {
            team = newTeam("Vip",VIPColor);
            team.addPlayer(player.getPlayer());
        }
        else if (player.hasPermission("iris.nameplates.premium"))
        {
            team = newTeam("Premium",PremiumColor);
            team.addPlayer(player.getPlayer());
        }
        if (this.modifyTab)
        {
            this.modifiedtab = true;
            player.setPlayerListName(player.getName());
        }
        else if (this.modifiedtab)
        {
            this.modifiedtab = false;
            player.setPlayerListName(player.getName());
        }
        if (this.cleanTab)
        {
            player.setPlayerListName(ChatColor.RESET + player.getName());
        }
    }

    public static Scoreboard getBoard()
    {
        return board;
    }

    public static void setBoard(Scoreboard b)
    {
        board = b;
    }
}
