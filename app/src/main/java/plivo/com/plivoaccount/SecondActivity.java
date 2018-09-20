package plivo.com.plivoaccount;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondActivity extends AppCompatActivity {

    public IncomingCallReceiver callReceiver;
    public SipManager mSipManager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    @BindView(R.id.errorTextView)
    TextView mErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        this.registerReceiver(callReceiver, filter);
        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(this);
        }
        initializeLocalProfile();

    }

    /**
     * registering this device with SIP address.
     */
    public void initializeLocalProfile() {
        if (mSipManager == null) {
            return;
        }

        if (me != null) {
            closeLocalProfile();
        }

        String username = "123";
        String domain = "";
        String password = "123";

        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            Toast.makeText(this, "Please update your SIP Account Settings.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            me = builder.build();

            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            mSipManager.open(me, pi, null);


            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.

            mSipManager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    //updateStatus("Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    // updateStatus("Ready");
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    //updateStatus("Registration failed.  Please check settings.");
                }
            });
        } catch (ParseException pe) {
            updateStatus("Connection Error.");
        } catch (SipException se) {
            updateStatus("Connection error.");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * unregistering .
     */
    public void closeLocalProfile() {
        if (mSipManager == null) {
            return;
        }
        try {
            if (me != null) {
                mSipManager.close(me.getUriString());
            }
        } catch (Exception ee) {
        }
    }

    /**
     * Dsiplay Status
     */
    public void updateStatus(final String status) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                mErrorText.setText(status);
            }
        });
    }

    /**
     * Updates  SIP address of the current call.
     */
    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if (useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.close();
        }

        closeLocalProfile();
        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
        }
    }

}
