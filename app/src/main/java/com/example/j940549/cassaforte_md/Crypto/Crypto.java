package com.example.j940549.cassaforte_md.Crypto;

/**
 * Created by Manuel on 18/06/2017.
 */


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private byte[] key;

    private static final String ALGORITHM = "AES";

    public Crypto (byte[] key) {
        this.key = key;
    }

    /**
     * Encrypts the given plain text
     *
     * @param plainText The plain text to encrypt
     */
    public String encrypt(byte[] plainText) throws Exception {
        String plainText_=Base64.encodeToString(plainText,Base64.DEFAULT);
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encVal = cipher.doFinal(plainText_.getBytes());
        String encrypted=Base64.encodeToString(encVal, Base64.DEFAULT);
        return encrypted;
        //return new String(cipher.doFinal(plainText_));
    }

    /**
     * Decrypts the given byte array
     *
     * @param cipherText The data to decrypt
     */
    public String decrypt(byte[] cipherText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decordedValue = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] decValue = cipher.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        String decoded=new String(Base64.decode(decryptedValue,Base64.DEFAULT));

        return decoded;//new String(cipher.doFinal(cipherText));
    }


}
