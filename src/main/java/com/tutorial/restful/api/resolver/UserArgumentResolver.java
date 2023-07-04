package com.tutorial.restful.api.resolver;

import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    // menangkap parameter di Controller Method seperti @ModelAttribute, @RequestBody, @RequestParam, dan lain-lain
    // class yang digunakan untuk mengisi object argument yang kita inginkan secara otomatis
    // ketika kita sudah membuat resolver, daftarkan ke Web MVC Configurer

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType()); // set entity menjadi argument resolver
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(); // return objek permintaan asli yang mendasarinya. // kita return HttpServletRequest supaya bisa mendapat header
        String token = servletRequest.getHeader("X-API-TOKEN"); // get nilai header yang ditentukan sebagai String.

        // cek apakah value header null. jika iya kasih Unauthorized status code 401
        if (token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // cek apakah token user ada. jika iya kasih. jika tidak ada kasih Unauthorized status code 401
        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        // cek apakah tokenExpiredAt db lebih kecil dari dari waktu saat ini. jika iya kasih Unauthorized status code 401
        if (user.getTokenExpiredAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return user;
    }
}
