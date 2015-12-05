package at.ameise.moodtracker.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;

/**
 * Contains methods to share mood.
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 21.03.15.
 */
public class ShareUtil {

    /**
     * Opens a dialog to share the mood.
     * @param ctx
     * @param mood
     */
    public static final void shareMood(Context ctx, Mood mood) {

        final String mandatoryText = ctx.getString(R.string.share_message_text, getMoodText(ctx, mood));
        final String optionalText = ctx.getString(R.string.share_message_subject);

        final PackageManager pm = ctx.getPackageManager();

        final Intent genericShareIntent = new Intent();
        genericShareIntent.setAction(Intent.ACTION_SEND);
        genericShareIntent.putExtra(Intent.EXTRA_SUBJECT, optionalText);
        genericShareIntent.putExtra(Intent.EXTRA_TEXT, mandatoryText);
        genericShareIntent.setType("text/plain");

        final Intent openInChooser = Intent.createChooser(genericShareIntent,ctx.getString(R.string.share_chooser_text));

        final Intent queryIntent = new Intent(Intent.ACTION_SEND);
        queryIntent.setType("text/plain");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY);//TODO refine query
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a
            // LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;

            if (packageName.contains("android.email")) {

                genericShareIntent.setPackage(packageName);

            } else if (packageName.contains("twitter")
                    || packageName.contains("facebook")
                    || packageName.contains("com.google.android.apps.plus")) {

                final Intent shareIntent = new Intent();
                shareIntent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                if (packageName.contains("twitter")) {

                    shareIntent.putExtra(Intent.EXTRA_TEXT, mandatoryText);

                } else if (packageName.contains("facebook")) {

                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, optionalText);
                    shareIntent.putExtra(Intent.EXTRA_TITLE, mandatoryText);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mandatoryText);

                } else if (packageName.contains("com.google.android.apps.plus")) {

                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, optionalText);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mandatoryText);
                }

                intentList.add(new LabeledIntent(shareIntent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        openInChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(openInChooser);
    }

    /**
     * @param ctx
     * @param mood
     * @return returns a textual representation of the mood.
     */
    private static String getMoodText(Context ctx, Mood mood) {

        final int maxMood = ctx.getResources().getInteger(R.integer.max_mood);

        final float veryGoodMoodLowerRange = ((float) maxMood * 3 / 4);
        final float goodMoodLowerRange = ((float) maxMood * 2 / 4);
        final float badMoodLowerRange = ((float) maxMood * 1 / 4);

        if(veryGoodMoodLowerRange <= mood.getMood()) {
            //very good
            return ctx.getString(R.string.textual_mood_very_good);

        } else if(goodMoodLowerRange <= mood.getMood()) {
            //good
            return ctx.getString(R.string.textual_mood_good);

        } else if(badMoodLowerRange <= mood.getMood()) {
            //bad
            return ctx.getString(R.string.textual_mood_bad);

        } else {
            //very bad
            return ctx.getString(R.string.textual_mood_very_bad);
        }
    }

}
