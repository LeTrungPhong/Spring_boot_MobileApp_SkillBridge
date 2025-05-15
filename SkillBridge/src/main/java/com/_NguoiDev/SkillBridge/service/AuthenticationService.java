package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.controller.InformationResponse;
import com._NguoiDev.SkillBridge.dto.request.AuthenticationRequest;
import com._NguoiDev.SkillBridge.dto.request.IntrospectRequest;
import com._NguoiDev.SkillBridge.dto.request.LogoutRequest;
import com._NguoiDev.SkillBridge.dto.request.RefreshRequest;
import com._NguoiDev.SkillBridge.dto.response.AuthenticationResponse;
import com._NguoiDev.SkillBridge.dto.response.IntrospectResponse;
import com._NguoiDev.SkillBridge.entity.InvalidToken;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.enums.Role;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.InformationMapper;
import com._NguoiDev.SkillBridge.repository.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    InvalidTokenRepository invalidTokenRepository;
    InformationMapper informationMapper;
    TeacherRepository teacherRepository;
    StudentRepository studentRepository;
    UserDeviceTokenService userDeviceTokenService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshDuration;

    public AuthenticationResponse Authentication(AuthenticationRequest request) throws JOSEException {
        User userLogIn = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), userLogIn.getPassword());
        if (!authenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = generateToken(userLogIn);
        InformationResponse info = null;
        if (userLogIn.getAuthorities().getFirst().getAuthorityId().getAuthority().equals(Role.ROLE_TEACHER.getCodeRole())){
            info = informationMapper.toInformationResponse(teacherRepository.findByUserUsername(request.getUsername())
                    .orElseThrow(()->new AppException(ErrorCode.TEACHER_NOT_EXISTED)));
            info.setRole("TEACHER");
            info.setUsername(request.getUsername());
        } else if (userLogIn.getAuthorities().getFirst().getAuthorityId().getAuthority().equals(Role.ROLE_STUDENT.getCodeRole())){
            info = informationMapper.toInformationResponse(studentRepository.findByUserUsername(request.getUsername())
                    .orElseThrow(()->new AppException(ErrorCode.STUDENT_NOT_EXISTED)));
            info.setRole("STUDENT");
            info.setUsername(request.getUsername());
        }
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .info(info)
                .build();

    }


    private String generateToken(User user) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("skill_bridge")
                .claim("scope", user.getAuthorities().getFirst().getAuthorityId().getAuthority())
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()))
                .build();
        //cau truc jwt: <Header>.<Payload>.<Signature>
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        return jwsObject.serialize();
    }

    public void Logout(LogoutRequest request)throws ParseException, JOSEException{
        try{
            SignedJWT signedJWT = verifyToken(request.getToken(), true);

            String jwtid = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(refreshDuration, ChronoUnit.SECONDS).toEpochMilli());

            InvalidToken invalidatedToken = InvalidToken.builder()
                    .id(jwtid)
                    .expirationDate(expirationTime)
                    .build();

            invalidTokenRepository.save(invalidatedToken);
        }catch (AppException exception){
            System.out.println(exception.getMessage());
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getToken(), true);

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        invalidTokenRepository.save(InvalidToken.builder()
                        .expirationDate(expirationTime)
                        .id(jwtId)
                .build());
        String username = signedJWT.getJWTClaimsSet().getSubject();
        String newToken = generateToken(userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found")));
        return AuthenticationResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }


    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = isRefresh?
                new Date(signedJWT.getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(refreshDuration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                :new Date(signedJWT.getJWTClaimsSet()
                        .getExpirationTime()
                        .toInstant()
                        .toEpochMilli());
        boolean verified = signedJWT.verify(verifier);
        if (!(verified&expirationDate.after(new Date()))) {
            throw new RuntimeException("Token expired");
        }
        if (invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new RuntimeException("Token invalid");
        }
        return signedJWT;
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException{
        String token = request.getToken();
        boolean isValid = true;
        try{
            verifyToken(token, false);
        }  catch (RuntimeException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
}
