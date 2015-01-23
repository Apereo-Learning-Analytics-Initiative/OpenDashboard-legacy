package od.utils;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class AppControllerAdvice {
    private static final String X_REQUESTED_WITH_HEADER_NAME = "X-Requested-With";
    private static final String X_REQUESTED_WITH_AJAX_VALUE = "XMLHttpRequest";
    private static final Logger logger = LoggerFactory.getLogger(AppControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody Object handleAppException(HttpServletRequest request, Exception ex) throws IOException {
        logger.error("Exception", ex);
        if (isAjaxCall(request)) {
            logger.error(ex.getMessage(), ex);
            Response response = new Response();
            response.setErrors(Arrays.asList(ex.getMessage()));
            response.setData(ExceptionUtils.getStackTrace(ex));
            response.setUrl(request.getRequestURL().toString());
            return response;
        } else {
            ModelAndView modelAndView = new ModelAndView("error");
            logger.error(ex.getMessage(), ex);
            Response response = new Response();
            response.setErrors(Arrays.asList(ex.getMessage()));
            response.setData(ExceptionUtils.getStackTrace(ex));
            response.setUrl(request.getRequestURL().toString());
            modelAndView.addObject("response", response);
            return modelAndView;
        }
    }

    private boolean isAjaxCall(HttpServletRequest request) {
        boolean isAjaxCall = false;
        String xRequestedWithHeaderValue = request.getHeader(X_REQUESTED_WITH_HEADER_NAME);
        if (!StringUtils.isEmpty(xRequestedWithHeaderValue) && xRequestedWithHeaderValue.equalsIgnoreCase(X_REQUESTED_WITH_AJAX_VALUE)) {
            isAjaxCall = true;
        }
        return isAjaxCall;
    }

}
