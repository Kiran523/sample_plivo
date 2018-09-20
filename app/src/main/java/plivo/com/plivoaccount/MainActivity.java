package plivo.com.plivoaccount;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.editText1)
    EditText mUsername;

    @BindView(R.id.editText2)
    EditText mPassword;

    String mUsernameStr, mPasswordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginBtn)
    public void login(Button button) {
        if (mUsername.getText().toString().equals("") || mPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Please Enter Proper Username or Password", Toast.LENGTH_SHORT).show();
        } else {
            mUsernameStr = mUsername.getText().toString();
            mPasswordStr = mPassword.getText().toString();
            if (mUsername.equals(mPasswordStr)){
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Please Enter Proper Username or Password", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
