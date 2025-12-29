package wfcore.api.block;

import com.google.common.collect.ImmutableList;
import com.hbm.api.block.IToolable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wfcore.api.items.AbstractStack;

import java.util.Collection;

public interface IToolableVariant {
    @Nullable
    IToolable.ToolType getTool();

    @NotNull
    default Collection<AbstractStack> getCost(){return ImmutableList.of();}
}
