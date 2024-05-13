package com.prodia.technical.common.helper;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component("encryption")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptionHelper {

  private static String secretKey = "test@prodia";
  private static String salt = "test@prodia";

  public static String encrypt(String strToEncrypt) {
    byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    IvParameterSpec ivspec = new IvParameterSpec(iv);

    SecretKeyFactory factory;
    try {
      factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    } catch (NoSuchAlgorithmException e) {
      System.out.println("NoSuchAlgorithmException: " + e.toString());
      return null;
    }

    KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
    SecretKey tmp;
    try {
      tmp = factory.generateSecret(spec);
    } catch (InvalidKeySpecException e) {
      System.out.println("InvalidKeySpecException: " + e.toString());
      return null;
    }
    SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

    Cipher cipher;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    } catch (NoSuchAlgorithmException e) {
      System.out.println("NoSuchAlgorithmException: " + e.toString());
      return null;
    } catch (NoSuchPaddingException e) {
      System.out.println("NoSuchPaddingException: " + e.toString());
      return null;
    }

    try {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
    } catch (InvalidKeyException e) {
      System.out.println("InvalidKeyException: " + e.toString());
      return null;
    } catch (InvalidAlgorithmParameterException e) {
      System.out.println("InvalidAlgorithmParameterException: " + e.toString());
      return null;
    }

    try {
      return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    } catch (IllegalBlockSizeException e) {
      System.out.println("IllegalBlockSizeException: " + e.toString());
      return null;
    } catch (BadPaddingException e) {
      System.out.println("BadPaddingException: " + e.toString());
      return null;
    } catch (UnsupportedEncodingException e) {
      System.out.println("UnsupportedEncodingException: " + e.toString());
      return null;
    }
  }


  public static String decrypt(String strToDecrypt) {
    try {
      byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      IvParameterSpec ivspec = new IvParameterSpec(iv);

      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    } catch (Exception e) {
      System.out.println("Error while decrypting: " + e.toString());
    }
    return null;
  }
}
