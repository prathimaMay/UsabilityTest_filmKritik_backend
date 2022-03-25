package com.Filmkritik.authservice.service;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.hibernate.query.criteria.internal.predicate.IsEmptyPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.Filmkritik.authservice.controller.AuthenticationController;
import com.Filmkritik.authservice.dto.JwtTokenResponse;
import com.Filmkritik.authservice.dto.UserDto;
import com.Filmkritik.authservice.entities.RefreshTokenEntity;
import com.Filmkritik.authservice.entities.UserEntity;
import com.Filmkritik.authservice.exception.TokenRefreshException;
import com.Filmkritik.authservice.utilities.JwtTokenUtil;

@Service
public class AuthService {

	private static final Logger logger = Logger.getLogger(AuthService.class);
	
	@Autowired
	private JwtUserDetailsService jstUserDetailsService;

	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	/**
	 * Creates and persists the refresh token for the user device. If device exists
	 * already, we don't care. Unused devices with expired tokens should be cleaned
	 * with a cron job. The generated token would be encapsulated within the jwt.
	 * Remove the existing refresh token as the old one should not remain valid.
	 */
	public Optional<RefreshTokenEntity> createAndPersistRefreshTokenForUser(Authentication authentication) {
		UserEntity currentUser = new UserEntity();
		currentUser.setId(jwtUserDetailsService.getUserIdbyUsername(authentication.getName())); 
		if (refreshTokenService.isUserIdPresent(currentUser)) {
			logger.info("UserID - {"+currentUser.getId()+"} is already present in refresh table, Deleting this RERESH_TOKEN ");
			refreshTokenService.deleteById(currentUser);
		}
		RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken();
		refreshToken.setUserEntity(currentUser);
		refreshToken = refreshTokenService.save(refreshToken);
		logger.info("Created New Refresh Token - {"+refreshToken.getToken()+"}  for UserId - {"+refreshToken.getUserEntity().getId()+"}");
		return Optional.ofNullable(refreshToken);

	}

	public JwtTokenResponse generateToken(Authentication authentication, String refreshToken) {
		// TODO Auto-generated method stub
		logger.info("Generating JWT Token for User - "+authentication.getName());
		return jwtTokenUtil.generateToken(authentication,refreshToken);
	}

	public Optional<RefreshTokenEntity> validateRefreshToken(String refreshTokenReq) {	
		logger.info("Validating Refresh Token -"+refreshTokenReq);
		 return Optional.of(refreshTokenService.findByToken(refreshTokenReq)
	                .map(refreshToken -> {
	                    refreshTokenService.verifyExpiration(refreshToken);
	                    refreshTokenService.increaseCount(refreshToken);
	                    return refreshToken; 
	                })
	                .orElseThrow(() -> new TokenRefreshException(refreshTokenReq, "Missing refresh token in database.Please login again")));
	    }

	public JwtTokenResponse refreshJwtToken(String token, String refreshToken) {
		// TODO Auto-generated method stub
		logger.info("Request to refresh JWT Token");
		return jwtTokenUtil.refreshToken(token,refreshToken);
	}


}
