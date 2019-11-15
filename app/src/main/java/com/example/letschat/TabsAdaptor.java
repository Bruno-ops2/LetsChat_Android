package com.example.letschat;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class TabsAdaptor extends FragmentPagerAdapter {

    public TabsAdaptor(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment(new ArrayList<Users>());
                return requestsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment(new ArrayList<Friends>());
                return friendsFragment;

            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                    return null;
        }
    }
}
