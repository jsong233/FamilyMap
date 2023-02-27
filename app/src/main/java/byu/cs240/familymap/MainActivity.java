package byu.cs240.familymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import byu.cs240.familymap.R;
import result.LoginResult;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if(fragment == null) {
            fragment = createLoginFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.fragmentFrameLayout, fragment)
                    .commit();
        } else {
            if(fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
    }

    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void notifyDone() {

        DataCache data = DataCache.getInstance();

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, fragment)
                .commit();
    }
}