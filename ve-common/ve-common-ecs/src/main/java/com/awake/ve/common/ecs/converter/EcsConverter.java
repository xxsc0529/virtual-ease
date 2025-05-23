package com.awake.ve.common.ecs.converter;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awake.ve.common.core.exception.ServiceException;
import com.awake.ve.common.core.utils.StringUtils;
import com.awake.ve.common.ecs.api.network.*;
import com.awake.ve.common.ecs.api.response.BaseApiResponse;
import com.awake.ve.common.ecs.api.spice.PVECreateVmSpiceProxyApiResponse;
import com.awake.ve.common.ecs.api.vm.config.PVEGetVmConfigApiResponse;
import com.awake.ve.common.ecs.api.vm.config.PVEPostVmConfigApiRequest;
import com.awake.ve.common.ecs.api.vm.config.PVEPutVmConfigApiRequest;
import com.awake.ve.common.ecs.api.vm.status.PVECreateOrRestoreVmApiRequest;
import com.awake.ve.common.ecs.api.vm.status.PVENodeVmListApiResponse;
import com.awake.ve.common.ecs.api.vm.status.PVEVmStatusApiResponse;
import com.awake.ve.common.ecs.api.vnc.PVEVncProxyApiResponse;
import com.awake.ve.common.ecs.domain.ha.PveHaObject;
import com.awake.ve.common.ecs.domain.network.Network;
import com.awake.ve.common.ecs.domain.network.children.BridgeNetwork;
import com.awake.ve.common.ecs.domain.network.children.EthNetwork;
import com.awake.ve.common.ecs.domain.network.children.VlanNetwork;
import com.awake.ve.common.ecs.domain.vm.PveVmInfo;
import com.awake.ve.common.ecs.enums.network.NetworkType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.awake.ve.common.ecs.constants.ApiParamConstants.*;
import static com.awake.ve.common.ecs.constants.PVEJsonPathConstants.*;

/**
 * ecs 模块转换器
 *
 * @author wangjiaxing
 * @date 2025/2/25 12:14
 */
public class EcsConverter {

    /**
     * @param json {@link JSON}
     * @return {@link BaseApiResponse}
     * @author wangjiaxing
     * @date 2025/2/24 11:37
     */
    public static BaseApiResponse buildPVEVmStatusApiResponse(JSON json) {
        PveHaObject pveHaObject = new PveHaObject();

        Integer haManaged = json.getByPath(VM_STATUS_HA_MANAGED, Integer.class);
        pveHaObject.setManaged(haManaged);

        String status = json.getByPath(VM_STATUS_STATUS, String.class);
        Long vmId = json.getByPath(VM_STATUS_VMID, Long.class);
        String name = json.getByPath(VM_STATUS_NAME, String.class);
        Boolean agent = json.getByPath(VM_STATUS_QEMU_AGENT, Boolean.class);
        String lock = json.getByPath(VM_STATUS_LOCK, String.class);
        String clipboard = json.getByPath(VM_STATUS_CLIPBOARD, String.class);
        Integer cpus = json.getByPath(VM_STATUS_CPUS, Integer.class);
        Double maxDisk = json.getByPath(VM_STATUS_MAX_DISK, Double.class);
        Double maxMem = json.getByPath(VM_STATUS_MAXMEM, Double.class);
        Double diskRead = json.getByPath(VM_STATUS_DISK_READ, Double.class);
        Double diskWrite = json.getByPath(VM_STATUS_DISK_WRITE, Double.class);
        Double netIn = json.getByPath(VM_STATUS_NET_IN, Double.class);
        Double netOut = json.getByPath(VM_STATUS_NET_OUT, Double.class);
        Long pid = json.getByPath(VM_STATUS_QEMU_PID, Long.class);
        String qmpStatus = json.getByPath(VM_STATUS_QMP_STATUS, String.class);
        String runningMachine = json.getByPath(VM_STATUS_RUNNING_MACHINE, String.class);
        String runningQemu = json.getByPath(VM_STATUS_RUNNING_QEMU, String.class);
        Boolean spice = json.getByPath(VM_STATUS_SPICE, Boolean.class);
        String tags = json.getByPath(VM_STATUS_TAGS, String.class);
        Boolean template = json.getByPath(VM_STATUS_TEMPLATE, Boolean.class);
        Integer uptime = json.getByPath(VM_STATUS_UPTIME, Integer.class);

        return PVEVmStatusApiResponse.builder()
                .ha(pveHaObject)
                .agent(agent)
                .cpus(cpus)
                .diskRead(diskRead)
                .runningQemu(runningQemu)
                .name(name)
                .netIn(netIn)
                .netOut(netOut)
                .pid(pid)
                .status(status)
                .vmId(vmId)
                .lock(lock)
                .clipboard(clipboard)
                .maxDisk(maxDisk)
                .maxMem(maxMem)
                .diskWrite(diskWrite)
                .qmpStatus(qmpStatus)
                .runningMachine(runningMachine)
                .spice(spice)
                .tags(tags)
                .template(template)
                .uptime(uptime)
                .build();
    }

