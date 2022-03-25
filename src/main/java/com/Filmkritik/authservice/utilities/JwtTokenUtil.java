package com.Filmkritik.authservice.utilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.Filmkritik.authservice.cache.InvalidatedJwtTokenCache;
import com.Filmkritik.authservice.dto.JwtTokenRequest;
import com.Filmkritik.authservice.dto.JwtTokenResponse;
import com.Filmkritik.authservice.exception.InvalidTokenRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

@Component
public class JwtTokenUtil implements Serializable {

	private static final Logger logger = Logger.getLogger(JwtTokenUtil.class);
	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private static final long serialVersionUID = -3301605591108950415L;
	private Clock clock = DefaultClock.INSTANCE;

	@Value("${jwt.signing.key.secret}")
	private String SIGNING_KEY;

	@Value("${jwt.authorities.key.secret}")
	private String AUTHORITIES_KEY;

	@Value("${jwt.token.expiration.in.seconds}")
	private Long expiration;

	@Autowired
	private InvalidatedJwtTokenCache invalidatedJwtTokenCache;

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		Boolean isExpired= expiration.before(clock.now());
		if (isExpired) {
			logger.info("JWT Token Expired.");
			throw new ExpiredJwtException(null, null, token);
		}
		return isExpired;
	}

	private Boolean isTokenInvalidated(String token) {
		if (invalidatedJwtTokenCache.isInvalidatedToken(token)) {
			String username = invalidatedJwtTokenCache.getUsernameForInvalidatedToken(token);
			String errorMessage="Token is invalidated";
			logger.error(errorMessage);
//			String errorMessage = String.format("Token corresponds to an already logged out user [%s] or is invalidated. Please login again",username);
			throw new InvalidTokenRequestException("JWT", token, errorMessage);
//			return true;
		}
		return false;
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public JwtTokenResponse generateToken(Authentication authentication, String refreshToken) {
		final String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		String token = doGenerateToken(authentication, authorities);
		logger.info("Token Genereted :- "+ token);
		return new JwtTokenResponse(token,refreshToken,authorities,expiration);
	}

	private String doGenerateToken(Authentication authentication, String authorities) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);

		return Jwts.builder().claim(AUTHORITIES_KEY, authorities).setSubject(authentication.getName())
				.setIssuedAt(createdDate).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
				.compact();
	}

	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public JwtTokenResponse refreshToken(String token, String refreshToken) {
		logger.info("Marking Token - {"+token+"} Invalidated and Generating new JWT Token ");
		invalidatedJwtTokenCache.markInvalidateToken(token);
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);

		final Claims claims = getAllClaimsFromToken(token);
		final String authorities = (String) claims.get(AUTHORITIES_KEY);
		claims.setIssuedAt(createdDate);
		claims.setExpiration(expirationDate);

		return new JwtTokenResponse(
				Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact(),
				authorities,refreshToken, expiration);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
//    JwtUserDetails user = (JwtUserDetails) userDetails;
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenInvalidated(token));
	}

	private Date calculateExpirationDate(Date createdDate) {
		return new Date(createdDate.getTime() + expiration * 1000);
	}

	public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth,
			final UserDetails userDetails) {

		final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);

		final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

		final Claims claims = claimsJws.getBody();

		final Collection<? extends GrantedAuthority> authorities = Arrays
				.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	}

	public static String generateRandomUuid() {
		 return UUID.randomUUID().toString();
	}
}
