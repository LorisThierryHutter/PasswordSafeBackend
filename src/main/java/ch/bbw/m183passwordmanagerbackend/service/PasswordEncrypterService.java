package ch.bbw.m183passwordmanagerbackend.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordEncrypterService implements DefaultPasswordEncrypterService {

    public String encryptPassword(String password, String userKey) {
        try {
            // Generate a random salt
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[16]; // Salt length in bytes
            secureRandom.nextBytes(salt);

            // Convert the salt to a Base64-encoded string for storage
            String saltString = Base64.getEncoder().encodeToString(salt);

            // Derive a secret key from the user's master password and salt
            SecretKeySpec secretKey = deriveSecretKey(userKey, salt);

            // Create AES cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize the cipher in encryption mode with the secret key
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt the password
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes as a Base64 string
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // Handle encryption errors
            e.printStackTrace();
            // Return an empty string or handle the error case as appropriate
            return "";
        }
    }

    public SecretKeySpec deriveSecretKey(String userKey, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Convert the user's master password to a char array
        char[] passwordChars = userKey.toCharArray();

        // Derive a secret key using PBKDF2 with the provided salt and iteration count
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec keySpec = new PBEKeySpec(passwordChars, salt, 65536, 256);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        // Convert the secret key to AES key specification
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
}
