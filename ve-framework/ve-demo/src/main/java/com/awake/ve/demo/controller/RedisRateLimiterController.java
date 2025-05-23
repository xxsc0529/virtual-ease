package com.awake.ve.demo.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.awake.ve.common.core.domain.R;
import com.awake.ve.common.rateLimiter.annotation.RateLimiter;
import com.awake.ve.common.rateLimiter.enums.LimitType;
import com.awake.ve.common.ssh.domain.SSHCommandResult;
import com.awake.ve.common.ssh.domain.dto.SSHCommandDTO;
import com.awake.ve.common.ssh.enums.ChannelType;
import com.awake.ve.common.ssh.utils.SSHUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * 测试分布式限流样例
 *
 * @author Lion Li
 */
@Slf4j
@RestController
@SaIgnore
@RequestMapping("/demo/rateLimiter")
public class RedisRateLimiterController {

    @GetMapping("/testSSH")
    public R<SSHCommandResult> testSSH() {
        ConcurrentLinkedQueue<String> commands = new ConcurrentLinkedQueue<>();
        commands.offer("cd /");
        commands.offer("ls -l");
        commands.offer("cd /opt");
        commands.offer("ls -l");
        commands.offer("pwd");
        commands.offer("cd 1panel/");
        commands.offer("ls -l");
        commands.offer("cd log/");
        commands.offer("cd ssl/");
        commands.offer("ls -l");
        commands.offer("cd ../");
        commands.offer("ls -l");
        SSHCommandResult commandResult = SSHUtils.sendCommand(SSHCommandDTO.createSSHCommandDTO(commands, ChannelType.EXEC));
        return R.ok(commandResult);
    }

    /**
     * 测试全局限流
     * 全局影响
     */
    @RateLimiter(count = 2, time = 10, message = "请稍后重试")
    @GetMapping("/test")
    public R<String> test(String value) {
        return R.ok("操作成功", value);
    }

    /**
     * 测试请求IP限流
     * 同一IP请求受影响
     */
    @RateLimiter(count = 2, time = 10, limitType = LimitType.IP)
    @GetMapping("/testip")
    public R<String> testip(String value) {
        return R.ok("操作成功", value);
    }

    /**
     * 测试集群实例限流
     * 启动两个后端服务互不影响
     */
    @RateLimiter(count = 2, time = 10, limitType = LimitType.CLUSTER)
    @GetMapping("/testcluster")
    public R<String> testcluster(String value) {
        return R.ok("操作成功", value);
    }

    /**
     * 测试请求IP限流(key基于参数获取)
     * 同一IP请求受影响
     * <p>
     * 简单变量获取 #变量 复杂表达式 #{#变量 != 1 ? 1 : 0}
     */
    @RateLimiter(count = 2, time = 10, limitType = LimitType.IP, key = "#value")
    @GetMapping("/testObj")
    public R<String> testObj(String value) {
        return R.ok("操作成功", value);
    }

}
