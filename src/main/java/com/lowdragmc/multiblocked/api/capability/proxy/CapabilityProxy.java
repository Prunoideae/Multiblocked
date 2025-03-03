package com.lowdragmc.multiblocked.api.capability.proxy;


import com.lowdragmc.multiblocked.api.capability.ICapabilityProxyHolder;
import com.lowdragmc.multiblocked.api.capability.IInnerCapabilityProvider;
import com.lowdragmc.multiblocked.api.capability.IO;
import com.lowdragmc.multiblocked.api.capability.MultiblockCapability;
import com.lowdragmc.multiblocked.api.recipe.Recipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Proxy of a specific capability that has been detected {@link MultiblockCapability}. Providing I/O and such features to a controller.
 */
public abstract class CapabilityProxy<K> {
    public final MultiblockCapability<? super K> capability;
    public Direction facing;
    private long latestPeriodID;

    private TileEntity tileEntity;

    public CapabilityProxy(MultiblockCapability<? super K> capability, TileEntity tileEntity) {
        this.capability = capability;
        this.tileEntity = tileEntity;
        this.facing = Direction.UP;
    }

    public TileEntity getTileEntity() {
        if (tileEntity != null && tileEntity.isRemoved()) {
            tileEntity = tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos());
        }
        return tileEntity;
    }

    public <C> C getCapability(Capability<C> capability) {
        TileEntity tileEntity = getTileEntity();
        return tileEntity == null ? null : tileEntity instanceof IInnerCapabilityProvider ? ((IInnerCapabilityProvider) tileEntity).getInnerCapability(capability, facing).orElse(null) : tileEntity.getCapability(capability, facing).orElse(null);
    }

    public long getLatestPeriodID() {
        return latestPeriodID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CapabilityProxy<?>)) return false;
        return Objects.equals(getTileEntity(), ((CapabilityProxy<?>) obj).getTileEntity());
    }

    /**
     * matching or handling the given recipe.
     *
     * @param io the IO type of this recipe. always be one of the {@link IO#IN} or {@link IO#OUT}
     * @param recipe recipe.
     * @param left left contents for to be handled.
     * @param simulate simulate.
     * @return left contents for continue handling by other proxies.
     * <br>
     *      null - nothing left. handling successful/finish. you should always return null as a handling-done mark.
     */
    protected abstract List<K> handleRecipeInner(IO io, Recipe recipe, List<K> left, boolean simulate);

    /**
     * Check whether scheduling recipe checking. Check to see if any changes have occurred.
     * Do not do anything that causes conflicts here.
     * @return if changed.
     */
    protected boolean hasInnerChanged() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public final K copyContent(Object content) {
        return (K) capability.copyInner((K)content);
    }

    public final List<K> searchingRecipe(IO io, Recipe recipe, List<?> left) {
        return handleRecipeInner(io, recipe, left.stream().map(this::copyContent).collect(Collectors.toList()), true);
    }

    public final List<K> handleRecipe(IO io, Recipe recipe, List<?> left) {
        return handleRecipeInner(io, recipe, left.stream().map(this::copyContent).collect(Collectors.toList()), false);
    }

    public final void updateChangedState(long periodID) {
        if (hasInnerChanged() || periodID < latestPeriodID) {
            latestPeriodID = periodID;
        }
    }

    public void preWorking(ICapabilityProxyHolder holder, IO io, Recipe recipe) {
    }

    public void postWorking(ICapabilityProxyHolder holder, IO io, Recipe recipe) {
    }
}
