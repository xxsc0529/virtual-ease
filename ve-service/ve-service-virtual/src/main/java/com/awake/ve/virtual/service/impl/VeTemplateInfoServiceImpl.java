package com.awake.ve.virtual.service.impl;

import com.awake.ve.common.core.utils.MapstructUtils;
import com.awake.ve.common.core.utils.StringUtils;
import com.awake.ve.common.ecs.api.template.request.PVECreateTemplateApiRequest;
import com.awake.ve.common.ecs.api.template.response.PVECreateTemplateApiResponse;
import com.awake.ve.common.ecs.api.vm.status.PVENodeVmListApiRequest;
import com.awake.ve.common.ecs.api.vm.status.PVENodeVmListApiResponse;
import com.awake.ve.common.ecs.domain.vm.PveVmInfo;
import com.awake.ve.common.ecs.enums.api.PVEApi;
import com.awake.ve.common.ecs.handler.ApiHandler;
import com.awake.ve.virtual.domain.vo.VeTemplateListVo;
import com.awake.ve.virtual.service.IVeTemplateInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VeTemplateInfoServiceImpl implements IVeTemplateInfoService {

    /**
     * 获取模板列表
     *
     * @author wangjiaxing
     * @date 2025/3/19 9:28
     */
    @Override
    public List<VeTemplateListVo> templates() {

        ApiHandler apiHandler = PVEApi.NODE_VM_LIST.getApiHandler();

        // api参数
        PVENodeVmListApiRequest request = new PVENodeVmListApiRequest();
        request.setNode("pve");
        request.setFull(0);

        // api结果
        PVENodeVmListApiResponse response = (PVENodeVmListApiResponse) apiHandler.handle(request);
        List<PveVmInfo> vmList = response.getVmList();
        vmList = vmList.stream().filter(PveVmInfo::getTemplate).sorted(Comparator.comparing(PveVmInfo::getVmId)).toList();

        return MapstructUtils.convert(vmList, VeTemplateListVo.class);
    }

    /**
     * 根据虚拟机创建模板
     *
     * @param vmId 虚拟机id
     * @author wangjiaxing
     * @date 2025/3/19 14:38
     */
    @Override
    public Boolean createTemplate(Long vmId) {
        ApiHandler apiHandler = PVEApi.CREATE_TEMPLATE.getApiHandler();

        // api参数
        PVECreateTemplateApiRequest request = PVECreateTemplateApiRequest.builder().node("pve").vmId(vmId).build();

        // api响应
        PVECreateTemplateApiResponse response = (PVECreateTemplateApiResponse) apiHandler.handle(request);

        return StringUtils.isNotBlank(response.getData());
    }
}
