package com.bharath.springboot;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

@RestController
public class HelloController {

	@Value("${auth.signature}")
	private String cosmosKey;

	private static String hmacWithApacheCommons(String algorithm, String data, String key) {
		String hmac = new HmacUtils(algorithm, key).hmacHex(data);
		return hmac;
	}
	
	@GetMapping("/cosmosAuth")
	public String getCosmosAuthorization(@RequestBody String payload) throws UnsupportedEncodingException {

		byte[] decodedBytes = Base64.getDecoder().decode(cosmosKey);
		String decodedKey = new String(decodedBytes);

		String signature = hmacWithApacheCommons("HmacSHA256", payload, decodedKey);
		System.out.println(signature);

		String encodedSignature = Base64.getEncoder().encodeToString(signature.getBytes());
		System.out.println(encodedSignature);

		String token = "type=master&ver=1.0&sig=" + encodedSignature;
		System.out.println(token);

		String auth = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
		System.out.println(auth);

		return decodedKey;
	}

}
