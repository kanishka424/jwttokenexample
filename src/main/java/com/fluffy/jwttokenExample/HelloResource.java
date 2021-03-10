package com.fluffy.jwttokenExample;


import com.fluffy.jwttokenExample.model.AuthenticationRequest;
import com.fluffy.jwttokenExample.model.AuthenticationResponse;
import com.fluffy.jwttokenExample.services.MyUserDetailService;
import com.fluffy.jwttokenExample.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResource {


    @Autowired
    private AuthenticationManager authenticationManager;//we use the authenticate() of this class to authenticate our UsernamePasswordAuthenticationToken
    //A bean method must be created in WebSecurityConfigurerAdapter extending class to use authenticationManager here
    //there is a method to override authenticationManagerBean() here

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    JwtUtil jwtUtil;



    @RequestMapping("/hello")
    public String getHelloResource() {
        return "Hello World";
    }


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
//this method is used because we will use JWT token to access resources
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {//the method which creates the token after authentication
        try {//this try catch takes care of the Authentication using Token
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())//the standered token for spring MVC use for UN and PW
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect Username Password", e);
        }
        //========================above try/catch handles authentication========================================

        //=========================now if the authentication is success we will create the token as follows========
        final UserDetails userDetails=myUserDetailService.loadUserByUsername(authenticationRequest.getUsername());

        String jwt=jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));//

//=====above three lines create and return Token========================

    }

}



//=========================================NOTE================================================================
//RESPONEENTITY ResponseEntity<?>
//ResponseEntity represents the whole HTTP response: status code, headers, and body. As a result,
//        we can use it to fully configure the HTTP response.
//        If we want to use it, we have to return it from the endpoint; Spring takes care of the rest


//body of requets  is parsed to  authenticationRequest//(createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest))





//==========================HOW  authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())//the standered token for spring MVC use for UN and PW
//            ); WORKS===============================================================================
//it is passing the UsernamePasswordAuthenticationToken to the default AuthenticationProvider,
// which will use the userDetailsService to get the user based on username and compare that user's password with the one in the authentication token.