package com.vince.empire.mbasera.scanner.search;

import com.vince.empire.mbasera.database.helper.Product;

/**
 * Created by VinceGee on 06/11/2016.
 */
public class ShoppingCartEntry {
    private Product mProduct;
    private int mQuantity;

    public ShoppingCartEntry(Product product, int quantity) {
        mProduct = product;
        mQuantity = quantity;
    }

    public Product getProduct() {
        return mProduct;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

}
