/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.ameise.moodtracker.app.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.ApiConstant;
import at.ameise.moodtracker.app.PreferenceConstant;
import at.ameise.moodtracker.app.util.IntentUtil;
import at.ameise.moodtracker.app.util.Logger;
import at.ameise.moodtracker.app.util.ToastUtil;

/**
 * Activity that allows the user to select the account they want to use to sign
 * in. The class also implements integration with Google Play Services and
 * Google Accounts.
 *
 * TODO reconsider and refactor the signin process and the double use of this activity.
 * Modified by Mario Gastegger <mario DOT gastegger AT gmail DOT com>.
 */
public class SignInActivity extends Activity implements DialogInterface.OnClickListener {

    public static final String TAG = SignInActivity.class.getSimpleName();

    /**
     * If this boolean extra is passed with an intent, this activity asks the user to sign in.
     */
    public static final String EXTRA_SIGN_IN = "at.ameise.moodtracker.app.activitySignInActivity.extraSignIn";

    private static final int REQUEST_ACCOUNT_PICKER = 1;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 2;

    private SharedPreferences applicationPreferences;

    /**
     *  Google Account credentials manager.
     */
    private static GoogleAccountCredential credential;

    /**
     *
     * @return the google account credential manager.
     */
    public static GoogleAccountCredential getCredential() {
        return credential;
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);

        applicationPreferences = getSharedPreferences(PreferenceConstant.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);

        if (isSignedIn(this)) {

            Logger.info(TAG, "Already signed in.");
            startMainActivity();

        } else {

            if(applicationPreferences.getBoolean(PreferenceConstant.KEY_FIRST_APPLICATION_START, true)
                    || IntentUtil.getBooleanExtra(getIntent(), EXTRA_SIGN_IN)) {

                Logger.info(TAG, "First application start.");
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("Do you want to sign in with your google account to synchronize your mood data among all your devices?")
                        .setCancelable(false)
                        .setPositiveButton("Yes!", this)
                        .setNegativeButton("No, thanks.", this).create();
                alertDialog.show();
                applicationPreferences.edit().putBoolean(PreferenceConstant.KEY_FIRST_APPLICATION_START, false).apply();

            } else {

                Logger.info(TAG, "User decided not to sign in.");
                startMainActivity();
            }
        }
    }

    /**
     * Handles the results from activities launched to select an account and to install Google Play
     * Services.
     */
    @Override
    protected final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != Activity.RESULT_OK) {

                    Logger.info(TAG, "Play services request canceled by user!");
                    ToastUtil.showMustInstallGooglePlayServicesText(this);
                    cancelSignIn();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
            default:

                if (resultCode == Activity.RESULT_OK) {

                    if (IntentUtil.hasExtra(data, AccountManager.KEY_ACCOUNT_NAME)) {

                        String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);

                        if (accountName != null) {

                            Logger.info(TAG, "User signed in with '" + accountName + "'.");
                            onSignedIn(accountName);

                        } else {

                            Logger.info(TAG, "User didn't choose an account.");
                            cancelSignIn();
                        }
                    }
                } else {

                    Logger.info(TAG, "Account picker canceled by user!");
                    cancelSignIn();
                }
                break;
        }
    }

    /**
     * Sets the a flag to reflect that the user does not want to sign in and start the MainActivity.
     */
    private void cancelSignIn() {

        applicationPreferences.edit().putString(PreferenceConstant.KEY_ACCOUNT_NAME, null).apply();

        startMainActivity();
    }

    /**
     * Called when the user selected an account. The account name is stored in the application
     * preferences and set in the credential object.
     * @param accountName the account that the user selected.
     */
    private void onSignedIn(final String accountName) {

        credential.setSelectedAccountName(accountName);

        applicationPreferences.edit().putString(PreferenceConstant.KEY_ACCOUNT_NAME, accountName).apply();

        startMainActivity();
    }

    /**
     * Navigates to the MainActivity.
     */
    private void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected final void onResume() {
        super.onResume();

        if(applicationPreferences.getString(PreferenceConstant.KEY_ACCOUNT_NAME, null) != null) {
            // As per GooglePlayServices documentation, an application needs to
            // check from within onResume if Google Play Services is available.
            checkPlayServices();
        }
    }

    /**
     * Checks if Google Play Services are installed and if not it initializes opening the dialog to
     * allow user to install Google Play Services.
     * @return a boolean indicating if the Google Play Services are available.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, REQUEST_GOOGLE_PLAY_SERVICES).show();

            } else {

                Logger.info(TAG, "This device is not supported.");
                cancelSignIn();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE:

                startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                break;

            case DialogInterface.BUTTON_NEGATIVE:

                cancelSignIn();
                break;

            default:
                throw new AssertionError("Unhandled user action!");
        }

    }

    /**
     * Retrieves the previously used account name from the application preferences and checks if the
     * credential object can be set to this account.
     * @return a boolean indicating if the user is signed in or not
     */
    public static boolean isSignedIn(final Context context) {

        if(credential == null)
            credential = GoogleAccountCredential.usingAudience(context, ApiConstant.AUDIENCE_ANDROID_CLIENT_ID);

        String accountName = context.getSharedPreferences(PreferenceConstant.APPLICATION_PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceConstant.KEY_ACCOUNT_NAME, null);

        credential.setSelectedAccountName(accountName);

        return credential.getSelectedAccount() != null;
    }

    /**
     * Called to sign out the user, so user can later on select a different account.
     * @param activity activity that initiated the sign out.
     */
    public static void onSignOut(final Activity activity) {

        SharedPreferences applicationPreferences = activity.getSharedPreferences(PreferenceConstant.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);

        applicationPreferences.edit().putString(PreferenceConstant.KEY_ACCOUNT_NAME, "").apply();
        credential.setSelectedAccountName("");

        startSignInActivity(activity);
    }

    /**
     * Navigates to the SignInActivity.
     */
    private static void startSignInActivity(Activity activity) {

        Intent intent = new Intent(activity, SignInActivity.class);
        activity.startActivity(intent);
    }
}
