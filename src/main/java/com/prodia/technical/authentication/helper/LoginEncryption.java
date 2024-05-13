package com.prodia.technical.authentication.helper;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class LoginEncryption {
  private static final String ALGO = "AES"; // Default uses ECB PKCS5Padding
  private static final String SECRET = encodeKey("mustbe16byteskey");

  public static String encrypt(String Data) throws Exception {
    Key key = generateKey();
    Cipher c = Cipher.getInstance(ALGO);
    c.init(Cipher.ENCRYPT_MODE, key);
    byte[] encVal = c.doFinal(Data.getBytes());
    String encryptedValue = Base64.getEncoder().encodeToString(encVal);
    return encryptedValue;
  }

  public static String decrypt(String strToDecrypt) {
    try {
      Key key = generateKey();
      Cipher cipher = Cipher.getInstance(ALGO);
      cipher.init(Cipher.DECRYPT_MODE, key);
      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    } catch (Exception e) {
      System.out.println("Error while decrypting: " + e.toString());
    }
    return null;
  }

  private static Key generateKey() {
    byte[] decoded = Base64.getDecoder().decode(SECRET.getBytes());
    return new SecretKeySpec(decoded, ALGO);
  }

  public static String decodeKey(String str) {
    byte[] decoded = Base64.getDecoder().decode(str.getBytes());
    return new String(decoded);
  }

  public static String encodeKey(String str) {
    byte[] encoded = Base64.getEncoder().encode(str.getBytes());
    return new String(encoded);
  }
}