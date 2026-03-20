package br.com.forum_hub.domain.autenticacao;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OAuth2UserService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate;

    public OAuth2UserService(OAuth2AuthorizedClientService authorizedClientService, RestTemplate restTemplate, RestTemplate restTemplate1) {
        this.authorizedClientService = authorizedClientService;
        this.restTemplate = restTemplate1;
    }

    public String getEmailFromOAuth2AuthenticationToken(Authentication authentication, String provider) {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        if ("github".equals(provider)) {
            return getGithubEmail(token);
        }

        // Google, etc
        OAuth2User user = token.getPrincipal();
        return user.getAttribute("email");

    }

    private String getGithubEmail(OAuth2AuthenticationToken token){
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody()
                .stream()
                .filter(e ->(Boolean) e.get("primary"))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElse(null);

    }

}
