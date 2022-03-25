package com.Filmkritik.authservice.cache;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.Filmkritik.authservice.utilities.JwtTokenUtil;

import net.jodah.expiringmap.ExpiringMap;

@Component
public class InvalidatedJwtTokenCache {
	private static final Logger logger = Logger.getLogger(InvalidatedJwtTokenCache.class);

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private final ExpiringMap<String, String> tokenEventMap;

	@Autowired
	public InvalidatedJwtTokenCache(@Value("${app.cache.logoutToken.maxSize}") int maxSize) {
		this.tokenEventMap = ExpiringMap.builder().variableExpiration().maxSize(maxSize).build();
	}

	public void markInvalidateToken(String token) {
		String username = jwtTokenUtil.getUsernameFromToken(token);
		if (tokenEventMap.containsKey(token)) {
			logger.info(String.format("Token for user [%s] is already present in the cache", username));

		} else {
			Date tokenExpiryDate = jwtTokenUtil.getExpirationDateFromToken(token);
			long ttlForToken = getTTLForToken(tokenExpiryDate);
			logger.info(
					String.format("Token cache set for [%s] with a TTL of [%s] seconds. Token is due expiry at [%s]",
							username, ttlForToken, tokenExpiryDate));
			tokenEventMap.put(token, username, ttlForToken, TimeUnit.SECONDS);
		}
	}

	public String getUsernameForInvalidatedToken(String token) {
		return tokenEventMap.get(token);
	}

	public boolean isInvalidatedToken(String token) {
		return tokenEventMap.containsKey(token);
	}

	private long getTTLForToken(Date date) {
		long secondAtExpiry = date.toInstant().getEpochSecond();
		long secondAtLogout = Instant.now().getEpochSecond();
		return Math.max(0, secondAtExpiry - secondAtLogout);
	}
}
