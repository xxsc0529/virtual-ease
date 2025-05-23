package com.awake.ve.common.ssh.domain.dto;

import com.awake.ve.common.ssh.enums.ChannelType;
import lombok.Data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ssh命令参数dto
 *
 * @author wangjiaxing
 * @date 2025/2/20 9:00
 */
@Data
public class SSHCommandDTO {
    /**
     * 命令队列
     */
    private static Queue<String> commands;

    /**
     * 通道类型 {@link com.awake.ve.common.ssh.enums.ChannelType}
     */
    private static ChannelType channelType;

    // 构造器私有化
    private SSHCommandDTO() {
    }

    private SSHCommandDTO(Queue<String> queue, ChannelType type) {
        commands = queue;
        channelType = type;
    }

    public static SSHCommandDTO createSSHCommandDTO(Queue<String> queue, ChannelType channelType) {
        commands = new ConcurrentLinkedQueue<>();
        for (String command : queue) {
            commands.offer(command);
        }

        return new SSHCommandDTO(commands, channelType);
    }

    public Queue<String> getCommands() {
        return commands;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

}
