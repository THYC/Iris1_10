package net.teraoctet.iris;
import org.bukkit.Material;

public class Sapling
{
    public Material item;
    public Material block;
    public short itemMeta;
    public byte blockMeta;

    public Sapling(Material _item, short _itemMeta, Material _block, byte _blockMeta)
    {
        this.item = _item;
        this.itemMeta = _itemMeta;
        this.block = _block;
        this.blockMeta = _blockMeta;
    }
}
