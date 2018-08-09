package org.nextprot.api.security.service.impl;

import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.spring.security.auth0.Auth0JWTToken;
import com.auth0.spring.security.auth0.Auth0TokenException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.security.service.NextprotAuth0Endpoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NextprotAuthProvider implements AuthenticationProvider, InitializingBean {

	private JWTVerifier jwtVerifier;
	private String clientSecret;
	private String clientId;
	private final Log logger = LogFactory.getLog(NextprotAuthProvider.class);

	@Autowired
	private NextprotAuth0Endpoint nextprotAuth0Endpoint;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JWTCodec<Map<String, Object>> codec;

	private Auth0User getUserInfoFromAuth0BasedOnAccessToken(String accessToken){

		try {

			this.logger.debug("Will ask auth0 service");
			//in case we send the access token
			Auth0User auth0User = nextprotAuth0Endpoint.fetchUser(accessToken);
			this.logger.debug("Authenticating with access token (asking auth0 endpoint)" + auth0User);
			return auth0User;

		}catch (Exception e){
			e.printStackTrace();
			this.logger.error(e.getMessage());
			throw new SecurityException("client id not found");
		}

	}

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String token = ((Auth0JWTToken) authentication).getJwt();

		this.logger.debug("Trying to authenticate with token: " + token);
		try {

			Map<String, Object> map = null;
			Auth0User auth0User = null;

			Boolean validateOnAuth0EndPoint = false;
			
			//Should put this in 2 different providers
			if(token.split("\\.").length == 3){
				try {
					map = jwtVerifier.verify(token);
				}catch (IllegalStateException e){
					if(e.getMessage().equals("unsupported algorithm")){
						//If the algo is not supported ask Auth0 to validate for you the token.
						//Note that with the update of the libraries this should be possible but there are many things which are not backward compatible
						validateOnAuth0EndPoint = true;
					}else {
						throw e;
					}
				}
				this.logger.debug("Verifying JWT");
			}

			// 3 cases could happen
			// 1) ID Token is sent and email is present (should not be done like that)
			// 2) Access Token is sent with the email set in: https://www.nextprot.org/userinfo/email (see Auth0 rule: Enrich AccessToken with User email and name)
			// 3) Access Token is sent without any email information set and therefore userinfo endpoint will be used to retrieve user info profile

			String ID_TOKEN_EMAIL_ATTRIBUTE = "email";
			String ACCESS_TOKEN_EMAIL_ATTRIBUTE = "https://www.nextprot.org/userinfo/email";

			String attributeUsed = "NOT-SET";
			//Case 1
			if(!validateOnAuth0EndPoint && map.containsKey(ID_TOKEN_EMAIL_ATTRIBUTE)){
				attributeUsed = ID_TOKEN_EMAIL_ATTRIBUTE;
			}
			//Case 2
			else if(!validateOnAuth0EndPoint && map.containsKey(ACCESS_TOKEN_EMAIL_ATTRIBUTE)){
				attributeUsed = ACCESS_TOKEN_EMAIL_ATTRIBUTE;
			}
			//Case 3
			else { // 3rd case we ask Auth0 for who is this guy
				auth0User = getUserInfoFromAuth0BasedOnAccessToken(token);
			}

			this.logger.debug("Decoded JWT token" + map);

			UserDetails userDetails;
			if ((auth0User != null && auth0User.getEmail() != null) || (map != null && map.containsKey(attributeUsed))) {
				String username;
				if(auth0User != null && auth0User.getEmail() != null) {
					 username = auth0User.getEmail();
				}
				else {
					username = (String) map.get(attributeUsed);
				}
				
				if (username != null) {
					userDetails = userDetailsService.loadUserByUsername(username);
					authentication.setAuthenticated(true);
					
					return createSuccessAuthentication(userDetails, map);
				
				}
				else return null;
			}
			// Codec map
			else if (map != null && map.containsKey("payload")) {

				Map<String, Object> payload = codec.decodeJWT(token);
				String username = (String) payload.get(attributeUsed);

				if (username != null) {
					userDetails = userDetailsService.loadUserByUsername(username);
					userDetails.getAuthorities().clear();

					List<String> auths = (List<String>) payload.get("authorities");

					for (String authority : auths) {
						((Set<GrantedAuthority>)userDetails.getAuthorities()).add(new SimpleGrantedAuthority(authority));
					}
					authentication.setAuthenticated(true);

					return createSuccessAuthentication(userDetails, map);

				} else {
					return null;
				}
			}
			else throw new SecurityException("client id not found");

			/*//TODO add the application here or as another provider else if (map.containsKey("app_id")) {
				long appId = (Long) map.get("app_id");
				UserApplication userApp = userApplicationService.getUserApplication(appId);
				if (userApp.hasUserDataAccess()) {

					userDetails = userDetailsService.loadUserByUsername(userApp.getOwner());
					if (userDetails == null) {
						userService.createUser(buildUserFromAuth0(map));
					}
					userDetails = userDetailsService.loadUserByUsername(userApp.getOwner());
				}
			}*/

		} catch (InvalidKeyException e) {
			//this.logger.error("InvalidKeyException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new Auth0TokenException(e);
		} catch (NoSuchAlgorithmException e) {
			//this.logger.error("NoSuchAlgorithmException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new Auth0TokenException(e);
		} catch (IllegalStateException e) {
			//this.logger.error("IllegalStateException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new Auth0TokenException(e);
		} catch (SignatureException e) {
			//this.logger.error("SignatureException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new Auth0TokenException(e);
		} catch (IOException e) {
			//this.logger.error("IOException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new Auth0TokenException("invalid token", e);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return Auth0JWTToken.class.isAssignableFrom(authentication);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if ((this.clientSecret == null) || (this.clientId == null)) {
			throw new RuntimeException("client secret and client id are not set for Auth0AuthenticationProvider");
		}

		this.jwtVerifier = new JWTVerifier(this.clientSecret, this.clientId);
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	@Value("${auth0.clientSecret}")
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return this.clientId;
	}

	@Value("${auth0.clientId}")
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	

    /**
     * Creates a successful {@link Authentication} object
     *
     * @return the successful authentication token
     */
    private final Authentication createSuccessAuthentication(UserDetails userDetails, Map<String, Object> map) {
        
    	NextprotUserToken usrToken = new NextprotUserToken();
    	usrToken.setAuthenticated(true);
    	usrToken.setPrincipal(userDetails);
    	usrToken.setDetails(map);
    	usrToken.getAuthorities().addAll(userDetails.getAuthorities());
    	
        return usrToken;
    }

 

}