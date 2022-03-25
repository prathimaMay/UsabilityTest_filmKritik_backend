package com.Filmkritik.authservice.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Filmkritik.authservice.service.JwtUserDetailsService;
import com.Filmkritik.authservice.utilities.JwtTokenUtil;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtOncePerRequestFilter extends OncePerRequestFilter {

	private static final Logger logger = Logger.getLogger(JwtOncePerRequestFilter.class);

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String jwtToken = getJwtFromRequest(request);
		String username = null;
		if(jwtToken!=null) {
			 username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			logger.info("Extracted Token:{" + jwtToken + "} for User:{" + username + "}");
		}
		try {		
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
			{
				UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
				if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = jwtTokenUtil
							.getAuthentication(jwtToken, SecurityContextHolder.getContext().getAuthentication(),
									userDetails);
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
			chain.doFilter(request, response);
		} catch (IllegalArgumentException e) {
			logger.info("Unable to get JWT Token");
		} catch (ExpiredJwtException e) {
			logger.info("JWT Token has expired");
		}

		// Once we get the token validate it.

	}

	/**
	 * Extract the token from the Authorization request header
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}