    /**
     * 构建虚拟机列表
     *
     * @param json 参数
     * @return {@link com.awake.ve.common.ecs.api.vm.status.PVENodeVmListApiResponse}
     * @author wangjiaxing
     * @date 2025/2/24 14:20
     */
    public static BaseApiResponse buildPVENodeVmListApiResponse(JSON json) {
        String data = json.getByPath(PVE_BASE_RESP, String.class);
        if (StringUtils.isBlank(data)) {
            return new PVENodeVmListApiResponse(new ArrayList<>());
        }

        JSONArray array = JSONUtil.parseArray(data);

        List<PveVmInfo> pveVmInfoList = array.stream().map(o -> {
            JSONObject jsonObject = JSONUtil.parseObj(o);
            String status = jsonObject.getByPath(VM_LIST_STATUS, String.class);
            Long vmId = jsonObject.getByPath(VM_LIST_VMID, Long.class);
            String name = jsonObject.getByPath(VM_LIST_NAME, String.class);
            String lock = jsonObject.getByPath(VM_LIST_LOCK, String.class);
            Integer cpus = jsonObject.getByPath(VM_LIST_CPUS, Integer.class);
            Double maxDisk = jsonObject.getByPath(VM_LIST_MAX_DISK, Double.class);
            Double maxMem = jsonObject.getByPath(VM_LIST_MAXMEM, Double.class);
            Double diskRead = jsonObject.getByPath(VM_LIST_DISK_READ, Double.class);
            Double diskWrite = jsonObject.getByPath(VM_LIST_DISK_WRITE, Double.class);
            Double netIn = jsonObject.getByPath(VM_LIST_NET_IN, Double.class);
            Double netOut = jsonObject.getByPath(VM_LIST_NET_OUT, Double.class);
            Long pid = jsonObject.getByPath(VM_LIST_QEMU_PID, Long.class);
            String qmpStatus = jsonObject.getByPath(VM_LIST_QMP_STATUS, String.class);
            String runningMachine = jsonObject.getByPath(VM_LIST_RUNNING_MACHINE, String.class);
            String runningQemu = jsonObject.getByPath(VM_LIST_RUNNING_QEMU, String.class);
            String tags = jsonObject.getByPath(VM_LIST_TAGS, String.class);
            Boolean template = jsonObject.getByPath(VM_LIST_TEMPLATE, Boolean.class) != null && jsonObject.getByPath(VM_LIST_TEMPLATE, Boolean.class);
            Integer uptime = jsonObject.getByPath(VM_LIST_UPTIME, Integer.class);

            return PveVmInfo.builder()
                    .status(status)
                    .vmId(vmId)
                    .name(name)
                    .lock(lock)
                    .cpus(cpus)
                    .maxDisk(maxDisk)
                    .maxMem(maxMem)
                    .diskRead(diskRead)
                    .diskWrite(diskWrite)
                    .netIn(netIn)
                    .netOut(netOut)
                    .pid(pid)
                    .qmpStatus(qmpStatus)
                    .runningMachine(runningMachine)
                    .runningQemu(runningQemu)
                    .tags(tags)
                    .template(template)
                    .uptime(uptime)
                    .build();

        }).collect(Collectors.toList());
        return new PVENodeVmListApiResponse(pveVmInfoList);
    }

