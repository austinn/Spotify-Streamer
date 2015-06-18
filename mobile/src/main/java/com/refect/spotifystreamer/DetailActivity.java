package com.refect.spotifystreamer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ViewPager vpSessions;
    private SessionsPagerAdapter sessionsPagerAdapter;

    private ViewPager vpEvents;
    private EventsPagerAdapter eventsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        vpSessions = (ViewPager) findViewById(R.id.vp_sessions);
        sessionsPagerAdapter = new SessionsPagerAdapter(getSupportFragmentManager());
        vpSessions.setAdapter(sessionsPagerAdapter);

        vpEvents = (ViewPager) findViewById(R.id.vp_events);
        eventsPagerAdapter = new EventsPagerAdapter(getSupportFragmentManager());
        vpEvents.setAdapter(eventsPagerAdapter);

        ArrayList<Fragment> frags = new ArrayList<>();
        frags.add(new Fragment());

        eventsPagerAdapter.setFragments(frags);
        sessionsPagerAdapter.setFragments(frags);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class SessionsPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fragments;

        public SessionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            this.fragments = fragments;
            notifyDataSetChanged();
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class EventsPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fragments;

        public EventsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            this.fragments = fragments;
            notifyDataSetChanged();
        }
    }
}
