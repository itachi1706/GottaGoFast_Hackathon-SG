package com.itachi1706.hackathonsg;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.itachi1706.hackathonsg.Database.ProductDB;

import java.util.Date;
import java.util.List;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class MainSettings extends AppCompatActivity {


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @SuppressWarnings("ConstantConditions")
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            //Debug Info Get
            String version = "NULL", packName = "NULL";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
                packName = pInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Preference resetDB = findPreference("resetDB");
            resetDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ProductDB db = new ProductDB(getActivity());
                    db.dropEverythingAndRebuild();
                    sp.edit().remove("lastQueried").apply();
                    return true;
                }
            });

            Preference verPref = findPreference("view_app_version");
            verPref.setSummary(version);
            Preference pNamePref = findPreference("view_app_name");
            pNamePref.setSummary(packName);
            Preference prefs = findPreference("view_sdk_version");
            prefs.setSummary(android.os.Build.VERSION.RELEASE);

            Preference favJson = findPreference("storedPurchases");
            favJson.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String json = sp.getString("storedPurchases", "No Cart JSON");
                    new AlertDialog.Builder(getActivity()).setMessage(json).setTitle("Cart JSON String")
                            .setPositiveButton("Close", null).show();
                    return true;
                }
            });

            Preference timeDBUpdateBus = findPreference("lastQueried");
            long dbBus = sp.getLong("lastQueried", -1);
            updateSummaryDBBus(timeDBUpdateBus, dbBus);

            verPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!isActive) {
                        if (count == 10) {
                            count = 0;
                            startEgg();
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "This app is verified to be made by GottaGoFast for Hackathon@SG 2015", Snackbar.LENGTH_LONG)
                                    .setAction("Okay", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(getActivity(), "Aww okay... :(", Toast.LENGTH_SHORT).show();
                                            endEgg();
                                        }
                                    }).show();
                        } else {
                            switch (count) {
                                case 5:
                                    prompt(5);
                                    break;
                                case 6:
                                    prompt(4);
                                    break;
                                case 7:
                                    prompt(3);
                                    break;
                                case 8:
                                    prompt(2);
                                    break;
                                case 9:
                                    prompt(1);
                                    break;
                            }
                        }
                        count++;
                    }
                    return false;
                }
            });

        }

        MediaPlayer mp;
        int count = 0;
        Toast toasty;
        boolean isActive = false;

        private void prompt(int left){
            if (toasty != null){
                toasty.cancel();
            }
            if (left > 1)
                toasty = Toast.makeText(getActivity(), left + " more clicks for verification!", Toast.LENGTH_SHORT);
            else
                toasty = Toast.makeText(getActivity(), left + " more click for verification!", Toast.LENGTH_SHORT);
            toasty.show();
        }

        private void updateSummaryDBBus(Preference timeDBUpdateBus, long dbBus){
            if (dbBus == -1) {
                timeDBUpdateBus.setSummary("Never");
                return;
            }
            Date date = new Date(dbBus);
            timeDBUpdateBus.setSummary(date.toString());
        }

        @Override
        public void onResume(){
            super.onResume();
            count = 0;
        }

        @Override
        public void onPause(){
            super.onPause();
            endEgg();
        }

        private void startEgg(){
            if (!isActive) {
                mp = MediaPlayer.create(getActivity(), R.raw.settings_fun);
                mp.start();
                isActive = true;
            }
        }

        private void endEgg(){
            count = 0;
            isActive = false;
            if (mp != null){
                if (mp.isPlaying()){
                    mp.stop();
                    mp.reset();
                }
                mp.release();
                mp = null;
            }
        }
    }
}
