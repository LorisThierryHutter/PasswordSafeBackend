package ch.bbw.m183passwordmanagerbackend.service;

import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface DefaultPasswordEncrypterService {

    String encryptPassword(String password, String userKey);

    SecretKeySpec deriveSecretKey(String userKey, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