    /**
     * 构建json
     *
     * @param request {@link PVECreateOrRestoreVmApiRequest}
     * @return {@link JSONObject}
     * @author wangjiaxing
     * @date 2025/2/25 11:55
     */
    public static JSONObject buildJSONObject(PVECreateOrRestoreVmApiRequest request) {
        /**
         * 注意 以下api的参数,如果没填的话,一定传null
         * 例如boolean的值,false时传了false,就会导致最后的jsonBody中的结构包含很多多余的键值对,这就会导致PVE认为你有缺失的参数
         * 例如缺失force,archive
         */
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set(VM_ID, request.getVmId());
        jsonObject.set(ACPI, request.getAcpi() != null && request.getAcpi() ? 1 : null);
        jsonObject.set(AFFINITY, request.getAffinity());
        jsonObject.set(AGENT, request.getAgent());
        jsonObject.set(AMD_SEV, request.getAmdSev());
        jsonObject.set(ARCH, request.getArch());
        jsonObject.set(ARCHIVE, request.getArchive());
        jsonObject.set(ARGS, request.getArgs());
        jsonObject.set(AUDIO0, request.getAudio0());
        jsonObject.set(AUTO_START, request.getAutoStart());
        jsonObject.set(BALLOON, request.getBalloon());
        jsonObject.set(BIOS, request.getBios());
        jsonObject.set(BOOT, request.getBoot());
        jsonObject.set(BOOK_DISK, request.getBootDisk());
        jsonObject.set(BW_LIMIT, request.getBwLimit());
        jsonObject.set(CD_ROM, request.getCdrom());
        jsonObject.set(CI_CUSTOM, request.getCiCustom());
        jsonObject.set(CI_PASSWORD, request.getCiPassword());
        jsonObject.set(CI_TYPE, request.getCiType());
        jsonObject.set(CI_UPGRADE, request.getCiUpgrade() != null && request.getCiUpgrade() ? 1 : null);
        jsonObject.set(CI_USER, request.getCiUser());
        jsonObject.set(CORES, request.getCores());
        jsonObject.set(CPU, request.getCpu());
        jsonObject.set(CPU_LIMIT, request.getCpuLimits());
        jsonObject.set(CPU_UNITS, request.getCpuUnits());
        jsonObject.set(DESCRIPTION, request.getDescription());
        jsonObject.set(EFI_DISK_0, request.getEfiDisk0());
        jsonObject.set(FORCE, request.getForce() != null && request.getForce() ? 1 : null);
        jsonObject.set(FREEZE, request.getFreeze() != null && request.getFreeze() ? 1 : null);
        jsonObject.set(HOOK_SCRIPT, request.getHookScript());
        jsonObject.set(HOT_PLUG, request.getHotPlug());
        jsonObject.set(HUGE_PAGES, request.getHugePages());
        jsonObject.set(IMPORT_WORKING_STORAGE, request.getImportWorkingStorage());
        jsonObject.set(IVSH_MEM, request.getIvshmem());
        jsonObject.set(KEEP_HUGE_PAGES, request.getKeepHugePages());
        jsonObject.set(KEY_BOARD, request.getKeyBoard());
        jsonObject.set(KVM, request.getKvm());
        jsonObject.set(LIVE_RESTORE, request.getLiveRestore());
        jsonObject.set(LOCAL_TIME, request.getLocalTime());
        jsonObject.set(LOCK, request.getLock());
        jsonObject.set(MACHINE, request.getMachine());
        jsonObject.set(MEMORY, request.getMemory());
        jsonObject.set(MIGRATE_DOWNTIME, request.getMigrateDowntime());
        jsonObject.set(MIGRATE_SPEED, request.getMigrateSpeed());
        jsonObject.set(NAME, request.getName());
        jsonObject.set(NAME_SERVER, request.getNameserver());
        jsonObject.set(ON_BOOT, request.getOnBoot() != null && request.getOnBoot() ? 1 : null);
        jsonObject.set(OS_TYPE, request.getOsType());
        jsonObject.set(POOL, request.getPool());
        jsonObject.set(PROTECTION, request.getProtection());
        jsonObject.set(REBOOT, request.getReboot());
        jsonObject.set(RNG0, request.getRng0());
        jsonObject.set(SCSI_HW, request.getScsiHw());
        jsonObject.set(SEARCH_DOMAIN, request.getSearchDomain());
        jsonObject.set(SHARES, request.getShares());
        jsonObject.set(SM_BIOS1, request.getSmBios1());
        jsonObject.set(SMP, request.getSmp());
        jsonObject.set(SOCKETS, request.getSockets());
        jsonObject.set(SPICE_ENHANCEMENTS, request.getSpiceEnhancements());
        jsonObject.set(SSH_KEYS, request.getSshKeys());
        jsonObject.set(START, request.getStart());
        jsonObject.set(START_UP, request.getStartup());
        jsonObject.set(START_DATE, request.getStartDate());
        jsonObject.set(STORAGE, request.getStorage());
        jsonObject.set(TABLET, request.getTablet());
        jsonObject.set(TAGS, request.getTags());
        jsonObject.set(TDF, request.getTdf());
        jsonObject.set(TEMPLATE, request.getTemplate());
        jsonObject.set(TPM_STATE0, request.getTpmState0());
        jsonObject.set(UNIQUE, request.getUnique());
        jsonObject.set(V_CPUS, request.getVCpus());
        jsonObject.set(VGA, request.getVga());
        jsonObject.set(VM_GEN_ID, request.getVmGenId());
        jsonObject.set(VM_STATE_STORAGE, request.getVmStateStorage());
        jsonObject.set(WATCH_DOG, request.getWatchDog());
        if (!CollectionUtils.isEmpty(request.getHostPci())) {
            for (int i = 0; i < request.getHostPci().size(); i++) {
                jsonObject.set(HOSTPCI + i, request.getHostPci().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getIde())) {
            for (int i = 0; i < request.getIde().size(); i++) {
                jsonObject.set(IDE + i, request.getIde().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getIpConfig())) {
            for (int i = 0; i < request.getIpConfig().size(); i++) {
                jsonObject.set(IP_CONFIG + i, request.getIpConfig().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getNet())) {
            for (int i = 0; i < request.getNet().size(); i++) {
                jsonObject.set(NET + i, request.getNet().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getNuma())) {
            for (int i = 0; i < request.getNuma().size(); i++) {
                jsonObject.set(NUMA + i, request.getNuma().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getParallel())) {
            for (int i = 1; i < request.getParallel().size(); i++) {
                jsonObject.set(PARALLEL + i, request.getParallel().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getSata())) {
            for (int i = 0; i < request.getSata().size(); i++) {
                jsonObject.set(SATA + i, request.getSata().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getScsi())) {
            for (int i = 0; i < request.getScsi().size(); i++) {
                jsonObject.set(SCSI + i, request.getScsi().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getSerial())) {
            for (int i = 0; i < request.getSerial().size(); i++) {
                jsonObject.set(SERIAL + i, request.getSerial().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getUnused())) {
            for (int i = 0; i < request.getUnused().size(); i++) {
                jsonObject.set(UNUSED + i, request.getUnused().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getUsb())) {
            for (int i = 0; i < request.getUsb().size(); i++) {
                jsonObject.set(USB + i, request.getUsb().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getVirtio())) {
            for (int i = 0; i < request.getVirtio().size(); i++) {
                jsonObject.set(VIRTIO + i, request.getVirtio().get(i));
            }
        }
        return jsonObject;
    }

    /**
     * 构建json
     *
     * @param request {@link PVEPostVmConfigApiRequest}
     * @return {@link JSONObject}
     * @author wangjiaxing
     * @date 2025/2/25 11:55
     */
    public static JSONObject buildJSONObject(PVEPostVmConfigApiRequest request) {
        /**
         * 注意 以下api的参数,如果没填的话,一定传null
         * 例如boolean的值,false时传了false,就会导致最后的jsonBody中的结构包含很多多余的键值对,这就会导致PVE认为你有缺失的参数
         * 例如缺失force,archive
         */
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set(VM_ID, request.getVmId());
        jsonObject.set(ACPI, request.getAcpi() != null && request.getAcpi() ? 1 : null);
        jsonObject.set(AFFINITY, request.getAffinity());
        jsonObject.set(AGENT, request.getAgent());
        jsonObject.set(AMD_SEV, request.getAmdSev());
        jsonObject.set(ARCH, request.getArch());
        jsonObject.set(ARCHIVE, request.getArchive());
        jsonObject.set(ARGS, request.getArgs());
        jsonObject.set(AUDIO0, request.getAudio0());
        jsonObject.set(AUTO_START, request.getAutoStart());
        jsonObject.set(BALLOON, request.getBalloon());
        jsonObject.set(BIOS, request.getBios());
        jsonObject.set(BOOT, request.getBoot());
        jsonObject.set(BOOK_DISK, request.getBootDisk());
        jsonObject.set(BW_LIMIT, request.getBwLimit());
        jsonObject.set(CD_ROM, request.getCdrom());
        jsonObject.set(CI_CUSTOM, request.getCiCustom());
        jsonObject.set(CI_PASSWORD, request.getCiPassword());
        jsonObject.set(CI_TYPE, request.getCiType());
        jsonObject.set(CI_UPGRADE, request.getCiUpgrade() != null && request.getCiUpgrade() ? 1 : null);
        jsonObject.set(CI_USER, request.getCiUser());
        jsonObject.set(CORES, request.getCores());
        jsonObject.set(CPU, request.getCpu());
        jsonObject.set(CPU_LIMIT, request.getCpuLimits());
        jsonObject.set(CPU_UNITS, request.getCpuUnits());
        jsonObject.set(DESCRIPTION, request.getDescription());
        jsonObject.set(EFI_DISK_0, request.getEfiDisk0());
        jsonObject.set(FORCE, request.getForce() != null && request.getForce() ? 1 : null);
        jsonObject.set(FREEZE, request.getFreeze() != null && request.getFreeze() ? 1 : null);
        jsonObject.set(HOOK_SCRIPT, request.getHookScript());
        jsonObject.set(HOT_PLUG, request.getHotPlug());
        jsonObject.set(HUGE_PAGES, request.getHugePages());
        jsonObject.set(IMPORT_WORKING_STORAGE, request.getImportWorkingStorage());
        jsonObject.set(IVSH_MEM, request.getIvshmem());
        jsonObject.set(KEEP_HUGE_PAGES, request.getKeepHugePages());
        jsonObject.set(KEY_BOARD, request.getKeyBoard());
        jsonObject.set(KVM, request.getKvm());
        jsonObject.set(LIVE_RESTORE, request.getLiveRestore());
        jsonObject.set(LOCAL_TIME, request.getLocalTime());
        jsonObject.set(LOCK, request.getLock());
        jsonObject.set(MACHINE, request.getMachine());
        jsonObject.set(MEMORY, request.getMemory());
        jsonObject.set(MIGRATE_DOWNTIME, request.getMigrateDowntime());
        jsonObject.set(MIGRATE_SPEED, request.getMigrateSpeed());
        jsonObject.set(NAME, request.getName());
        jsonObject.set(NAME_SERVER, request.getNameserver());
        jsonObject.set(ON_BOOT, request.getOnBoot() != null && request.getOnBoot() ? 1 : null);
        jsonObject.set(OS_TYPE, request.getOsType());
        jsonObject.set(POOL, request.getPool());
        jsonObject.set(PROTECTION, request.getProtection());
        jsonObject.set(REBOOT, request.getReboot());
        jsonObject.set(RNG0, request.getRng0());
        jsonObject.set(SCSI_HW, request.getScsiHw());
        jsonObject.set(SEARCH_DOMAIN, request.getSearchDomain());
        jsonObject.set(SHARES, request.getShares());
        jsonObject.set(SM_BIOS1, request.getSmBios1());
        jsonObject.set(SMP, request.getSmp());
        jsonObject.set(SOCKETS, request.getSockets());
        jsonObject.set(SPICE_ENHANCEMENTS, request.getSpiceEnhancements());
        jsonObject.set(SSH_KEYS, request.getSshKeys());
        jsonObject.set(START, request.getStart());
        jsonObject.set(START_UP, request.getStartup());
        jsonObject.set(START_DATE, request.getStartDate());
        jsonObject.set(STORAGE, request.getStorage());
        jsonObject.set(TABLET, request.getTablet());
        jsonObject.set(TAGS, request.getTags());
        jsonObject.set(TDF, request.getTdf());
        jsonObject.set(TEMPLATE, request.getTemplate());
        jsonObject.set(TPM_STATE0, request.getTpmState0());
        jsonObject.set(UNIQUE, request.getUnique());
        jsonObject.set(V_CPUS, request.getVCpus());
        jsonObject.set(VGA, request.getVga());
        jsonObject.set(VM_GEN_ID, request.getVmGenId());
        jsonObject.set(VM_STATE_STORAGE, request.getVmStateStorage());
        jsonObject.set(WATCH_DOG, request.getWatchDog());
        if (!CollectionUtils.isEmpty(request.getHostPci())) {
            for (int i = 0; i < request.getHostPci().size(); i++) {
                jsonObject.set(HOSTPCI + i, request.getHostPci().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getIde())) {
            for (int i = 0; i < request.getIde().size(); i++) {
                jsonObject.set(IDE + i, request.getIde().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getIpConfig())) {
            for (int i = 0; i < request.getIpConfig().size(); i++) {
                jsonObject.set(IP_CONFIG + i, request.getIpConfig().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getNet())) {
            for (int i = 0; i < request.getNet().size(); i++) {
                jsonObject.set(NET + i, request.getNet().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getNuma())) {
            for (int i = 0; i < request.getNuma().size(); i++) {
                jsonObject.set(NUMA + i, request.getNuma().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getParallel())) {
            for (int i = 1; i < request.getParallel().size(); i++) {
                jsonObject.set(PARALLEL + i, request.getParallel().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getSata())) {
            for (int i = 0; i < request.getSata().size(); i++) {
                jsonObject.set(SATA + i, request.getSata().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getScsi())) {
            for (int i = 0; i < request.getScsi().size(); i++) {
                jsonObject.set(SCSI + i, request.getScsi().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getSerial())) {
            for (int i = 0; i < request.getSerial().size(); i++) {
                jsonObject.set(SERIAL + i, request.getSerial().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getUnused())) {
            for (int i = 0; i < request.getUnused().size(); i++) {
                jsonObject.set(UNUSED + i, request.getUnused().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getUsb())) {
            for (int i = 0; i < request.getUsb().size(); i++) {
                jsonObject.set(USB + i, request.getUsb().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getVirtio())) {
            for (int i = 0; i < request.getVirtio().size(); i++) {
                jsonObject.set(VIRTIO + i, request.getVirtio().get(i));
            }
        }
        return jsonObject;
    }

    /**
     * 构建json
     *
     * @param request {@link PVEPutVmConfigApiRequest}
     * @return {@link JSONObject}
     * @author wangjiaxing
     * @date 2025/2/25 11:55
     */
    public static JSONObject buildJSONObject(PVEPutVmConfigApiRequest request) {
        /**
         * 注意 以下api的参数,如果没填的话,一定传null
         * 例如boolean的值,false时传了false,就会导致最后的jsonBody中的结构包含很多多余的键值对,这就会导致PVE认为你有缺失的参数
         * 例如缺失force,archive
         */
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set(VM_ID, request.getVmId());
        jsonObject.set(ACPI, request.getAcpi() != null && request.getAcpi() ? 1 : null);
        jsonObject.set(AFFINITY, request.getAffinity());
        jsonObject.set(AGENT, request.getAgent());
        jsonObject.set(AMD_SEV, request.getAmdSev());
        jsonObject.set(ARCH, request.getArch());
        jsonObject.set(ARCHIVE, request.getArchive());
        jsonObject.set(ARGS, request.getArgs());
        jsonObject.set(AUDIO0, request.getAudio0());
        jsonObject.set(AUTO_START, request.getAutoStart());
        jsonObject.set(BALLOON, request.getBalloon());
        jsonObject.set(BIOS, request.getBios());
        jsonObject.set(BOOT, request.getBoot());
        jsonObject.set(BOOK_DISK, request.getBootDisk());
        jsonObject.set(BW_LIMIT, request.getBwLimit());
        jsonObject.set(CD_ROM, request.getCdrom());
        jsonObject.set(CI_CUSTOM, request.getCiCustom());
        jsonObject.set(CI_PASSWORD, request.getCiPassword());
        jsonObject.set(CI_TYPE, request.getCiType());
        jsonObject.set(CI_UPGRADE, request.getCiUpgrade() != null && request.getCiUpgrade() ? 1 : null);
        jsonObject.set(CI_USER, request.getCiUser());
        jsonObject.set(CORES, request.getCores());
        jsonObject.set(CPU, request.getCpu());
        jsonObject.set(CPU_LIMIT, request.getCpuLimits());
        jsonObject.set(CPU_UNITS, request.getCpuUnits());
        jsonObject.set(DESCRIPTION, request.getDescription());
        jsonObject.set(EFI_DISK_0, request.getEfiDisk0());
        jsonObject.set(FORCE, request.getForce() != null && request.getForce() ? 1 : null);
        jsonObject.set(FREEZE, request.getFreeze() != null && request.getFreeze() ? 1 : null);
        jsonObject.set(HOOK_SCRIPT, request.getHookScript());
        jsonObject.set(HOT_PLUG, request.getHotPlug());
        jsonObject.set(HUGE_PAGES, request.getHugePages());
        jsonObject.set(IMPORT_WORKING_STORAGE, request.getImportWorkingStorage());
        jsonObject.set(IVSH_MEM, request.getIvshmem());
        jsonObject.set(KEEP_HUGE_PAGES, request.getKeepHugePages());
        jsonObject.set(KEY_BOARD, request.getKeyBoard());
        jsonObject.set(KVM, request.getKvm());
        jsonObject.set(LIVE_RESTORE, request.getLiveRestore());
        jsonObject.set(LOCAL_TIME, request.getLocalTime());
        jsonObject.set(LOCK, request.getLock());
        jsonObject.set(MACHINE, request.getMachine());
        jsonObject.set(MEMORY, request.getMemory());
        jsonObject.set(MIGRATE_DOWNTIME, request.getMigrateDowntime());
        jsonObject.set(MIGRATE_SPEED, request.getMigrateSpeed());
        jsonObject.set(NAME, request.getName());
        jsonObject.set(NAME_SERVER, request.getNameserver());
        jsonObject.set(ON_BOOT, request.getOnBoot() != null && request.getOnBoot() ? 1 : null);
        jsonObject.set(OS_TYPE, request.getOsType());
        jsonObject.set(POOL, request.getPool());
        jsonObject.set(PROTECTION, request.getProtection());
        jsonObject.set(REBOOT, request.getReboot());
        jsonObject.set(RNG0, request.getRng0());
        jsonObject.set(SCSI_HW, request.getScsiHw());
        jsonObject.set(SEARCH_DOMAIN, request.getSearchDomain());
        jsonObject.set(SHARES, request.getShares());
        jsonObject.set(SM_BIOS1, request.getSmBios1());
        jsonObject.set(SMP, request.getSmp());
        jsonObject.set(SOCKETS, request.getSockets());
        jsonObject.set(SPICE_ENHANCEMENTS, request.getSpiceEnhancements());
        jsonObject.set(SSH_KEYS, request.getSshKeys());
        jsonObject.set(START, request.getStart());
        jsonObject.set(START_UP, request.getStartup());
        jsonObject.set(START_DATE, request.getStartDate());
        jsonObject.set(STORAGE, request.getStorage());
        jsonObject.set(TABLET, request.getTablet());
        jsonObject.set(TAGS, request.getTags());
        jsonObject.set(TDF, request.getTdf());
        jsonObject.set(TEMPLATE, request.getTemplate());
        jsonObject.set(TPM_STATE0, request.getTpmState0());
        jsonObject.set(UNIQUE, request.getUnique());
        jsonObject.set(V_CPUS, request.getVCpus());
        jsonObject.set(VGA, request.getVga());
        jsonObject.set(VM_GEN_ID, request.getVmGenId());
        jsonObject.set(VM_STATE_STORAGE, request.getVmStateStorage());
        jsonObject.set(WATCH_DOG, request.getWatchDog());
        if (!CollectionUtils.isEmpty(request.getHostPci())) {
            for (int i = 0; i < request.getHostPci().size(); i++) {
                jsonObject.set(HOSTPCI + i, request.getHostPci().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getIde())) {
            for (int i = 0; i < request.getIde().size(); i++) {
                jsonObject.set(IDE + i, request.getIde().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getIpConfig())) {
            for (int i = 0; i < request.getIpConfig().size(); i++) {
                jsonObject.set(IP_CONFIG + i, request.getIpConfig().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getNet())) {
            for (int i = 0; i < request.getNet().size(); i++) {
                jsonObject.set(NET + i, request.getNet().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getNuma())) {
            for (int i = 0; i < request.getNuma().size(); i++) {
                jsonObject.set(NUMA + i, request.getNuma().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getParallel())) {
            for (int i = 1; i < request.getParallel().size(); i++) {
                jsonObject.set(PARALLEL + i, request.getParallel().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getSata())) {
            for (int i = 0; i < request.getSata().size(); i++) {
                jsonObject.set(SATA + i, request.getSata().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getScsi())) {
            for (int i = 0; i < request.getScsi().size(); i++) {
                jsonObject.set(SCSI + i, request.getScsi().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getSerial())) {
            for (int i = 0; i < request.getSerial().size(); i++) {
                jsonObject.set(SERIAL + i, request.getSerial().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getUnused())) {
            for (int i = 0; i < request.getUnused().size(); i++) {
                jsonObject.set(UNUSED + i, request.getUnused().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getUsb())) {
            for (int i = 0; i < request.getUsb().size(); i++) {
                jsonObject.set(USB + i, request.getUsb().get(i));
            }
        }
        if (!CollectionUtils.isEmpty(request.getVirtio())) {
            for (int i = 0; i < request.getVirtio().size(); i++) {
                jsonObject.set(VIRTIO + i, request.getVirtio().get(i));
            }
        }
        return jsonObject;
    }

    /**
     * 构建虚拟机配置信息回显
     *
     * @author wangjiaxing
     * @date 2025/2/26 10:27
     */
    public static BaseApiResponse buildPVEGetVmConfigApiResponse(JSON data) {
        PVEGetVmConfigApiResponse apiResponse = new PVEGetVmConfigApiResponse();
        apiResponse.setName(data.getByPath(NAME, String.class));
        apiResponse.setVmGenId(data.getByPath(VM_GEN_ID, String.class));
        apiResponse.setOsType(data.getByPath(OS_TYPE, String.class));
        apiResponse.setCpu(data.getByPath(CPU, String.class));
        apiResponse.setMemory(data.getByPath(MEMORY, Double.class));
        apiResponse.setAgent(data.getByPath(AGENT, Boolean.class));
        apiResponse.setScsiHw(data.getByPath(SCSI_HW, String.class));
        apiResponse.setBoot(data.getByPath(BOOT, String.class));
        apiResponse.setSmBios1(data.getByPath(SM_BIOS1, String.class));
        apiResponse.setSockets(data.getByPath(SOCKETS, Integer.class));
        apiResponse.setCores(data.getByPath(CORES, Integer.class));
        apiResponse.setMeta(data.getByPath(META, String.class));
        apiResponse.setVga(data.getByPath(VGA, String.class));
        apiResponse.setDigest(data.getByPath(DIGEST, String.class));
        apiResponse.setNuma(data.getByPath(NUMA, Integer.class));
        apiResponse.setCiUser(data.getByPath(CI_USER, String.class));
        apiResponse.setCiPassword(data.getByPath(CI_PASSWORD, String.class));
        List<String> ide = new ArrayList<>();
        List<String> ip = new ArrayList<>();
        List<String> net = new ArrayList<>();
        List<String> scsi = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String ideI = data.getByPath(IDE + i, String.class);
            if (StringUtils.isNotBlank(ideI)) {
                ide.add(IDE + i + EQUAL_MARK + ideI);
            }

            String ipI = data.getByPath(IP_CONFIG + i, String.class);
            if (StringUtils.isNotBlank(ipI)) {
                ip.add(IP + i + EQUAL_MARK + ipI);
            }

            String netI = data.getByPath(NET + i, String.class);
            if (StringUtils.isNotBlank(netI)) {
                net.add(NET + i + EQUAL_MARK + netI);
            }

            String scsiI = data.getByPath(SCSI + i, String.class);
            if (StringUtils.isNotBlank(scsiI)) {
                scsi.add(SCSI + i + EQUAL_MARK + scsiI);
            }
        }
        apiResponse.setIde(ide);
        apiResponse.setIpConfig(ip);
        apiResponse.setNet(net);
        apiResponse.setScsi(scsi);
        return apiResponse;
    }

    /**
     * 构建虚拟机网络列表信息回显
     *
     * @author wangjiaxing
     * @date 2025/2/26 15:31
     */
    public static BaseApiResponse buildPVENetworkListApiResponse(JSON json) {
        PVENodeNetworkListApiResponse response = new PVENodeNetworkListApiResponse();
        List<Network> networks = new ArrayList<>();

        String data = json.getByPath(PVE_BASE_RESP, String.class);

        JSONArray array = JSONUtil.parseArray(data);

        for (Object o : array) {
            JSONObject jsonObject = JSONUtil.parseObj(o);
            String type = jsonObject.getByPath(NETWORK_LIST_TYPE, String.class);

            NetworkType networkType = NetworkType.fromType(type);
            switch (Objects.requireNonNull(networkType)) {
                case BRIDGE -> {
                    BridgeNetwork network = new BridgeNetwork();
                    packageNetworkCommonInfo(network, jsonObject);
                    network.setGateway(jsonObject.getByPath(NETWORK_LIST_BRIDGE_GATEWAY, String.class));
                    network.setAddress(jsonObject.getByPath(NETWORK_LIST_BRIDGE_ADDRESS, String.class));
                    network.setNetmask(jsonObject.getByPath(NETWORK_LIST_BRIDGE_NETMASK, String.class));
                    network.setCidr(jsonObject.getByPath(NETWORK_LIST_BRIDGE_CIDR, String.class));
                    network.setBridgeFd(jsonObject.getByPath(NETWORK_LIST_BRIDGE_FD, String.class));
                    network.setBridgePorts(jsonObject.getByPath(NETWORK_LIST_BRIDGE_PORTS, String.class));
                    network.setBridgeStp(jsonObject.getByPath(NETWORK_LIST_BRIDGE_STP, String.class));
                    networks.add(network);
                }
                case VLAN -> {
                    VlanNetwork network = new VlanNetwork();
                    packageNetworkCommonInfo(network, jsonObject);
                    network.setVlanRawDevice(jsonObject.getByPath(NETWORK_LIST_VLAN_RAW_DEVICE, String.class));
                    networks.add(network);
                }
                case ETH -> {
                    EthNetwork network = new EthNetwork();
                    packageNetworkCommonInfo(network, jsonObject);
                    networks.add(network);
                }
                case BOND -> {
                }
                case ALIAS -> {
                }
                case OVS_BOND -> {
                }
                case OVS_PORT -> {
                }
                case ANY_BRIDGE -> {
                }
                case OVS_BRIDGE -> {
                }
                case OVS_INT_PORT -> {
                }
                case ANY_LOCAL_BRIDGE -> {
                }
                default -> throw new ServiceException("不支持的网络类型");
            }
        }
        response.setNetworks(networks);
        return response;
    }

    /**
     * 封装网络公共信息
     *
     * @author wangjiaxing
     * @date 2025/2/26 16:15
     */
    private static void packageNetworkCommonInfo(Network network, JSONObject jsonObject) {
        network.setType(jsonObject.getByPath(NETWORK_LIST_TYPE, String.class));
        network.setIface(jsonObject.getByPath(NETWORK_LIST_IFACE, String.class));
        network.setExists(Objects.equals(jsonObject.getByPath(NETWORK_LIST_EXISTS, Integer.class), 1));
        network.setMethod(jsonObject.getByPath(NETWORK_LIST_METHOD, String.class));
        network.setMethod6(jsonObject.getByPath(NETWORK_LIST_METHOD6, String.class));
        network.setAutostart(Objects.equals(jsonObject.getByPath(NETWORK_LIST_AUTOSTART, Integer.class), 1));
        network.setActive(Objects.equals(jsonObject.getByPath(NETWORK_LIST_ACTIVE, Integer.class), 1));
        network.setPriority(jsonObject.getByPath(NETWORK_LIST_PRIORITY, Integer.class));

        String families = jsonObject.getByPath(NETWORK_LIST_FAMILIES, String.class);
        JSONArray array = JSONUtil.parseArray(families);
        List<String> familyList = array.stream().map(Object::toString).toList();
        network.setFamilies(familyList);
    }

    /**
     * 构建json
     *
     * @param request {@link PVENodeCreateNetworkApiRequest}
     * @return {@link JSONObject}
     * @author wangjiaxing
     * @date 2025/2/25 11:55
     */
    public static JSONObject buildJSONObject(PVENodeCreateNetworkApiRequest request) {
        /**
         * 注意 以下api的参数,如果没填的话,一定传null
         * 例如boolean的值,false时传了false,就会导致最后的jsonBody中的结构包含很多多余的键值对,这就会导致PVE认为你有缺失的参数
         */
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set(IFACE, request.getIface());
        jsonObject.set(TYPE, request.getType());
        jsonObject.set(ADDRESS, request.getAddress());
        jsonObject.set(ADDRESS6, request.getAddress6());
        jsonObject.set(AUTO_START, request.getAutoStart() != null && request.getAutoStart() ? 1 : null);
        jsonObject.set(CIDR, request.getCidr());
        jsonObject.set(CIDR6, request.getCidr6());
        jsonObject.set(COMMENTS, request.getComments());
        jsonObject.set(COMMENTS6, request.getComments6());
        jsonObject.set(GATEWAY, request.getGateway());
        jsonObject.set(GATEWAY6, request.getGateway6());
        jsonObject.set(NETMASK, request.getNetmask());
        jsonObject.set(NETMASK6, request.getNetmask6());
        jsonObject.set(MTU, request.getMtu());
        jsonObject.set(BRIDGE_PORTS, request.getBridgePorts());
        jsonObject.set(BRIDGE_VIDS, request.getBridgeVids());
        jsonObject.set(BRIDGE_VLAN_AWARE, request.getBridgeVlanAware() != null && request.getBridgeVlanAware() ? 1 : null);
        jsonObject.set(VLAN_ID, request.getVlanId());
        jsonObject.set(VLAN_RAW_DEVICE, request.getVlanRawDevice());
        jsonObject.set(OVS_BONDS, request.getOvsBonds());
        jsonObject.set(OVS_BRIDGE, request.getOvsBridge());
        jsonObject.set(OVS_OPTIONS, request.getOvsOptions());
        jsonObject.set(OVS_PORTS, request.getOvsPorts());
        jsonObject.set(OVS_TAGS, request.getOvsTags());
        jsonObject.set(BOND_PRIMARY, request.getBondPrimary());
        jsonObject.set(BOND_MODE, request.getBondMode());
        jsonObject.set(BOND_XMIT_HASH_POLICY, request.getBondXmitHashPolicy());
        jsonObject.set(SLAVES, request.getSlaves());
        return jsonObject;
    }

    /**
     * 构建json
     *
     * @param request {@link PVENodePutNetworkConfigApiRequest}
     * @return {@link JSONObject}
     * @author wangjiaxing
     * @date 2025/2/25 11:55
     */
    public static JSONObject buildJSONObject(PVENodePutNetworkConfigApiRequest request) {
        /**
         * 注意 以下api的参数,如果没填的话,一定传null
         * 例如boolean的值,false时传了false,就会导致最后的jsonBody中的结构包含很多多余的键值对,这就会导致PVE认为你有缺失的参数
         */
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set(IFACE, request.getIface());
        jsonObject.set(TYPE, request.getType());
        jsonObject.set(ADDRESS, request.getAddress());
        jsonObject.set(ADDRESS6, request.getAddress6());
        jsonObject.set(AUTO_START, request.getAutoStart() != null && request.getAutoStart() ? 1 : null);
        jsonObject.set(CIDR, request.getCidr());
        jsonObject.set(CIDR6, request.getCidr6());
        jsonObject.set(COMMENTS, request.getComments());
        jsonObject.set(COMMENTS6, request.getComments6());
        jsonObject.set(GATEWAY, request.getGateway());
        jsonObject.set(GATEWAY6, request.getGateway6());
        jsonObject.set(NETMASK, request.getNetmask());
        jsonObject.set(NETMASK6, request.getNetmask6());
        jsonObject.set(MTU, request.getMtu());
        jsonObject.set(BRIDGE_PORTS, request.getBridgePorts());
        jsonObject.set(BRIDGE_VIDS, request.getBridgeVids());
        jsonObject.set(BRIDGE_VLAN_AWARE, request.getBridgeVlanAware() != null && request.getBridgeVlanAware() ? 1 : null);
        jsonObject.set(VLAN_ID, request.getVlanId());
        jsonObject.set(VLAN_RAW_DEVICE, request.getVlanRawDevice());
        jsonObject.set(OVS_BONDS, request.getOvsBonds());
        jsonObject.set(OVS_BRIDGE, request.getOvsBridge());
        jsonObject.set(OVS_OPTIONS, request.getOvsOptions());
        jsonObject.set(OVS_PORTS, request.getOvsPorts());
        jsonObject.set(OVS_TAGS, request.getOvsTags());
        jsonObject.set(BOND_PRIMARY, request.getBondPrimary());
        jsonObject.set(BOND_MODE, request.getBondMode());
        jsonObject.set(BOND_XMIT_HASH_POLICY, request.getBondXmitHashPolicy());
        jsonObject.set(SLAVES, request.getSlaves());
        return jsonObject;
    }

    /**
     * 构建json
     *
     * @param json {@link JSON}
     * @return {@link BaseApiResponse}
     * @author wangjiaxing
     * @date 2025/2/26 19:24
     */
    public static BaseApiResponse buildPVEGetNetworkConfigApiResponse(JSON json) {
        String data = json.getByPath(PVE_BASE_RESP, String.class);
        JSONObject jsonObject = JSONUtil.parseObj(data);

        PVENodeGetNetworkConfigApiResponse response = new PVENodeGetNetworkConfigApiResponse();

        String type = jsonObject.getByPath(NETWORK_CONFIG_TYPE, String.class);

        NetworkType networkType = NetworkType.fromType(type);
        switch (Objects.requireNonNull(networkType)) {
            case BRIDGE -> {
                BridgeNetwork network = new BridgeNetwork();
                packageNetworkCommonInfo(network, jsonObject);
                network.setGateway(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_GATEWAY, String.class));
                network.setAddress(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_ADDRESS, String.class));
                network.setNetmask(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_NETMASK, String.class));
                network.setCidr(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_CIDR, String.class));
                network.setBridgeFd(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_FD, String.class));
                network.setBridgePorts(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_PORTS, String.class));
                network.setBridgeStp(jsonObject.getByPath(NETWORK_CONFIG_BRIDGE_STP, String.class));
                response.setNetwork(network);
            }
            case VLAN -> {
                VlanNetwork network = new VlanNetwork();
                packageNetworkCommonInfo(network, jsonObject);
                network.setVlanRawDevice(jsonObject.getByPath(NETWORK_LIST_VLAN_RAW_DEVICE, String.class));
                response.setNetwork(network);
            }
            case ETH -> {
                EthNetwork network = new EthNetwork();
                packageNetworkCommonInfo(network, jsonObject);
                response.setNetwork(network);
            }
            case ANY_LOCAL_BRIDGE -> {
            }
            case OVS_INT_PORT -> {
            }
            case OVS_BRIDGE -> {
            }
            case ANY_BRIDGE -> {
            }
            case OVS_PORT -> {
            }
            case OVS_BOND -> {
            }
            case ALIAS -> {
            }
            case BOND -> {
            }
            default -> throw new ServiceException("不支持的网络类型");
        }
        return response;
    }

    /**
     * 构建PVEVncProxyApiResponse
     *
     * @param json {@link JSON}
     * @author wangjiaxing
     * @date 2025/2/27 12:22
     */
    public static BaseApiResponse buildPVEVncProxyApiResponse(JSON json) {
        PVEVncProxyApiResponse response = new PVEVncProxyApiResponse();
        String data = json.getByPath(PVE_BASE_RESP, String.class);
        JSONObject jsonObject = JSONUtil.parseObj(data);
        response.setPort(jsonObject.getByPath(PVE_VNC_PROXY_PORT, Integer.class));
        response.setUPid(jsonObject.getByPath(PVE_VNC_PROXY_UPID, String.class));
        response.setUser(jsonObject.getByPath(PVE_VNC_PROXY_USER, String.class));
        response.setPassword(jsonObject.getByPath(PVE_VNC_PROXY_PASSWORD, String.class));
        response.setVncTicket(jsonObject.getByPath(PVE_VNC_PROXY_TICKET, String.class));
        response.setCert(jsonObject.getByPath(PVE_VNC_PROXY_CERT, String.class));
        return response;
    }

    /**
     * 构建PVECreateVmSpiceProxyApiResponse
     *
     * @author wangjiaxing
     * @date 2025/2/27 15:29
     */
    public static BaseApiResponse buildPVECreateVmSpiceProxyApiResponse(JSON json) {
        String data = json.getByPath(PVE_BASE_RESP, String.class);
        JSONObject jsonObject = JSONUtil.parseObj(data);

        PVECreateVmSpiceProxyApiResponse response = new PVECreateVmSpiceProxyApiResponse();
        response.setHost(jsonObject.getByPath(PVE_VM_SPICE_HOST, String.class));
        response.setTitle(jsonObject.getByPath(PVE_VM_SPICE_TITLE, String.class));
        response.setHostSubject(jsonObject.getByPath(PVE_VM_SPICE_HOST_SUBJECT, String.class));
        response.setToggleFullScreen(jsonObject.getByPath(PVE_VM_SPICE_TOGGLE_FULL_SCREEN, String.class));
        response.setCa(jsonObject.getByPath(PVE_VM_SPICE_CA, String.class));
        response.setPassword(jsonObject.getByPath(PVE_VM_SPICE_PASSWORD, String.class));
        response.setProxy(jsonObject.getByPath(PVE_VM_SPICE_PROXY, String.class));
        response.setTlsPort(jsonObject.getByPath(PVE_VM_SPICE_TLS_PORT, Integer.class));
        response.setType(jsonObject.getByPath(PVE_VM_SPICE_TYPE, String.class));
        response.setSecureAttention(jsonObject.getByPath(PVE_VM_SPICE_SECURE_ATTENTION, String.class));
        response.setDeleteThisFile(jsonObject.getByPath(PVE_VM_SPICE_DELETE_THIS_FILE, Integer.class));
        response.setReleaseCursor(jsonObject.getByPath(PVE_VM_SPICE_RELEASE_CURSOR, String.class));
        return response;
    }
}
