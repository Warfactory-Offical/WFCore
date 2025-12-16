package wfcore.common.items;

import net.minecraft.item.Item;

import java.util.HashSet;
import java.util.Set;

public class ItemRegistry {

    //Declare public static final items here
    public static final Set<Item> ITEMS =  new HashSet<>();

    //Texture should be in assets/wfcore/textures/items
    public static final Item example = new BaseItem("screwdriver", "screwdriver_default");

}
