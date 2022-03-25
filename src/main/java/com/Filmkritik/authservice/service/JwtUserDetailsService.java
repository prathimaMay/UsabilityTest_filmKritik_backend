package com.Filmkritik.authservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Filmkritik.authservice.dto.UserDto;
import com.Filmkritik.authservice.entities.SecurityQuestionsEntity;
import com.Filmkritik.authservice.entities.UserEntity;
import com.Filmkritik.authservice.entities.UserSecQuestMappingEntity;
import com.Filmkritik.authservice.repository.SecurityQuestionsRepository;
import com.Filmkritik.authservice.repository.UserRepository;
import com.Filmkritik.authservice.repository.UserSecQuestMappingRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	private static final Logger logger = Logger.getLogger(JwtUserDetailsService.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private SecurityQuestionsRepository securityQuestionsRepo;

	@Autowired
	private UserSecQuestMappingRepository userSecQuestMappingRepository;

	@Autowired
    private JavaMailSender emailSender;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Getting User Details by Username -"+ username);
		UserEntity user = userRepo.findByUsername(username);
		if (user == null) {
			logger.error("User not found with username");
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), null);
//				getAuthority(user));
	}


//	private Set<SimpleGrantedAuthority> getAuthority(UserEntity user) {
//        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//		user.getRoles().forEach(role -> {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN" ));
//		});
//		return authorities;
//	}
	
	public String save(UserDto user) {
		if(userRepo.findByUsername(user.getEmail()) != null)
			return "User Already Present";
		UserEntity newUser = new UserEntity();
		newUser.setFirstname(user.getFirstname());
		newUser.setLastname(user.getLastname());
		newUser.setPhoneno(user.getPhonenumber());
		newUser.setUsername(user.getEmail());

		newUser.setPassword((user.getPassword()));

		newUser.setPassword(user.getPassword());

		UserEntity savedUser = userRepo.save(newUser);
		saveSecurityQ_AByUser(savedUser.getId(),user.getSQ_A());
		return savedUser.getId() + "";
	}
	
	
	public String update(UserDto user) {
		Long uID = getUserIdbyUsername(user.getEmail());
		Optional<UserEntity> newUser = findByUserId(uID);
		UserEntity obj_entity = newUser.stream().findFirst().get();
		obj_entity.setUsername(user.getEmail());

		obj_entity.setPassword((user.getPassword()));

		obj_entity.setPassword(user.getPassword());

		obj_entity.setFirstname(user.getFirstname());
		obj_entity.setLastname(user.getLastname());
		obj_entity.setPhoneno(user.getPhonenumber());
		userRepo.save(obj_entity);
		return "Success";
	}
	
	private void saveSecurityQ_AByUser(long userid, Map<Integer, String> sq_A) {
		sq_A.forEach((qid,answer)->{
			UserSecQuestMappingEntity newQA = new UserSecQuestMappingEntity();
			newQA.setUid(userid);
			newQA.setSid(qid);
			newQA.setAnswer(answer);
			userSecQuestMappingRepository.save(newQA);
		});;
		
	}


	public Optional<UserEntity> findByUserId(Long id) {
		return userRepo.findById(id);
	}
	
	public Long getUserIdbyUsername(String username) {
		return userRepo.findByUsername(username).getId();
	}


	public Map<String, String> getSQbyUserId(long userId) {
		// TODO Auto-generated method stub
		Map<String, String> SQA= new HashMap<String, String>();
		List<UserSecQuestMappingEntity> questions = userSecQuestMappingRepository.getByUserId(userId);
		questions.forEach((quest)->{
			
			SQA.put(securityQuestionsRepo.findById(quest.getSid()).getQuestion(), quest.getAnswer());
		});
		return SQA;
	}


	public String sendSecurityCode(long userId) {
		// TODO Auto-generated method stub
		String uid= generateCode();
		sendEmail(userRepo.findById(userId).get().getUsername(), "Confirm Password has been Changed", uid);
		return uid;
	}
	
	 private String generateCode() {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString();
	}


	public void sendEmail(String to, String subject, String code) {
		        SimpleMailMessage message = new SimpleMailMessage(); 
		        message.setFrom("filmkritik.se@gmail.com");
		        message.setTo(to); 
		        message.setSubject(subject); 
		        message.setText("Your Password has been changed.\\n Your New Password is:\\t" + code + "\\n\\n "
		        		+ "if you dont request changing password please contact to us.\\n\\n Thank you for trusting our Services."
		        		+ "\\n Have a great life.\"");
		        emailSender.send(message);
		    }


	public String updatePassword(long userId, String password) {
		// TODO Auto-generated method stub
		Optional<UserEntity> user = userRepo.findById(userId);
		UserEntity upUser = new UserEntity();
		upUser.setFirstname(user.get().getFirstname());
		upUser.setLastname(user.get().getLastname());
		upUser.setPhoneno(user.get().getPhoneno());
		upUser.setId(user.get().getId());
		upUser.setUsername(user.get().getUsername());
		upUser.setPassword(password);
		userRepo.save(upUser);
		return "Success";
	}
}