package com.zkcm.hydrobiologicasinica.common.utils;

import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.jce.provider.JCEBlockCipher;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @description:
 * @author: Lidl
 * @create: 2020-06-09 15:52
 **/
public class Z3DESCipher extends JCEBlockCipher {

    public Z3DESCipher() {
        super(new DESedeEngine());
    }

    public void init(int mode, Key key) {
        try {
            this.engineInit(mode, key, new SecureRandom());
        } catch (InvalidKeyException var4) {
            var4.printStackTrace();
        }

    }

    public byte[] doFinal(byte[] str) throws Exception {
        return this.engineDoFinal(str, 0, str.length);
    }
}
