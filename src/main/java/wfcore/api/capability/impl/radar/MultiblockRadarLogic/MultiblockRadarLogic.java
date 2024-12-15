package wfcore.api.capability.impl.radar.MultiblockRadarLogic;

import codechicken.lib.model.loader.blockstate.ITransformFactory;
import gregtech.api.metatileentity.MetaTileEntity;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import wfcore.common.metatileentities.multi.MetaTileEntityRadar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiblockRadarLogic {
    private static final Set<Class<? extends TileEntity>> TE_WHITELIST = new HashSet<>();
    static {
        //TODO: add any viable tileenttity from GT, AE2, WARFORGE and more
    }
    private int voltageTier;
    private int overclockAmount;
    private int speed;
    private MetaTileEntity MetaTileEntity;

    public MultiblockRadarLogic(@NotNull MetaTileEntity MetaTileEntity, int speed){
        this.MetaTileEntity = MetaTileEntity;
        this.speed = speed;

    }

    static public HashMap<Object, BlockPos> collectValidEntites(){
        World world = Minecraft.getMinecraft().world;
        HashMap<Object, BlockPos>  entityPosMap  = new HashMap<>();

        if(world != null && !world.isRemote){
            List<EntityPlayer> worldPlayers = world.playerEntities;
            for(EntityPlayer player :  worldPlayers){
                entityPosMap.put(player, player.getPosition());
            }

            List<TileEntity> worldTileEntites = world.loadedTileEntityList;

        }

    }

    public void setVoltageTier(int tier) {
        this.voltageTier = tier;
    }
    public int getVoltageTier(){
       return voltageTier;
    }

    public void setOverclockAmount(int amount) {
        this.overclockAmount = amount;
    }

    public int getOverclockAmount() {
        return this.overclockAmount;
    }

    public void getSpeed() {
    }

    public boolean isWorking() {
        return false;
    }
}
