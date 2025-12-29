package wfcore.api.items;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Desugar
public record AbstractStack(Item item, int meta, byte count)
        implements Comparable<AbstractStack> {

    public static final AbstractStack EMPTY =
            new AbstractStack(null, 0, (byte) 0);

    public AbstractStack copy() {
        return new AbstractStack(this.item, this.meta, this.count);
    }
    public AbstractStack(ItemStack stack) {
        this(
                stack.isEmpty() ? null : stack.getItem(),
                stack.isEmpty() ? 0 : stack.getMetadata(),
                (byte) (stack.isEmpty() ? 0 : stack.getCount())
        );
    }

    private static Object registryName(Item item) {
        return item == null ? null : item.getRegistryName();
    }

    @Override
    public int compareTo(@NotNull AbstractStack o) {
        if (this.item == null && o.item == null) {
        } else if (this.item == null) {
            return -1;
        } else if (o.item == null) {
            return 1;
        } else {
            // Compare registry names
            var a = this.item.getRegistryName();
            var b = o.item.getRegistryName();

            if (a == null && b == null) {
                // continue
            } else if (a == null) {
                return -1;
            } else if (b == null) {
                return 1;
            } else {
                int cmp = a.compareTo(b);
                if (cmp != 0) return cmp;
            }
        }

        // Metadata
        int cmp = Integer.compare(this.meta, o.meta);
        if (cmp != 0) return cmp;

        // Count
        return Byte.compare(this.count, o.count);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractStack other)) return false;

        return this.meta == other.meta
                && this.count == other.count
                && Objects.equals(
                registryName(this.item),
                registryName(other.item)
        );
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(registryName(item));
        result = 31 * result + meta;
        result = 31 * result + count;
        return result;
    }

    public boolean matches(@NotNull ItemStack stack) {
        if (this.item == null || stack.isEmpty()) return false;
        if (stack.getItem() != this.item) return false;
        return this.meta == stack.getMetadata();
    }

    public ItemStack getStack() {
        return item == null || count <= 0
                ? ItemStack.EMPTY
                : new ItemStack(item, count, meta);
    }
    /**
     * Checks if the player has the required stacks in their inventory and optionally removes them.
     *
     * <p>If {@code shouldRemove} is true, the matching amounts are subtracted from the
     * player's inventory, leaving empty slots as {@link ItemStack#EMPTY}.</p>
     *
     * @param player the player whose inventory is checked
     * @param stacks the list of {@link AbstractStack} representing required items and counts
     * @param shouldRemove if true, remove the matched items from the player's inventory
     * @return {@code true} if the player has all required stacks in sufficient quantity;
     *         {@code false} otherwise
     */
    public static boolean resolveCost(EntityPlayer player, List<AbstractStack> stacks, boolean shouldRemove) {
        NonNullList<ItemStack> original = player.inventory.mainInventory;
        ItemStack[] inventory = new ItemStack[original.size()];

        for (int i = 0; i < original.size(); i++) {
            inventory[i] = original.get(i).copy();
        }

        int[] remainingCounts = stacks.stream().mapToInt(AbstractStack::count).toArray();

        for (int i = 0; i < stacks.size(); i++) {
            AbstractStack required = stacks.get(i);
            int remaining = remainingCounts[i];

            for (int j = 0; j < inventory.length && remaining > 0; j++) {
                ItemStack inv = inventory[j];
                if (required.matches(inv)) {
                    int taken = Math.min(remaining, inv.getCount());
                    remaining -= taken;
                    inv.setCount(inv.getCount() - taken);

                    if (inv.getCount() <= 0) {
                        inventory[j] = ItemStack.EMPTY;
                    }
                }
            }

            remainingCounts[i] = remaining;
        }

        for (int remaining : remainingCounts) {
            if (remaining > 0) {
                return false;
            }
        }

        if (shouldRemove) {
            for (int i = 0; i < original.size(); i++) {
                original.set(i, inventory[i]);
            }
        }

        return true;
    }
    public ItemStack extractForCyclingDisplay(int cycle) {
        List<ItemStack> list = this.extractForJEI();
        cycle *= 50;
        return (ItemStack)list.get((int)(System.currentTimeMillis() % (long)(cycle * list.size()) / (long)cycle));
    }

    private List<ItemStack> extractForJEI() {
        return Collections.singletonList(this.getStack());
    }
}


