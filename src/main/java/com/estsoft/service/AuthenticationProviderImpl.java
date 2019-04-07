package com.estsoft.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

	 @Override
	 public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication; 
    //������ �Է��� ������ ���̵��������θ����.(�α����� �������̵�����������´�)

    User userInfo = UserDetailsService.loadUserByUsername(authToken.getName()); //UserDetailsService���� ���������� �ҷ��´�.
	if (userInfo == null) {
	  throw new UsernameNotFoundException(authToken.getName());
	}

	if (!matchPassword(userInfo.getPassword(), authToken.getCredentials())) {
		throw new BadCredentialsException("not matching username or password");
    }

    List<GrantedAuthority> authorities = (List<GrantedAuthority>) userInfo.getAuthorities();

    return new UsernamePasswordAuthenticationToken(userInfo,null,authorities);
  }

  private boolean matchPassword(String password, Object credentials) {
    return password.equals(credentials);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
