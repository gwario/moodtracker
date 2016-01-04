package at.ameise.moodtracker.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import at.ameise.moodtracker.app.TagConstant;
import at.ameise.moodtracker.app.util.Logger;

/**
 * Since the application class's onCreate is called, which initializes the app, it is nothing to be done here.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //XXX MGA: Nothing to be done here.
        Logger.debug(TagConstant.INITIALIZATION, "Boot completed.");
    }
}
