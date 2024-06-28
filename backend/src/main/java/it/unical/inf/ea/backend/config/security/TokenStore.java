package it.unical.inf.ea.backend.config.security;

import com.nimbusds.jwt.JWTClaimsSet;
import it.unical.inf.ea.backend.data.dao.InvalidTokensDao;
import it.unical.inf.ea.backend.data.entities.InvalidToken;
import it.unical.inf.ea.backend.exception.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.nimbusds.jose.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.crypto.MACSigner;
import java.text.ParseException;
import java.time.Instant;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.MACVerifier;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenStore {

    private final InvalidTokensDao invalidTokensDao;
    private final JwtContextUtils jwtContextUtils;

    public String createAccessToken(Map<String, Object> claims) throws JOSEException {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(Constants.JWT_EXPIRATION_TIME, ChronoUnit.MINUTES);


        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        for(String entry : claims.keySet())
            builder.claim(entry, claims.get(entry));
        JWTClaimsSet claimsSet = builder.issueTime(Date.from(issuedAt)).notBeforeTime(Date.from(issuedAt)).expirationTime(Date.from(expiration)).build();
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), payload);
        String secretKey = Constants.TOKEN_SECRET_KEY;
        jwsObject.sign(new MACSigner(secretKey.getBytes()));
        return jwsObject.serialize();
    }

    public boolean verifyToken(String token, String claim) throws JOSEException, ParseException {
        try {
            jwtContextUtils.getUsernameFromContext().ifPresentOrElse(username -> {
                try {
                    if (!username.equals(getUser(token)) || invalidTokensDao.findByToken(token).isPresent())
                        throw new RuntimeException("Invalid token");

                    String tokenClaim = getClaim(token);
                    if (claim != null && !claim.equals(tokenClaim))
                        throw new RuntimeException("Invalid token");

                    if ("refresh".equals(tokenClaim) && !"refresh".equals(claim))
                        throw new RuntimeException("Refresh token cannot be used for this operation");

                } catch (JOSEException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                throw new RuntimeException("Invalid token");
            });
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String getClaim(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getClaim("claim").toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectToken(String token) throws ParseException {
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken(token);
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        invalidToken.setExpirationDate(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
        invalidTokensDao.save(invalidToken);
    }

    public String getUser(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        SignedJWT signedKeycloakJWT = SignedJWT.parse(token);
        JWSVerifier jwsVerifier = new MACVerifier(Constants.TOKEN_SECRET_KEY.getBytes());

        if(signedJWT.verify(jwsVerifier)) {
            if(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()) && new Date().after(signedJWT.getJWTClaimsSet().getNotBeforeTime()))
                return (String) signedJWT.getPayload().toJSONObject().get("username");
            else
                throw new TokenExpiredException();
        } else {
            if (new Date().before(signedKeycloakJWT.getJWTClaimsSet().getExpirationTime()) && new Date().after(signedKeycloakJWT.getJWTClaimsSet().getIssueTime()))
                return (String) signedKeycloakJWT.getPayload().toJSONObject().get("name");
            else
                throw new TokenExpiredException();
        }
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header != null && header.startsWith("Bearer "))
            return header.replace("Bearer ", "");
        return "invalid";
    }

    public String createRefreshToken(String username) {
        flushInvalidTokens();
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("username", username)
                    .claim("claim", "refresh")
                    .expirationTime(Date.from(Instant.now().plus(Constants.JWT_REFRESH_EXPIRATION_TIME, ChronoUnit.HOURS)))
                    .notBeforeTime(Date.from(Instant.now()))
                    .issueTime(Date.from(Instant.now()))
                    .build();

            Payload payload = new Payload(claims.toJSONObject());

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                    payload);

            jwsObject.sign(new MACSigner(Constants.TOKEN_SECRET_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error to create JWT", e);
        }
    }

    public String createEmailToken(String username, String claim) {
        flushInvalidTokens();
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("username", username)
                    .claim("claim", claim)
                    .expirationTime(Date.from(Instant.now().plus(Constants.EMAIL_VERIFICATION_TOKEN_EXPIRATION_TIME, ChronoUnit.HOURS)))
                    .notBeforeTime(Date.from(Instant.now()))
                    .issueTime(Date.from(Instant.now()))
                    .build();

            Payload payload = new Payload(claims.toJSONObject());

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                    payload);

            jwsObject.sign(new MACSigner(Constants.TOKEN_SECRET_KEY));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            throw new RuntimeException("Error to create JWT", e);
        }
    }

    public void logout(String accessToken, String refreshToken) throws ParseException, JOSEException {
        if( !verifyToken(accessToken, null))
            throw new RuntimeException("Invalid token");
        if( !verifyToken(refreshToken, null))
            throw new RuntimeException("Invalid token");

        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken(accessToken);
        SignedJWT signedJWT = SignedJWT.parse(accessToken);
        invalidToken.setExpirationDate(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
        invalidTokensDao.save(invalidToken);

        invalidToken = new InvalidToken();
        invalidToken.setToken(refreshToken);
        signedJWT = SignedJWT.parse(refreshToken);
        invalidToken.setExpirationDate(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
        invalidTokensDao.save(invalidToken);
    }

    public void flushInvalidTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<InvalidToken> invalidTokens = invalidTokensDao.findAllByExpirationDateBefore(now);
        invalidTokensDao.deleteAll(invalidTokens);
    }
}
