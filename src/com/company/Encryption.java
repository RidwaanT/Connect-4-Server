package com.company;

import java.security.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;


public class Encryption {
    private SecretKey key;
    private int KEY_SIZE = 128;
    private IvParameterSpec ivParameterSpec;

    /**
     * Initialization will create a key and an IV to be used for the encryption
     * @throws NoSuchAlgorithmException
     */
    public Encryption() throws NoSuchAlgorithmException {
        generateKey();
        generateIv();
    }

    /**
     * We will generate a key to be used which is the private key between two parties. Saving it to the class
     * @return Key used for encryption/decryption
     * @throws NoSuchAlgorithmException
     */
    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        key = keyGenerator.generateKey();
        return key;
    }

    /**
     * The IV is somewhat like a nonce, we use the IV for encrypting our string, instead of a common string. It gives us
     * Pseudorandom text.
     * @return
     */
    private IvParameterSpec generateIv(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    /**
     *
     * @return IV of enncryption class
     */
    public IvParameterSpec getIv(){
        return ivParameterSpec;
    }

    /**
     *
     * @return Secrey Key of Encryption class
     */
    public SecretKey getKey(){
        return key;
    }

    /**
     * Sets the IV of the instance to the parameter
     * @param IV this is the Iv used for decryption/encryption
     */
    public void setIv(IvParameterSpec IV){
         ivParameterSpec = IV;
    }

    /**
     * Sets the SecretKEy for the current instance
     * @param newKey Secret Key to be used.
     */
    public void setKey(SecretKey newKey){
        key = newKey;
    }
    /**
     *
     * @param input string input to be encrypted
     * @return encrypted string of input.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public String encrypt(String input) throws NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    /**
     * Decrypts a cipher that follows our protocols.
     * @param cipherText
     * @return plain text of the decrypted cipher
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decrypt( String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String (plainText);
    }

}
