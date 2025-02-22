package com.awake.ve.common.ecs.handler.impl;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awake.ve.common.core.utils.SpringUtils;
import com.awake.ve.common.ecs.api.request.BaseApiRequest;
import com.awake.ve.common.ecs.api.response.BaseApiResponse;
import com.awake.ve.common.ecs.api.template.PVECreateTemplateApiRequest;
import com.awake.ve.common.ecs.api.template.PVECreateTemplateApiResponse;
import com.awake.ve.common.ecs.api.ticket.PVETicketApiResponse;
import com.awake.ve.common.ecs.config.propterties.EcsProperties;
import com.awake.ve.common.ecs.director.PVECreateTemplateDirector;
import com.awake.ve.common.ecs.director.base.BaseApiDirector;
import com.awake.ve.common.ecs.enums.PVEApi;
import com.awake.ve.common.ecs.handler.ApiHandler;
import com.awake.ve.common.ecs.utils.EcsUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.awake.ve.common.ecs.constants.ApiParamConstants.*;
import static com.awake.ve.common.ecs.constants.JsonPathConstants.PVE_BASE_RESP;

/**
 * PVE api POST /api2/json/nodes/{node}/qemu/{vmid}/template
 * 根据虚拟机创建模板(虚拟机需关闭)
 *
 * @author wangjiaxing
 * @date 2025/2/22 14:51
 */
@Data
public class PVECreateTemplateApiHandler implements ApiHandler {

    private static final EcsProperties ECS_PROPERTIES = SpringUtils.getBean(EcsProperties.class);

    private PVECreateTemplateApiHandler() {

    }

    public static PVECreateTemplateApiHandler newInstance() {
        PVECreateTemplateApiHandler handler = new PVECreateTemplateApiHandler();
        return handler;
    }

    @Override
    public BaseApiResponse handle() {
        return null;
    }

    @Override
    public BaseApiResponse handle(BaseApiDirector director) {
        PVETicketApiResponse ticket = EcsUtils.checkTicket();

        if (!(director instanceof PVECreateTemplateDirector createTemplateDirector)) {
            return new PVECreateTemplateApiResponse();
        }
        BaseApiRequest request = createTemplateDirector.buildRequest();
        if (!(request instanceof PVECreateTemplateApiRequest createTemplateApiRequest)) {
            return new PVECreateTemplateApiResponse();
        }

        PVEApi pveApi = PVEApi.CREATE_TEMPLATE;
        String api = pveApi.getApi();

        Map<String, Object> params = new HashMap<>();
        params.put(HOST, ECS_PROPERTIES.getHost());
        params.put(PORT, ECS_PROPERTIES.getPort());
        params.put(NODE, ECS_PROPERTIES.getNode());
        params.put(VM_ID, createTemplateApiRequest.getVmId());

        String url = StrFormatter.format(api, params, true);

        // 创建请求体JSON
        JSONObject jsonBody = new JSONObject();
        jsonBody.set(NODE, ECS_PROPERTIES.getNode());
        jsonBody.set(VM_ID, createTemplateApiRequest.getVmId());
        String string = jsonBody.toString();

        HttpRequest requests = HttpRequest.post(url)
                .body(string, APPLICATION_JSON)
                .header(CSRF_PREVENTION_TOKEN, ticket.getCSRFPreventionToken(), false)
                .header(COOKIE, PVE_AUTH_COOKIE + ticket.getTicket(), false);
        // 设置重定向
        requests.setFollowRedirects(true);
        HttpResponse response = requests.execute();

        JSON json = JSONUtil.parse(response.body());
        return new PVECreateTemplateApiResponse(json.getByPath(PVE_BASE_RESP, String.class));
    }
}
