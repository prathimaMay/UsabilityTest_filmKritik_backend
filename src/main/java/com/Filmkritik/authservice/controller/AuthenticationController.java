package com.Filmkritik.authservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Filmkritik.authservice.dto.JwtTokenRequest;
import com.Filmkritik.authservice.dto.JwtTokenResponse;
import com.Filmkritik.authservice.dto.TokenRefreshRequest;
import com.Filmkritik.authservice.dto.UserDto;
import com.Filmkritik.authservice.entities.RefreshTokenEntity;
import com.Filmkritik.authservice.entities.SecurityQuestionsEntity;
import com.Filmkritik.authservice.entities.UserEntity;
import com.Filmkritik.authservice.exception.AuthenticationException;
import com.Filmkritik.authservice.exception.TokenRefreshException;
import com.Filmkritik.authservice.repository.SecurityQuestionsRepository;
import com.Filmkritik.authservice.repository.UserRepository;
import com.Filmkritik.authservice.service.AuthService;
import com.Filmkritik.authservice.service.JwtUserDetailsService;
import com.Filmkritik.authservice.utilities.JwtTokenUtil;
import com.Filmkritik.authservice.utilities.JwtUserDetails;

@RestController
@CrossOrigin
public class AuthenticationController {

	private static final Logger logger = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	@Autowired
	private SecurityQuestionsRepository securityQuestionsRepository;

	@Value("${jwt.http.request.header}")
	private String tokenHeader;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserRepository userRepo;

	@PostMapping(value = "/authenticate")
	public ResponseEntity<JwtTokenResponse> createAuthenticationToken(
			@RequestBody JwtTokenRequest authenticationRequest) throws Exception {

		logger.info("Requested to authenticate user: " + authenticationRequest.getUsername());
		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
						authenticationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		logger.info("User :" + authenticationRequest.getUsername() + " Authenticated");
		return authService.createAndPersistRefreshTokenForUser(authentication).map(RefreshTokenEntity::getToken)
				.map(refreshToken -> {
					return ResponseEntity.ok(authService.generateToken(authentication, refreshToken));
				}).orElseThrow(
						() -> new AuthException("Couldn't create refresh token for: [" + authenticationRequest + "]"));

	}

	@PostMapping(value = "/register")
	public ResponseEntity<?> saveUser(@RequestBody UserDto user) throws Exception {
		logger.info("Requested to register user: " + user.getEmail());
		return ResponseEntity.ok(userDetailsService.save(user));
	}
	
	@PostMapping(value = "/getUserInfo")
	public void GetUserInfo() throws Exception {
		
	}
	
	@PostMapping(value = "/UpdateUser")
	public ResponseEntity<?> UpdateUser(@RequestBody UserDto user) throws Exception {
		logger.info("Requested to update user details: " + user.getEmail());
		return ResponseEntity.ok(userDetailsService.update(user));
	}
	


	@GetMapping(value = "/forgot/verifyUser")
	public  ResponseEntity<?> verifyUser(@RequestParam String username) throws UsernameNotFoundException{	
		return ResponseEntity.ok(userDetailsService.getUserIdbyUsername(username));
	}
	
	@GetMapping(value = "/login")
	public  ResponseEntity<?> login(@RequestParam String username, @RequestParam String pwd) throws Exception{
		try {
			UserEntity obj_User= userRepo.findByUsername(username);
			//BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();  
			
			if(obj_User.getId() > 0 && pwd.equals(obj_User.getPassword()))
				return ResponseEntity.ok(obj_User.getId()+"");
			else 
				return ResponseEntity.ok("Error: No user found");
		}
		catch(Exception e){
			return ResponseEntity.ok("Error: No user found");
		}
			
	}
	
	@GetMapping(value = "/getUserDetails")
	public  ResponseEntity<?> getUserDetails(@RequestParam long userId) throws UsernameNotFoundException{	
		return ResponseEntity.ok(userDetailsService.findByUserId(userId));
	}
	
	@GetMapping(value = "/user/securityQuestions")
	public Map<String, String> getSQbyUserId(@RequestParam long userId){
		return  userDetailsService.getSQbyUserId(userId);
	}
	
	@GetMapping(value = "/getAllSecurityQuestions")
	public List<SecurityQuestionsEntity> getSecurityQuestions(){
		return  securityQuestionsRepository.findAll();
	}
	
	@PostMapping(value = "/forgot/securityCode")
	public  ResponseEntity<String> sendSecurityCode(@RequestParam long userId){	
		return ResponseEntity.ok(userDetailsService.sendSecurityCode(userId));
	}

	@PostMapping(value = "/forgot/updatePassword")
	public  ResponseEntity<String> updatePassword(@RequestParam long userId,@RequestParam String password ){	
		return ResponseEntity.ok(userDetailsService.updatePassword(userId,password));
	}

	@PostMapping(value = "/refresh")
	public ResponseEntity<JwtTokenResponse> refreshAndGetAuthenticationToken(@RequestBody TokenRefreshRequest request) {
		logger.info(
				"Requested to refresh JWT token for User :" + jwtTokenUtil.getUsernameFromToken(request.getToken()));
		return authService.validateRefreshToken(request.getRefreshToken()).map(refreshToken -> {
			return ResponseEntity.ok(authService.refreshJwtToken(request.getToken(), request.getRefreshToken()));
		}).orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(),
				"Unexpected error during token refresh. Please logout and login again."));

	}

	@ExceptionHandler({ AuthenticationException.class })
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

}
