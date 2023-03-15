package com.xxl.job.extend.biz.enums;

import com.xxl.job.extend.biz.model.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-08 10:55
 */
@AllArgsConstructor
@Getter
public enum PlanEnum {

    ASSIGN(0, "指定日期", Collections.emptyList()),
    DAILY(1, "每日", Collections.emptyList()),
    WEEK(2, "每周", new ArrayList<Option>() {{
        add(new Option(1, "周一"));
        add(new Option(2, "周二"));
        add(new Option(3, "周三"));
        add(new Option(4, "周四"));
        add(new Option(5, "周五"));
        add(new Option(6, "周六"));
        add(new Option(7, "周日"));
    }}),
    MONTH(3, "每月", new ArrayList<Option>() {{
        add(new Option(1, "1号"));
        add(new Option(2, "2号"));
        add(new Option(3, "3号"));
        add(new Option(4, "4号"));
        add(new Option(5, "5号"));
        add(new Option(6, "6号"));
        add(new Option(7, "7号"));
        add(new Option(8, "8号"));
        add(new Option(9, "9号"));
        add(new Option(10, "10号"));
        add(new Option(11, "11号"));
        add(new Option(12, "12号"));
        add(new Option(13, "13号"));
        add(new Option(14, "14号"));
        add(new Option(15, "15号"));
        add(new Option(16, "16号"));
        add(new Option(17, "17号"));
        add(new Option(18, "18号"));
        add(new Option(19, "19号"));
        add(new Option(20, "20号"));
        add(new Option(21, "21号"));
        add(new Option(22, "22号"));
        add(new Option(23, "23号"));
        add(new Option(24, "24号"));
        add(new Option(25, "25号"));
        add(new Option(26, "26号"));
        add(new Option(27, "27号"));
        add(new Option(28, "28号"));
        add(new Option(29, "29号"));
        add(new Option(30, "30号"));
        add(new Option(31, "31号"));
        add(new Option(-1, "最后一天"));
    }}),
    SEASON(4, "每季度", new ArrayList<Option>() {{
        add(new Option(TWO_STAGE_OPTION + 1, "第一个月"));
        add(new Option(TWO_STAGE_OPTION + 2, "第二个月"));
        add(new Option(TWO_STAGE_OPTION + 3, "第三个月"));

        addAll(MONTH.options);
    }});

    private final int value;
    private final String desc;
    private final List<Option> options;

    public Option matchOption(int optionValue) {
        for (Option option : options) {
            if (optionValue == option.getValue()) {
                return option;
            }
        }

        return null;
    }


    public static PlanEnum match(int value) {
        for (PlanEnum planEnum : PlanEnum.values()) {
            if (value == planEnum.getValue()) {
                return planEnum;
            }
        }

        return null;
    }

    public static final int TWO_STAGE_OPTION = 100;

}
