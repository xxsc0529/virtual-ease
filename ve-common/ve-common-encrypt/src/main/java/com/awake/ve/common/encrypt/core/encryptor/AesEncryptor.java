package com.awake.ve.common.encrypt.core.encryptor;

import com.awake.ve.common.encrypt.core.EncryptContext;
import com.awake.ve.common.encrypt.enums.AlgorithmType;
import com.awake.ve.common.encrypt.enums.EncodeType;
import com.awake.ve.common.encrypt.utils.EncryptUtils;

/**
 * AES算法实现
 *
 * @author 老马
 * @version 4.6.0
 */
public class AesEncryptor extends AbstractEncryptor {

    private final EncryptContext context;

    public AesEncryptor(EncryptContext context) {
        super(context);
        this.context = context;
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.AES;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (encodeType == EncodeType.HEX) {
            return EncryptUtils.encryptByAesHex(value, context.getPassword());
        } else {
            return EncryptUtils.encryptByAes(value, context.getPassword());
        }
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptByAes(value, context.getPassword());
    }
}
