package com.hiplataform.bot;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BotIniciarPermissoesActivity extends AppCompatActivity {

    private Activity oActivity;

    private Button btnIniciarAtendimento;

    private int CAMERA_PERMISSION_CODE = 1;
    private int INTERNET_PERMISSION_CODE = 1;
    private int WRITE_STORAGE_PERMISSION_CODE = 1;
    private int READ_STORAGE_PERMISSION_CODE = 1;
    private int ACCESS_NETWORK_PERMISSION_CODE = 1;
    private int RECORD_AUDIO_PERMISSION_CODE = 1;
    private int AUDIO_SETTINGS_PERMISSION_CODE = 1;

    private String camPermission = Manifest.permission.CAMERA;
    private String wePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String rePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String audioPerm = Manifest.permission.RECORD_AUDIO;
    private String audioStPerm = Manifest.permission.MODIFY_AUDIO_SETTINGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_iniciar_permissoes);

        oActivity = this;

        configurarBarraSuperior();

        VerificarPermissoes();

        btnIniciarAtendimento = (Button) findViewById(R.id.btnIniciarAtendimento);

        btnIniciarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VerificarPermissoes();
            }
        });

    }

    private void abrirWebView() {
        Intent oIntent = new Intent(oActivity, BotWebActivity.class);
        startActivity(oIntent);
        this.finish();
    }

    private void VerificarPermissoes() {
        if(Build.VERSION.SDK_INT >= 23 &&(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) !=PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED))
        {
            String[] permissionList = {audioPerm, audioStPerm, camPermission, wePermission, rePermission};
            ActivityCompat.requestPermissions(this, permissionList, 1);
        }
        else{
            abrirWebView();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirWebView();
            } else {
                Toast.makeText(oActivity, "Permissões negadas!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void configurarBarraSuperior()
    {
        ActionBar mActionBar = getSupportActionBar();

        if(mActionBar != null) {
            mActionBar.setIcon(null);

            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.view_navigation, null);

            TextView lblTitulo = (TextView) mCustomView.findViewById(R.id.lblTitulo);
            lblTitulo.setText(this.getTitle());

            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            mActionBar.setCustomView(mCustomView, params);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    public void voltar(View view)
    {
        oActivity.finish();
    }
}
