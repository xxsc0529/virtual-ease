package com.awake.ve.common.tenant.manager;

import com.awake.ve.common.core.constant.GlobalConstants;
import com.awake.ve.common.core.utils.StringUtils;
import com.awake.ve.common.tenant.helper.TenantHelper;
import com.awake.ve.common.translation.manager.PlusSpringCacheManager;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

/**
 * 重写 cacheName 处理方法 支持多租户
 *
 * @author wangjiaxing
 * @date 2025/1/7 11:54
 */
@Slf4j
public class TenantSpringCacheManager extends PlusSpringCacheManager {

    public TenantSpringCacheManager() {
    }

    @Override
    public Cache getCache(String name) {
        if (InterceptorIgnoreHelper.willIgnoreTenantLine("")) {
            return super.getCache(name);
        }
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.getCache(name);
        }
        String tenantId = TenantHelper.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null");
        }
        if (StringUtils.startsWith(name, tenantId)) {
            // 如果存在则直接返回
            return super.getCache(name);
        }
        return super.getCache(tenantId + ":" + name);
    }
}
