package com.example.j940549.cassaforte_md;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.FingerprintDialog.BiometricCallback;
import com.example.j940549.cassaforte_md.FingerprintDialog.BiometricCallbackV28;
import com.example.j940549.cassaforte_md.FingerprintDialog.BiometricManager;
import com.example.j940549.cassaforte_md.FingerprintDialog.FingerprintAuthenticationDialogFragment;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    public static final String DEFAULT_KEY_NAME = "default_key";
    public static final String KEYNAME = "cassaforte1";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private  KeyguardManager keyguardManager = null;
    private  FingerprintManager fingerprintManager=null;
    private SharedPreferences mSharedPreferences;
    private EditText editPassword;
    private EditText editnomeUser;
    private ImageButton fp_button;
    private CheckBox chkboxRicordami;
    private boolean siFingerprint;
    private String token, user;
    private String SECURITYKEY;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editnomeUser = (EditText) findViewById(R.id.user);
        editPassword = (EditText) findViewById(R.id.password);
        chkboxRicordami = (CheckBox) findViewById(R.id.chkboxRicordami);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        leggiPreferenze();

        chkboxRicordami.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    salvaUtenteinPreferenze();
                }
                if (!isChecked) {
                    salvaUtenteinPreferenze();
                }
            }
        });

    creaNewKeytoKeyStore();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerprintManager = getSystemService(FingerprintManager.class);

            if (fingerprintManager.isHardwareDetected()) {
                fp_button = (ImageButton) findViewById(R.id.purchase_button);
                fp_button.setVisibility(View.VISIBLE);
                try {
                    mKeyStore = KeyStore.getInstance("AndroidKeyStore");
                } catch (KeyStoreException e) {
                    throw new RuntimeException("Failed to get an instance of KeyStore", e);
                }
                try {
                    mKeyGenerator = KeyGenerator
                            .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
                }
                Cipher defaultCipher;

                try {
                    defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    throw new RuntimeException("Failed to get an instance of Cipher", e);
                }


                // createKey(DEFAULT_KEY_NAME, true);

                fp_button.setEnabled(true);
            fp_button.setOnClickListener(
                        new PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
            }
            if(siFingerprint){
                Log.i("fingerprint",""+siFingerprint);

                fp_button.performClick();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean controllaSetUpFingerprint(){
        boolean setOn=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = getBaseContext().getSystemService(KeyguardManager.class);
        }
        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(this,
               /*     "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",*/
                    "il Blocco dello schermo non è settato.\n"
                            + "vai a 'Settings -> Security -> Fingerprint' ed impostalo a fingerprint",
                    Toast.LENGTH_LONG).show();
            fp_button.setEnabled(false);

             setOn=false;
        }else {

            // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
            // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
            // The line below prevents the false positive inspection from Android Studio
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // noinspection ResourceType
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fp_button.setEnabled(false);
                    // This happens when no fingerprints are registered.
                    Toast.makeText(this,
//                        "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                            "Non hai registrato nessun fingerprint\n" +
                                    "vai a  'Settings -> Security -> Fingerprint' e registerane almeno uno",
                            Toast.LENGTH_LONG).show();
                    setOn = false;
                } else {
                    setOn = true;
                }
            }
        }
        return setOn;
    }
    private void leggiPreferenze(){
        //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);

        String utente= mSharedPreferences.getString("nomeUtente", "");
        siFingerprint=mSharedPreferences.getBoolean("chkFingerprint", false);
        Log.i("si fingerprint login",""+siFingerprint);

        boolean check=mSharedPreferences.getBoolean("chkbox", false);

        if(check||!utente.equals("")){
            editnomeUser.setText(utente);
            chkboxRicordami.setChecked(check);
            editPassword.requestFocus();
        }
        if(!check){
            editnomeUser.setText("");
            chkboxRicordami.setChecked(check);
        }

    }
    private void salvaUtenteinPreferenze() {

        editnomeUser = (EditText) findViewById(R.id.user);
        String user = editnomeUser.getText().toString();
        Boolean cheked=false;
        if(chkboxRicordami.isChecked()){
            cheked=true;
        }else{
            cheked=false;
        }
        if(!user.equals("")&& cheked) {
            //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);
            String utente= mSharedPreferences.getString("nomeUtente", "");
            if(utente.equals("")) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("nomeUtente", user);
                editor.putBoolean("chkbox",cheked);
                editor.commit();
            }
        }else if (user.equals("")&& cheked) {
            Toast.makeText(this, "inserisci prima il nome Utente", Toast.LENGTH_SHORT).show();
            chkboxRicordami.setChecked(false);
        }else if (!user.equals("")&& !cheked){
            //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("nomeUtente", "");
            editor.putBoolean("chkbox",cheked);
            editor.commit();
        }else if(user.equals("")&& !cheked){
            //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("nomeUtente", "");
            editor.putBoolean("chkbox",cheked);
            editor.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void creaNewKeytoKeyStore() {
        //String alias = aliasText.getText().toString();

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            Calendar start= Calendar.getInstance();
            Calendar end=Calendar.getInstance();
            end.add(Calendar.YEAR,3);
            Log.d("login no nuova key ",""+mKeyStore.containsAlias(KEYNAME));
            // Create new key if needed
            if (!mKeyStore.containsAlias(KEYNAME)) {
                Log.d("login si nuova key ",""+!mKeyStore.containsAlias(KEYNAME));
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this)
                        .setAlias(KEYNAME)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }else{
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(KEYNAME, null);
                Log.d("login key privata log",privateKeyEntry.toString());
                //prendiamo la chiave pubblica
                RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
                Log.d("login key privata log",publicKey.toString());

            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

  //  @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) throws KeyPermanentlyInvalidatedException {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * Proceed the purchase operation
     *
     * @param withFingerprint {@code true} if the purchase was made by using a fingerprint
     * @param cryptoObject the Crypto object
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
           // showConfirmation(null);
        }
    }



    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt(Cipher cipher) {
        try {
            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
           // showConfirmation(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private class PurchaseButtonClickListener implements View.OnClickListener {

        Cipher mCipher;
        String mKeyName;


        PurchaseButtonClickListener(Cipher cipher, String keyName) {
            mCipher = cipher;
            mKeyName = keyName;
        }

        //  @RequiresApi(api = Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View view) {
            Toast.makeText(LoginActivity.this, "devo ancora correggere qlc errore per android 9", Toast.LENGTH_SHORT).show();

            user = editnomeUser.getText().toString();
            if (!user.equals("")) {
                if (controllaSetUpFingerprint()) {

                    Log.d("getpw onclick", user);
                    // Set up the crypto object for later. The object will be authenticated by use
                    // of the fingerprint.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            if (initCipher(mCipher, mKeyName)) {

                                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                                // crypto, or you can fall back to using a server-side verified password.
                                FingerprintAuthenticationDialogFragment fragment
                                        = new FingerprintAuthenticationDialogFragment();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                                }
                                boolean useFingerprintPreference = mSharedPreferences
                                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                                true);
                                if (useFingerprintPreference) {
                                    fragment.setStage(
                                            FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
                                } else {
                                    fragment.setStage(
                                            FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                                }
                                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                            } else {
                                // This happens if the lock screen has been disabled or or a fingerprint got
                                // enrolled. Thus show the dialog to authenticate with their password first
                                // and ask the user if they want to authenticate with fingerprints in the
                                // future
                                FingerprintAuthenticationDialogFragment fragment
                                        = new FingerprintAuthenticationDialogFragment();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                                }
                                fragment.setStage(
                                        FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                            }
                        } catch (KeyPermanentlyInvalidatedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Toast.makeText(LoginActivity.this, "ho bisogno del nome utente. \nSpunta \"ricordami\"", Toast.LENGTH_SHORT).show();
            }

        }
    }









    public void login(View v) {



        editnomeUser = (EditText) findViewById(R.id.user);
        editPassword = (EditText) findViewById(R.id.password);

        String user = editnomeUser.getText().toString();
        String password = editPassword.getText().toString();
        token="";
        if(!user.equals("")&&!password.equals("")) {
            SECURITYKEY=creaSecurKey(user,password);

            String PWcontrollo = "";
            String TKcontrollo="";
            String TKorig="";
            String isffpp="";
            DBLayer dbLayer = null;

            try {
                dbLayer = new DBLayer(this);
                dbLayer.open();
                Cursor cursor = dbLayer.getPassword(user);

                if(cursor.getCount()>0) {
                    cursor.moveToPosition(0);
                    Log.d("login curor", ""+cursor.getCount() + "-" + token);

                    do{
                        PWcontrollo = cursor.getString(0);
                        token=(cursor.getString(1));
                        Log.d("login encript", PWcontrollo + "-" + token);

                    }  while (cursor.moveToNext());

                    Log.d("login encript", PWcontrollo + "-" + token);

                    Crypto crypto=new Crypto(SECURITYKEY.getBytes());

                    PWcontrollo=crypto.decrypt(PWcontrollo.getBytes());
                    Log.d("login decript", PWcontrollo + "-" + token);
                    TKcontrollo=dbLayer.creaToken(user,PWcontrollo);

                    if(token.length()>0) {

                        TKorig = token.substring(0, token.indexOf("-"));
                        isffpp=token.substring((token.indexOf("-")+1),token.length());
                    }
                    Log.d("login decrypto",PWcontrollo+"-"+token+"-??"+isffpp);
                    if (PWcontrollo.equals(password)&&TKorig.equalsIgnoreCase(TKcontrollo)) {

                        /*Intent vaiaMenu = new Intent(this, MenuActivity.class);
                        vaiaMenu.putExtra("user", token);
                        vaiaMenu.putExtra("isffpp", isffpp);
                        startActivity(vaiaMenu);
*/
                        archiviaSecurityKey(SECURITYKEY);
                        Intent vaiaSplash = new Intent(this, SplashActivity2.class);
                        vaiaSplash .putExtra("user", token);
                        //vaiaSplash .putExtra("securityKey", SECURITYKEY);
                        vaiaSplash .putExtra("isffpp", isffpp);
                        startActivity(vaiaSplash );

                        finish();
                    } else {
                        Toast.makeText(this, "utente o password errati", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "utente non registrato", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException ex) {
                Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "inserisci i dati per il login", Toast.LENGTH_SHORT).show();
        }
    }

    public void archiviaSecurityKey(String SECURITYKEY) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String key = sharedPref.getString("securityKey", SECURITYKEY);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("securityKey", key);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate( R.menu.menu_registrati,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){
        boolean esito=false;
        switch(menuItem.getItemId()){
            case R.id.optionRegistrati:{

                Intent vaiaRegistrati = new Intent(this, RegistratiActivity.class);

                startActivity(vaiaRegistrati);
                finish();
                esito= true;
                break;
            }

        }
        return esito;
    }

    public String creaSecurKey(String user,String password){
        String securKey;
        Log.d("crea key",user);
        Log.d("crea key",password);

        StringBuilder token=new StringBuilder();
        int x=user.length()-1;
        int y=password.length()-1;

        if(x<y){
            for(int i=0; i<=y;i++){
                if(i<=x){
                    token.append(user.charAt(i));}else{
                    token.append(user.charAt(x));
                }
                token.append(password.charAt(i));

            }

        }else{
            for(int i=0; i<=x;i++){

                token.append(user.charAt(i));
                if(i<=y){
                    token.append(password.charAt(i));}else{

                    token.append(password.charAt(y));
                }

            }

        }

        if(token.length()<=16) {
            while (token.length() < 16) {
                token.append("x");
            }
            securKey=token.toString();
        }else{

            securKey=token.substring(0,16);

        }
        Log.d("key",securKey);

        return securKey;
    }
}
