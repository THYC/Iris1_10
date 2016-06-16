package net.teraoctet.iris.world;

import java.util.Arrays;
import java.util.LinkedHashSet;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class BorderManager
{
    private double x = 0.0D;
    private double z = 0.0D;
    private int radiusX = 0;
    private int radiusZ = 0;
    private Boolean shapeRound = null;
    private boolean wrapping = false;
    private double maxX;
    private double minX;
    private double maxZ;
    private double minZ;
    private double radiusXSquared;
    private double radiusZSquared;
    private double DefiniteRectangleX;
    private double DefiniteRectangleZ;
    private double radiusSquaredQuotient;

    public BorderManager(double x, double z, int radiusX, int radiusZ, Boolean shapeRound, boolean wrap)
    {
        setData(x, z, radiusX, radiusZ, shapeRound, wrap);
    }

    public BorderManager(double x, double z, int radiusX, int radiusZ)
    {
        setData(x, z, radiusX, radiusZ, null);
    }

    public BorderManager(double x, double z, int radiusX, int radiusZ, Boolean shapeRound)
    {
        setData(x, z, radiusX, radiusZ, shapeRound);
    }

    public BorderManager(double x, double z, int radius)
    {
        setData(x, z, radius, null);
    }

    public BorderManager(double x, double z, int radius, Boolean shapeRound)
    {
        setData(x, z, radius, shapeRound);
    }

    public final void setData(double x, double z, int radiusX, int radiusZ, Boolean shapeRound, boolean wrap)
    {
        this.x = x;
        this.z = z;
        this.shapeRound = shapeRound;
        this.wrapping = wrap;
        setRadiusX(radiusX);
        setRadiusZ(radiusZ);
    }

    public final void setData(double x, double z, int radiusX, int radiusZ, Boolean shapeRound)
    {
        setData(x, z, radiusX, radiusZ, shapeRound, false);
    }

    public final void setData(double x, double z, int radius, Boolean shapeRound)
    {
        setData(x, z, radius, radius, shapeRound, false);
    }

    public BorderManager copy()
    {
        return new BorderManager(this.x, this.z, this.radiusX, this.radiusZ, this.shapeRound, this.wrapping);
    }

    public double getX()
    {
        return this.x;
    }

    public void setX(double x)
    {
        this.x = x;
        this.maxX = (x + this.radiusX);
        this.minX = (x - this.radiusX);
    }

    public double getZ()
    {
        return this.z;
    }

    public void setZ(double z)
    {
        this.z = z;
        this.maxZ = (z + this.radiusZ);
        this.minZ = (z - this.radiusZ);
    }

    public int getRadiusX()
    {
        return this.radiusX;
    }

    public int getRadiusZ()
    {
        return this.radiusZ;
    }

    public void setRadiusX(int radiusX)
    {
        this.radiusX = radiusX;
        this.maxX = (this.x + radiusX);
        this.minX = (this.x - radiusX);
        this.radiusXSquared = (radiusX * radiusX);
        this.radiusSquaredQuotient = (this.radiusXSquared / this.radiusZSquared);
        this.DefiniteRectangleX = Math.sqrt(0.5D * this.radiusXSquared);
    }

    public void setRadiusZ(int radiusZ)
    {
        this.radiusZ = radiusZ;
        this.maxZ = (this.z + radiusZ);
        this.minZ = (this.z - radiusZ);
        this.radiusZSquared = (radiusZ * radiusZ);
        this.radiusSquaredQuotient = (this.radiusXSquared / this.radiusZSquared);
        this.DefiniteRectangleZ = Math.sqrt(0.5D * this.radiusZSquared);
    }

    public int getRadius()
    {
        return (this.radiusX + this.radiusZ) / 2;
    }

    public void setRadius(int radius)
    {
        setRadiusX(radius);
        setRadiusZ(radius);
    }

    public Boolean getShape()
    {
        return this.shapeRound;
    }

    public void setShape(Boolean shapeRound)
    {
        this.shapeRound = shapeRound;
    }

    public boolean getWrapping()
    {
        return this.wrapping;
    }

    public void setWrapping(boolean wrap)
    {
        this.wrapping = wrap;
    }

    @Override
    public String toString()
    {
        return "radius " + (this.radiusX == this.radiusZ ? Integer.valueOf(this.radiusX) : new StringBuilder().append(this.radiusX).append("x").append(this.radiusZ).toString()) + " at X: " + ConfigWorld.coord.format(this.x) + " Z: " + ConfigWorld.coord.format(this.z) + (this.shapeRound != null ? " (shape override: " + ConfigWorld.ShapeName(this.shapeRound) + ")" : "") + (this.wrapping ? " (wrapping)" : "");
    }

    public boolean insideBorder(double xLoc, double zLoc, boolean round)
    {
        if (this.shapeRound != null) {
            round = this.shapeRound;
        }
        if (!round) {
            return (xLoc >= this.minX) && (xLoc <= this.maxX) && (zLoc >= this.minZ) && (zLoc <= this.maxZ);
        }
        double X = Math.abs(this.x - xLoc);
        double Z = Math.abs(this.z - zLoc);
        if ((X < this.DefiniteRectangleX) && (Z < this.DefiniteRectangleZ)) 
        {
            return true;
        }
        if ((X >= this.radiusX) || (Z >= this.radiusZ)) {
            return false;
        }
        return X * X + Z * Z * this.radiusSquaredQuotient < this.radiusXSquared;
    }

    public boolean insideBorder(double xLoc, double zLoc)
    {
        return insideBorder(xLoc, zLoc, ConfigWorld.ShapeRound());
    }

    public boolean insideBorder(Location loc)
    {
        return insideBorder(loc.getX(), loc.getZ(), ConfigWorld.ShapeRound());
    }

    public boolean insideBorder(CoordXZ coord, boolean round)
    {
        return insideBorder(coord.x, coord.z, round);
    }

    public boolean insideBorder(CoordXZ coord)
    {
        return insideBorder(coord.x, coord.z, ConfigWorld.ShapeRound());
    }

    public Location correctedPosition(Location loc, boolean round, boolean flying)
    {
        if (this.shapeRound != null) {
            round = this.shapeRound;
        }
        double xLoc = loc.getX();
        double zLoc = loc.getZ();
        double yLoc = loc.getY();
        if (!round)
        {
            if (this.wrapping)
            {
                if (xLoc <= this.minX) 
                {
                    xLoc = this.maxX - ConfigWorld.KnockBack();
                } 
                else if (xLoc >= this.maxX) 
                {
                    xLoc = this.minX + ConfigWorld.KnockBack();
                }
                if (zLoc <= this.minZ) 
                {
                    zLoc = this.maxZ - ConfigWorld.KnockBack();
                } 
                else if (zLoc >= this.maxZ) 
                {
                    zLoc = this.minZ + ConfigWorld.KnockBack();
                }
            }
            else
            {
                if (xLoc <= this.minX) 
                {
                    xLoc = this.minX + ConfigWorld.KnockBack();
                } 
                else if (xLoc >= this.maxX) 
                {
                    xLoc = this.maxX - ConfigWorld.KnockBack();
                }
                if (zLoc <= this.minZ) 
                {
                    zLoc = this.minZ + ConfigWorld.KnockBack();
                } 
                else if (zLoc >= this.maxZ) 
                {
                    zLoc = this.maxZ - ConfigWorld.KnockBack();
                }
            }
        }
        else
        {
            double dX = xLoc - this.x;
            double dZ = zLoc - this.z;
            double dU = Math.sqrt(dX * dX + dZ * dZ);
            double dT = Math.sqrt(dX * dX / this.radiusXSquared + dZ * dZ / this.radiusZSquared);
            double f = 1.0D / dT - ConfigWorld.KnockBack() / dU;
            if (this.wrapping)
            {
                xLoc = this.x - dX * f;
                zLoc = this.z - dZ * f;
            }
            else
            {
                xLoc = this.x + dX * f;
                zLoc = this.z + dZ * f;
            }
        }
        int ixLoc = Location.locToBlock(xLoc);
        int izLoc = Location.locToBlock(zLoc);


        Chunk tChunk = loc.getWorld().getChunkAt(CoordXZ.blockToChunk(ixLoc), CoordXZ.blockToChunk(izLoc));
        if (!tChunk.isLoaded()) 
        {
            tChunk.load();
        }
        yLoc = getSafeY(loc.getWorld(), ixLoc, Location.locToBlock(yLoc), izLoc, flying);
        if (yLoc == -1.0D) 
        {
            return null;
        }
        return new Location(loc.getWorld(), Math.floor(xLoc) + 0.5D, yLoc, Math.floor(zLoc) + 0.5D, loc.getYaw(), loc.getPitch());
    }

    public Location correctedPosition(Location loc, boolean round)
    {
        return correctedPosition(loc, round, false);
    }

    public Location correctedPosition(Location loc)
    {
        return correctedPosition(loc, ConfigWorld.ShapeRound(), false);
    }

    public static final LinkedHashSet<Integer> safeOpenBlocks = new LinkedHashSet(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(6), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(27), Integer.valueOf(28), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(50), Integer.valueOf(55), Integer.valueOf(59), Integer.valueOf(63), Integer.valueOf(64), Integer.valueOf(65), Integer.valueOf(66), Integer.valueOf(68), Integer.valueOf(69), Integer.valueOf(70), Integer.valueOf(71), Integer.valueOf(72), Integer.valueOf(75), Integer.valueOf(76), Integer.valueOf(77), Integer.valueOf(78), Integer.valueOf(83), Integer.valueOf(90), Integer.valueOf(93), Integer.valueOf(94), Integer.valueOf(96), Integer.valueOf(104), Integer.valueOf(105), Integer.valueOf(106), Integer.valueOf(115), Integer.valueOf(131), Integer.valueOf(132), Integer.valueOf(141), Integer.valueOf(142), Integer.valueOf(149), Integer.valueOf(150), Integer.valueOf(157), Integer.valueOf(171) }));
    public static final LinkedHashSet<Integer> painfulBlocks = new LinkedHashSet(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(51), Integer.valueOf(81), Integer.valueOf(119) }));

    private boolean isSafeSpot(World world, int X, int Y, int Z, boolean flying)
    {
        boolean safe = (safeOpenBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(X, Y, Z)))) && (safeOpenBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(X, Y + 1, Z))));
        if ((!safe) || (flying)) 
        {
            return safe;
        }
        Integer below = Integer.valueOf(world.getBlockTypeIdAt(X, Y - 1, Z));
        return (safe) && ((!safeOpenBlocks.contains(below)) || (below.intValue() == 8) || (below.intValue() == 9)) && (!painfulBlocks.contains(below));
    }

    private double getSafeY(World world, int X, int Y, int Z, boolean flying)
    {
        int limTop = world.getEnvironment() == World.Environment.NETHER ? 125 : world.getMaxHeight() - 2;
        int y1 = Y;
        for (int y2 = Y; (y1 > 1) || (y2 < limTop); y2++)
        {
            if (y1 > 1) 
            {
                if (isSafeSpot(world, X, y1, Z, flying)) 
                {
                    return y1;
                }
            }
            if ((y2 < limTop) && (y2 != y1)) 
            {
                if (isSafeSpot(world, X, y2, Z, flying)) 
                {
                    return y2;
                }
            }
            y1--;
        }
        return -1.0D;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj) {
        return true;
      }
      if ((obj != null) && (obj.getClass() == getClass())) {
      } else {
          return false;
        }
      BorderManager test = (BorderManager)obj;
      return (test.x == this.x) && (test.z == this.z) && (test.radiusX == this.radiusX) && (test.radiusZ == this.radiusZ);
    }

    @Override
    public int hashCode()
    {
      return ((int)(this.x * 10.0D) << 4) + (int)this.z + (this.radiusX << 2) + (this.radiusZ << 3);
    }
}
