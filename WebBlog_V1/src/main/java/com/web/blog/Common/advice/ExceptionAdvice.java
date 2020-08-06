package com.web.blog.Common.advice;

import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.response.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        return responseService.getFailResult(Integer.valueOf(getMessage("unKnown.code")), getMessage("unKnown.msg"));
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSigninFailed(HttpServletRequest request, CEmailSigninFailedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("emailSigninFailed.code")), getMessage("emailSigninFailed.msg"));
    }

    @ExceptionHandler(CAuthenticationEntryPointException.class)
    public CommonResult authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("entryPointException.code")), getMessage("entryPointException.msg"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult AccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("accessDenied.code")), getMessage("accessDenied.msg"));
    }

    @ExceptionHandler(CCommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult communicationException(HttpServletRequest request, CCommunicationException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("communicationError.code")), getMessage("communicationError.msg"));
    }

    @ExceptionHandler(CUserExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult communicationException(HttpServletRequest request, CUserExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("existingUser.code")), getMessage("existingUser.msg"));
    }

    @ExceptionHandler(CNicknameExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult nicknameExistException(HttpServletRequest request, CNicknameExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("existingNickname.code")), getMessage("existingNickname.msg"));
    }

    @ExceptionHandler(CNotOwnerException.class)
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    public CommonResult notOwnerException(HttpServletRequest request, CNotOwnerException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("notOwner.code")), getMessage("notOwner.msg"));
    }

    @ExceptionHandler(CResourceNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult resourceNotExistException(HttpServletRequest request, CResourceNotExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("resourceNotExist.code")), getMessage("resourceNotExist.msg"));
    }

    @ExceptionHandler(CBoardExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult resourceNotExistException(HttpServletRequest request, CBoardExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("boardNameExist.code")), getMessage("boardNameExist.msg"));
    }

    @ExceptionHandler(CAlreadyLikedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult alreadyLikedException(HttpServletRequest request, CAlreadyLikedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("alreadyLikedExeption.code")), getMessage("alreadyLikedExeption.msg"));
    }

    @ExceptionHandler(COwnerCannotLike.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, COwnerCannotLike e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("ownerCannotLike.code")), getMessage("ownerCannotLike.msg"));
    }

    @ExceptionHandler(CAnsweredQuestionException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CAnsweredQuestionException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("answeredQuestion.code")), getMessage("answeredQuestion.msg"));
    }

    @ExceptionHandler(CSelectedAnswerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CSelectedAnswerException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("selectedAnswer.code")), getMessage("selectedAnswer.msg"));
    }

    @ExceptionHandler(CAskedQuestionException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CAskedQuestionException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("youAskedQuestion.code")), getMessage("youAskedQuestion.msg"));
    }

    @ExceptionHandler(CAlreadyFollowedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CAlreadyFollowedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("alreadyFollowed.code")), getMessage("alreadyFollowed.msg"));
    }

    @ExceptionHandler(CYouHaveNotFollowedThisBlogerEver.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CYouHaveNotFollowedThisBlogerEver e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("youNeverFollowedThisBloger.code")), getMessage("youNeverFollowedThisBloger.msg"));
    }

    @ExceptionHandler(CPasswordDoesntMatch.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CPasswordDoesntMatch e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("passwordIncorrect.code")), getMessage("passwordIncorrect.msg"));
    }

    @ExceptionHandler(CPasswordLengthException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CPasswordLengthException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("passwordLength.code")), getMessage("passwordLength.msg"));
    }

    @ExceptionHandler(CSharedPostException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult ownerCannotLike(HttpServletRequest request, CSharedPostException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("sharedPost.code")), getMessage("sharedPost.msg"));
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
