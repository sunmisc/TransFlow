package me.sunmisc.transflow.inputs;

import me.sunmisc.transflow.io.SimpleBytesInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Optional;

public final class Aes128TransportStream implements Input {
    private final Input source, keyPub;

    public Aes128TransportStream(Input source, Input keyPub) {
        this.source = source;
        this.keyPub = keyPub;
    }

    @Override
    public Optional<byte[]> stream() throws Exception {
        return source.stream().flatMap(src -> {
            try {
                return keyPub.stream().map(key -> {
                    try {
                        Key keySpec = new SecretKeySpec(key, "AES");
                        byte[] iv = new byte[16];
                        IvParameterSpec ivps = new IvParameterSpec(iv);

                        Cipher cipher = Cipher.getInstance(
                                "AES/CBC/PKCS5Padding");
                        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
                        try (CipherInputStream cip = new CipherInputStream(
                                new SimpleBytesInputStream(src), cipher)) {
                            return cip.readAllBytes();
                        }
                    } catch (GeneralSecurityException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
