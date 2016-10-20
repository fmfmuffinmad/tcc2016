package com.tcc.tccpinut.tccpinut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcc.tccpinut.tccpinut.classes.Pinut;
import com.tcc.tccpinut.tccpinut.helpers.AudioHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MostrarDetalhesPinut extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgView;
    private TextView txtVTitle, txtVText, txtVDuration;
    private Button btTocar, btParar, btFechar;

    private AudioHelper aHelper;
    private Pinut pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_detalhes_pinut);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pin = getIntent().getParcelableExtra("pinut");

        imgView = (ImageView)findViewById(R.id.ciarPinutFoto);
        txtVTitle = (TextView)findViewById(R.id.vTxtTitulo);
        txtVText = (TextView)findViewById(R.id.vTxtTexto);
        txtVDuration = (TextView)findViewById(R.id.vTxtDuracaoHoras);

        preencherCampos();

        btTocar = (Button)findViewById(R.id.btparar);
        btParar = (Button)findViewById(R.id.bttocar);
        btFechar = (Button)findViewById(R.id.btFecharPin);

        btTocar.setOnClickListener(this);
        btParar.setOnClickListener(this);
        btFechar.setOnClickListener(this);
    }

    private void preencherCampos() {
        txtVTitle.setText(pin.getTitle());
        txtVText.setText(pin.getText());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date(pin.getExpireOn());
        txtVDuration.setText("Duração: " + sdf.format(d));
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch(item){
            case R.id.bttocar:
                if(aHelper == null){
                    if(pin.getAudiopath() != null){
                        aHelper = new AudioHelper(pin.getAudiopath());
                        aHelper.play();
                    }    else {
                        Toast.makeText(this, "Essa pin não possui áudio!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.btparar:
                if(aHelper != null){
                    aHelper.stop();
                }
                break;
            case R.id.btFecharPin:
                finish();
                break;
        }
    }
}
