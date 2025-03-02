package svenhjol.charm.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import svenhjol.charm.world.block.BlockNetherGoldDeposit;
import svenhjol.charm.world.generator.NetherGoldDepositGenerator;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ForgeHelper;

public class NetherGoldDeposits extends Feature
{
    public static Block ore;
    public static int clusterSize;
    public static int clusterCount;
    public static int minNuggets;
    public static int maxNuggets;
    public static float hardness;
    public static float resistance;

    @Override
    public String getDescription()
    {
        return "Gold deposits spawn in the Nether that can be broken to receive gold nuggets.";
    }

    @Override
    public void configure()
    {
        super.configure();

        // internal
        clusterSize = 12;
        clusterCount = 12;
        hardness = 3.0f;
        resistance = 10.0f;
        minNuggets = 2;
        maxNuggets = 5;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        ore = new BlockNetherGoldDeposit();
        GameRegistry.registerWorldGenerator(new NetherGoldDepositGenerator(), 0);
    }

    @Override
    public boolean isEnabled()
    {
        return !ForgeHelper.areModsLoaded("nethergoldore");
    }
}