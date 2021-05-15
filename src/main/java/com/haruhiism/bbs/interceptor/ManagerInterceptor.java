package com.haruhiism.bbs.interceptor;

import com.haruhiism.bbs.domain.AccountLevel;
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
public class ManagerInterceptor implements HandlerInterceptor {

    private final AccountService accountService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute("loginSessionInfo");
        List<AccountLevel> levels = accountService.getAccountLevels(
                BoardAccountDTO.builder().userId(loginSessionInfo.getUserID()).build()).getLevels();

        // TODO: 추후 NORMAL권한을 없애고 AccountLevel을 ManagerLevel로 변경.
        // values() 메서드로 iteration하기 위함.
        if(levels.contains(AccountLevel.ACCOUNT_MANAGER) || levels.contains(AccountLevel.BOARD_MANAGER)){
            return true;
        } else {
            response.sendRedirect("/board/list");
            return false;
        }
    }
}
