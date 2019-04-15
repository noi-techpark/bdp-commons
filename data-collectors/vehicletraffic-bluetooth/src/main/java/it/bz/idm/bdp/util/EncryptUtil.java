package it.bz.idm.bdp.util;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptUtil {

	@Value("${encryption.key}")
	private String key;

	private HmacUtils utils;

	@PostConstruct
	public void init() {
		utils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key);
	}

	public String encrypt(String mac) {
		return utils.hmacHex(mac);
	}
}
