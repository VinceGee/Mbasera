package com.vince.empire.mbasera.scanner;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.scanner.search.Search;
import com.vince.empire.mbasera.scanner.search.ShoppingCartFragment;


/**
 * Created by VinceGee on 2/20/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_ITEMS = 2;
    public static final int SEARCH = 0;
    public static final int CART = 1;
   // public static final int PAY = 2;

    private Context context;

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SEARCH:
               // return FullScannerFragment.newInstance();
                Search search = new Search();
                return search;
            case CART:
                //CartFragmentYeScanner fragment = new CartFragmentYeScanner();
                ShoppingCartFragment cart = new ShoppingCartFragment();
                return cart;

            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case SEARCH:
                return context.getString(R.string.search);
            case CART:
                return context.getString(R.string.cart);
            /*case PAY:
                return context.getString(R.string.pay);*/
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}

