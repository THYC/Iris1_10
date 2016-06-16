package net.teraoctet.iris;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class InfoBook
implements CommandExecutor
{
    private static List<String> INFOBOOK_PAGES = new ArrayList();
    private static String INFOBOOK_TITLE;
    private static String INFOBOOK_AUTHOR;
    private static List<String> INFOBOOK_DESCRIPTION = new ArrayList();
    private static final ConfigFile conf = new ConfigFile();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = (Player)sender;
        if ((sender instanceof Player))
        {  
            if (label.equalsIgnoreCase("infobook")) 
              {   
                  if (args.length == 0)
                    {
                        String defaultBook = conf.getStringYAML("books.yml", "default",null);
                        
                        ItemStack book = createDefaultBook(defaultBook,player);                        
                        if (book != null) 
                        {
                            player.getInventory().addItem(new ItemStack[] {book});
                            sender.sendMessage(formatMsg.format("<aqua>Le book <white>" +  defaultBook + " <aqua>a <e_ai>t<e_ai> ajout<e_ai> a votre inventaire."));
                            return true;
                        }
                    }
                  
                    if ("list".equalsIgnoreCase(args[0]))
                    {
                        Set<String> list = conf.getKeysYAML("books.yml", "book");
                        sender.sendMessage(Iris.formatMsg.format("<gold>Book : " + list, player));
                        return true;
                    }
                    else
                    {
                        ItemStack book = createDefaultBook(args[0],player);
                        if (book != null) 
                        {
                            player.getInventory().addItem(new ItemStack[] {book});
                            return true;
                        }  
                    }
                }
                else
                {
                    sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml", "noPermission","Vous n'avez pas la permission")));
                }
        }
        else 
        {
            sender.sendMessage(ChatColor.RED + "This is a player-only command. Derp.");
        }
        return true;
    }
    
    public ItemStack createDefaultBook(String bookname, Player player)
    {
        ItemStack i = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta)i.getItemMeta();
        INFOBOOK_AUTHOR = conf.getStringYAML("books.yml", "book." + bookname + ".author","Staff Teraoctet");
        INFOBOOK_TITLE = conf.getStringYAML("books.yml", "book." + bookname + ".title","HelpBook");
        INFOBOOK_PAGES = conf.getListYAML("books.yml", "book." + bookname + ".pages");
        INFOBOOK_DESCRIPTION = conf.getListYAML("books.yml", "book." + bookname + ".lore");

        bookMeta.setAuthor(INFOBOOK_AUTHOR);
        bookMeta.setTitle(INFOBOOK_TITLE);
        bookMeta.setLore(INFOBOOK_DESCRIPTION);
        for (String page : INFOBOOK_PAGES)
        {
          page = page.replaceAll("&x", "\n");
          page = page.replaceAll("&", "§");
          page = Iris.formatMsg.format(page, player);
          bookMeta.addPage(new String[] { page });
        }
        i.setItemMeta(bookMeta);

        return i;
    }
}

