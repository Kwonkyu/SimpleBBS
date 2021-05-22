package com.haruhiism.bbs.interceptor;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountManagerInterceptor implements HandlerInterceptor {

    private final AccountService accountService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute("loginSessionInfo");
        List<ManagerLevel> levels = accountService.getAccountLevels(
                BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()).getLevels();

        if(levels.contains(ManagerLevel.ACCOUNT_MANAGER)) {
            return true;
        } else {
            response.sendRedirect("/manage/console");
            return false;
        }
    }
}
