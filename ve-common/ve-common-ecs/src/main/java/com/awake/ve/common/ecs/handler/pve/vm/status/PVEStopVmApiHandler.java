package com.awake.ve.common.ecs.handler.pve.vm.status;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awake.ve.common.core.constant.HttpStatus;
import com.awake.ve.common.core.exception.ServiceException;
import com.awake.ve.common.core.utils.SpringUtils;
import com.awake.ve.common.ecs.api.request.BaseApiRequest;
import com.awake.ve.common.ecs.api.response.BaseApiResponse;
import com.awake.ve.common.ecs.api.ticket.PVETicketApiResponse;
import com.awake.ve.common.ecs.api.vm.status.PVEStopVmApiRequest;
import com.awake.ve.common.ecs.api.vm.status.PVEStopVmApiResponse;
import com.awake.ve.common.ecs.config.propterties.EcsProperties;
import com.awake.ve.common.ecs.enums.api.PVEApi;
import com.awake.ve.common.ecs.handler.ApiHandler;
import com.awake.ve.common.ecs.utils.EcsUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.awake.ve.common.ecs.constants.ApiParamConstants.*;
import static com.awake.ve.common.ecs.constants.PVEJsonPathConstants.PVE_BASE_RESP;

@Slf4j
public class PVEStopVmApiHandler implements ApiHandler {

    private final static EcsProperties ECS_PROPERTIES = SpringUtils.getBean(EcsProperties.class);

    private PVEStopVmApiHandler() {

    }

    public static PVEStopVmApiHandler newInstance() {
        return new PVEStopVmApiHandler();
    }

    @Override
    public BaseApiResponse handle() {
        return null;
    }

    @Override
    public BaseApiResponse handle(BaseApiRequest baseApiRequest) {
        if (!(baseApiRequest instanceof PVEStopVmApiRequest request)) {
            log.info("[PVEStopVmApiHandler][handle] api请求参数异常 期待:{} , 实际:{}", PVEStopVmApiRequest.class.getName(), baseApiRequest.getClass().getName());
            throw new ServiceException("api请求参数类型异常", HttpStatus.WARN);
        }

        PVETicketApiResponse ticket = EcsUtils.checkTicket();

        String api = PVEApi.STOP_VM.getApi();
        Map<String, Object> params = new HashMap<>();
        params.put(HOST, ECS_PROPERTIES.getHost());
        params.put(PORT, ECS_PROPERTIES.getPort());
        params.put(NODE, request.getNode());
        params.put(VM_ID, request.getVmId());
        String url = StrFormatter.format(api, params, true);

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set(NODE, request.getNode());
        jsonObject.set(VM_ID, request.getVmId());
        jsonObject.set(KEEP_ALIVE, request.getKeepAlive());
        jsonObject.set(MIGRATED_FROM, request.getMigratedFrom());
        jsonObject.set(SKIP_LOCK, request.getSkipLock());
        jsonObject.set(TIMEOUT, request.getTimeout());
        jsonObject.set(OVERRULE_SHUTDOWN, request.getOverruleShutdown());
        String body = jsonObject.toString();

        HttpRequest httpRequest = HttpRequest.post(url)
                .body(body, APPLICATION_JSON)
                .header(CSRF_PREVENTION_TOKEN, ticket.getCSRFPreventionToken(), false)
                .header(COOKIE, PVE_AUTH_COOKIE + ticket.getTicket(), false)
                .setFollowRedirects(true);
        try (HttpResponse response = httpRequest.execute()) {
            String string = response.body();
            log.info("[PVEStopVmApiHandler][handle] 请求url:{} , 响应:{}", url, string);
            JSONObject json = JSONUtil.parseObj(string);
            return new PVEStopVmApiResponse(json.getByPath(PVE_BASE_RESP, String.class));
        } catch (Exception e) {
            log.error("[PVEStopVmApiHandler][handle] 停止虚拟机请求异常", e);
            throw new RuntimeException(e);
        }
    }
}
