package net.teraoctet.iris.messageauto;

public class Message 
{
    public String messagename;
    public String groupeperm;
    public int actif;
            
    public Message(String messagename, String groupeperm, int actif)
    {
        this.messagename = messagename;
        this.groupeperm = groupeperm;
        this.actif = actif;
    } 
    
}
