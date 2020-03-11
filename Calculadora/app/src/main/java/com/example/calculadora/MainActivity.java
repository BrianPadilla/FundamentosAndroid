package com.example.calculadora;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.contentMain)
    RelativeLayout contentMain;

    private boolean isEditinProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configEditText();
    }

    private void configEditText() {
        /*etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });*/

        etInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= etInput.getRight()
                            - etInput.getCompoundDrawables()
                            [Constantes.DRAWABLE_RIGHT].getBounds().width()){
                        if(etInput.length() > 0 ){
                            final int lenght = etInput.getText().length();
                            etInput.getText().delete(lenght -1, lenght);
                        }
                    }

                    return true;
                }
                return false;
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!isEditinProgress && Metodos.canReplaceOperator(s)){
                        isEditinProgress = true;
                        etInput.getText().
                                delete(etInput.getText().length()
                                        - 2,etInput.getText().length() - 1);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
                    isEditinProgress = false;
            }
        });
    }



    @OnClick({R.id.btnSeven, R.id.btnFour, R.id.btnOne, R.id.btnPoint, R.id.btnEight,
            R.id.btnFive, R.id.btnTwo, R.id.btnZero, R.id.btnNine, R.id.btnSix, R.id.btnThree})
    public void onClickedNumber(View view) {
        final String valrStr = ((Button) view).getText().toString();

        switch (view.getId()) {
            case R.id.btnZero:
            case R.id.btnOne:
            case R.id.btnTwo:
            case R.id.btnThree:
            case R.id.btnFour:
            case R.id.btnFive:
            case R.id.btnSix:
            case R.id.btnSeven:
            case R.id.btnEight:
            case R.id.btnNine:
                etInput.getText().append(valrStr);
                break;
            case R.id.btnPoint:
                final String operacion = etInput.getText().toString();

                final String operador = Metodos.getOperator(operacion);

                final int count = operacion.length() - operacion.replace(".", "").length();

                if (!operacion.contains(Constantes.POINT) ||
                        (count < 2 && !operador.equals(Constantes.OPERATOR_NULL))) {
                    etInput.getText().append(valrStr);
                }
                break;


        }
    }


    @OnClick({R.id.btnClear, R.id.btnDiv, R.id.btnMultiplication, R.id.btnSub, R.id.btnSum, R.id.btnResult})
    public void onClickedControls(View view) {
        switch (view.getId()) {
            case R.id.btnClear:
                etInput.setText("");
                break;
            case R.id.btnDiv:
            case R.id.btnMultiplication:
            case R.id.btnSub:
            case R.id.btnSum:
                resolve(false);

                final String operador = ((Button)view).getText().toString();
                final String operacion = etInput.getText().toString();
                final String ultimoCaracter = operacion.isEmpty() ? "" :
                        operacion.substring(operacion.length() - 1);

                if(operador.equals(Constantes.OPERATOR_SUB)){
                    if(operacion.isEmpty() ||
                            (!(ultimoCaracter
                                    .equals(Constantes.OPERATOR_SUB)) &&
                                    (!(ultimoCaracter).equals(Constantes.POINT)))){
                            etInput.getText().append(operador);
                    }
                } else{
                    if(!operacion.isEmpty() && !(ultimoCaracter.equals(Constantes.OPERATOR_SUB))
                        && !(ultimoCaracter.equals(Constantes.POINT))){
                        etInput.getText().append(operador);
                    }
                }
            case R.id.btnResult:
                resolve(true);
                break;
        }
    }

    private void resolve(boolean fromResult) {
        Metodos.tryResolve(fromResult, etInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                isEditinProgress = true;
            }
        });
    }

    private void showMessage(int errorRes) {
        Snackbar.make(contentMain,errorRes,Snackbar.LENGTH_SHORT);

    }
}
