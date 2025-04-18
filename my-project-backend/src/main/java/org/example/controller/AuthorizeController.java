package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.ConfirmResetVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.EmailResetVO;
import org.example.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("api/auth")
public class AuthorizeController {


    @Resource
    AccountService service;
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam() @Email String email,
                                        @RequestParam() @Pattern(regexp = "(register|reset)") String type,
                                        HttpServletRequest request) {
        return this.messageHandler(()->
                service.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
        return this.messageHandler(vo,service::registerEmailAccount);
    }
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody() @Valid ConfirmResetVO vo)
    {
        return this.messageHandler(vo,service::resetConfirm);
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody() @Valid EmailResetVO vo)
    {
        return this.messageHandler(vo,service::resetEmailAccountPassword);
    }

    private <T> RestBean<Void> messageHandler(T vo, Function<T,String> function)
    {
        return messageHandler(() -> function.apply(vo));
    }
    private RestBean<Void> messageHandler(Supplier<String> action)
    {
        String message = action.get();
        return  message == null ? RestBean.success() : RestBean.failure(400,message);
    }
}
