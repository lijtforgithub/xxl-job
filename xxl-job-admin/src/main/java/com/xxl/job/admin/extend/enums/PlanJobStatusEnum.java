package com.xxl.job.admin.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-09 15:32
 */
@Getter
@AllArgsConstructor
public enum PlanJobStatusEnum {

    CREATE(0, "创建"),
    SYNC_JOB(1, "生成任务"),
    RUNNING(2, "已执行过一次"),
    END(10, "已结束"),
    CANCEL(20, "取消");

    private final int value;
    private final String desc;


    public static boolean isEnd(Integer value) {
        return Objects.nonNull(value) && (value == END.value || value == CANCEL.value);
    }

    public static PlanJobStatusEnum match(int value) {
        for (PlanJobStatusEnum statusEnum : PlanJobStatusEnum.values()) {
            if (value == statusEnum.getValue()) {
                return statusEnum;
            }
        }

        return null;
    }

}
