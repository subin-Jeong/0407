package com.estsoft.web;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.estsoft.domain.Member;
import com.estsoft.domain.MemberRole;
import com.estsoft.repository.MemberRepository;
import com.estsoft.service.UserDetailsServiceImpl;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailServiceImpl;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * ȸ������ ������
	 * @return �����̷�Ʈ �� �� ������
	 */
	@GetMapping("/register")
	public String register() {
		return "member/register";
	}
	
	/**
	 * �α��� ������
	 * @return �����̷�Ʈ �� �� ������
	 */
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	/**
	 * ȸ������
	 * @param member
	 * @return ��ϵ� Member Entity
	 */
	@PostMapping("/register")
	@ResponseBody
	public Member register(@RequestBody Member member) {
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		return memberRepository.save(member);
	}
	
	/**
	 * �α���
	 * @param member
	 * @return �α����� Member Entity
	 */
	@PostMapping("/login")
	@ResponseBody
	public Member login(@RequestBody Member member) {
		userDetailServiceImpl.loadUserByUsername(member.getEmail());
	}
	
	
	
	@PostMapping("")
	public String create(Member member) {
		MemberRole role = new MemberRole();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		role.setRoleName("BASIC");
		member.setRoles(Arrays.asList(role));
		
		memberRepository.save(member);
		
		return "redirect:/";
	}
}
