package me.sunmisc.transflow.inputs;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;

public final class Aes128TransportStream implements Input {
    private final Input source, keyPub;

    public Aes128TransportStream(Input source, Input keyPub) {
        this.source = source;
        this.keyPub = keyPub;
    }

    @Override
    public InputStream stream() throws Exception {
        try (InputStream o = keyPub.stream()) {
            Key keySpec = new SecretKeySpec(o.readAllBytes(),
                    "AES");
            byte[] iv = new byte[16];
            IvParameterSpec ivps = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(
                    "AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
            return new CipherInputStream(source.stream(), cipher);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
