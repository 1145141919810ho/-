package com.edu_management.JWT;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JWTUtil {
	private static final long EXPIRE_TIME=120*60*1000;//两小时过期
	private static final String TOKENKEY_STRING="cjh990517";//加密密钥
	
	public static String sign(String id) {
		try {
			Date expDate=new Date(System.currentTimeMillis()+EXPIRE_TIME);//设置过期时间
			Algorithm algorithm=Algorithm.HMAC256(TOKENKEY_STRING) ;//私钥和加密算法
			//设置header
			Map<String, Object> header=new HashMap<String, Object>();
			header.put("Type","jwt");
			header.put("alg", "HS256");
			
			return JWT.create()
					.withHeader(header)
					.withClaim("id", id)
					.withExpiresAt(expDate)
					.sign(algorithm);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static String verify(String token) {
		try {
			Algorithm algorithm =Algorithm.HMAC256(TOKENKEY_STRING);
			JWTVerifier verifier =JWT.require(algorithm).build();
			DecodedJWT jwt=verifier.verify(token);
			String id=jwt.getClaim("id").asString();
			return id;
		} catch (Exception e) {
			return null;
		}
	}
	
	
}
