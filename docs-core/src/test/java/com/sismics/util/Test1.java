package com.sismics.util;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.sismics.util.totp.GoogleAuthenticator;
import com.sismics.util.totp.GoogleAuthenticatorKey;

/**
 * Test of {@link GoogleAuthenticator}
 */
public class Test1 {
    @Test
    public void testGoogleAuthenticator() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        Assert.assertNotNull(key.getVerificationCode());
        Assert.assertEquals(5, key.getScratchCodes().size());

        int validationCode = gAuth.calculateCode(key.getKey(), new Date().getTime() / 30000);
        Assert.assertTrue(gAuth.authorize(key.getKey(), validationCode));
        int wrongCode = (validationCode + 1) % 1000000; 
        Assert.assertFalse("Wrong code should not pass", gAuth.authorize(key.getKey(), wrongCode));
    }
}
