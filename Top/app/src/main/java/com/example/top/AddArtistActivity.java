package com.example.top;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddArtistActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.imagefoto)
    AppCompatImageView imagefoto;
    @BindView(R.id.etNombre)
    TextInputEditText etNombre;
    @BindView(R.id.etApellidos)
    TextInputEditText etApellidos;
    @BindView(R.id.etFechaNacimiento)
    TextInputEditText etFechaNacimiento;
    @BindView(R.id.etEstatura)
    TextInputEditText etEstatura;
    @BindView(R.id.etLugarNacimiento)
    TextInputEditText etLugarNacimiento;
    @BindView(R.id.etNotas)
    TextInputEditText etNotas;

    private Artista mArtista;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
        ButterKnife.bind(this);

        configActionBar();
        configArtista(getIntent());
        configCalendar();

        Intent intent = getIntent();
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configArtista(Intent intent) {
        mArtista = new Artista();
        mArtista.setFechaNacimiento(System.currentTimeMillis());
        mArtista.setOrden(intent.getIntExtra(Artista.ORDEN, 0));
    }

    private void configCalendar() {
        mCalendar = Calendar.getInstance(Locale.ROOT);
        etFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
                .format(System.currentTimeMillis()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_save:
                saveArtist();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveArtist() {
        /*mArtista.setNombre(etNombre.getText().toString().trim());
        mArtista.setApellidos(etApellidos.getText().toString().trim());
        mArtista.setEstatura(Short.valueOf(etEstatura.getText().toString().trim()));
        mArtista.setLugarNacimiento(etLugarNacimiento.getText().toString().trim());
        mArtista.setNotas(etNotas.getText().toString().trim());*/

        MainActivity.sArtista.setNombre(etNombre.getText().toString().trim());
        MainActivity.sArtista.setApellidos(etApellidos.getText().toString().trim());
        MainActivity.sArtista.setEstatura(Short.valueOf(etEstatura.getText().toString().trim()));
        MainActivity.sArtista.setLugarNacimiento(etLugarNacimiento.getText().toString().trim());
        MainActivity.sArtista.setNotas(etNotas.getText().toString().trim());
        MainActivity.sArtista.setOrden(mArtista.getOrden());
        MainActivity.sArtista.setFotoUrl(mArtista.getFotoUrl());

        setResult(RESULT_OK);
        finish();

    }

    @OnClick(R.id.etFechaNacimiento)
    public void onSetFecha() {
        DialogSelectorFecha dialogSelectorFecha = new DialogSelectorFecha();
        dialogSelectorFecha.setListener(AddArtistActivity.this);
        Bundle args = new Bundle();
        args.putLong(DialogSelectorFecha.sFECHA, mArtista.getFechaNacimiento());
        dialogSelectorFecha.setArguments(args);
        dialogSelectorFecha.show(getSupportFragmentManager(), DialogSelectorFecha.sSELECTED_DATE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        etFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
                .format(mCalendar.getTimeInMillis()));

        mArtista.setFechaNacimiento(mCalendar.getTimeInMillis());
    }

    @OnClick({R.id.imagedeletefoto, R.id.imgFromGallery, R.id.imgFromURL})
    public void imageEvents(View view) {
        switch (view.getId()) {
            case R.id.imagedeletefoto:
                break;
            case R.id.imgFromGallery:
                break;
            case R.id.imgFromURL:
                showAddPhotoDialog();
                break;
        }
    }

    private void showAddPhotoDialog() {
        final EditText etFotoUrl = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.addArtist_dialogUrl_tittle)
                .setPositiveButton(R.string.label_dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        configImageView(etFotoUrl.getText().toString().trim());
                    }
                })
                .setNegativeButton(R.string.label_dialog_cancel,null);

                builder.setView(etFotoUrl);
                builder.show();
    }

    private void configImageView(String fotoUrl) {
            if (fotoUrl != null){
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop();

                Glide.with(this)
                        .load(fotoUrl)
                        .apply(options)
                        .into(imagefoto);
            }
            else{
                imagefoto.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_image));
            }

            mArtista.setFotoUrl(fotoUrl);
    }
}
