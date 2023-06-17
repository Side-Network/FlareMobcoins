package net.devtm.tmmobcoins.util;

public class ShopStock {
    int stock;

    StockType type;

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public StockType getType() {
        return this.type;
    }

    public void setType(StockType type) {
        this.type = type;
    }

    public enum StockType {
        SERVER, PLAYER;
    }
}
