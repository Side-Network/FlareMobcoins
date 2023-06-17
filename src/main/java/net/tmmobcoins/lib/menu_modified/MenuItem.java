package net.tmmobcoins.lib.menu_modified;

import java.util.List;
import net.tmmobcoins.lib.menu_modified.item.ItemHandler;

public class MenuItem {
    public List<ItemHandler> items;

    public List<ItemHandler> getItems() {
        return this.items;
    }

    public void setItems(List<ItemHandler> items) {
        this.items = items;
    }
}
